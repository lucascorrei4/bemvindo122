package controllers.howtodo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import controllers.Admin;
import controllers.Application;
import controllers.CRUD;
import controllers.Security;
import controllers.CRUD.For;
import controllers.CRUD.ObjectType;
import models.howtodo.Parameter;
import models.howtodo.SequenceMail;
import models.howtodo.SequenceMailQueue;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.Utils;
import util.howtodo.SequenceMailTO;
import util.howtodo.TO;

@CRUD.For(models.howtodo.SequenceMailQueue.class)
public class SequenceMailQueueCRUD extends CRUD {

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

	public static void listAll(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		if (orderBy == null) {
			orderBy = "id";
		}
		if (order == null) {
			order = "DESC";
		}
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
		Long count = type.count(search, searchFields, (String) request.args.get("where"));
		Long totalCount = type.count(null, null, (String) request.args.get("where"));
		try {
			render("howtodo/SequenceMailQueueCRUD/listAll.html", type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("howtodo/SequenceMailQueueCRUD/listAll.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void listAllGrouped(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		if (orderBy == null) {
			orderBy = "id";
		}
		if (order == null) {
			order = "DESC";
		}
		List<SequenceMailQueue> objects = SequenceMailQueue.find("order by postedAt desc").fetch();
		Long count = type.count(search, searchFields, (String) request.args.get("where"));
		Long totalCount = type.count(null, null, (String) request.args.get("where"));
		List<SequenceMailTO> listGroupedUrls = getGroupedUrls(objects);
		try {
			render("howtodo/SequenceMailQueueCRUD/listAllGrouped.html", type, objects, count, totalCount, page, orderBy, order, listGroupedUrls);
		} catch (TemplateNotFoundException e) {
			render("howtodo/SequenceMailQueueCRUD/listAllGrouped.html", type, objects, count, totalCount, page, orderBy, order, listGroupedUrls);
		}
	}

	private static List<SequenceMailTO> getGroupedUrls(List<SequenceMailQueue> objects) {
		List<String> urls = getUrls(objects);
		List<SequenceMailTO> listSequenceMailTO = new ArrayList<SequenceMailTO>();
		List<SequenceMail> listSequenceMail = new ArrayList<SequenceMail>();
		for (String url : urls) {
			SequenceMailTO sequenceMailTO = new SequenceMailTO();
			sequenceMailTO.setUrl(url);
			listSequenceMail = new ArrayList<SequenceMail>();
			for (SequenceMailQueue sequenceMailQueue : objects) {
				SequenceMail sequenceMail = sequenceMailQueue.getSequenceMail();
				if (url.equals(sequenceMailQueue.getSequenceMail().getUrl())) {
					if (!listSequenceMail.contains(sequenceMail)) {
						listSequenceMail.add(sequenceMail);
					}
				}
			}
			if (!Utils.isNullOrEmpty(listSequenceMail.isEmpty())) {
				sequenceMailTO.setTotalSent(Long.valueOf(listSequenceMail.size()));
				for (SequenceMailQueue sequenceMailQueue : objects) {
					long total = getTotalMails(listSequenceMail);
					long clicked = 0;
					long sent = 0;
					long opened = 0;
					for (SequenceMail sequenceMail : listSequenceMail) {
						if (sequenceMail.id == sequenceMailQueue.getSequenceMail().id) {
							if (!sequenceMail.getListSequenceMailQueue().contains(sequenceMailQueue)) {
								sequenceMail.getListSequenceMailQueue().add(sequenceMailQueue);
								for (SequenceMailQueue queue : sequenceMail.getListSequenceMailQueue()) {
									total += 1;
									if (queue.isSent()) {
										sent += 1;
									}
									if (queue.isMailRead()) {
										opened += 1;
									}
									if (queue.isMailClicked()) {
										clicked += 1;
									}
								}
								sequenceMail.setTotalSent(sent);
								sequenceMail.setTotalClicked(clicked);
								sequenceMail.setTotalOpened(opened);
							}
						}
					}
					clicked = 0;
					sent = 0;
					opened = 0;
					for (SequenceMail sequenceMail : listSequenceMail) {
						if (!Utils.isNullOrEmpty(sequenceMail.getListSequenceMailQueue())) {
							for (SequenceMailQueue queue : sequenceMail.getListSequenceMailQueue()) {
								total += 1;
								if (queue.isSent()) {
									sent += 1;
								}
								if (queue.isMailRead()) {
									opened += 1;
								}
								if (queue.isMailClicked()) {
									clicked += 1;
								}
							}
						}
					}
					sequenceMailTO.setTotal(total);
					sequenceMailTO.setTotalSent(sent);
					sequenceMailTO.setTotalClicked(clicked);
					sequenceMailTO.setTotalOpened(opened);
				}
			}
			Collections.sort(listSequenceMail, new Comparator<SequenceMail>() {
				public int compare(SequenceMail o1, SequenceMail o2) {
					return o1.getSequence() - o2.getSequence();
				}
			});
			sequenceMailTO.setListSequenceMail(listSequenceMail);
			listSequenceMailTO.add(sequenceMailTO);
		}
		return listSequenceMailTO;
	}

	private static long getTotalMails(List<SequenceMail> sequenceMailList) {
		long total = 0l;
		long clicked = 0;
		long sent = 0;
		long opened = 0;

		return total;
	}

	private static List<String> getUrls(List<SequenceMailQueue> objects) {
		List<String> listUrls = new ArrayList<String>();
		for (SequenceMailQueue sequenceMailQueue : objects) {
			if (!listUrls.contains(sequenceMailQueue.getSequenceMail().getUrl())) {
				listUrls.add(sequenceMailQueue.getSequenceMail().getUrl());
			}
		}
		return listUrls;
	}

	public static void remove(String id) throws Exception {
		SequenceMailQueue sellPage = SequenceMailQueue.find("id = " + Long.valueOf(id)).first();
		sellPage.delete();
		SequenceMailQueueCRUD.listAll(0, null, null, null, null);
	}
}
