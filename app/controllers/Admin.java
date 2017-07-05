package controllers;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sun.mail.imap.protocol.Status;

import models.BodyMail;
import models.Client;
import models.Country;
import models.Institution;
import models.Invoice;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.Service;
import models.StatusMail;
import models.StatusPUSH;
import models.StatusSMS;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import util.StatusEnum;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.UserInstitutionParameter;
import util.Utils;
import util.VideoHelpEnum;

@With(Secure.class)
public class Admin extends Controller {
	public static UserInstitutionParameter loggedUserInstitution;
	public static boolean userFreeTrial = false;
	public static boolean smsExceedLimit = false;

	@Before
	static void globals() {
		if (getLoggedUserInstitution() == null || getLoggedUserInstitution().getUser() == null) {
			Application.index();
		}
		renderArgs.put("poweradmin",
				"lucascorreiaevangelista@gmail.com".equals(getLoggedUserInstitution().getUser().getEmail()) ? "true"
						: "false");
		renderArgs.put("logged", getLoggedUserInstitution().getUser().id);
		renderArgs.put("enableUser", Security.enableMenu() ? "true" : "false");
		renderArgs.put("idu", getLoggedUserInstitution().getUser().getId());
		renderArgs.put("id", getLoggedUserInstitution().getInstitution() != null
				? Admin.getLoggedUserInstitution().getInstitution().getId() : null);
		renderArgs.put("institutionName", getLoggedUserInstitution().getInstitution() != null
				? Admin.getLoggedUserInstitution().getInstitution().getInstitution() : null);
		renderArgs.put("videohelp", VideoHelpEnum.Index);
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
				int contOrderOfServices = OrderOfService.find("institutionId = " + connectedUser.getInstitutionId())
						.fetch().size();
				int contSentSMSs = StatusSMS.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				int contSentPushs = StatusPUSH.find("institutionId = " + connectedUser.getInstitutionId()).fetch()
						.size();
				int contSentMails = StatusMail.find("institutionId = " + connectedUser.getInstitutionId()).fetch()
						.size();
				List<Client> listClients = Client.find("institutionId = " + connectedUser.getInstitutionId()
						+ " and isActive = true order by postedAt desc").fetch(5);
				List<Service> listServices = Service.find("institutionId = " + connectedUser.getInstitutionId()
						+ " and isActive = true order by postedAt desc").fetch(5);
				List<OrderOfService> listOrderOfServices = OrderOfService.find("institutionId = "
						+ connectedUser.getInstitutionId() + " and isActive = true order by postedAt desc").fetch(5);
				List<OrderOfService> listOrderOfServicesByMonth = calculateTotalOrderOfService(connectedUser);
				String totalOfOrderOfServiceByMonth = Utils.getCurrencyValue(calculateTotalRevenueOfMonth(listOrderOfServicesByMonth));
				Institution institution = Institution.find("byId", connectedUser.getInstitutionId()).first();
				String institutionName = institution.getInstitution();
				Parameter parameter = Parameter.all().first();
				boolean smsExceedLimit = isSmsExceedLimit();
				boolean userFreeTrial = isUserFreeTrial();
				int allSents = contSentSMSs + contSentPushs + contSentMails;
				render(listClients, listServices, listOrderOfServices, contClients, contServices, contOrderOfServices,
						connectedUser, institutionName, contSentSMSs, institution, contSentPushs, parameter,
						smsExceedLimit, userFreeTrial, allSents, contSentMails, listOrderOfServicesByMonth, totalOfOrderOfServiceByMonth);
			} else {
				/* Redirect to page of information about expired license */
				render("@Admin.expiredLicense", connectedUser);
			}
		}
	}

	private static List<OrderOfService> calculateTotalOrderOfService(User connectedUser) {
		String firstDayOfMonth = Utils.getFirstDayMonthDate();
		String lastDayOfMonth = Utils.getLastDayMonthDate();
		List<OrderOfService> listOrderOfServices = OrderOfService
				.find("institutionId = " + connectedUser.getInstitutionId() + " and postedAt > '" + firstDayOfMonth
						+ "' and postedAt < '" + lastDayOfMonth + "' and isActive = true order by postedAt desc")
				.fetch();
		for (OrderOfService order : listOrderOfServices) {
			List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue
					.find("orderOfServiceId = " + Long.valueOf(order.id)).fetch();
			/* Get somatories values */
			Float totalGeral = 0f;
			for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				totalGeral += orderOfServiceValue.getTotalPrice();
			}
			order.setTotalOrderOfService(totalGeral);
			List<OrderOfServiceStep> listOrderOfServiceStep = OrderOfServiceStep
					.find("orderOfService_id = " + Long.valueOf(order.id) + " and institutionId = "
							+ order.getInstitutionId() + " and isActive = true")
					.fetch();
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
		long institutionId = userInstitutionParameter.getInstitution().getId();
		Invoice financial = Invoice
				.find("institutionId = " + institutionId + " and isActive = true order by postedAt desc").first();
		if (financial != null) {
			if (financial.getInvoiceDueDate().after(new Date())) {
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
		Invoice financial = Invoice.find("institutionId = " + getLoggedUserInstitution().getInstitution().getId()
				+ " and statusInvoice = 'Current' and isActive = true order by postedAt desc").first();
		if ("Isento".equals(financial.getStatusPayment().toString())) {
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
		int contSentSMSs = StatusSMS.find("institutionId = " + getLoggedUserInstitution().getInstitution().getId())
				.fetch().size();
		if (isUserFreeTrial() && contSentSMSs >= 5) {
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
					loggedUserInstitution
							.setInstitution((Institution) Institution.findById(loggedUser.getInstitutionId()));
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
		sender.setCompany("Seu Pedido Online");
		sender.setFrom("contato@seupedido.online");
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
		bodyMail.setBodyHTML(MailController.getHTMLTemplate(bodyMail));
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null);
		sendTo = new SendTo();
		sendTo.setDestination("th4mmy@gmail.com");
		sendTo.setName("Thammy");
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null);
	}

	static void sendMailToMeWithCustomInfo(String message, String info) {
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
		sender.setCompany("Seu Pedido Online");
		sender.setFrom("contato@seupedido.online");
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
		bodyMail.setBodyHTML(MailController.getHTMLTemplateResetPass(bodyMail));
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null);
	}

}
