package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.CRUD.ObjectType;
import models.City;
import models.Country;
import models.Institution;
import models.OrderOfService;
import models.State;
import models.StatusSMS;
import models.Step;
import models.User;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import util.Utils;

@CRUD.For(models.User.class)
@With(Secure.class)
public class UserController extends CRUD {

	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = "institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId();
		orderBy = "id";
		order = "DESC";
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("UserController/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
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
				render("UserController/show.html", type, object, country, state, city);
			}
		} else {
			redirect("Admin.index");
		}
	}
	
}
