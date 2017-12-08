package controllers;

import java.util.List;

import controllers.CRUD.ObjectType;
import models.HighlightProduct;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;

@CRUD.For(models.HighlightProduct.class)
public class HighlightProductCRUD extends CRUD {
	
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
			render("HighlightProductCRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}
	
	public static void remove(String id) throws Exception {
		HighlightProduct highlightProduct = HighlightProduct.find("id = " + Long.valueOf(id)).first();
		highlightProduct.delete();
		HighlightProductCRUD.list(0, null, null, null, null);
	}
	
}
