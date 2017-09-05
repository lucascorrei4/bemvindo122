package controllers;

import play.mvc.Before;

@CRUD.For(models.MailBase.class)
public class MailBaseCRUD extends CRUD {
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
}
