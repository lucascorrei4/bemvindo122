package controllers;

import java.util.List;

import models.ContractModel;
import models.howtodo.Parameter;
import play.mvc.Controller;
import util.Utils;

public class ContractModelController extends Controller {
	public static void list() {
		List<ContractModel> highlightContractModels = ContractModel.find("highlight = true and isActive = true").fetch(2);
		List<ContractModel> listContractModels = ContractModel.find("highlight = false and isActive = true order by postedAt desc")
				.fetch(6);
		List<ContractModel> sidebarRightNews = listContractModels.subList(0, 2);
		List<ContractModel> bottomNews = listContractModels.subList(2, listContractModels.size());
		Parameter parameter = Parameter.all().first();
		render(bottomNews, sidebarRightNews, highlightContractModels, parameter);
	}

	public static void details(String friendlyUrl) {
		List<ContractModel> bottomNews = null;
		ContractModel theSystem = ContractModel.findByFriendlyUrl(friendlyUrl);
		List<ContractModel> listContractModels = ContractModel.find(
				"highlight = false and isActive = true and id <>  " + theSystem.id + " order by postedAt desc")
				.fetch();
		if (!listContractModels.isEmpty()) {
			bottomNews = listContractModels.subList(0, listContractModels.size());
		}
		Parameter parameter = Parameter.all().first();
		String title = Utils.removeHTML(theSystem.getMainTitle());
		String headline = Utils.removeHTML(theSystem.getDescriptionSEO());
		render(theSystem, bottomNews, parameter, listContractModels, title, headline);
	}
}
