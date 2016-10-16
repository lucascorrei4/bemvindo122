package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import models.City;
import models.Country;
import models.Institution;
import models.State;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

@Check("admin")
@CRUD.For(models.Institution.class)
@With(Secure.class)
public class InstitutionController extends CRUD {
	public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
	
	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = "userId = " + Admin.getLoggedUserInstitution().getUser().getId();
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		Institution institution = Institution.find("userId = " + Admin.getLoggedUserInstitution().getUser().getId()).first();
		try {
			render(type, objects, count, totalCount, page, orderBy, order, institution);
		} catch (TemplateNotFoundException e) {
			render("InstitutionCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		try {
			render(type, object);
		} catch (TemplateNotFoundException e) {
			render("InstitutionCRUD/blank.html", type, object);
		}
	}
	
	public static void show(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        // Filtro pelo usuário conectado para proteger os dados dos demais usuários
     	Institution object = Institution.find("id = " + id + " and userId = " + Admin.getLoggedUserInstitution().getUser().getId()).first();
        notFoundIfNull(object);
        Country country = Country.find("byId", object.getCountryId()).first();
        State state = State.find("byId", object.getStateId()).first();
        City city = City.find("byId", object.getCityId()).first();
        try {
            render(type, object, country, state, city);
        } catch (TemplateNotFoundException e) {
            render("InstitutionCRUD/show.html", type, object, country, state, city);
        }
    }
	
	public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        Institution institution = Institution.findById(Long.valueOf(id).longValue());
        Country country = Country.find("byId", institution.getCountryId()).first();
        State state = State.find("byId", institution.getStateId()).first();
        City city = City.find("byId", institution.getCityId()).first();
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object, country, state, city);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object, country, state, city);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
	
}
