package controllers.howtodo;

import java.util.Date;
import java.util.List;

import controllers.Application;
import controllers.Secure;
import controllers.Security;
import models.Country;
import models.Institution;
import models.Invoice;
import models.MonetizzeTransaction;
import models.SendTo;
import models.Sender;
import models.StatusMail;
import models.User;
import models.howtodo.Article;
import models.howtodo.BodyMail;
import models.howtodo.FreePage;
import models.howtodo.MailList;
import models.howtodo.Message;
import models.howtodo.Parameter;
import models.howtodo.SellPage;
import models.howtodo.SequenceMail;
import play.db.jpa.JPA;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import util.PlansEnum;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.UserInstitutionParameter;
import util.Utils;
import util.VideoHelpEnum;

@With(Secure.class)
public class AdminPub extends Controller {
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
		renderArgs.put("id", getLoggedUserInstitution().getInstitution() != null ? AdminPub.getLoggedUserInstitution().getInstitution().getId() : null);
		renderArgs.put("institutionName", getLoggedUserInstitution().getInstitution() != null ? AdminPub.getLoggedUserInstitution().getInstitution().getInstitution() : null);
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
		Parameter parameter = Parameter.all().first();
		if (connectedUser == null || connectedUser.getInstitutionId() == 0) {
			AdminPub.firstStep();
		} else {
			/* Verify expiration license */
			if (validateLicenseDate(getLoggedUserInstitution())) {
				int contLeads = MailList.findAll().size();
				int contArticles = Article.find("isActive = true order by postedAt desc").fetch().size();
				int contSequenceMails = SequenceMail.find("isActive = true order by postedAt desc").fetch().size();
				List<SellPage> listSellPages = SellPage.find("isActive = true order by postedAt desc").fetch(5);
				List<FreePage> listFreePage = FreePage.find("isActive = true order by postedAt desc").fetch(5);
				List<Article> listArticles = Article.find("isActive = true order by postedAt desc").fetch(5);
				List<MailList> listLast5Leads = MailList.find("order by postedAt desc").fetch(5);
				List<Message> listMessages = Message.find("order by postedAt desc").fetch(10);
				Institution institution = Institution.find("byId", connectedUser.getInstitutionId()).first();
				String institutionName = institution.getInstitution();
				List<Object> leadsByPage = getleadsByPage();
				String top3LeadPages = top3LeadPages();
				render(listSellPages, listFreePage, contLeads, connectedUser, institutionName, institution, parameter, smsExceedLimit, userFreeTrial, leadsByPage, listArticles, contArticles, top3LeadPages, contSequenceMails, contSequenceMails, listLast5Leads, listMessages);
			} else {
				/* Redirect to page of information about expired license */
				render("@Admin.expiredLicense", connectedUser, parameter);
			}
		}
	}

	public static List<Object> getleadsByPage() {
		List<Object> leadsByPage = JPA.em().createNativeQuery("SELECT count(*), url, typeContentPage FROM MailList where url is not null group by typeContentPage, url order by COUNT(*) desc limit 20").getResultList();
		for (int i = 0; i < leadsByPage.size(); i++) {
			Object[] obj = (Object[]) leadsByPage.get(i);
			if (!Utils.isNullOrEmpty(obj[2])) {
				if ("TextContent".equals(obj[2])) {
					obj[2] = "Texto";
				} else if ("VideoContent".equals(obj[2])) {
					obj[2] = "Video";
				}
			}
		}
		return leadsByPage;
	}

	public static String top3LeadPages() {
		List<Object> leadsByPage = getleadsByPage();
		List<Object> top3leadsByPage = null;
		String json = "";
		if (leadsByPage != null && leadsByPage.size() > 3) {
			top3leadsByPage = leadsByPage.subList(0, 3);
			for (int i = 0; i < top3leadsByPage.size(); i++) {
				json += "{";
				Object[] x = (Object[]) top3leadsByPage.get(i);
				json += "label: \"" + String.valueOf(x[1]) + "\",";
				json += "value: " + x[0];
				if (i < top3leadsByPage.size() - 1) {
					json += "},";
				}
			}
			json += "}";
		}
		return json;
	}

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
		invoice.setSmsUnitPrice(0f);
		invoice.setSmsValue(0f);
		invoice.setValue(0f);
		invoice.willBeSaved = true;
		invoice.merge();
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
		AdminPub.loggedUserInstitution = loggedUserInstitution;
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
		bodyMail.setBodyHTML(MailController.getHTMLTemplate(bodyMail, parameter));
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
		bodyMail.setBodyHTML(MailController.getHTMLTemplateSimple(bodyMail, parameter));
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

	public static void main(String[] args) {
		Integer a = new Integer(3);
		Integer b = new Integer(3);
		if (a == b) {
			System.out.println(a == b);
		}
	}

}