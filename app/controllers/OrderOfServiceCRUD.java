package controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.howtodo.MailController;
import controllers.howtodo.SMSController;
import models.Activity;
import models.Client;
import models.Institution;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.SendTo;
import models.Sender;
import models.Service;
import models.StatusMail;
import models.StatusPUSH;
import models.StatusSMS;
import models.Step;
import models.User;
import models.howtodo.BodyMail;
import models.howtodo.Parameter;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import util.ActivitiesEnum;
import util.ApplicationConfiguration;
import util.PlansEnum;
import util.PushNotification;
import util.ServiceOrderOfServiceSteps;
import util.StatusEnum;
import util.Utils;
import util.VideoHelpEnum;
import util.howtodo.MailTemplates;

@CRUD.For(models.OrderOfService.class)
public class OrderOfServiceCRUD extends CRUD {

	public static String STR_PUSH_APP_ID = ApplicationConfiguration.getInstance().getOneSignalAppId();
	public static String STR_PUSH_AUTH_ID = ApplicationConfiguration.getInstance().getOneSignalAuthId();

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
		renderArgs.put("videohelp", VideoHelpEnum.OrderOfService);
		renderArgs.put("videohelpUpdateOrders", VideoHelpEnum.UpdateOrders);
	}

	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		OrderOfService object = (OrderOfService) constructor.newInstance();
		List<Client> clients = Client.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by name, lastName asc").fetch();
		List<Service> services = Service.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by title asc").fetch();
		try {
			render(type, object, services, clients);
		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type, object, services, clients);
		}
	}

	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = "institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId();
		if (orderBy == null) {
			orderBy = "id";
		}
		if (order == null) {
			order = "DESC";
		}
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		Parameter parameter = Parameter.all().first();
		try {
			render("OrderOfServiceCRUD/list.html", type, objects, count, totalCount, page, orderBy, order, smsExceedLimit, parameter);
		} catch (TemplateNotFoundException e) {
			render("OrderOfServiceCRUD/list.html", type, objects, count, totalCount, page, orderBy, order, smsExceedLimit, parameter);
		}
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		// Filtro pelo usuário conectado para proteger os dados dos demais
		// usuários
		OrderOfService object = OrderOfService.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()).first();
		List<Service> services = Service.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by title asc").fetch();
		notFoundIfNull(object);
		List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(id)).fetch();
		try {
			render(type, object, services, orderOfServiceValues);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object, services, orderOfServiceValues);
		}
	}

	public static void save(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html", type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		}
		object._save();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	public static String orderId = null;

	public static void orderOfService(final String id) {
		Parameter parameter = Parameter.all().first();
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService order = OrderOfService.find("id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		List<Service> services = order.getServices();
		List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(id)).fetch();
		/* Get somatories values */
		Float subTotalGeral = 0f;
		Float discountGeral = 0f;
		Float totalGeral = 0f;
		for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
			subTotalGeral += orderOfServiceValue.getSubTotal();
			discountGeral += orderOfServiceValue.getDiscount();
			totalGeral += orderOfServiceValue.getTotalPrice();
		}
		String subTotalGeralCurrency = Utils.getCurrencyValue(subTotalGeral);
		String totalGeralCurrency = Utils.getCurrencyValue(totalGeral);
		String discountGeralCurrency = Utils.getCurrencyValue(discountGeral);
		render(order, institution, services, orderOfServiceValues, subTotalGeralCurrency, totalGeralCurrency, discountGeralCurrency, parameter);
	}

	public static void customerNotificationModal(final String id) {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		Parameter parameter = (Parameter) Parameter.getCurrentParameter();
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render(orderOfService, institution, smsExceedLimit, planSPO02, parameter);
	}

	public static void thankfulNotificationModal(final String id) {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render(orderOfService, institution, smsExceedLimit, planSPO02);
	}

	public static void evaluationNotificationModal(final String id) {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render(orderOfService, institution, smsExceedLimit, planSPO02);
	}

	public static void orderByOrderOfServiceId(final String id) {
		List<OrderOfService> prayOrders = getOrderByOrderOfServiceId(id);
		render(prayOrders);
	}

	public static List<OrderOfService> getOrderByOrderOfServiceId(String id) {
		return OrderOfService.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by description asc").fetch();
	}

	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		OrderOfService object = (OrderOfService) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		object.setOrderCode(getOrderOfServiceRandomCode());
		validation.valid(object);
		if (validation.hasErrors()) {
			List<Service> services = Service.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by title asc").fetch();
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html", type, object, services);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object, services);
			}
		}
		object._save();
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		OrderOfService orderOfService = (OrderOfService) object;
		String[] jsonContent = params.get("orderOfServiceValue", String[].class);
		generateOrderOfServiceValues(orderOfService, jsonContent[0]);
		generateStepsByService(orderOfService);
		/* Generate Activity */
		String activityTitle = "Nova OS (" + orderOfService.getOrderCode() + ")";
		String activityDescription = "Observação da OS: \"" + orderOfService.getObs() + "\"";
		User loggedUser = Admin.getLoggedUserInstitution().getUser();
		ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.OS);
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	private static String getOrderOfServiceRandomCode() {
		String initials = Admin.getLoggedUserInstitution().getInstitution().getInstitution().replaceAll(" ", "").toUpperCase().substring(0, 2).concat(Admin.getLoggedUserInstitution().getInstitution().getId().toString());
		String orderCode = null;
		OrderOfService orderOfService = null;
		do {
			orderCode = initials.concat(String.valueOf(Utils.generateRandomId()));
			orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true").first();
		} while (orderOfService != null);
		return orderCode;
	}

	private static void generateOrderOfServiceValues(OrderOfService orderOfService, String jsonContent) {
		if (jsonContent != null) {
			JsonParser parser = new JsonParser();
			JsonArray jsonArray = parser.parse(jsonContent).getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jObject = (JsonObject) jsonArray.get(i);
				OrderOfServiceValue orderOfServiceValue = new OrderOfServiceValue();
				orderOfServiceValue.setOrderOfServiceId(orderOfService.getId());
				Service service = Service.findById(Long.valueOf(jObject.get("serviceId").getAsString()));
				orderOfServiceValue.setService(service);
				orderOfServiceValue.setReference(jObject.get("reference").getAsString().replace(",", "."));
				orderOfServiceValue.setQtd(Float.valueOf(jObject.get("qtd").getAsString().replace(",", ".")));
				orderOfServiceValue.setUnitPrice(Float.valueOf(jObject.get("basePrice").getAsString().replace(",", ".")));
				orderOfServiceValue.setDiscount(Float.valueOf(jObject.get("discount").getAsString().replace(",", ".")));
				/*
				 * Saving subtotal without discount even it was calculated in
				 * files orderofservicevalue.html and scriptOrderOfService.html
				 * subtotal = unitprice * qtd
				 */
				Float subTotalWithoutDiscount = (orderOfServiceValue.getUnitPrice() * orderOfServiceValue.getQtd());
				orderOfServiceValue.setSubTotal(subTotalWithoutDiscount);
				orderOfServiceValue.setInstitutionId(orderOfService.getInstitutionId());
				orderOfServiceValue.setOrderCode(getOrderOfServiceRandomCode());
				orderOfServiceValue.willBeSaved = true;
				orderOfServiceValue.save();
			}
		}
	}

	private static void generateStepsByService(OrderOfService orderOfService) {
		List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + orderOfService.getId()).fetch();
		for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
			List<Step> steps = Step.find("service_id = " + orderOfServiceValue.getService().getId() + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by position, id asc").fetch();
			for (Step step : steps) {
				String reference = orderOfServiceValue.getReference();
				String orderCode = orderOfServiceValue.getOrderCode();
				OrderOfServiceStep orderServiceStep = new OrderOfServiceStep();
				orderServiceStep.setOrderOfService(orderOfService);
				orderServiceStep.setStep(step);
				orderServiceStep.setService(step.getService());
				orderServiceStep.setStatus(StatusEnum.NotStarted);
				orderServiceStep.setObs("Nenhuma");
				orderServiceStep.setReference(Utils.isNullOrEmpty(reference) ? "Nenhuma" : reference);
				orderServiceStep.setOrderCode(orderCode);
				orderServiceStep.setPostedAt(Utils.getCurrentDateTime());
				orderServiceStep.setInstitutionId(orderOfService.getInstitutionId());
				orderServiceStep.willBeSaved = true;
				orderServiceStep.save();
			}
		}
	}

	public static void updateOrder() {
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		render(listOrderOfService, institution, smsExceedLimit);
	}

	public static void afterSale() {
		List<OrderOfService> listOrderOfServices = calculateTotalOrderOfService();
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		render(listOrderOfServices, institution);
	}

	public static void evaluation() {
		List<OrderOfService> listOrderOfServices = calculateTotalOrderOfService();
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		render(listOrderOfServices, institution);
	}

	public static void afterSaleScript() {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		render(institution);
	}

	private static List<OrderOfService> calculateTotalOrderOfService() {
		List<OrderOfService> listOrderOfServices = OrderOfService.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by postedAt desc").fetch(20);
		for (OrderOfService order : listOrderOfServices) {
			List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(order.id)).fetch();
			/* Get somatories values */
			Float totalGeral = 0f;
			for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				totalGeral += orderOfServiceValue.getTotalPrice();
			}
			order.setTotalOrderOfService(totalGeral);
			List<OrderOfServiceStep> listOrderOfServiceStep = OrderOfServiceStep.find("orderOfService_id = " + Long.valueOf(order.id) + " and institutionId = " + order.getInstitutionId() + " and isActive = true").fetch();
			boolean isOpened = false;
			for (OrderOfServiceStep orderOfServiceStep : listOrderOfServiceStep) {
				if (orderOfServiceStep.status != StatusEnum.Finished) {
					isOpened = true;
					break;
				}
			}
			order.setCurrentStatus(isOpened ? StatusEnum.InProgress.getLabel() : StatusEnum.Finished.getLabel());
		}
		return listOrderOfServices;
	}

	public static Float calculateTotalOrderOfServiceByClient(Client client) {
		Float totalGeral = 0f;
		List<OrderOfService> listOrderOfServices = OrderOfService.find("client_id = " + client.id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by postedAt desc").fetch(20);
		for (OrderOfService order : listOrderOfServices) {
			List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + Long.valueOf(order.id)).fetch();
			/* Get somatories values */
			for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				totalGeral += orderOfServiceValue.getTotalPrice();
			}
		}
		return totalGeral;
	}

	private static void verifyIfOrderAreOpenAndUpdateServicesReferences(List<OrderOfService> listOrderOfService) {
		verifyIfOrdersAreOpen(listOrderOfService);
		updateServicesReferences(listOrderOfService);
	}

	private static void updateServicesReferences(List<OrderOfService> listOrderOfService) {
		for (OrderOfService orderOfService : listOrderOfService) {
			for (ServiceOrderOfServiceSteps serviceOrderOfServiceSteps : orderOfService.getServiceOrderOfServiceSteps()) {
				if (!Utils.isNullOrEmpty(serviceOrderOfServiceSteps.getOrderOfServiceSteps()) && serviceOrderOfServiceSteps.getOrderOfServiceSteps().iterator().next() != null) {
					String reference = serviceOrderOfServiceSteps.getOrderOfServiceSteps().iterator().next().getReference();
					serviceOrderOfServiceSteps.setReference(Utils.isNullOrEmpty(reference) ? "Não referenciado." : reference);
				} else {
					serviceOrderOfServiceSteps.setReference("Não referenciado.");
				}
			}
		}
	}

	private static void verifyIfOrdersAreOpen(List<OrderOfService> listOrderOfService) {
		boolean isOpened = false;
		for (OrderOfService orderOfService : listOrderOfService) {
			List<OrderOfServiceValue> orderOfServiceValues = (List<OrderOfServiceValue>) (Utils.isNullOrEmpty(orderOfService.getOrderOfServiceValue()) ? OrderOfServiceValue.find("orderOfServiceId = " + orderOfService.getId()).fetch() : orderOfService.getOrderOfServiceValue());
			outerloop: for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				List<OrderOfServiceStep> orderOfServiceStep = OrderOfServiceStep.find("service_id = " + orderOfServiceValue.getService().getId() + " and orderOfService_id = " + orderOfService.getId() + " and isActive = true").fetch();
				for (OrderOfServiceStep orderStep : orderOfServiceStep) {
					if (orderStep.status != StatusEnum.Finished) {
						isOpened = true;
						break outerloop;
					}
				}
			}
			orderOfService.setCurrentStatus(isOpened ? StatusEnum.InProgress.getLabel() : StatusEnum.Finished.getLabel());
			isOpened = false;
		}
	}

	private static List<OrderOfService> loadListOrderOfService() {
		List<OrderOfService> listOrderOfService = OrderOfService.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by id desc").fetch();
		for (OrderOfService orderOfService : listOrderOfService) {
			List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + orderOfService.getId()).fetch();
			List<ServiceOrderOfServiceSteps> serviceOrderOfServiceSteps = new ArrayList<ServiceOrderOfServiceSteps>();
			ServiceOrderOfServiceSteps serviceOrderOfServiceStep = null;
			orderOfService.setOrderOfServiceValue(orderOfServiceValues);
			for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				List<OrderOfServiceStep> orderOfServiceStep = OrderOfServiceStep.find("service_id = " + orderOfServiceValue.getService().getId() + " and orderOfService_id = " + orderOfService.getId() + " and isActive = true").fetch();
				List<OrderOfServiceStep> orderOfServiceStepNew = new ArrayList<OrderOfServiceStep>();
				List<Step> stepsOfService = Step.find("service_id = " + orderOfServiceValue.getService().getId() + " and isActive = true").fetch();
				int qtdStepOfCurrentService = stepsOfService.size();
				if (orderOfServiceStep.size() > qtdStepOfCurrentService) {
					for (OrderOfServiceStep orderStep : orderOfServiceStep) {
						if (serviceOrderOfServiceSteps.isEmpty() || stepsHadBeenNotInsertedInList(orderStep, serviceOrderOfServiceSteps)) {
							orderOfServiceStepNew.add(orderStep);
						}
						if (orderOfServiceStepNew.size() == qtdStepOfCurrentService) {
							serviceOrderOfServiceStep = new ServiceOrderOfServiceSteps();
							serviceOrderOfServiceStep.setService(orderOfServiceValue.getService());
							serviceOrderOfServiceStep.setOrderOfServiceSteps(orderOfServiceStepNew);
							serviceOrderOfServiceSteps.add(serviceOrderOfServiceStep);
							orderOfServiceStepNew = new ArrayList<OrderOfServiceStep>();
						}
					}
				} else {
					serviceOrderOfServiceStep = new ServiceOrderOfServiceSteps();
					serviceOrderOfServiceStep.setService(orderOfServiceValue.getService());
					serviceOrderOfServiceStep.setOrderOfServiceSteps(orderOfServiceStep);
					serviceOrderOfServiceSteps.add(serviceOrderOfServiceStep);
				}
				orderOfService.setServiceOrderOfServiceSteps(serviceOrderOfServiceSteps);
			}
		}
		return listOrderOfService;
	}

	private static boolean stepsHadBeenNotInsertedInList(OrderOfServiceStep orderSteps, List<ServiceOrderOfServiceSteps> serviceOrderOfServiceSteps) {
		boolean ret = true;
		for (ServiceOrderOfServiceSteps serviceSteps : serviceOrderOfServiceSteps) {
			if (serviceSteps.getService().id == orderSteps.service.id) {
				if (serviceSteps.getOrderOfServiceSteps().contains(orderSteps)) {
					ret = false;
				}
			}
		}
		return ret;
	}

	public static void updateRadioValue() {
		String response = null;
		String status = null;
		String name = params.get("name", String.class);
		String newOrderStatus = params.get("value", String.class);
		String[] nameSpplited = name.split("-");
		String orderCode = nameSpplited[1];
		String orderServiceStepId = nameSpplited[2];
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		/*
		 * Find OrderOfServiceStep object to update object with newOrderStatus
		 */
		OrderOfServiceStep orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		orderOfServiceStep.setStatus(StatusEnum.getNameByValue(newOrderStatus));
		orderOfServiceStep.save();
		/* Generate Activity */
		String activityTitle = "Etapa da OS + (" + orderOfServiceStep.getOrderCode() + ") atualizada para \"" + orderOfServiceStep.getStatus().getLabel() + "\"";
		String activityDescription = "Observação da etapa da OS: \"" + orderOfServiceStep.getObs() + "\"; Ref.: " + (Utils.isNullOrEmpty(orderOfServiceStep.getReference()) ? "\"Nenhuma\"" : "\"" + orderOfServiceStep.getReference() + "\"");
		User loggedUser = Admin.getLoggedUserInstitution().getUser();
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.OSStepUpdated);
		/*
		 * Creating new object to do new search to see if object was saved
		 * correctly
		 */
		orderOfServiceStep = new OrderOfServiceStep();
		orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		boolean isSavedOrderOfServiceStep = String.valueOf(orderOfServiceStep.getStatus().getValue()).equals(String.valueOf(newOrderStatus));
		if (isSavedOrderOfServiceStep) {
			status = "SUCCESS";
			response = "Etapa do pedido ".concat(orderCode).concat(" atualizada com sucesso!");
		} else {
			status = "ERROR";
			response = "Houve um erro ao atualizar a etapa do pedido ".concat(orderCode).concat("!");
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit);
	}

	public static void updateObsOrderStep() {
		String response = null;
		String status = null;
		String name = params.get("name", String.class);
		String obs = params.get("obs", String.class);
		String[] nameSpplited = name.split("-");
		String orderCode = nameSpplited[1];
		String orderServiceStepId = nameSpplited[2];
		String obsParam = obs;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		/*
		 * Find OrderOfServiceStep object to update object with newOrderStatus
		 */
		OrderOfServiceStep orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		orderOfServiceStep.setObs(obsParam);
		orderOfServiceStep.save();
		/*
		 * Creating new object to do new search to see if object was saved
		 * correctly
		 */
		orderOfServiceStep = new OrderOfServiceStep();
		orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		boolean isSavedOrderOfServiceStep = String.valueOf(orderOfServiceStep.getObs()).equals(String.valueOf(obsParam));
		if (isSavedOrderOfServiceStep) {
			/* Generate Activity */
			String activityTitle = "OBS da OS + (" + orderOfServiceStep.getOrderCode() + ") atualizada";
			String activityDescription = "Descrição: \"" + obsParam + "\"";
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.OSStepUpdated);
			status = "SUCCESS";
			response = "Observação do pedido ".concat(orderCode).concat(" inserida com sucesso!");
		} else {
			status = "ERROR";
			response = "Erro ao inserir a observação do pedido ".concat(orderCode).concat(".");
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit);
	}

	public static void sendSMS() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String idUpdate = params.get("idUpdate", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		String sender = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		String destination = Utils.replacePhoneNumberCaracteres(orderOfService.client.phone);
		if (!Utils.isNullOrEmpty(destination)) {
			StatusSMS statusSMS = new StatusSMS();
			SMSController smsController = new SMSController();
			String sendResponse = smsController.sendSMS(destination, sender, message, statusSMS);
			if ("SUCCESS".equals(sendResponse)) {
				/* Save sms object */
				statusSMS.setInstitutionId(institution.id);
				statusSMS.setPostedAt(Utils.getCurrentDateTime());
				statusSMS.setClientName(orderOfService.client.toString());
				statusSMS.willBeSaved = true;
				statusSMS.id = 0l;
				statusSMS.merge();
				status = "SUCCESS";
				response = "SMS enviada com sucesso!";
			} else {
				status = "ERROR";
				response = "Erro ao enviar a SMS! Tente novamente!";
			}
		} else {
			response = "Não há nenhum número de telefone cadastrado para " + orderOfService.client.toString();
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		if ("accordion".equals(idUpdate)) {
			render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit);
		} else if ("notificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/customerNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit);
		}
	}

	public static void sendSMSThankful() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		String sender = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		String destination = Utils.replacePhoneNumberCaracteres(orderOfService.client.phone);
		if (!Utils.isNullOrEmpty(destination)) {
			StatusSMS statusSMS = new StatusSMS();
			SMSController smsController = new SMSController();
			String sendResponse = smsController.sendSMS(destination, sender, message, statusSMS);
			if ("SUCCESS".equals(sendResponse)) {
				/* Save sms object */
				statusSMS.setInstitutionId(institution.id);
				statusSMS.setPostedAt(Utils.getCurrentDateTime());
				statusSMS.setClientName(orderOfService.client.toString());
				statusSMS.willBeSaved = true;
				statusSMS.id = 0l;
				statusSMS.merge();
				status = "SUCCESS";
				response = "SMS enviada com sucesso!";
				/* Set thanked Order of Service */
				orderOfService.setThanked(true);
				orderOfService.willBeSaved = true;
				orderOfService.merge();
			} else {
				status = "ERROR";
				response = "Erro ao enviar a SMS! Tente novamente!";
			}
		} else {
			response = "Não há nenhum número de telefone cadastrado para " + orderOfService.client.toString();
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/thankfulNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02);
	}

	public static void sendSMSEvaluation() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		String sender = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		String destination = Utils.replacePhoneNumberCaracteres(orderOfService.client.phone);
		if (!Utils.isNullOrEmpty(destination)) {
			StatusSMS statusSMS = new StatusSMS();
			SMSController smsController = new SMSController();
			String sendResponse = smsController.sendSMS(destination, sender, message, statusSMS);
			if ("SUCCESS".equals(sendResponse)) {
				/* Save sms object */
				statusSMS.setInstitutionId(institution.id);
				statusSMS.setPostedAt(Utils.getCurrentDateTime());
				statusSMS.setClientName(orderOfService.client.toString());
				statusSMS.willBeSaved = true;
				statusSMS.id = 0l;
				statusSMS.merge();
				status = "SUCCESS";
				response = "SMS enviada com sucesso!";
				/* Set thanked Order of Service */
				orderOfService.setThanked(true);
				orderOfService.willBeSaved = true;
				orderOfService.merge();
			} else {
				status = "ERROR";
				response = "Erro ao enviar a SMS! Tente novamente!";
			}
		} else {
			response = "Não há nenhum número de telefone cadastrado para " + orderOfService.client.toString();
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/evaluationNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02);
	}

	public static void sendWhatsAppThankful() throws IOException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String idUpdate = params.get("idUpdate", String.class);
		String isMobile = params.get("isMobile", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = value;
		String sendResponse = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		String destination = Utils.replacePhoneNumberCaracteres(orderOfService.client.phone);
		Parameter parameter = Parameter.all().first();
		if (!Utils.isNullOrEmpty(destination)) {
			String origin = "true".equals(isMobile) ? "api" : "web";
			sendResponse = "https://" + origin + ".whatsapp.com/send?phone=".concat("55").concat(destination).concat("&text=").concat(message.replace(" ", "%20"));
			if (destination.length() == 11 && Utils.testUrl(sendResponse)) {
				status = "SUCCESS";
				response = sendResponse;
				/* Set thanked Order of Service */
				orderOfService.setThanked(true);
				orderOfService.willBeSaved = true;
				orderOfService.merge();
				/* Generate Activity */
				String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
				String activityDescription = "Mensagem enviada: \"" + message + "\"";
				User loggedUser = Admin.getLoggedUserInstitution().getUser();
				ActivitiesCRUD.generateActivity(activityTitle, activityDescription, orderOfService.getClient(), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.SystemAfterSaleByWhatsApp);
			} else {
				status = "ERROR";
				response = "Erro ao enviar a mensagem! Talvez este cliente não tenha Whatsapp ou você esteja sem internet no momento!";
			}
		} else {
			response = "Não há nenhum número de telefone cadastrado para " + orderOfService.client.toString();
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		if ("thankfulNotificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/thankfulNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02, parameter);
		} else if ("notificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/customerNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, parameter);
		} else if ("accordion".equals(idUpdate)) {
			List<OrderOfService> listOrderOfService = loadListOrderOfService();
			verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
			render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit, parameter);
		}
	}

	public static void thankfulDoneByTelephone() throws IOException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String idUpdate = params.get("idUpdate", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String sendResponse = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		Parameter parameter = Parameter.all().first();
		status = "SUCCESS";
		response = sendResponse;
		/* Set thanked Order of Service */
		orderOfService.setThanked(true);
		orderOfService.willBeSaved = true;
		orderOfService.merge();
		/* Generate Activity */
		String activityTitle = "Pós-venda realizado por telefone";
		String activityDescription = "Ação de pós-venda realizada por telefone referente a OS (" + orderOfService.getOrderCode() + ")";
		User loggedUser = Admin.getLoggedUserInstitution().getUser();
		ActivitiesCRUD.generateActivity(activityTitle, activityDescription, orderOfService.getClient(), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.CallAfterSale);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		if ("thankfulNotificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/thankfulNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02, parameter);
		} else if ("notificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/customerNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, parameter);
		} else if ("accordion".equals(idUpdate)) {
			List<OrderOfService> listOrderOfService = loadListOrderOfService();
			verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
			render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit, parameter);
		}
	}

	public static void sendWhatsAppEvaluation() throws IOException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String idUpdate = params.get("idUpdate", String.class);
		String isMobile = params.get("isMobile", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = value;
		String sendResponse = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		String destination = Utils.replacePhoneNumberCaracteres(orderOfService.client.phone);
		Parameter parameter = Parameter.all().first();
		if (!Utils.isNullOrEmpty(destination)) {
			String origin = "true".equals(isMobile) ? "api" : "web";
			sendResponse = "https://" + origin + ".whatsapp.com/send?phone=".concat("55").concat(destination).concat("&text=").concat(message.replace(" ", "%20"));
			if (destination.length() == 11 && Utils.testUrl(sendResponse)) {
				status = "SUCCESS";
				response = sendResponse;
				/* Set thanked Order of Service */
				orderOfService.setThanked(true);
				orderOfService.willBeSaved = true;
				orderOfService.merge();
				/* Generate Activity */
				String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
				String activityDescription = "Mensagem enviada: \"" + message + "\"";
				User loggedUser = Admin.getLoggedUserInstitution().getUser();
				ActivitiesCRUD.generateActivity(activityTitle, activityDescription, orderOfService.getClient(), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.SystemAfterSaleByWhatsApp);
			} else {
				status = "ERROR";
				response = "Erro ao enviar a mensagem! Talvez este cliente não tenha Whatsapp ou você esteja sem internet no momento!";
			}
		} else {
			response = "Não há nenhum número de telefone cadastrado para " + orderOfService.client.toString();
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		if ("evaluationNotificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/evaluationNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02, parameter);
		} else if ("notificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/customerNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, parameter);
		} else if ("accordion".equals(idUpdate)) {
			List<OrderOfService> listOrderOfService = loadListOrderOfService();
			verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
			render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit, parameter);
		}
	}

	public static void sendWhatsAppOS() throws IOException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String isMobile = params.get("isMobile", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = value;
		String sendResponse = null;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		String destination = Utils.replacePhoneNumberCaracteres(orderOfService.client.phone);
		if (!Utils.isNullOrEmpty(destination)) {
			String origin = "true".equals(isMobile) ? "api" : "web";
			sendResponse = "https://" + origin + ".whatsapp.com/send?phone=".concat("55").concat(destination).concat("&text=").concat(message.replace(" ", "%20"));
			if (destination.length() == 11 && Utils.testUrl(sendResponse)) {
				status = "SUCCESS";
				response = sendResponse;
				/* Set thanked Order of Service */
				orderOfService.setThanked(true);
				orderOfService.willBeSaved = true;
				orderOfService.merge();
				/* Generate Activity */
				String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
				String activityDescription = "Mensagem enviada: \"" + message + "\"";
				User loggedUser = Admin.getLoggedUserInstitution().getUser();
				ActivitiesCRUD.generateActivity(activityTitle, activityDescription, orderOfService.getClient(), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.WppSent);
			} else {
				status = "ERROR";
				response = "Erro ao enviar a mensagem! Talvez este cliente não tenha Whatsapp ou você esteja sem internet no momento!";
			}
		} else {
			response = "Não há nenhum número de telefone cadastrado para " + orderOfService.client.toString();
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit);
	}

	public static void sendPUSH() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String idUpdate = params.get("idUpdate", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		Map<String, String> paramTags = new HashMap<String, String>();
		paramTags.put("orderCode", orderOfService.orderCode);
		paramTags.put("message", message);
		PushNotification send = new PushNotification(STR_PUSH_AUTH_ID, STR_PUSH_APP_ID);
		String strJsonBody = send.getTags(paramTags);
		String jsonResponse = send.sentToUserBySpecificTag(strJsonBody);
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonResponse).getAsJsonObject();
		JsonElement jsonElement = obj.get("response");
		if ("200".equals(jsonElement.getAsString())) {
			/* Generate Activity */
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: \"" + message + "\"";
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.PushSent);
			/* Save sms object */
			StatusPUSH statusPUSH = new StatusPUSH();
			statusPUSH.setInstitutionId(institution.id);
			statusPUSH.setMessage(message);
			statusPUSH.setMsgId(obj.get("id").getAsString());
			statusPUSH.setPostedAt(Utils.getCurrentDateTime());
			statusPUSH.setClientName(orderOfService.client.toString());
			statusPUSH.setDestination(orderOfService.orderCode);
			statusPUSH.setSendDate(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
			statusPUSH.setPostedAt(Utils.getCurrentDateTime());
			statusPUSH.setPushSent(true);
			statusPUSH.willBeSaved = true;
			statusPUSH.id = 0l;
			statusPUSH.merge();
			status = "SUCCESS";
			response = "Notificação Push enviada com sucesso.";
		} else {
			status = "ERROR";
			response = "Erro ao enviar a notificação Push! Tente novamente!";
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		if ("accordion".equals(idUpdate)) {
			render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit);
		} else if ("notificationArea".equals(idUpdate)) {
			render("OrderOfServiceCRUD/customerNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit);
		}
	}

	public static void sendPUSHThankful() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		Map<String, String> paramTags = new HashMap<String, String>();
		paramTags.put("orderCode", orderOfService.orderCode);
		paramTags.put("message", message);
		PushNotification send = new PushNotification(STR_PUSH_AUTH_ID, STR_PUSH_APP_ID);
		String strJsonBody = send.getTags(paramTags);
		String jsonResponse = send.sentToUserBySpecificTag(strJsonBody);
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonResponse).getAsJsonObject();
		JsonElement jsonElement = obj.get("response");
		if ("200".equals(jsonElement.getAsString())) {
			/* Generate Activity */
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: \"" + message + "\"";
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.SystemAfterSaleByPush);
			/* Save sms object */
			StatusPUSH statusPUSH = new StatusPUSH();
			statusPUSH.setInstitutionId(institution.id);
			statusPUSH.setMessage(message);
			statusPUSH.setMsgId(obj.get("id").getAsString());
			statusPUSH.setPostedAt(Utils.getCurrentDateTime());
			statusPUSH.setClientName(orderOfService.client.toString());
			statusPUSH.setDestination(orderOfService.orderCode);
			statusPUSH.setSendDate(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
			statusPUSH.setPostedAt(Utils.getCurrentDateTime());
			statusPUSH.setPushSent(true);
			statusPUSH.willBeSaved = true;
			statusPUSH.id = 0l;
			statusPUSH.merge();
			status = "SUCCESS";
			response = "Notificação Push enviada com sucesso.";
			/* Set thanked Order of Service */
			orderOfService.setThanked(true);
			orderOfService.willBeSaved = true;
			orderOfService.merge();
		} else {
			status = "ERROR";
			response = "Erro ao enviar a notificação Push! Tente novamente!";
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/thankfulNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02);
	}

	public static void sendPUSHEvaluation() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		Map<String, String> paramTags = new HashMap<String, String>();
		paramTags.put("orderCode", orderOfService.orderCode);
		paramTags.put("message", message);
		PushNotification send = new PushNotification(STR_PUSH_AUTH_ID, STR_PUSH_APP_ID);
		String strJsonBody = send.getTags(paramTags);
		String jsonResponse = send.sentToUserBySpecificTag(strJsonBody);
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonResponse).getAsJsonObject();
		JsonElement jsonElement = obj.get("response");
		if ("200".equals(jsonElement.getAsString())) {
			/* Generate Activity */
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: \"" + message + "\"";
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.ClientEvaluationPush);
			/* Save sms object */
			StatusPUSH statusPUSH = new StatusPUSH();
			statusPUSH.setInstitutionId(institution.id);
			statusPUSH.setMessage(message);
			statusPUSH.setMsgId(obj.get("id").getAsString());
			statusPUSH.setPostedAt(Utils.getCurrentDateTime());
			statusPUSH.setClientName(orderOfService.client.toString());
			statusPUSH.setDestination(orderOfService.orderCode);
			statusPUSH.setSendDate(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
			statusPUSH.setPostedAt(Utils.getCurrentDateTime());
			statusPUSH.setPushSent(true);
			statusPUSH.willBeSaved = true;
			statusPUSH.id = 0l;
			statusPUSH.merge();
			status = "SUCCESS";
			response = "Notificação Push enviada com sucesso.";
			/* Set thanked Order of Service */
			orderOfService.setThanked(true);
			orderOfService.willBeSaved = true;
			orderOfService.merge();
		} else {
			status = "ERROR";
			response = "Erro ao enviar a notificação Push! Tente novamente!";
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/evaluationNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02);
	}

	public static void remove(String id) throws Exception {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		OrderOfServiceStep.delete("orderOfService_id = " + orderOfService.getId());
		OrderOfServiceValue.delete("orderOfServiceId = " + orderOfService.getId());
		orderOfService.delete();
		/* Generate Activity */
		String activityTitle = "OS de código (" + orderOfService.getOrderCode() + ") removida";
		String activityDescription = "Nenhuma descrição adicional.";
		User loggedUser = Admin.getLoggedUserInstitution().getUser();
		ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.OSRemoveAction);
		OrderOfServiceCRUD.list(0, null, null, null, null);
	}

	public static void sendEmail() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String[] paramsSpplited = id.split("-");
		String orderServiceStepId = paramsSpplited[2];
		/* Institution object */
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		/* OrderOfServiceStep object */
		OrderOfServiceStep orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId) + " and institutionId = " + institution.getId() + " and isActive = true").first();
		String phase = String.valueOf(orderOfServiceStep.getStep().getPosition());
		String statusPhase = orderOfServiceStep.getStatus().getLabel();
		String obs = orderOfServiceStep.getObs();
		/* OrderOfService object */
		OrderOfService orderOfService = orderOfServiceStep.getOrderOfService();
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination(orderOfService.getClient().getMail());
		sendTo.setName(orderOfService.getClient().getName());
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany(institution.getInstitution());
		sender.setFrom("notificacoes@acompanheseupedido.com");
		sender.setReplyTo(institution.getEmail());
		sender.setKey(orderOfService.orderCode);
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		String clientName = Utils.escapeSpecialCharacters(orderOfService.getClient().getName());
		String institutionName = Utils.escapeSpecialCharacters(institution.getInstitution());
		bodyMail.setTitle1("Ol&aacute;, " + clientName + "!");
		bodyMail.setTitle2("Seu pedido foi atualizado!");
		bodyMail.setParagraph1(orderOfService.orderCode);
		bodyMail.setParagraph2(phase + ", " + statusPhase);
		bodyMail.setParagraph3(obs);
		bodyMail.setFooter1("Atenciosamente, " + institutionName + ".");
		bodyMail.setImage1(parameter.getSiteDomain() + "/logo-empresa/" + institution.getId());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplate(bodyMail, parameter));
		if (mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter)) {
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: \"Seu pedido foi atualizado!\"";
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.Mail);
			/* Save sms object */
			StatusMail statusMail = new StatusMail();
			statusMail.setInstitutionId(institution.id);
			statusMail.setSubject(Utils.removeHTML(bodyMail.title2) + " Acompanhe!");
			statusMail.setMessage(bodyMail.getBodyHTML());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setClientName(orderOfService.client.toString());
			statusMail.setDestination(sendTo.getDestination());
			statusMail.setSendDate(Utils.getCurrentDateTime());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setMailSent(true);
			statusMail.willBeSaved = true;
			statusMail.id = 0l;
			statusMail.merge();
			status = "SUCCESS";
			response = "E-mail enviado com sucesso.";
		} else {
			status = "ERROR";
			response = "Erro ao enviar o e-mail! Tente novamente em instantes.";
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		verifyIfOrderAreOpenAndUpdateServicesReferences(listOrderOfService);
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution, smsExceedLimit);
	}

	public static void sendEmailNotificationToCustomer() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		/* Institution object */
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		/* OrderOfService object */
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination(orderOfService.getClient().getMail());
		sendTo.setName(orderOfService.getClient().getName());
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany(institution.getInstitution());
		sender.setFrom("notificacoes@acompanheseupedido.com");
		sender.setReplyTo(institution.getEmail());
		sender.setKey(orderOfService.orderCode);
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		String clientName = Utils.escapeSpecialCharacters(orderOfService.getClient().getName());
		String institutionName = Utils.escapeSpecialCharacters(institution.getInstitution());
		bodyMail.setTitle1("Ol&aacute;, " + clientName + "!");
		bodyMail.setTitle2("Acompanhe seu pedido pelo c&oacute;digo <br />" + orderOfService.getOrderCode() + " <br /> Clique abaixo: <br /> <a href=\"" + parameter.getSiteDomain() + "/acompanhe\" target=\"_blank\">" + parameter.getSiteDomain() + "/acompanhe</a>");
		bodyMail.setParagraph1("");
		bodyMail.setParagraph2("");
		bodyMail.setParagraph3("");
		bodyMail.setFooter1("Atenciosamente, " + institutionName + ".");
		bodyMail.setImage1(parameter.getSiteDomain() + "/logo-empresa/" + institution.getId());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplateSimple(bodyMail, parameter));
		if (mailController.sendHTMLMail(sendTo, sender, bodyMail, orderOfService.getClient().getName() + ", seu código de acompanhamento. :)", parameter)) {
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: " + bodyMail.getTitle2();
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.Mail);
			/* Save sms object */
			StatusMail statusMail = new StatusMail();
			statusMail.setInstitutionId(institution.id);
			statusMail.setSubject(Utils.removeHTML(bodyMail.title2) + " Acompanhe!");
			statusMail.setMessage(bodyMail.getBodyHTML());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setClientName(orderOfService.client.toString());
			statusMail.setDestination(sendTo.getDestination());
			statusMail.setSendDate(Utils.getCurrentDateTime());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setMailSent(true);
			statusMail.willBeSaved = true;
			statusMail.id = 0l;
			statusMail.merge();
			status = "SUCCESS";
			response = "E-mail enviado com sucesso.";
		} else {
			status = "ERROR";
			response = "Erro ao enviar o e-mail! Tente novamente em instantes.";
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/customerNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02, parameter);
	}

	public static void sendEmailNotificationThankful() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		/* Institution object */
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		/* OrderOfService object */
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination(orderOfService.getClient().getMail());
		sendTo.setName(orderOfService.getClient().getName());
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany(institution.getInstitution());
		sender.setFrom("notificacoes@acompanheseupedido.com");
		sender.setReplyTo(institution.getEmail());
		sender.setKey(orderOfService.orderCode);
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		bodyMail.setTitle1("Ol&aacute;, " + orderOfService.getClient().getName() + "!");
		bodyMail.setTitle2("Saiba que voc&ecirc; &eacute; importante e &eacute; um prazer te servir. Gratid&atilde;o.<br />");
		bodyMail.setParagraph1("");
		bodyMail.setParagraph2("");
		bodyMail.setParagraph3("");
		bodyMail.setFooter1("Atenciosamente, " + institution.getInstitution() + ".");
		bodyMail.setImage1(parameter.getSiteDomain() + "/logo-empresa/" + institution.getId());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplateSimple(bodyMail, parameter));
		if (mailController.sendHTMLMail(sendTo, sender, bodyMail, orderOfService.getClient().getName() + ", viemos te agradecer. :)", parameter)) {
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: " + bodyMail.getTitle2();
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.SystemAfterSaleByMail);
			/* Save sms object */
			StatusMail statusMail = new StatusMail();
			statusMail.setInstitutionId(institution.id);
			statusMail.setSubject(Utils.removeHTML(bodyMail.title2) + " Acompanhe!");
			statusMail.setMessage(bodyMail.getBodyHTML());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setClientName(orderOfService.client.toString());
			statusMail.setDestination(sendTo.getDestination());
			statusMail.setSendDate(Utils.getCurrentDateTime());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setMailSent(true);
			statusMail.willBeSaved = true;
			statusMail.id = 0l;
			statusMail.merge();
			status = "SUCCESS";
			response = "E-mail enviado com sucesso.";
			/* Set thanked Order of Service */
			orderOfService.setThanked(true);
			orderOfService.willBeSaved = true;
			orderOfService.merge();
		} else {
			status = "ERROR";
			response = "Erro ao enviar o e-mail! Tente novamente em instantes.";
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/thankfulNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02);
	}

	public static void sendEmailNotificationEvaluation() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		/* Institution object */
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true").first();
		/* OrderOfService object */
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination(orderOfService.getClient().getMail());
		sendTo.setName(orderOfService.getClient().getName());
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany(institution.getInstitution());
		sender.setFrom("notificacoes@acompanheseupedido.com");
		sender.setReplyTo(institution.getEmail());
		sender.setKey(orderOfService.orderCode);
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		bodyMail.setTitle1("Ol&aacute;, " + orderOfService.getClient().getName() + "!");
		bodyMail.setTitle2("Saiba que voc&ecirc; &eacute; importante e &eacute; um prazer te servir. Gratid&atilde;o.<br />");
		bodyMail.setParagraph1("");
		bodyMail.setParagraph2("");
		bodyMail.setParagraph3("");
		bodyMail.setFooter1("Atenciosamente, " + institution.getInstitution() + ".");
		bodyMail.setImage1(parameter.getSiteDomain() + "/logo-empresa/" + institution.getId());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplateSimple(bodyMail, parameter));
		if (mailController.sendHTMLMail(sendTo, sender, bodyMail, orderOfService.getClient().getName() + ", pode nos avaliar?", parameter)) {
			String activityTitle = "Msg. referente a OS (" + orderOfService.getOrderCode() + ")";
			String activityDescription = "Mensagem enviada: " + bodyMail.getTitle2();
			User loggedUser = Admin.getLoggedUserInstitution().getUser();
			ActivitiesCRUD.generateActivity(activityTitle, activityDescription, (Client) Client.findById(orderOfService.getClient().id), orderOfService.getInstitutionId(), loggedUser, ActivitiesEnum.ClientEvaluationMail);
			/* Save sms object */
			StatusMail statusMail = new StatusMail();
			statusMail.setInstitutionId(institution.id);
			statusMail.setSubject(Utils.removeHTML(bodyMail.title2) + " Acompanhe!");
			statusMail.setMessage(bodyMail.getBodyHTML());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setClientName(orderOfService.client.toString());
			statusMail.setDestination(sendTo.getDestination());
			statusMail.setSendDate(Utils.getCurrentDateTime());
			statusMail.setPostedAt(Utils.getCurrentDateTime());
			statusMail.setMailSent(true);
			statusMail.willBeSaved = true;
			statusMail.id = 0l;
			statusMail.merge();
			status = "SUCCESS";
			response = "E-mail enviado com sucesso.";
			/* Set thanked Order of Service */
			orderOfService.setThanked(true);
			orderOfService.willBeSaved = true;
			orderOfService.merge();
		} else {
			status = "ERROR";
			response = "Erro ao enviar o e-mail! Tente novamente em instantes.";
		}
		boolean smsExceedLimit = Admin.isSmsExceedLimit();
		boolean planSPO02 = PlansEnum.isPlanSPO02(Admin.getInstitutionInvoice().getPlan().getValue()) || PlansEnum.isPlanBETA(Admin.getInstitutionInvoice().getPlan().getValue());
		render("OrderOfServiceCRUD/evaluationNotificationModal.html", orderOfService, response, status, institution, smsExceedLimit, planSPO02);
	}

}
