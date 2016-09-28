package controllers;

import java.util.Date;
import java.util.List;

import models.Institution;
import models.Service;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import util.UserInstitutionParameter;

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
		render();
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
	}

	public static User connectedUser() {
		String userId = session.get("logged");
		return userId == null ? null : (User) User.findById(Long.parseLong(userId));
	}

	public static void index() {
		User connectedUser = connectedUser();
		List<Service> ordersChalkChildren = Service.find("order by postedAt desc").fetch(25);
		render(connectedUser, ordersChalkChildren);
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
			if (getLoggedUser() != null) {
				userInstitutionParameter.setUser(getLoggedUser());
				if (getLoggedUser().getInstitutionId() > 0) {
					userInstitutionParameter.setInstitution(getLoggedInstitution());
				}
			}
		}
		return userInstitutionParameter;
	}
	
	protected static void enableUserConditions(User user) {
		if (enableMenu()) {
			session.put("enableUser", "true");
		} else {
			session.put("enableUser", "false");
		}
		return;
	}
	
	static boolean enableMenu() {
		if (userBelongsToInstitution() && validateLicenseDate()) {
			return true;
		}
		return false;
	}
	
	private static boolean userBelongsToInstitution() {
		if (getLoggedInstitution() == null) {
			return false;
		}
		return true;
	}
	
	private static boolean validateLicenseDate() {
		if (getLoggedUserInstitution().getInstitution().getLicenseDate().after(new Date()))
			return true;
		else
			return false;
	}

}
