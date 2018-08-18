package controllers.howtodo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import models.howtodo.Gallery;
import models.howtodo.Include;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http;
import play.vfs.VirtualFile;
import util.Utils;

public class GalleryController extends Controller {

	@Before
	@Finally
	static void CORS() {
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9001/");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
		Http.Header hd = new Http.Header();
		hd.name = "Access-Control-Allow-Origin";
		hd.values = new ArrayList<String>();
		hd.values.add("http://localhost:9001");
		Http.Response.current().headers.put("Access-Control-Allow-Origin", hd);
	}

	public static File getVirtualFile() {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/binarybg.jpg");
		File f = vf.getRealFile();
		return f;
	}

	public static void main(String[] args) {
		System.out.println(Utils.isNullOrZero(1l));
	}
	
	public static void getImage(long id, String index) {
		final Gallery gallery = Gallery.findById(id);
		notFoundIfNull(gallery);
		if ("1".equals(index)) {
			if (gallery.getImage1() != null) {
				renderBinary(gallery.getImage1().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("2".equals(index)) {
			if (gallery.getImage2() != null) {
				renderBinary(gallery.getImage2().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("3".equals(index)) {
			if (gallery.getImage3() != null) {
				renderBinary(gallery.getImage3().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("4".equals(index)) {
			if (gallery.getImage4() != null) {
				renderBinary(gallery.getImage4().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("5".equals(index)) {
			if (gallery.getImage5() != null) {
				renderBinary(gallery.getImage5().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("6".equals(index)) {
			if (gallery.getImage6() != null) {
				renderBinary(gallery.getImage6().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("7".equals(index)) {
			if (gallery.getImage7() != null) {
				renderBinary(gallery.getImage7().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("8".equals(index)) {
			if (gallery.getImage8() != null) {
				renderBinary(gallery.getImage8().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("9".equals(index)) {
			if (gallery.getImage9() != null) {
				renderBinary(gallery.getImage9().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("10".equals(index)) {
			if (gallery.getImage10() != null) {
				renderBinary(gallery.getImage10().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("11".equals(index)) {
			if (gallery.getImage11() != null) {
				renderBinary(gallery.getImage11().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("12".equals(index)) {
			if (gallery.getImage12() != null) {
				renderBinary(gallery.getImage12().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("13".equals(index)) {
			if (gallery.getImage13() != null) {
				renderBinary(gallery.getImage13().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("14".equals(index)) {
			if (gallery.getImage14() != null) {
				renderBinary(gallery.getImage14().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else if ("15".equals(index)) {
			if (gallery.getImage15() != null) {
				renderBinary(gallery.getImage15().get(), index.concat("-").concat(Utils.stringToUrl(Utils.removeHTML(gallery.title.trim()))));
				return;
			}
		} else {
			getVirtualFile();
		}
	}

}
