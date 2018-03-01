package controllers;

import java.io.File;
import java.util.List;

import models.Institution;
import models.howtodo.Article;
import models.howtodo.Parameter;
import models.howtodo.TheSystem;
import play.mvc.Before;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.Utils;

public class InstitutionController extends Controller {
	
	public static void getImage(long id) {
		final Institution institution = Institution.findById(id);
		notFoundIfNull(institution);
		if (institution.getLogo() != null && institution.getLogo().get() != null) {
			renderBinary(institution.getLogo().get());
			return;
		} else {
			renderBinary(Utils.getVirtualFile());
		}
	}

}
