package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Activities;
import models.Client;
import models.HighlightProduct;
import models.Parameter;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.PlansEnum;
import util.Utils;

@CRUD.For(models.Activities.class)
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
		Activities object = (Activities) constructor.newInstance();
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
		Activities object = Activities.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()).first();
		notFoundIfNull(object);
		try {
			render(type, object, users, clients);
		} catch (TemplateNotFoundException e) {
			render("ActivitiesCRUD/show.html", type, object, users, clients);
		}
	}
	
	public static void timeline(Client client) {
		List<Client> clients = null;
		if (client.id == null) {
			clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
			client = clients.iterator().next();
		}
		List<Activities> activities = Activities.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true and client_id = " + client.id + " order by postedAt desc").fetch();
		render(clients, client, activities);
	}
	
	public static void remove(String id) throws Exception {
		Activities activities = Activities.find("id = " + Long.valueOf(id)).first();
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
			Client client = Client.findById(Long.valueOf(clientId));
			List<Activities> activities = Activities.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true and client_id = " + client.id + "  order by postedAt desc").fetch();
			render("includes/timeline.html", client, activities);
		}
	}
	
	public static void getImage(long id) {
		final Activities activity = Activities.findById(id);
		notFoundIfNull(activity);
		if (activity.getImage() != null) {
			renderBinary(activity.getImage().get());
			return;
		}
	}
	
	
}
