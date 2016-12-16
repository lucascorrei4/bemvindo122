package controllers;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.BodyMail;
import models.Institution;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.Service;
import models.StatusMail;
import models.StatusPUSH;
import models.StatusSMS;
import models.Step;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import util.ApplicationConfiguration;
import util.PushNotification;
import util.StatusEnum;
import util.Utils;

@CRUD.For(models.OrderOfService.class)
public class OrderOfServiceController extends CRUD {
	
	public static String STR_PUSH_APP_ID = ApplicationConfiguration.getInstance().getOneSignalAppId();
	public static String STR_PUSH_AUTH_ID = ApplicationConfiguration.getInstance().getOneSignalAuthId();

	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		OrderOfService object = (OrderOfService) constructor.newInstance();
		List<Service> services = Service.find("institutionId = "
				+ Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by title asc")
				.fetch();
		try {
			render(type, object, services);
		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type, object, services);
		}
	}

	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		String where = "institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId();
		orderBy = "id";
		order = "DESC";
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
		Long count = type.count(search, searchFields, where);
		Long totalCount = type.count(null, null, where);
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("OrderOfServiceController/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		// Filtro pelo usuário conectado para proteger os dados dos demais
		// usuários
		OrderOfService object = OrderOfService.find(
				"id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId())
				.first();
		List<Service> services = Service.find("institutionId = "
				+ Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by title asc")
				.fetch();
		notFoundIfNull(object);
		List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue
				.find("orderOfServiceId = " + Long.valueOf(id)).fetch();
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
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService order = OrderOfService.find(
				"id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true")
				.first();
		List<Service> services = order.getServices();
		List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue
				.find("orderOfServiceId = " + Long.valueOf(id)).fetch();
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
		render(order, institution, services, orderOfServiceValues, subTotalGeralCurrency, totalGeralCurrency,
				discountGeralCurrency);
	}

	public static void orderByOrderOfServiceId(final String id) {
		List<OrderOfService> prayOrders = getOrderByOrderOfServiceId(id);
		render(prayOrders);
	}

	public static List<OrderOfService> getOrderByOrderOfServiceId(String id) {
		return OrderOfService
				.find("id = " + id + " and institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()
						+ " and isActive = true order by description asc")
				.fetch();
	}

	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		OrderOfService object = (OrderOfService) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		String initials = Admin.getLoggedInstitution().getInstitution().replaceAll(" ", "").toUpperCase()
				.substring(0, 2).concat(Admin.getLoggedInstitution().getId().toString());
		object.setOrderCode(initials.concat(String.valueOf(Utils.generateRandomId())));
		validation.valid(object);
		if (validation.hasErrors()) {
			List<Service> services = Service
					.find("institutionId = " + Admin.getLoggedUserInstitution().getInstitution().getId()
							+ " and isActive = true order by title asc")
					.fetch();
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
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
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
				orderOfServiceValue.setQtd(Float.valueOf(jObject.get("qtd").getAsString().replace(",", ".")));
				orderOfServiceValue
						.setUnitPrice(Float.valueOf(jObject.get("basePrice").getAsString().replace(",", ".")));
				orderOfServiceValue.setDiscount(Float.valueOf(jObject.get("discount").getAsString().replace(",", ".")));
				orderOfServiceValue.setSubTotal(Float.valueOf(jObject.get("subTotal").getAsString().replace(",", ".")));
				orderOfServiceValue.setInstitutionId(orderOfService.getInstitutionId());
				orderOfServiceValue.willBeSaved = true;
				orderOfServiceValue.save();
			}
		}
	}

	private static void generateStepsByService(OrderOfService orderOfService) {
		List<Service> services = new ArrayList<Service>();
		List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue
				.find("orderOfServiceId = " + orderOfService.getId()).fetch();
		for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
			Service service = Service.find("id = " + orderOfServiceValue.getService().getId()).first();
			services.add(service);
		}
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
				orderServiceStep.setPostedAt(Utils.getCurrentDateTime());
				orderServiceStep.setInstitutionId(orderOfService.getInstitutionId());
				orderServiceStep.willBeSaved = true;
				orderServiceStep.save();
				i++;
			}
		}
	}

	public static void updateOrder() {
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		render(listOrderOfService, institution);
	}

	private static List<OrderOfService> loadListOrderOfService() {
		List<OrderOfService> listOrderOfService = OrderOfService.find("institutionId = "
				+ Admin.getLoggedUserInstitution().getInstitution().getId() + " and isActive = true order by id desc")
				.fetch();
		for (OrderOfService orderOfService : listOrderOfService) {
			List<Service> services = new ArrayList<Service>();
			List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue
					.find("orderOfServiceId = " + orderOfService.getId()).fetch();
			for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
				Service service = Service.find("id = " + orderOfServiceValue.getService().getId()).first();
				services.add(service);
			}
			Map<Service, List<OrderOfServiceStep>> mapOrderServiceSteps = new HashMap<Service, List<OrderOfServiceStep>>();
			for (Service service : services) {
				List<OrderOfServiceStep> orderOfServiceStep = OrderOfServiceStep.find("service_id = " + service.getId()
						+ " and orderOfService_id = " + orderOfService.getId() + " and isActive = true").fetch();
				mapOrderServiceSteps.put(service, orderOfServiceStep);
				orderOfService.setMapOrderServiceSteps(mapOrderServiceSteps);
			}
		}
		return listOrderOfService;
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
		OrderOfServiceStep orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId)
				+ " and institutionId = " + institution.getId() + " and isActive = true").first();
		orderOfServiceStep.setStatus(StatusEnum.getNameByValue(newOrderStatus));
		orderOfServiceStep.save();
		/*
		 * Creating new object to do new search to see if object was saved
		 * correctly
		 */
		orderOfServiceStep = new OrderOfServiceStep();
		orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId)
				+ " and institutionId = " + institution.getId() + " and isActive = true").first();
		boolean isSavedOrderOfServiceStep = String.valueOf(orderOfServiceStep.getStatus().getValue())
				.equals(String.valueOf(newOrderStatus));
		if (isSavedOrderOfServiceStep) {
			status = "SUCCESS";
			response = "Etapa do pedido ".concat(orderCode).concat(" atualizada com sucesso!");
		} else {
			status = "ERROR";
			response = "Houve um erro ao atualizar a etapa do pedido ".concat(orderCode).concat("!");
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution);
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
		OrderOfServiceStep orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId)
				+ " and institutionId = " + institution.getId() + " and isActive = true").first();
		orderOfServiceStep.setObs(obsParam);
		orderOfServiceStep.save();
		/*
		 * Creating new object to do new search to see if object was saved
		 * correctly
		 */
		orderOfServiceStep = new OrderOfServiceStep();
		orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId)
				+ " and institutionId = " + institution.getId() + " and isActive = true").first();
		boolean isSavedOrderOfServiceStep = String.valueOf(orderOfServiceStep.getObs())
				.equals(String.valueOf(obsParam));
		if (isSavedOrderOfServiceStep) {
			status = "SUCCESS";
			response = "Observação do pedido ".concat(orderCode).concat(" inserida com sucesso!");
		} else {
			status = "ERROR";
			response = "Erro ao inserir a observação do pedido ".concat(orderCode).concat(".");
		}
		List<OrderOfService> listOrderOfService = loadListOrderOfService();
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution);
	}

	public static void main(String[] args) {
		String[] spplited = "option-JV127680-7".split("-");
		System.out.println("option-JV127680-7".split("-")[2]);
	}

	public static void sendSMS() throws UnsupportedEncodingException {
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
		OrderOfService orderOfService = OrderOfService.find(
				"orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true")
				.first();
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
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution);
	}

	public static void sendPUSH() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderCode = paramsSpplited[1];
		String message = decodedValue;
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find(
				"orderCode = '" + orderCode + "' and institutionId = " + institution.getId() + " and isActive = true")
				.first();
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
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution);
	}


	public static void remove(String id) throws Exception {
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		OrderOfService orderOfService = OrderOfService.find(
				"id = " + Long.valueOf(id) + " and institutionId = " + institution.getId() + " and isActive = true")
				.first();
		OrderOfServiceStep.delete("orderOfService_id = " + orderOfService.getId());
		OrderOfServiceValue.delete("orderOfServiceId = " + orderOfService.getId());
		orderOfService.delete();
		OrderOfServiceController.list(0, null, null, null, null);
	}
	public static void sendEmail() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String id = params.get("id", String.class);
		String value = params.get("value", String.class);
		String decodedValue = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		String[] paramsSpplited = id.split("-");
		String orderServiceStepId = paramsSpplited[2];
		String message = decodedValue;
		/* Institution object */
		Institution institution = Institution.findById(Admin.getLoggedUserInstitution().getInstitution().getId());
		/* OrderOfServiceStep object */
		OrderOfServiceStep orderOfServiceStep = OrderOfServiceStep.find("id = " + Long.valueOf(orderServiceStepId)
		+ " and institutionId = " + institution.getId() + " and isActive = true").first();
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
		Sender sender= new Sender();
		sender.setCompany(institution.getInstitution());
		sender.setFrom(institution.getEmail());
		sender.setKey(orderOfService.orderCode);
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		bodyMail.setTitle1("Ol&aacute;, " + orderOfService.getClient().getName() + "!");
		bodyMail.setTitle2("Seu pedido foi atualizado!");
		bodyMail.setParagraph1(orderOfService.orderCode);
		bodyMail.setParagraph2(phase + ", " + statusPhase);
		bodyMail.setParagraph3(obs);
		bodyMail.setFooter1("Atenciosamente, " + institution.getInstitution() + ".");
		bodyMail.setImage1(parameter.getLogoUrl());
		bodyMail.setBodyHTML(getHTMLTemplate(bodyMail));
		if (mailController.sendHTMLMail(sendTo , sender, bodyMail)) {
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
		render("includes/updateOrderSteps.html", listOrderOfService, response, status, institution);
	}
	
	public static String getHTMLTemplate(BodyMail bodyMail) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\":1fb\" class=\"a3s aXjCH m14d93659b00650ae\">                                                                                                                                                                                                                                                                                       ");                                                                                                                                                                                                                                                      
		sb.append("   <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                                      ");
		sb.append("      <div>                                                                                                                                                                                                                                                                                                                               ");
		sb.append("         <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                                   ");
		sb.append("            <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                             ");
		sb.append("               <div>                                                                                                                                                                                                                                                                                                                      ");
		sb.append("                  <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                          ");
		sb.append("                     <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                    ");
		sb.append("                        <div>                                                                                                                                                                                                                                                                                                             ");
		sb.append("                           <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                 ");
		sb.append("                              <div bgcolor=\"#EBEBEB\">                                                                                                                                                                                                                                                                                     ");
		sb.append("                                 <table bgcolor=\"#EBEBEB\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">                                                                                                                                                                                                                        ");
		sb.append("                                    <tbody>                                                                                                                                                                                                                                                                                               ");
		sb.append("                                       <tr align=\"center\">                                                                                                                                                                                                                                                                                ");
		sb.append("                                          <td align=\"center\">                                                                                                                                                                                                                                                                             ");
		sb.append("                                             <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">                                                                                                                                                                                                                                           ");
		sb.append("                                                <tbody>                                                                                                                                                                                                                                                                                   ");
		sb.append("                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append("                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append("                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append("                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append("                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append("                                                         <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center; border: #e3e3e3 solid 1px; border-radius: 7px\" width=\"382\">                                                                                                                       ");
		sb.append("                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append("                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append("                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append("                                                                     <table bgcolor=\"#fff\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-radius: 7px\" width=\"380\">                                                                                                                                                             ");
		sb.append("                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append("                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append("                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append("                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append("                                                                                 <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"310\">                                                                                                                                              ");
		sb.append("                                                                                    <tbody>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr align=\"center\">                                                                                                                                                                                                                                ");
		sb.append("                                                                                          <td style=\"text-align: center\"><img alt=\"SeuPedido.Online - Acompanhamento de Servi&ccedil;os e Marketing Digital\" class=\"m_3594026484717175708CToWUd CToWUd\" src=\""+ bodyMail.getImage1() +"\"                                                                                                                                                    ");
		sb.append("                                                                                             style=\"border-radius: 5%; border: 1px solid rgb(227, 227, 227); width: 80px; min-height: 80px\"></td>                                                                                                                                         ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\">SeuPedido.Online</td>                                                                                                                                 ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\">                                                                                                                                                                                                                             ");
		sb.append("                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append("                                                                                                <span style=\"color: #000000\"><span                                                                                                                                                                                                        ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><span                                                           ");
		sb.append("                                                                                                   class=\"il\">" + bodyMail.getTitle1() + "</span></span></span>                                                                                                                                                             ");
		sb.append("                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append("                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append("                                                                                                <span style=\"color: #808000\"><span                                                                                                                                                                                                        ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><strong>" + bodyMail.getTitle2() + "</strong>                    ");
		sb.append("                                                                                                 </span></span><br> <br> <strong> <span                                                                                                                                                                    ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\">                                                                ");
		sb.append("                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append("                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\">                                                                ");
		sb.append("                                                                                                VEJA ABAIXO: </span>                                                                                                                                                                                                                      ");
		sb.append("                                                                                                </span>                                                                                                                                                                                                                                   ");
		sb.append("                                                                                                </strong>                                                                                                                                                                                                                                 ");
		sb.append("                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append("                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append("                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; text-align: center\"><span><span                                       ");
		sb.append("                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; float: left\">C&oacute;digo:                                           ");
		sb.append("                                                                                                <strong style=\"font-weight: bold\">"+ bodyMail.getParagraph1() +"</strong>                                                                                                                                                                                       ");
		sb.append("                                                                                                </span> </span></span>                                                                                                                                                                                                                    ");
		sb.append("                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append("                                                                                             <br />                                                                                                                                                                                                                                       ");
		sb.append("                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append("                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; text-align: center\"><span><span                                       ");
		sb.append("                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; float: left\">Fase:                                                    ");
		sb.append("                                                                                                <strong style=\"font-weight: bold\">"+ bodyMail.getParagraph2() +"</strong>                                                                                                                                                                                     ");
		sb.append("                                                                                                </span> </span></span>                                                                                                                                                                                                                    ");
		sb.append("                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append("                                                                                             <br />                                                                                                                                                                                                                                       ");
		sb.append("                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append("                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; text-align: center\"><span><span                                       ");
		sb.append("                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; float: left\">Obs.:                                                    ");
		sb.append("                                                                                                <strong style=\"font-weight: bold\">"+ bodyMail.getParagraph3() +"</strong>                                                                                                                                                                                        ");
		sb.append("                                                                                                </span> </span></span>                                                                                                                                                                                                                    ");
		sb.append("                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append("                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("																							 <td height=\"20\" style=\"height: 40px; border-bottom-color: rgb(227, 227, 227); border-bottom-style: solid; border-bottom-width: 1px; font-style: italic;\">" + bodyMail.getFooter1() + "</td>                                                      ");                                        
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; border-bottom: #e3e3e3 solid 1px; padding: 5px\">Clique para acompanhar as etapas e                                                                                                          ");
		sb.append("                                                                                             detalhes do seu pedido                                                                                                                                                                                                                       ");
		sb.append("                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"20\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" bgcolor=\"#4098BC\" style=\"border-radius: 5px\">                                                                                                                                                                                ");
		sb.append("                                                                                             <div>                                                                                                                                                                                                                                        ");
		sb.append("                                                                                                <a href=\"http://www.seupedido.online\"                                                                                                                                                                                                     ");
		sb.append("                                                                                                   style=\"background-color: #808000; border-radius: 5px; color: #ffffff; display: inline-block; font-family: sans-serif; font-size: 16px; font-weight: bold; line-height: 50px; text-align: center; text-decoration: none; width: 310px\"  ");
		sb.append("                                                                                                   target=\"_blank\" data-saferedirecturl=\"http://www.seupedido.online\">www.seupedido.online</a>                                                                                                                                            ");
		sb.append("                                                                                             </div>                                                                                                                                                                                                                                       ");
		sb.append("                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\"><strong>Acompanhamento de Servi&ccedil;os e                                                                                                                  ");
		sb.append("                                                                                             Marketing Digital</strong>                                                                                                                                                                                                                   ");
		sb.append("                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\"                                                                                                                                                                                                                              ");
		sb.append("                                                                                             style=\"font-size: 11px; color: #cfcfcf; font-style: italic; text-align: center; padding-top: 5px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif\">&nbsp;</td>                                                ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append("                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                    </tbody>                                                                                                                                                                                                                                              ");
		sb.append("                                                                                 </table>                                                                                                                                                                                                                                                 ");
		sb.append("                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append("                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append("                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append("                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append("                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append("                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append("                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append("                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append("                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append("                                                                     <table bgcolor=\"#F7F7F7\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"                                                                                                                                                                                                  ");
		sb.append("                                                                        style=\"border-top: #e3e3e3 solid 1px; border-bottom-right-radius: 7px; border-bottom-left-radius: 7px\" width=\"380\">                                                                                                                                               ");
		sb.append("                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append("                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append("                                                                              <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                              ");
		sb.append("                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append("                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append("                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append("                                                                                 <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"310\"></table>                                                                                                                                                                    ");
		sb.append("                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append("                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append("                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append("                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append("                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append("                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append("                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append("                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append("                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append("                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append("                                                   <tr align=\"center\">                                                                                                                                                                                                                                                                    ");
		sb.append("                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append("                                                         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"382\">                                                                                                                                                                                        ");
		sb.append("                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append("                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append("                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append("                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append("                                                               <tr style=\"text-align: center\">                                                                                                                                                                                                                                            ");
		sb.append("                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append("                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append("                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append("                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append("                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append("                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append("                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append("                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append("                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append("                                                </tbody>                                                                                                                                                                                                                                                                                  ");
		sb.append("                                             </table>                                                                                                                                                                                                                                                                                     ");
		sb.append("                                          </td>                                                                                                                                                                                                                                                                                           ");
		sb.append("                                       </tr>                                                                                                                                                                                                                                                                                              ");
		sb.append("                                    </tbody>                                                                                                                                                                                                                                                                                              ");
		sb.append("                                 </table>                                                                                                                                                                                                                                                                                                 ");
		sb.append("                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append("                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append("                              </div>                                                                                                                                                                                                                                                                                                      ");
		sb.append("                              <div>&nbsp;</div>                                                                                                                                                                                                                                                                                           ");
		sb.append("                           </div>                                                                                                                                                                                                                                                                                                         ");
		sb.append("                        </div>                                                                                                                                                                                                                                                                                                            ");
		sb.append("                        <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                     ");
		sb.append("                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append("                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append("                     </div>                                                                                                                                                                                                                                                                                                               ");
		sb.append("                     <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                    ");
		sb.append("                  </div>                                                                                                                                                                                                                                                                                                                  ");
		sb.append("               </div>                                                                                                                                                                                                                                                                                                                     ");
		sb.append("               <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                              ");
		sb.append("               <div class=\"m_3594026484717175708yj6qo\">&nbsp;</div>                                                                                                                                                                                                                                                                       ");
		sb.append("               <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                         ");
		sb.append("            </div>                                                                                                                                                                                                                                                                                                                        ");
		sb.append("            <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                            ");
		sb.append("         </div>                                                                                                                                                                                                                                                                                                                           ");
		sb.append("      </div>                                                                                                                                                                                                                                                                                                                              ");
		sb.append("      <p>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append("         <br>                                                                                                                                                                                                                                                                                                                             ");
		sb.append("      </p>                                                                                                                                                                                                                                                                                                                                ");
		sb.append("      <div class=\"yj6qo\"></div>                                                                                                                                                                                                                                                                                                           ");
		sb.append("      <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                             ");
		sb.append("   </div>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append("   <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                                ");
		sb.append("</div>                                                                                                                                                                                                                                                                                                                                    ");
		return sb.toString();
	}
}
