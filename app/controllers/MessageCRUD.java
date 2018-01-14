package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.CRUD.ObjectType;
import models.Message;
import models.User;
import play.data.validation.Error;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.Utils;

@CRUD.For(models.Message.class)
public class MessageCRUD extends CRUD {
	
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

	public static void saveMessage(String json) throws UnsupportedEncodingException {
		User connectedUser = Admin.getLoggedUserInstitution().getUser();
		String response = null;
		String status = null;
		/* Get body content from client request */
		String[] fields = request.params.data.get("body");
		String decodedFields = URLDecoder.decode(fields[0], "UTF-8");
		Gson gson = new GsonBuilder().create();
		/* Parse form content to JSON element */
		String jsonParam = Utils.transformQueryParamToJson(decodedFields, "message.");
		JsonParser parser = new JsonParser();
		JsonObject jsonElement = (JsonObject) parser.parse(jsonParam);
		jsonElement.addProperty("id", Long.valueOf(0));
		/* Save Client */
		/* Create object parsing JSON element */
		Message message = new Message();
		message = gson.fromJson(jsonElement, Message.class);
		message.id = 0l;
		message.setInstitutionId(Admin.getLoggedUserInstitution().getInstitution().getId());
		message.willBeSaved = true;
		/* Validate object before saving */
		if (!validateObjectToSave(message)) {
			List<Error> errors = validation.errors();
			response = "Favor, preencha todos os campos corretamente!";
			status = "ERROR";
			render("includes/formSaveMessage.html", connectedUser, message, response, status, errors);
		} else {
			message.setPostedAt(Utils.getCurrentDateTime());
			message.setInstitutionId(0l);
			message.merge();
			response = "Muito obrigado pela mensagem!";
			status = "SUCCESS";
			render("includes/formSaveMessage.html", connectedUser, message, response, status);
		}
	}

	private static boolean validateObjectToSave(Message message) {
		validation.clear();
		validation.valid(message);
		if (validation.hasErrors()) {
			for (play.data.validation.Error e : validation.errors()) {
				System.out.println(e.message());
			}
			params.flash();
			validation.keep();
			return false;
		}
		return true;
	}
	
	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = null;
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
			render("MessageCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}
}
