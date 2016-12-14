package controllers;

import java.util.List;

import play.db.Model;
import play.exceptions.TemplateNotFoundException;

@CRUD.For(models.StatusMail.class)
public class StatusMailController extends CRUD {
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
			render("StatusMailController/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}
	
}
