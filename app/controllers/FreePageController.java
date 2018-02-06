package controllers;

import java.io.File;

import models.FreePage;
import models.Parameter;
import models.SequenceMailQueue;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.Utils;

public class FreePageController extends Controller {

	public static void details(String friendlyUrl, long mid) {
		/* Verifying click on mail link */
		if (!Utils.isNullOrZero(mid)) {
			SequenceMailQueue seqMail = SequenceMailQueue.findById(mid);
			seqMail.setMailRead(true);
			seqMail.setMailClicked(true);
			seqMail.save();
		}
		FreePage freePage = FreePage.findByFriendlyUrl(friendlyUrl);
		Parameter parameter = Parameter.all().first();
		String title = Utils.removeHTML(freePage.getMainTitle());
		String headline = Utils.removeHTML(freePage.getSubtitle1());
		render(freePage, parameter, title, headline);
	}

	public static void getBackgroundImage(long id) {
		final FreePage freePage = FreePage.findById(id);
		notFoundIfNull(freePage);
		if (freePage.getBackgroundImage() != null) {
			renderBinary(freePage.getBackgroundImage().get());
			return;
		}
	}

	public static File getVirtualFile() {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/logo-271x149.png");
		File f = vf.getRealFile();
		return f;
	}
	
	public static void main(String[] args) {
		System.out.println(Utils.isNullOrZero(1l));
	}
}
