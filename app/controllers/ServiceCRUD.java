package controllers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.CRUD.ObjectType;
import models.Client;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.Service;
import models.Step;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.StatusEnum;
import util.Utils;
import util.VideoHelpEnum;

@CRUD.For(models.Service.class)
public class ServiceCRUD extends CRUD {
	
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
		renderArgs.put("videohelp", VideoHelpEnum.Services);
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
			render("ServiceCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("ServiceCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
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
				+ Admin.getLoggedUserInstitution().getInstitution().getId()
				+ " and isActive = true order by position asc").fetch();
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
			List<OrderOfService> ordersOfService = getOrdersOfServiceByService(service);
			removeStepsByService(service);
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
				addOrderOfServiceSteps(ordersOfService, step, service);
			}
		}
	}

	private static List<OrderOfService> getOrdersOfServiceByService(Service service) {
		List<OrderOfService> listOfOrderOfService = new ArrayList<OrderOfService>();
		List<OrderOfServiceValue> ordersOfServiceValueByService = OrderOfServiceValue
				.find("service_id = " + service.id + " and institutionId = " + service.getInstitutionId()).fetch();
		for (OrderOfServiceValue orderOfServiceValue : ordersOfServiceValueByService) {
			OrderOfService orderOfService = OrderOfService.find("id = " + orderOfServiceValue.getOrderOfServiceId()
					+ " and institutionId = " + service.getInstitutionId()).first();
			listOfOrderOfService.add(orderOfService);
		}
		return listOfOrderOfService;
	}

	private static void removeStepsByService(Service service) {
		List<Step> steps = Step.find("service_id = " + service.id).fetch();
		for (Step step : steps) {
			OrderOfServiceStep.delete("step_id = " + step.id);
			Step.delete("id = " + step.id);
		}
	}

	private static void addOrderOfServiceSteps(List<OrderOfService> ordersOfService, Step step, Service service) {
		for (OrderOfService orderOfService : ordersOfService) {
			OrderOfServiceStep orderServiceStep = new OrderOfServiceStep();
			orderServiceStep.setOrderOfService(orderOfService);
			orderServiceStep.setStep(step);
			orderServiceStep.setService(step.getService());
			orderServiceStep.setStatus(StatusEnum.NotStarted);
			orderServiceStep.setObs("Nenhuma");
			orderServiceStep.setPostedAt(Utils.getCurrentDateTime());
			orderServiceStep.setInstitutionId(orderOfService.getInstitutionId());
			orderServiceStep.willBeSaved = true;
			orderServiceStep.save();
		}
	}
	
	public static void remove(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Service object = Service.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()).first();
        notFoundIfNull(object);
        try {
            object.delete();
        } catch (Exception e) {
            flash.error(play.i18n.Messages.get("crud.delete.error", type.modelName));
            redirect(request.controller + ".show", object._key());
        }
        flash.success(play.i18n.Messages.get("crud.deleted", type.modelName));
        redirect(request.controller + ".list");
    }

}
