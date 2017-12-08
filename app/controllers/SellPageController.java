package controllers;

import java.io.File;

import models.Parameter;
import models.SellPage;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.Utils;

public class SellPageController extends Controller {

	public static void details(String friendlyUrl) {
		SellPage sellPage = SellPage.findByFriendlyUrl(friendlyUrl);
		Parameter parameter = Parameter.all().first();
		String title = Utils.removeHTML(sellPage.getMainTitle());
		String headline = Utils.removeHTML(sellPage.getSubtitle1());
		render(sellPage, parameter, title, headline);
	}

	public static void getImageProduct(long id) {
		final SellPage sellPage = SellPage.findById(id);
		notFoundIfNull(sellPage);
		if (sellPage.getImageProduct() != null) {
			renderBinary(sellPage.getImageProduct().get(), "imgprod", "image/png", true);
			return;
		}
	}

	public static void getBackgroundImage(long id) {
		final SellPage sellPage = SellPage.findById(id);
		notFoundIfNull(sellPage);
		if (sellPage.getBackgroundImage() != null) {
			renderBinary(sellPage.getBackgroundImage().get());
			return;
		}
	}

	public static File getVirtualFile() {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/logo-271x149.png");
		File f = vf.getRealFile();
		return f;
	}
}
