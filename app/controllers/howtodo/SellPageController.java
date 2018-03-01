package controllers.howtodo;

import java.io.File;

import models.howtodo.Parameter;
import models.howtodo.SellPage;
import models.howtodo.SequenceMailQueue;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.Utils;

public class SellPageController extends Controller {

	public static void details(String friendlyUrl, long mid) {
		/* Verifying click on mail link */
		if (!Utils.isNullOrZero(mid)) {
			SequenceMailQueue seqMail = SequenceMailQueue.findById(mid);
			seqMail.setMailRead(true);
			seqMail.setMailClicked(true);
			seqMail.save();
		}
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
