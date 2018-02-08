package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Activity;
import models.Client;
import models.HighlightProduct;
import models.Parameter;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.ActivitiesEnum;
import util.PlansEnum;
import util.Utils;

@CRUD.For(models.Activity.class)
public class ActivitiesCRUD extends CRUD {
	public static int AUTOCOMPLETE_MAX = 10;
	
	@Before
	static void globals() {
		if (Admin.getLoggedUserInstitution() == null || Admin.getLoggedUserInstitution().getUser() == null) {
			Application.index();
		} 
		renderArgs.put("poweradmin", "lucascorreiaevangelista@gmail.com".equals(Admin.getLoggedUserInstitution().getUser().getEmail()) ? "true" : "false");
		renderArgs.put("logged", Admin.getLoggedUserInstitution().getUser().id);
		renderArgs.put("enableUser", Security.enableMenu() ? "true" : "false");
		renderArgs.put("idu", Admin.getLoggedUserInstitution().getUser().getId());
		renderArgs.put("id", Admin.getLoggedUserInstitution().getInstitution() != null ? Admin.getLoggedUserInstitution().getInstitution().getId() : null);
		renderArgs.put("institutionName", Admin.getLoggedUserInstitution().getInstitution() != null ? Admin.getLoggedUserInstitution().getInstitution().getInstitution() : null);
		renderArgs.put("planSPO02", PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()));
	}
	
	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = "institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId();
		if (orderBy == null) {
			orderBy = "id";
		}
		if (order == null) {
			order = "DESC";
		}
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("ActivitiesCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}
	
	public static void main(String[] args) {
		String [] list = "[client.name] [title]".split("[ ]");
		for (String string : list) {
			System.out.println(string);
		}
	}
	
	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Activity object = (Activity) constructor.newInstance();
		List<Client> clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
		List<User> users = User.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
		try {
			render(type, object, users, clients);
		} catch (TemplateNotFoundException e) {
			render("ActivitiesCRUD/blank.html", type, object, users, clients);
		}
	}
	
	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		List<Client> clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
		List<User> users = User.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
		Activity object = Activity.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()).first();
		notFoundIfNull(object);
		try {
			render(type, object, users, clients);
		} catch (TemplateNotFoundException e) {
			render("ActivitiesCRUD/show.html", type, object, users, clients);
		}
	}
	
	public static void timeline(Client clientTimeline) {
		List<Client> clients = null;
		List<Activity> activities = null;
		if (Utils.isNullOrZero(clientTimeline.id)) {
			clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
			if (!Utils.isNullOrEmpty(clients)) {
				clientTimeline = clients.iterator().next();
				activities = Activity.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true and client_id = " + clientTimeline.id + " order by postedAt desc").fetch();
			}
		}
		render(clients, clientTimeline, activities);
	}
	
	public static void remove(String id) throws Exception {
		Activity activities = Activity.find("id = " + Long.valueOf(id)).first();
		activities.setActive(false);
		activities.willBeSaved = true;
		activities.save();
		ActivitiesCRUD.list(0, null, null, null, null);
	}
	
	public static void getClientsJSON() {
		List<Client> listClients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by postedAt desc").fetch();
		renderJSON(listClients);
	}

	public static void listClientsAutoComplete(final String[] term) {
		List<Client> listClients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by postedAt desc").fetch();
		List<Client> filteredClients = new ArrayList<Client>();
		String aux = term[0];
		if (aux != null) {
			for (Client member : listClients) {
				if (member.name.toLowerCase().startsWith(aux.toLowerCase())) {
					filteredClients.add(member);
				}
				if (filteredClients.size() == AUTOCOMPLETE_MAX) {
					break;
				}
			}
		}
		renderJSON(filteredClients);
	}
	
	public static void searchClient(final String clientId) {
		if (!Utils.isNullOrEmpty(clientId)) {
			Client clientTimeline = Client.findById(Long.valueOf(clientId));
			List<Activity> activities = Activity.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true and client_id = " + clientTimeline.id + "  order by postedAt desc").fetch();
			render("includes/timeline.html", clientTimeline, activities);
		}
	}
	
	public static void getImage(long id) {
		final Activity activity = Activity.findById(id);
		notFoundIfNull(activity);
		if (activity.getImage() != null) {
			renderBinary(activity.getImage().get());
			return;
		}
	}
	
	public static boolean generateActivity(String title, String description, Client client, long institutionId, User collaborator, ActivitiesEnum type) {
		Activity activity = new Activity();
		activity.setTitle(title);
		activity.setDescription(description);
		activity.setClient(client);
		activity.setType(type);
		activity.setCollaborator(collaborator);
		activity.setInstitutionId(institutionId);
		activity.setActivityDate(Utils.formatDateSimple(new Date()));
		activity.setPostedAt(Utils.getCurrentDateTime());
		activity.setGeneratedSale(false);
		activity.setActive(true);
		activity.willBeSaved = true;
		activity.save();
		return true;
	}

	
	
}
