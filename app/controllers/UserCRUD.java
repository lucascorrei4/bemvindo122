package controllers;

import java.util.List;

import models.City;
import models.Country;
import models.State;
import models.User;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.With;
import util.Utils;

@CRUD.For(models.User.class)
@With(Secure.class)
public class UserCRUD extends CRUD {
	
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
	}
	
	public static void listAll(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy == null) {
        	orderBy = "id";
        }
        if (order == null) {
        	order = "DESC";
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("UserCRUD/listAll.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		redirect(request.controller + ".show", Admin.getLoggedUserInstitution().getUser().getId());
	}

	public static void show(String id) throws Exception {
		if (Utils.validateUserSession(id)) {
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			// Filtro pelo usuário conectado para proteger os dados dos demais
			// usuários
			User object = User.find(
					"id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId())
					.first();
			Country country = Country.find("byId", object.getCountryId()).first();
			State state = State.find("byId", object.getStateId()).first();
			City city = City.find("byId", object.getCityId()).first();
			notFoundIfNull(object);
			try {
				render(type, object, country, state, city);
			} catch (TemplateNotFoundException e) {
				render("UserCRUD/show.html", type, object, country, state, city);
			}
		} else {
			redirect("Admin.index");
		}
	}
	
	public static void save(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		User user = User.findById(Long.valueOf(id).longValue());
		Country country = Country.find("byId", user.getCountryId()).first();
		State state = State.find("byId", user.getStateId()).first();
		City city = City.find("byId", user.getCityId()).first();
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html", type, object, country, state, city);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object, country, state, city);
			}
		}
		object._save();
		country = Country.find("byId", user.getCountryId()).first();
		state = State.find("byId", user.getStateId()).first();
		city = City.find("byId", user.getCityId()).first();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key(), country, state, city);
	}

	
}
