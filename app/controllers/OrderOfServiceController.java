package controllers;

import java.lang.reflect.Constructor;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.Institution;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.Service;
import models.Step;
import play.data.binding.Binder;
import play.data.binding.ParamNode;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import util.StatusEnum;
import util.Utils;

@CRUD.For(models.OrderOfService.class)
public class OrderOfServiceController extends CRUD {

	public static String orderId = null;

	public static void orderOfService(final String id) {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService order = OrderOfService
				.find("id = " + Long.valueOf(id) + " and institutionId = " + institution.getId()).first();
		List<Service> services = order.getServices();
		render(order, institution, services);
	}

	public static void orderByOrderOfServiceId(final String id) {
		List<OrderOfService> prayOrders = getOrderByOrderOfServiceId(id);
		render(prayOrders);
	}

	public static List<OrderOfService> getOrderByOrderOfServiceId(String id) {
		return OrderOfService.find("id = " + id + " and institutionId = "
				+ Admin.getLoggedUserInstitution().getInstitution().getId() + " order by description asc").fetch();
	}

	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html", type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}
		object._save();
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		OrderOfService orderOfService = (OrderOfService) object;
		generateStepsByService(orderOfService);
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	private static void generateStepsByService(OrderOfService orderOfService) {
		List<Service> services = orderOfService.getServices();
		for (Service service : services) {
			List<Step> steps = Step.find("service_id = " + service.getId() + " and institutionId = "
					+ Admin.getLoggedUserInstitution().getInstitution().getId()
					+ " and isActive = true order by position, id asc").fetch();
			int i = 0;
			for (Step step : steps) {
				OrderOfServiceStep orderServiceStep = new OrderOfServiceStep();
				orderServiceStep.setOrderOfService(orderOfService);
				orderServiceStep.setStep(step);
				orderServiceStep.setService(step.getService());
				if (i == 0) {
					orderServiceStep.setStatus(StatusEnum.InProgress);
				} else {
					orderServiceStep.setStatus(StatusEnum.NotStarted);
				}
				orderServiceStep.setObs("Nenhuma");
				orderServiceStep.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
				orderServiceStep.setInstitutionId(orderOfService.getInstitutionId());
				orderServiceStep.willBeSaved = true;
				orderServiceStep.save();
				i++;
			}
		}
	}

}
