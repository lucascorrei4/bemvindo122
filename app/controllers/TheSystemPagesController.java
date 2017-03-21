package controllers;

import java.io.File;
import java.util.List;

import models.Parameter;
import models.TheSystem;
import play.mvc.Controller;
import play.vfs.VirtualFile;

public class TheSystemPagesController extends Controller {
	public static void list() {
		List<TheSystem> highlightTheSystems = TheSystem.find("highlight = true and isActive = true").fetch(2);
		List<TheSystem> listTheSystems = TheSystem.find("highlight = false and isActive = true order by postedAt desc")
				.fetch(6);
		List<TheSystem> sidebarRightNews = listTheSystems.subList(0, 2);
		List<TheSystem> bottomNews = listTheSystems.subList(2, listTheSystems.size());
		Parameter parameter = Parameter.all().first();
		render(bottomNews, sidebarRightNews, highlightTheSystems, parameter);
	}

	public static void details(String friendlyUrl) {
		List<TheSystem> sidebarRightNews = null;
		List<TheSystem> bottomNews = null;
		TheSystem theSystem = TheSystem.findByFriendlyUrl(friendlyUrl);
		List<TheSystem> listTheSystems = TheSystem.find(
				"highlight = false and isActive = true and id <>  " + theSystem.id + " order by postedAt desc")
				.fetch(6);
		if (!listTheSystems.isEmpty()) {
			sidebarRightNews = listTheSystems.subList(0, 2);
			bottomNews = listTheSystems.subList(2, listTheSystems.size());
		}
		Parameter parameter = Parameter.all().first();
		render(theSystem, bottomNews, sidebarRightNews, parameter);
	}

	public static void getImage(long id, String index) {
		final TheSystem theSystem = TheSystem.findById(id);
		notFoundIfNull(theSystem);
		if ("1".equals(index)) {
			if (theSystem.getImage1() != null) {
				renderBinary(theSystem.getImage1().get());
				return;
			} else {
				renderBinary(getVirtualFile());
			}
		} else if ("2".equals(index)) {
			if (theSystem.getImage2() != null) {
				renderBinary(theSystem.getImage2().get());
				return;
			} else {
				renderBinary(getVirtualFile());
			}
		} else if ("3".equals(index)) {
			if (theSystem.getImage3() != null) {
				renderBinary(theSystem.getImage3().get());
				return;
			} else {
				renderBinary(getVirtualFile());
			}
		}
	}

	public static File getVirtualFile() {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/logo-271x149.png");
		File f = vf.getRealFile();
		return f;
	}
}
