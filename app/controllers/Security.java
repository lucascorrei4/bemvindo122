package controllers;

import models.Institution;
import models.User;
import util.UserInstitutionParameter;

public class Security extends Secure.Security {
	
	static boolean authentify(String username, String password) {
		return User.connect(username, password) != null;
	}

	static boolean check(String profile) {
		if ("admin".equals(profile)) {
			return User.find("byEmail", connected()).<User> first().isAdmin;
		}
		return false;
	}

	static void onDisconnected() {
		session.clear();
		Admin.loggedUserInstitution = null;
		Application.index();
	}

	static void onAuthenticated() {
		User connectedUser = User.find("byEmail", Security.connected()).first();
		connect(connectedUser);
		if (connectedUser.getInstitutionId() == 0) {
			Admin.firstStep();
		} else {
			Admin.index();
		}
	}

	static void connect(User connectedUser) {
		/* Setting main object on connect user from login form */
		UserInstitutionParameter userInstitutionParameter = new UserInstitutionParameter();
		userInstitutionParameter.setUser(connectedUser);
		long institutionId = connectedUser.getInstitutionId();
		userInstitutionParameter.setInstitution(institutionId == 0 ? null : (Institution) Institution.findById(institutionId));
		Admin.loggedUserInstitution = userInstitutionParameter;
		/* Verify if user belongs to a institution */
		setCurrentSessionParameters(connectedUser);
	}
	
	static void setCurrentSessionParameters(User connectedUser) {
		/* Verify if I am */
		session.put("poweradmin", "lucascorreiaevangelista@gmail.com".equals(connectedUser.getEmail()) ? "true" : "false");
		session.put("logged", connectedUser.id);
		session.put("enableUser", Admin.enableMenu() ? "true" : "false");
		session.put("idu", connectedUser.getId());
	}
	
}
