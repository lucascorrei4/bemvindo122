package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Institution;
import models.User;
import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Scope.Session;
import util.UserInstitutionParameter;

public class Security extends Secure.Security {
	
	static boolean authenticate(String username, String password) {
		return User.connect(username, password) != null;
	}

	static boolean check(String profile) {
		if ("admin".equals(profile)) {
			return User.find("byEmail", connected()).<User> first().isAdmin;
		}
		return false;
	}

	static void onDisconnected() {
		Cache.delete(Admin.getLoggedUserInstitution().getCurrentSession());
		Application.index();
	}

	static void onAuthenticated() {
		User connectedUser = User.find("byEmail", session.get("username")).first();
		connect(connectedUser);
		if (connectedUser.getInstitutionId() == 0) {
			Admin.firstStep();
		} else {
			Admin.index();
		}
	}

	static void connect(User connectedUser) {
		/* Setting main object on connect user from login form */
		setCurrentSessionParameters(connectedUser);
	}
	
	static void setCurrentSessionParameters(User connectedUser) {
		/* Verify if I am */
		Map<String, Object> sessionParameters = new HashMap<String, Object>();
		sessionParameters.put("poweradmin", "lucascorreiaevangelista@gmail.com".equals(connectedUser.getEmail()) ? "true" : "false");
		sessionParameters.put("logged", connectedUser.id);
		sessionParameters.put("enableUser", enableMenu() ? "true" : "false");
		sessionParameters.put("idu", connectedUser.getId());
		sessionParameters.put("id", Admin.getLoggedUserInstitution().getInstitution() != null ? Admin.getLoggedUserInstitution().getInstitution().getId() : null);
		Cache.set(session.get("username"), sessionParameters);
	}
	
	static boolean enableMenu() {
		if (Admin.getLoggedUserInstitution().getInstitution() != null && Admin.validateLicenseDate(Admin.getLoggedUserInstitution())) {
			return true;
		}
		return false;
	}

}
