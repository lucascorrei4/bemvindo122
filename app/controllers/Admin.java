
package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

import controllers.howtodo.MailController;
import models.Activity;
import models.Client;
import models.Country;
import models.Institution;
import models.Invoice;
import models.MonetizzeTransaction;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.SendTo;
import models.Sender;
import models.Service;
import models.StatusMail;
import models.StatusPUSH;
import models.StatusSMS;
import models.User;
import models.howtodo.BodyMail;
import models.howtodo.Parameter;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import util.PlansEnum;
import util.StatusEnum;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.UserInstitutionParameter;
import util.Utils;
import util.VideoHelpEnum;
import util.howtodo.MailTemplates;
import util.howtodo.TO;
import util.howtodo.TONumeric;

@With(Secure.class)
public class Admin extends Controller {
	public static UserInstitutionParameter loggedUserInstitution;
	public static boolean userFreeTrial = false;
	public static boolean smsExceedLimit = false;
	public static Invoice institutionInvoice;

	@Before
	static void globals() {
		if (getLoggedUserInstitution() == null || getLoggedUserInstitution().getUser() == null) {
			Application.index();
		}
		renderArgs.put("poweradmin", "lucascorreiaevangelista@gmail.com".equals(getLoggedUserInstitution().getUser().getEmail()) ? "true" : "false");
		renderArgs.put("logged", getLoggedUserInstitution().getUser().id);
		renderArgs.put("enableUser", Security.enableMenu() ? "true" : "false");
		renderArgs.put("idu", getLoggedUserInstitution().getUser().getId());
		renderArgs.put("id", getLoggedUserInstitution().getInstitution() != null ? Admin.getLoggedUserInstitution().getInstitution().getId() : null);
		renderArgs.put("institutionName", getLoggedUserInstitution().getInstitution() != null ? Admin.getLoggedUserInstitution().getInstitution().getInstitution() : null);
		renderArgs.put("videohelp", VideoHelpEnum.Index);
		renderArgs.put("planSPO02", PlansEnum.isPlanSPO02(getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(getInstitutionInvoice().getPlan().getValue()));
	}

	public void loadVariables() {
	}

	public static void firstStep() {
		User connectedUser = getLoggedUserInstitution().getUser();
		List<Country> listCountries = Country.findAll();
		Parameter parameter = Parameter.all().first();
		render(listCountries, connectedUser, parameter);
	}

	public static void save(User user) {
		validation.valid(user);
		if (validation.hasErrors()) {
			render("@form", user);
		}
		user.save();
		index();
	}

	public static void index() {
		User connectedUser = getLoggedUserInstitution().getUser();
		if (connectedUser == null || connectedUser.getInstitutionId() == 0) {
			Admin.firstStep();
		} else {
			/* Verify expiration license */
			if (validateLicenseDate(getLoggedUserInstitution())) {
				int contClients = Client.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				int contServices = Service.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				int contOrderOfServices = OrderOfService.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				int contSentSMSs = StatusSMS.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				int contSentPushs = StatusPUSH.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				int contSentMails = StatusMail.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				List<Client> listClients = Client.find("institutionId = " + connectedUser.getInstitutionId() + " and isActive = true order by postedAt desc").fetch(5);
				List<Service> listServices = Service.find("institutionId = " + connectedUser.getInstitutionId() + " and isActive = true order by postedAt desc").fetch(5);
				List<OrderOfService> listOrderOfServices = OrderOfService.find("institutionId = " + connectedUser.getInstitutionId() + " and isActive = true order by postedAt desc").fetch(5);
				List<OrderOfService> listOrderOfServicesByMonth = calculateTotalOrderOfService(connectedUser);
				String totalOfOrderOfServiceByMonth = Utils.getCurrencyValue(calculateTotalRevenueOfMonth(listOrderOfServicesByMonth));
				Institution institution = Institution.find("byId", connectedUser.getInstitutionId()).first();
				String institutionName = institution.getInstitution();
				Parameter parameter = Parameter.all().first();
				boolean smsExceedLimit = isSmsExceedLimit();
				boolean userFreeTrial = isUserFreeTrial();
				int allSents = contSentSMSs + contSentPushs + contSentMails;
				/* Get vip client and show his timeline */
				Client clientTimeline = null;
				List<Activity> activities = null;
				String totalSellByClient = null;
				Float totalGeral = 0f;
				Float biggerValue = 0f;
				List<Client> clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
				if (!Utils.isNullOrEmpty(clients)) {
					for (Client client : clients) {
						List<OrderOfService> listOrdOfServices = OrderOfService.find("client_id = " + client.id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by postedAt desc").fetch(20);
						if (!Utils.isNullOrEmpty(listOrdOfServices)) {
							for (OrderOfService order : listOrdOfServices) {
								List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(order.id)).fetch();
								/* Get somatories values */
								for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
									totalGeral += orderOfServiceValue.getTotalPrice();
								}
							}
						}
						if (totalGeral > biggerValue) {
							biggerValue = totalGeral;
							clientTimeline = client;
						}
						totalGeral = 0f;
					}
					activities = Activity.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true and client_id = " + clientTimeline.id + " order by postedAt desc").fetch(5);
					totalSellByClient = Utils.getCurrencyValue(biggerValue);
				}
				String top3Clients = null;
				if (!Utils.isNullOrEmpty(getTop3Clients())) {
					top3Clients = Utils.toJsonChart(getTop3Clients());
				} 
				render(listClients, listServices, listOrderOfServices, contClients, contServices, contOrderOfServices, connectedUser, institutionName, contSentSMSs, institution, contSentPushs, parameter, smsExceedLimit, userFreeTrial, allSents, contSentMails, listOrderOfServicesByMonth,
						totalOfOrderOfServiceByMonth, clientTimeline, activities, totalSellByClient, top3Clients);
			} else {
				/* Redirect to page of information about expired license */
				render("@Admin.expiredLicense", connectedUser);
			}
		}

	}

