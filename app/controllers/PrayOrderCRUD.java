package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.PrayOrder;
import models.PrayOrderItem;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.Utils;

@CRUD.For(models.PrayOrder.class)
public class PrayOrderCRUD extends CRUD {
	
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
			render("PrayOrderCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("PrayOrderCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		// Filtro pelo usuário conectado para proteger os dados dos demais
		// usuários
		PrayOrder object = PrayOrder.find(
				"id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId())
				.first();
		List<PrayOrderItem> prayOrderItems = PrayOrderItem.find("prayOrder_id = " + Long.valueOf(id) + " and institutionId = "
				+ Admin.getLoggedUserInstitution().getInstitution().getId()
				+ " and isActive = true order by position asc").fetch();
		notFoundIfNull(object);
		try {
			render(type, object, prayOrderItems);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object, prayOrderItems);
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
		PrayOrder prayOrder = (PrayOrder) object;
		String[] jsonContent = params.get("steps", String[].class);
		generatePrayOrderItems(prayOrder, jsonContent[0]);
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
		PrayOrder prayOrder = (PrayOrder) object;
		String[] jsonContent = params.get("steps", String[].class);
		updatePrayOrder(prayOrder, jsonContent[0]);
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	private static void generatePrayOrderItems(PrayOrder prayOrder, String jsonContent) {
		if (jsonContent != null) {
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(jsonContent).getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jObject = (JsonObject) jsonArray.get(i);
				PrayOrderItem prayOrderItem = new PrayOrderItem();
				prayOrderItem.setPrayOrder(prayOrder);
				prayOrderItem.setTitle(jObject.get("title").getAsString());
				prayOrderItem.setPostedAt(Utils.getCurrentDateTime());
				prayOrderItem.setInstitutionId(prayOrder.getInstitutionId());
				prayOrderItem.setActive(true);
				prayOrderItem.setPosition(i + 1);
				prayOrderItem.willBeSaved = true;
				prayOrderItem.save();
			}
		}
	}

	private static void updatePrayOrder(PrayOrder prayOrder, String jsonContent) {
		if (jsonContent != null) {
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(jsonContent).getAsJsonArray();
			removePrayOrderItems(prayOrder);
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jObject = (JsonObject) jsonArray.get(i);
				PrayOrderItem prayOrderItem = new PrayOrderItem();
				prayOrderItem.setPrayOrder(prayOrder);
				prayOrderItem.setTitle(jObject.get("title").getAsString());
				prayOrderItem.setPostedAt(Utils.getCurrentDateTime());
				prayOrderItem.setInstitutionId(prayOrder.getInstitutionId());
				prayOrderItem.setActive(true);
				prayOrderItem.setPosition(i + 1);
				prayOrderItem.willBeSaved = true;
				prayOrderItem.save();
			}
		}
	}


	private static void removePrayOrderItems(PrayOrder prayOrder) {
		List<PrayOrderItem> prayOrderItems = PrayOrderItem.find("prayOrder_id = " + prayOrder.id).fetch();
		for (PrayOrderItem prayOrderItem : prayOrderItems) {
			PrayOrderItem.delete("id = " + prayOrderItem.id);
		}
	}

	public static void remove(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        PrayOrder object = PrayOrder.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()).first();
        removePrayOrderItems(object);
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
