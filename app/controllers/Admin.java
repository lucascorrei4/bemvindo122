package controllers;

import java.util.Date;
import java.util.List;

import models.Client;
import models.Country;
import models.Institution;
import models.Invoice;
import models.OrderOfService;
import models.Parameter;
import models.Service;
import models.StatusPUSH;
import models.StatusSMS;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.UserInstitutionParameter;
import util.Utils;

@With(Secure.class)
public class Admin extends Controller {
	public static UserInstitutionParameter userInstitutionParameter = null;

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.name);
		}
	}

	@Before
	static void globals() {
		renderArgs.put("connected", connectedUser());
	}

	public static void firstStep() {
		List<Country> listCountries = Country.findAll();
		renderArgs.put("connectedUser", Admin.getLoggedUser());
		render(listCountries);
	}

	public static void save(User user) {
		validation.valid(user);
		if (validation.hasErrors()) {
			render("@form", user);
		}
		user.save();
		index();
	}

	public static void connect(User user) {
		session.put("logged", user.id);
		if ("lucascorreiaevangelista@gmail.com".equals(user.getEmail())) {
			session.put("poweradmin", "true");
		} else {
			session.put("poweradmin", "false");
		}
	}

	public static User connectedUser() {
		String userId = session.get("logged");
		return userId == null ? null : (User) User.findById(Long.parseLong(userId));
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
				int contSentPushs = StatusPUSH.find("institutionId = " + connectedUser.getInstitutionId()).fetch().size();
				List<Client> listClients = Client.find("institutionId = " + connectedUser.getInstitutionId()
						+ " and isActive = true order by postedAt desc").fetch(5);
				List<Service> listServices = Service.find("institutionId = " + connectedUser.getInstitutionId()
						+ " and isActive = true order by postedAt desc").fetch(5);
				List<OrderOfService> listOrderOfServices = OrderOfService.find("institutionId = "
						+ connectedUser.getInstitutionId() + " and isActive = true order by postedAt desc").fetch(5);
				Institution institution = Institution.find("byId", connectedUser.getInstitutionId()).first();
				String institutionName = institution.getInstitution();
				Invoice financial = Invoice
						.find("institutionId = " + institution.getId() + " and statusInvoice = 'Current' and isActive = true order by postedAt desc").first();
				Parameter parameter = Parameter.all().first();
				render(listClients, listServices, listOrderOfServices, contClients, contServices, contOrderOfServices,
						connectedUser, institutionName, contSentSMSs, institution, contSentPushs, financial, parameter);
			} else {
				/* Redirect to page of information about expired license */
				session.put("enableUser", "false");
				render("@admin.expiredLicense", connectedUser);
			}
		}
	}

	public static User getLoggedUser() {
		String userId = session.get("logged");
		return userId == null ? null : (User) User.findById(Long.parseLong(userId));
	}

	public static Institution getLoggedInstitution() {
		long institutionId = getLoggedUser().getInstitutionId();
		return institutionId == 0 ? null : (Institution) Institution.findById(institutionId);
	}

	public static UserInstitutionParameter getLoggedUserInstitution() {
		if (userInstitutionParameter == null)
			userInstitutionParameter = new UserInstitutionParameter();
		if (userInstitutionParameter.getUser() == null || userInstitutionParameter.getInstitution() == null) {
			User loggedUser = getLoggedUser();
			if (loggedUser != null) {
				userInstitutionParameter.setUser(loggedUser);
				if (loggedUser.getInstitutionId() > 0) {
					userInstitutionParameter.setInstitution(getLoggedInstitution());
				}
			}
		}
		return userInstitutionParameter;
	}

	protected static void enableUserConditions(User user) {
		if (enableMenu()) {
			session.put("enableUser", "true");
			session.put("idu", user.getId());
		} else {
			session.put("enableUser", "false");
		}
		return;
	}

	static boolean enableMenu() {
		if (userBelongsToInstitution() && validateLicenseDate(Admin.getLoggedUserInstitution())) {
			return true;
		}
		return false;
	}

	private static boolean userBelongsToInstitution() {
		if (getLoggedInstitution() == null) {
			return false;
		}
		session.put("id", getLoggedInstitution().getId());
		return true;
	}

	private static boolean validateLicenseDate(UserInstitutionParameter userInstitutionParameter) {
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
		invoice.setStatusPayment(StatusPaymentEnum.Free);
		invoice.setSmsQtd(0l);
		invoice.setSmsUnitPrice(parameter.getSmsPricePlan());
		invoice.setSmsValue(0f);
		invoice.setValue(parameter.getCurrentPricePlan());
		invoice.willBeSaved = true;
		invoice.merge();
	}

}
