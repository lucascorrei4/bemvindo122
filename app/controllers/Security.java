package controllers;

import models.Institution;
import models.User;

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
		Admin.userInstitutionParameter = null;
		Application.index();
	}

	static void onAuthenticated() {
		User user = User.find("byEmail", Security.connected()).first();
		connect(user);
		if (user.getInstitutionId() == 0) {
			Admin.firstStep();
		} else {
			Admin.index();
		}
	}

	static void connect(User user) {
		session.put("logged", user.id);
		// Verifica se o usuário já está vinculado a uma instituição
		Admin.enableUserConditions(user);
	}

	static User connectedUser() {
		String userId = session.get("logged");
		return userId == null ? null : (User) User.findById(Long.parseLong(userId));
	}
	
}
