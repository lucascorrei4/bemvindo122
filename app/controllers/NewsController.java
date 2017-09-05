package controllers;

import java.io.File;
import java.util.List;

import models.Article;
import models.Parameter;
import models.TheSystem;
import play.mvc.Before;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.Utils;

public class NewsController extends Controller {
	
	public static void list() {
		List<Article> highlightArticles = Article.find("highlight = true and isActive = true").fetch(2);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> sidebarRightNews = listArticles.subList(0, 2);
		List<Article> bottomNews = listArticles.subList(2, listArticles.size());
		Parameter parameter = Parameter.all().first();
		List<TheSystem> listTheSystems = TheSystem.find("highlight = false and isActive = true order by postedAt desc")
				.fetch(6);
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		render(bottomNews, sidebarRightNews, highlightArticles, parameter, listTheSystems, theSystem);
	}

	public static void details(String id) {
		Article article = Article.findById(Long.valueOf(id));
		List<Article> listArticles = Article.find("highlight = false and isActive = true and id <>  " + Long.valueOf(id) + " order by postedAt desc").fetch(6);
		List<Article> sidebarRightNews = listArticles.subList(0, 2);
		List<Article> bottomNews = listArticles.subList(2, listArticles.size());
		Parameter parameter = Parameter.all().first();
		List<TheSystem> listTheSystems = TheSystem.find("highlight = false and isActive = true order by postedAt desc")
				.fetch(6);
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		String title = Utils.removeHTML(article.getTitle());
		render(article, bottomNews, sidebarRightNews, parameter, listTheSystems, theSystem, title);
	}
	
	public static void getImage(long id, String index) {
		final Article article = Article.findById(id);
		notFoundIfNull(article);
		if ("1".equals(index)) {
			if (article.getImage1() != null) {
				renderBinary(article.getImage1().get());
				return;
			} else {
				renderBinary(getVirtualFile());
			}
		} else if ("2".equals(index)) {
			if (article.getImage2() != null) {
				renderBinary(article.getImage2().get());
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
