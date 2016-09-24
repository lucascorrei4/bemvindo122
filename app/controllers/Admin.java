package controllers;

import java.util.List;

import models.Service;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {
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

}
