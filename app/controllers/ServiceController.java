package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.CRUD.ObjectType;
import models.OrderOfService;
import models.OrderOfServiceValue;
import models.Service;
import models.Step;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import util.Utils;

@CRUD.For(models.Service.class)
public class ServiceController extends CRUD {
	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = "institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId();
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("ServiceController/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		// Filtro pelo usuário conectado para proteger os dados dos demais
		// usuários
		Service object = Service.find(
				"id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId())
				.first();
		List<Step> steps = Step.find("service_id = " + Long.valueOf(id) + " and institutionId = "
				+ Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by position asc")
				.fetch();
		notFoundIfNull(object);
		try {
			render(type, object, steps);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object, steps);
		}
	}

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
		Service service = (Service) object;
		String[] jsonContent = params.get("steps", String[].class);
		generateSteps(service, jsonContent[0]);
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}
	
	public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        Service service = (Service) object;
		String[] jsonContent = params.get("steps", String[].class);
		updateSteps(service, jsonContent[0]);
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

	private static void generateSteps(Service service, String jsonContent) {
		if (jsonContent != null) {
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(jsonContent).getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jObject = (JsonObject) jsonArray.get(i);
				Step step = new Step();
				step.setService(service);
				step.setTitle(jObject.get("title").getAsString());
				step.setDescription(jObject.get("description").getAsString());
				step.setEstimatedDuration(jObject.get("duration").getAsFloat());
				step.setPostedAt(Utils.getCurrentDateTime());
				step.setInstitutionId(service.getInstitutionId());
				step.setActive(true);
				step.setPosition(i + 1);
				step.willBeSaved = true;
				step.save();
			}
		}
	}

	private static void updateSteps(Service service, String jsonContent) {
		if (jsonContent != null) {
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(jsonContent).getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jObject = (JsonObject) jsonArray.get(i);
				String id = jObject.get("id").getAsString();
				Step step = null;
				if ("0".equals(id)) {
					step = new Step();
				} else {
					step = Step.find("id = " + Long.valueOf(id) + " and institutionId = "
							+ service.getInstitutionId()).first();
				}
				step.setService(service);
				step.setTitle(jObject.get("title").getAsString());
				step.setDescription(jObject.get("description").getAsString());
				step.setEstimatedDuration(jObject.get("duration").getAsFloat());
				step.setPostedAt(Utils.getCurrentDateTime());
				step.setInstitutionId(service.getInstitutionId());
				step.setActive(true);
				step.setPosition(i + 1);
				step.willBeSaved = true;
				step.save();
			}
		}
	}

}
