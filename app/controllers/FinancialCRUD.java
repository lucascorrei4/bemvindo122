package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import models.City;
import models.Country;
import models.Institution;
import models.Invoice;
import models.State;
import models.StatusSMS;
import models.User;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.With;
import util.Utils;

@CRUD.For(models.Invoice.class)
@With(Secure.class)
public class FinancialCRUD extends CRUD {
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
		String where = "institutionId = " + Admin.getLoggedUserInstitution().getInstitution();
		if (orderBy == null) {
			orderBy = "id";
		}
		if (order == null) {
			order = "DESC";
		}
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		Institution institution = Institution.find("userId = " + Admin.getLoggedUserInstitution().getUser().getId())
				.first();
		try {
			render(type, objects, count, totalCount, page, orderBy, order, institution);
		} catch (TemplateNotFoundException e) {
			render("FinancialCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
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
			render("FinancialCRUD/blank.html", type, object);
		}
	}

	public static void signature(String id) throws Exception {
		if (Utils.validateCompanySession(id)) {
			User user = Admin.getLoggedUserInstitution().getUser();
			ObjectType type = ObjectType.get(getControllerClass());
			notFoundIfNull(type);
			List<Invoice> listInvoice = Invoice.find("institutionId = " + id).fetch();
			for (Invoice invoice : listInvoice) {
				invoice.setSmsUnitPrice(0.10f);
				invoice.setSmsQtd(SMSController.getQtdSMSByDate(invoice.getInstitutionId(),
						Utils.getStringDateTime(invoice.getInvoiceGenerationDate()),
						Utils.getStringDateTime(invoice.getInvoiceDueDate())));
			}
			try {
				render(type, listInvoice, user);
			} catch (TemplateNotFoundException e) {
				render("FinancialCRUD/signature.html", type, listInvoice, user);
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
		country = Country.find("byId", institution.getCountryId()).first();
		state = State.find("byId", institution.getStateId()).first();
		city = City.find("byId", institution.getCityId()).first();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key(), country, state, city);
	}

}
