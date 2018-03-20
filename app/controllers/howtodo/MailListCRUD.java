package controllers.howtodo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import controllers.Admin;
import controllers.Application;
import controllers.CRUD;
import controllers.Security;
import models.OrderOfService;
import models.howtodo.MailList;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.Utils;
import util.howtodo.TONumeric;

@CRUD.For(models.howtodo.MailList.class)
public class MailListCRUD extends CRUD {

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
			render("howtodo/MailListCRUD/listAll.html", type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("howtodo/MailListCRUD/listAll.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void performance(int page, String search, String searchFields, String orderBy, String order) {
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
		List<Object> leadsByPage = AdminPub.getleadsByPage();
		String rankingLeads = null;
		if (!Utils.isNullOrEmpty(getListLeadsByPeriod())) {
			rankingLeads = Utils.toJsonChart(getListLeadsByPeriod());
		}
		try {
			render("howtodo/MailListCRUD/performance.html", type, objects, count, totalCount, page, orderBy, order, leadsByPage, rankingLeads);
		} catch (TemplateNotFoundException e) {
			render("howtodo/MailListCRUD/performance.html", type, objects, count, totalCount, page, orderBy, order, leadsByPage, rankingLeads);
		}
	}

	public static List<TONumeric> getListLeadsByPeriod() {
		List<TONumeric> allDaysLeads = new ArrayList<TONumeric>();
		List<MailList> listLeads = null;
		List<MailList> listAllLeads = MailList.find("isActive = true order by postedAt desc").fetch();
		TONumeric to = new TONumeric();
		to.setLabel("Total");
		to.setValue(Float.valueOf(listAllLeads.size()));
		allDaysLeads.add(to);
		Calendar cal = Calendar.getInstance();
		cal = Utils.getBrazilCalendar();
		/* Today Begin */
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		String dateTimeTodayBegin = Utils.getStringDateTime(cal.getTime());
		/* Today End */
		cal = Utils.getBrazilCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		String dateTimeTodayEnd = Utils.getStringDateTime(cal.getTime());
		listLeads = MailList.find("postedAt > '" + dateTimeTodayBegin + "' and postedAt < '" + dateTimeTodayEnd + "' and isActive = true order by postedAt desc").fetch();
		to = new TONumeric();
		to.setLabel("Hoje");
		to.setValue(Float.valueOf(listLeads.size()));
		allDaysLeads.add(to);
		/* Yesterday Begin */
		cal = Utils.getBrazilCalendar();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		String dateTimeYesterdayBegin = Utils.getStringDateTime(cal.getTime());
		/* Yesterday End */
		cal = Utils.getBrazilCalendar();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		String dateTimeYesterdayEnd = Utils.getStringDateTime(cal.getTime());
		listLeads = MailList.find("postedAt > '" + dateTimeYesterdayBegin + "' and postedAt < '" + dateTimeYesterdayEnd + "' and isActive = true order by postedAt desc").fetch();
		to = new TONumeric();
		to.setLabel("Ontem");
		to.setValue(Float.valueOf(listLeads.size()));
		allDaysLeads.add(to);
		/* Before Yesterday Begin */
		cal = Utils.getBrazilCalendar();
		cal.add(Calendar.DATE, -2);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		String dateTimeBeforeYesterdayBegin = Utils.getStringDateTime(cal.getTime());
		/* Before Yesterday End */
		cal = Utils.getBrazilCalendar();
		cal.add(Calendar.DATE, -2);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		String dateTimeBeforeYesterdayEnd = Utils.getStringDateTime(cal.getTime());
		listLeads = MailList.find("postedAt > '" + dateTimeBeforeYesterdayBegin + "' and postedAt < '" + dateTimeBeforeYesterdayEnd + "' and isActive = true order by postedAt desc").fetch();
		to = new TONumeric();
		to.setLabel("Antes de Ontem");
		to.setValue(Float.valueOf(listLeads.size()));
		allDaysLeads.add(to);
		/* Last Week Begin */
		cal = Utils.getBrazilCalendar();
		cal.set(Calendar.DATE, -6);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		String dateTimeLastWeekBegin = Utils.getStringDateTime(cal.getTime());
		listLeads = MailList.find("postedAt > '" + dateTimeLastWeekBegin + "' and postedAt < '" + dateTimeTodayEnd + "' and isActive = true order by postedAt desc").fetch();
		to = new TONumeric();
		to.setLabel("Últimos 7 dias");
		to.setValue(Float.valueOf(listLeads.size()));
		allDaysLeads.add(to);
		/* Last Month Begin */
		cal = Utils.getBrazilCalendar();
		cal.set(Calendar.DATE, -30);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		String dateTimeLastMonthBegin = Utils.getStringDateTime(cal.getTime());
		listLeads = MailList.find("postedAt > '" + dateTimeLastMonthBegin + "' and postedAt < '" + dateTimeTodayEnd + "' and isActive = true order by postedAt desc").fetch();
		to = new TONumeric();
		to.setLabel("Últimos 30 dias");
		to.setValue(Float.valueOf(listLeads.size()));
		allDaysLeads.add(to);
		return allDaysLeads;
	}
	
	public static void main(String[] args) {
		Calendar cal = Utils.getBrazilCalendar();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		String dateTimeYesterdayBegin = Utils.getStringDateTime(cal.getTime());
		System.out.println(dateTimeYesterdayBegin);
		cal = Utils.getBrazilCalendar();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		String dateTimeYesterdayEnd = Utils.getStringDateTime(cal.getTime());
		System.out.println(dateTimeYesterdayEnd);
	}

	public static void remove(String id) throws Exception {
		MailList mailList = MailList.find("id = " + Long.valueOf(id)).first();
		mailList.delete();
		MailListCRUD.listAll(0, null, null, null, null);
	}
}
