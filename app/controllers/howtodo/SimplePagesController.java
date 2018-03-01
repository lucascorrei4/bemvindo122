package controllers.howtodo;

import java.io.File;
import java.util.List;

import models.howtodo.Parameter;
import models.howtodo.SimplePage;
import play.mvc.Before;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.Utils;

public class SimplePagesController extends Controller {
	
	public static void details(String friendlyUrl) {
		
		SimplePage simplePage = SimplePage.findByFriendlyUrl(friendlyUrl);
		Parameter parameter = Parameter.all().first();
		String title = Utils.removeHTML(simplePage.getTitle());
		String headline = Utils.removeHTML(simplePage.getHeadline());
		render(simplePage, parameter, title, headline);
	}

	public static void getImage(long id, String index) {
		final SimplePage simplePage = SimplePage.findById(id);
		notFoundIfNull(simplePage);
		if ("1".equals(index)) {
			if (simplePage.getImage1() != null) {
				renderBinary(simplePage.getImage1().get());
				return;
			} else {
				renderBinary(new File(""));
			}
		}
	}

	public static File getVirtualFile() {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/logo-271x149.png");
		File f = vf.getRealFile();
		return f;
	}
}
