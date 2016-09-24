package controllers;

import models.User;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		User user = User.find("byEmail", username).first();
		return user != null && user.password.equals(password);
	}

	static boolean authentify(String username, String password) {
		return User.connect(username, password) != null;
	}

	static boolean check(String profile) {
		if ("admin".equals(profile)) {
			return User.find("byEmail", connected()).<User>first().isAdmin;
		}
		return false;
	}

	static void onDisconnected() {
		Application.index();
	}

	static void onAuthenticated() {
		User user = User.find("byEmail", Security.connected()).first();
		connect(user);
		CRUD.index();
	}

	static void connect(User user) {
		session.put("logged", user.id);
	}

	static User connectedUser() {
		String userId = session.get("logged");
		return userId == null ? null : (User) User.findById(Long.parseLong(userId));
	}

}