	private static List<TONumeric> getTop3Clients() {
		List<TONumeric> listTop3 = new ArrayList<TONumeric>();
		Float totalGeral = 0f;
		Float biggerValue1 = 0f;
		Float biggerValue2 = 0f;
		Float biggerValue3 = 0f;
		Client clientTop1 = null;
		Client clientTop2 = null;
		Client clientTop3 = null;
		List<Client> clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
		if (!Utils.isNullOrEmpty(clients)) {
			for (Client client : clients) {
				List<OrderOfService> listOrdOfServices = OrderOfService.find("client_id = " + client.id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by postedAt desc").fetch(20);
				if (!Utils.isNullOrEmpty(listOrdOfServices)) {
					for (OrderOfService order : listOrdOfServices) {
						List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(order.id)).fetch();
						/* Get somatories values */
						for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
							totalGeral += orderOfServiceValue.getTotalPrice();
						}
					}
				}
				if (totalGeral > biggerValue1) {
					biggerValue1 = totalGeral;
					clientTop1 = client;
				} else if (totalGeral > biggerValue2) {
					biggerValue2 = totalGeral;
					clientTop2 = client;
				} else if (totalGeral > biggerValue3) {
					biggerValue3 = totalGeral;
					clientTop3 = client;
				}
				totalGeral = 0f;
			}
		}
		TONumeric top1 = new TONumeric();
		top1.setLabel(clientTop1.name + "" + clientTop1.lastName);
		top1.setValue(biggerValue1);
		listTop3.add(top1);
		TONumeric top2 = new TONumeric();
		top2.setLabel(clientTop2.name + "" + clientTop2.lastName);
		top2.setValue(biggerValue2);
		listTop3.add(top2);
		TONumeric top3 = new TONumeric();
		top3.setLabel(clientTop3.name + "" + clientTop3.lastName);
		top3.setValue(biggerValue3);
		listTop3.add(top3);
		return listTop3;
	}

	private static List<OrderOfService> calculateTotalOrderOfService(User connectedUser) {
		String firstDayOfMonth = Utils.getFirstDayMonthDate();
		String lastDayOfMonth = Utils.getLastDayMonthDate();
		List<OrderOfService> listOrderOfServices = OrderOfService.find("institutionId = " + connectedUser.getInstitutionId() + " and postedAt > '" + firstDayOfMonth + "' and postedAt < '" + lastDayOfMonth + "' and isActive = true order by postedAt desc").fetch();
		for (OrderOfService order : listOrderOfServices) {
			List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(order.id)).fetch();
			/* Get somatories values */
			Float totalGeral = 0f;
			for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				totalGeral += orderOfServiceValue.getTotalPrice();
			}
			order.setTotalOrderOfService(totalGeral);
			List<OrderOfServiceStep> listOrderOfServiceStep = OrderOfServiceStep.find("orderOfService_id = " + Long.valueOf(order.id) + " and institutionId = " + order.getInstitutionId() + " and isActive = true").fetch();
			boolean isOpened = false;
			for (OrderOfServiceStep orderOfServiceStep : listOrderOfServiceStep) {
				if (orderOfServiceStep.status != StatusEnum.Finished) {
					isOpened = true;
					break;
				}
			}
			order.setCurrentStatus(isOpened ? StatusEnum.InProgress.getLabel() : StatusEnum.Finished.getLabel());
		}
		return listOrderOfServices;
	}

	private static Float calculateTotalRevenueOfMonth(List<OrderOfService> listOrderOfService) {
		Float totalGeral = 0f;
		for (OrderOfService order : listOrderOfService) {
			totalGeral += order.getTotalOrderOfService();
		}
		return totalGeral;
	}

	// public static User getLoggedUser() {
	// String userId = session.get("logged");
	// return userId == null ? null : (User)
	// User.findById(Long.parseLong(userId));
	// }
	//
	// public static Institution getLoggedInstitution() {
	// long institutionId = getLoggedUser().getInstitutionId();
	// return institutionId == 0 ? null : (Institution)
	// Institution.findById(institutionId);
	// }

	public static boolean validateLicenseDate(UserInstitutionParameter userInstitutionParameter) {
		Invoice invoice = getInstitutionInvoice();
		if (invoice != null) {
			if (invoice.getInvoiceDueDate().after(new Date())) {
				return true;
			} else {
				return false;
			}
		} else {
			saveNewPaymentInformation(userInstitutionParameter);
			return true;
		}
	}

	private static void saveNewPaymentInformation(UserInstitutionParameter userInstitutionParameter) {
		Parameter parameter = (Parameter) Parameter.findAll().iterator().next();
		Invoice invoice = new Invoice();
		invoice.setInstitutionId(userInstitutionParameter.getInstitution().getId());
		invoice.setUserId(userInstitutionParameter.getUser().getId());
		invoice.setMemberSinceDate(new Date());
		Date dueDate = Utils.addDays(invoice.getMemberSinceDate(), 30);
		invoice.setInvoiceGenerationDate(new Date());
		invoice.setInvoiceDueDate(dueDate);
		invoice.setPostedAt(Utils.getCurrentDateTime());
		invoice.setActive(true);
		invoice.setStatusInvoice(StatusInvoiceEnum.Current);
		if (userInstitutionParameter.getUser().isFromMonetizze()) {
			invoice.setStatusPayment(StatusPaymentEnum.Paid);
			MonetizzeTransaction financial = MonetizzeTransaction.find("customerMail = " + getLoggedUserInstitution().getUser().getEmail()).first();
			if (financial != null) {
				invoice.setPlan(PlansEnum.getValueByName(financial.getSellPlan()));
			}
		} else {
			invoice.setStatusPayment(StatusPaymentEnum.Free);
		}
		invoice.setSmsQtd(0l);
		invoice.setSmsUnitPrice(parameter.getSmsPricePlan());
		invoice.setSmsValue(0f);
		invoice.setValue(parameter.getCurrentPricePlan());
		invoice.willBeSaved = true;
		invoice.merge();
	}

	public static boolean isUserFreeTrial() {
		Invoice invoice = getInstitutionInvoice();
		if ("Isento".equals(invoice.getStatusPayment().toString())) {
			userFreeTrial = true;
		} else {
			userFreeTrial = false;
		}
		return userFreeTrial;
	}

	public static void setUserFreeTrial(boolean userFreeTrial) {
		Admin.userFreeTrial = userFreeTrial;
	}

	public static boolean isSmsExceedLimit() {
		int contSentSMSs = StatusSMS.find("institutionId = " + getLoggedUserInstitution().getInstitution().getId()).fetch().size();
		if (contSentSMSs >= 50) {
			smsExceedLimit = true;
		} else {
			smsExceedLimit = false;
		}
		return smsExceedLimit;
	}

	public static void setSmsExceedLimit(boolean smsExceedLimit) {
		Admin.smsExceedLimit = smsExceedLimit;
	}

	public static UserInstitutionParameter getLoggedUserInstitution() {
		if (loggedUserInstitution == null || loggedUserInstitution.getCurrentSession() != session.get("username"))
			loggedUserInstitution = new UserInstitutionParameter();
		if (loggedUserInstitution.getUser() == null || loggedUserInstitution.getInstitution() == null) {
			User loggedUser = User.find("byEmail", session.get("username")).first();
			if (loggedUser != null) {
				loggedUserInstitution.setUser(loggedUser);
				if (loggedUser.getInstitutionId() > 0) {
					loggedUserInstitution.setInstitution((Institution) Institution.findById(loggedUser.getInstitutionId()));
				}
			}
			loggedUserInstitution.setCurrentSession(session.get("username"));
		}
		return loggedUserInstitution;
	}

	public static void setLoggedUserInstitution(UserInstitutionParameter loggedUserInstitution) {
		Admin.loggedUserInstitution = loggedUserInstitution;
	}

	static void sendMailToMe(UserInstitutionParameter userInstitutionParameter, String message) {
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination("lucascorreiaevangelista@gmail.com");
		sendTo.setName("Eu mesmo");
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany(Utils.isNullOrEmpty(parameter.getMailSenderName()) ? parameter.getSiteTitle() : parameter.getMailSenderName());
		sender.setFrom(Utils.isNullOrEmpty(parameter.getMailSenderFrom()) ? parameter.getSiteMail() : parameter.getMailSenderFrom());
		sender.setKey("");
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		bodyMail.setTitle1("Ol&aacute;, Lucas e Thammy!");
		bodyMail.setTitle2(message);
		bodyMail.setParagraph1(userInstitutionParameter.getInstitution().getInstitution());
		bodyMail.setParagraph2(userInstitutionParameter.getInstitution().getCityStateCountry());
		bodyMail.setParagraph3(userInstitutionParameter.getInstitution().getEmail());
		bodyMail.setFooter1(Institution.findAll().size() + " empresas cadastradas.");
		bodyMail.setImage1(parameter.getLogoUrl());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplate(bodyMail, parameter));
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter);
		sendTo = new SendTo();
		sendTo.setDestination("th4mmy@gmail.com");
		sendTo.setName("Thammy");
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter);
	}

	public static void sendMailToMeWithCustomInfo(String message, String info) {
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination("lucascorreiaevangelista@gmail.com");
		sendTo.setName("Eu mesmo");
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany(Utils.isNullOrEmpty(parameter.getMailSenderName()) ? parameter.getSiteTitle() : parameter.getMailSenderName());
		sender.setFrom(Utils.isNullOrEmpty(parameter.getMailSenderFrom()) ? parameter.getSiteMail() : parameter.getMailSenderFrom());
		sender.setKey("");
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		bodyMail.setTitle1("Ol&aacute;, Lucas!");
		bodyMail.setTitle2(message);
		bodyMail.setParagraph1("Info: " + info);
		bodyMail.setParagraph2("");
		bodyMail.setParagraph3("");
		bodyMail.setFooter1("");
		bodyMail.setImage1(parameter.getLogoUrl());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplateSimple(bodyMail, parameter));
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter);
	}

	public static Invoice getInstitutionInvoice() {
		Invoice invoice = null;
		if (getLoggedUserInstitution().getInstitution() == null) {
			invoice = new Invoice();
			invoice.setPlan(PlansEnum.SPO01);
			return invoice;
		}
		invoice = Invoice.find("institutionId = " + getLoggedUserInstitution().getInstitution().getId() + " and statusInvoice = 'Current' and isActive = true order by postedAt desc").first();
		institutionInvoice = invoice;
		return institutionInvoice;
	}

}