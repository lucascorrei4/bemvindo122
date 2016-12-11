package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.Institution;
import models.MoipNotification;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.Service;
import models.User;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.mvc.Controller;
import util.UserInstitutionParameter;
import util.Utils;

public class Application extends Controller {

	public static void index() {
		render();
	}

	public static void generateServiceCode() {
		render();
	}

	public static void newAccount() {
		render();
	}

	private static boolean validatePassword(String password, String confirmationPassword) {
		boolean ret = false;
		if (!Utils.isNullOrEmpty(password) && !Utils.isNullOrEmpty(confirmationPassword)) {
			if (password.equals(confirmationPassword))
				ret = true;
		}
		return ret;
	}

	public static void howItWorks() {

	}

	public static void contact() {

	}

	public static int moipResponse() throws UnsupportedEncodingException {
		int httpResponse = HttpServletResponse.SC_PAYMENT_REQUIRED;
		String[] fields = request.params.data.get("body");
		String decodedFields = URLDecoder.decode(fields[0], "UTF-8");
		Gson gson = new GsonBuilder().create();
		/* Parse form content to JSON element */
		MoipNotification moipNotification = new MoipNotification();
		moipNotification = gson.fromJson(decodedFields, MoipNotification.class);
		if (moipNotification != null && (1 == moipNotification.getStatus_pagamento())) {
			String model = moipNotification.getId_transacao().substring(0, 2);
			if ("CCHFEM".equals(model) || "CCHMAS".equals(model)) {
				Service service = Service.find("orderCode = " + moipNotification.getId_transacao()).first();
				httpResponse = updatePayment(service, moipNotification);
			}
		}
		return httpResponse;
	}

	private static int updatePayment(Object object, MoipNotification moipNotification) {
		if (object instanceof Service) {
			Service service = (Service) object;
			// service.setPaymentForm(String.valueOf(moipNotification.getForma_pagamento()));
			// service.setPaymentType(moipNotification.getTipo_pagamento());
			service.merge();
			return HttpServletResponse.SC_OK;
		}
		return HttpServletResponse.SC_PAYMENT_REQUIRED;
	}

	public static void saveNewAccount(@Valid User user, String confirmPassword) {
		if (user.getName() != null) {
			if (!validateForm(user, confirmPassword)) {
				user.password = "";
				confirmPassword = "";
				render("@newAccount", user);
				return;
			} else {
				user.setAdmin(true);
				user.setActive(true);
				user.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
				user.setInstitutionId(0);
				user.save();
				flash.clear();
				validation.errors().clear();
				flash.success("Cadastro realizado com sucesso! Você está quase lá, " + user.getName()
						+ "! Para entrar, preencha os campos abaixo. :)", "");
				redirect("/login");
			}
		}
		render("@newAccount");
	}

	private static boolean validateForm(User user, String confirmPassword) {
		boolean ret = false;
		validation.required(user.getName()).message("Favor, insira o seu nome.").key("user.name");
		validation.required(user.getLastName()).message("Favor, insira o seu sobrenome.").key("user.lastName");
		validation.required(user.getEmail()).message("Favor, insira o seu e-mail.").key("user.email");
		validation.email(user.getEmail()).message("Favor, insira o seu e-mail no formato nome@provedor.com.br.")
				.key("user.email");
		validation.isTrue(User.verifyByEmail(user.getEmail()) == null).message("Já existe um usuário com este e-mail.")
				.key("user.email");
		validation.required(user.getPassword()).message("Favor, insira uma senha.").key("user.password");
		validation.required(confirmPassword).message("Favor, digite novamente a senha.").key("confirmPassword");
		validation.isTrue(validatePassword(user.getPassword(), confirmPassword))
				.message("As senhas digitadas devem ser iguais.").key("confirmPassword");
		params.flash();
		validation.keep();
		if (!validation.hasErrors())
			ret = true;
		else {
			for (play.data.validation.Error error : validation.errors()) {
				System.out.println(error.getKey() + " " + error.message());
			}
		}
		return ret;
	}

	public static void saveQuickAccount(String json) throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		/* Get body content from client request */
		String[] fields = request.params.data.get("body");
		String decodedFields = URLDecoder.decode(fields[0], "UTF-8");
		Gson gson = new GsonBuilder().create();
		/* Parse form content to JSON element */
		String jsonParam = Utils.transformQueryParamToJson(decodedFields, "user.");
		JsonParser parser = new JsonParser();
		JsonObject jsonElement = (JsonObject) parser.parse(jsonParam);
		jsonElement.addProperty("id", Long.valueOf(0));
		/* Save Client */
		/* Create object parsing JSON element */
		User user = new User();
		user = gson.fromJson(jsonElement, User.class);
		user.id = 0l;
		user.willBeSaved = true;
		/* Validate object before saving */
		if (!validateObjectToSave(user)) {
			List<Error> errors = validation.errors();
			response = "Favor, preencha todos os campos corretamente!";
			status = "ERROR";
			render("includes/newquickaccount.html", user, response, status, errors);
		} else {
			user.setAdmin(true);
			user.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
			user.setInstitutionId(0l);
			user.setActive(true);
			user.merge();
			response = "Pronto! Cadastro criado com sucesso! Agora, clique para entrar! :)";
			status = "SUCCESS";
			render("includes/newquickaccount.html", user, response, status);
		}
	}

	private static boolean validateObjectToSave(User user) {
		validation.clear();
		validation.valid(user);
		if (validation.hasErrors()) {
			for (play.data.validation.Error e : validation.errors()) {
				System.out.println(e.message());
			}
			params.flash();
			validation.keep();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private static boolean validateForm(User user) {
		boolean ret = false;
		validation.required(user.getName()).message("Favor, insira o seu nome.").key("user.name");
		validation.required(user.getLastName()).message("Favor, insira o seu sobrenome.").key("user.lastName");
		validation.required(user.getEmail()).message("Favor, insira o seu e-mail.").key("user.email");
		validation.email(user.getEmail()).message("Favor, insira o seu e-mail no formato nome@provedor.com.br.")
				.key("user.email");
		validation.isTrue(User.verifyByEmail(user.getEmail()) == null).message("Já existe um usuário com este e-mail.")
				.key("user.email");
		validation.required(user.getPassword()).message("Favor, insira uma senha.").key("user.password");
		validation.required(user.getRepeatPassword()).message("Favor, digite novamente a senha.")
				.key("confirmPassword");
		validation.isTrue(validatePassword(user.getPassword(), user.getRepeatPassword()))
				.message("As senhas digitadas devem ser iguais.").key("confirmPassword");
		params.flash();
		validation.keep();
		if (!validation.hasErrors())
			ret = true;
		else {
			for (play.data.validation.Error error : validation.errors()) {
				System.out.println(error.getKey() + " " + error.message());
			}
		}
		return ret;
	}

	public static void saveNewInstitution(@Valid Institution institution) {
		UserInstitutionParameter userInstitutionParameter = Admin.getLoggedUserInstitution();
		if (institution.getInstitution() != null) {
			if (!validateForm(institution)) {
				/* The user needs to create a institution */
				User user = userInstitutionParameter.getUser();
				render("@admin.firstStep", institution, user);
				return;
			} else {
				// Links the user to institution
				institution.setUserId(userInstitutionParameter.getUser().getId());
				institution.setPublishedId(userInstitutionParameter.getUser().getId());
				institution.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
				// Grants one month free to user
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, 1);
				institution.setLicenseDate(calendar.getTime());
				institution.save();
				// Links the institution to user
				userInstitutionParameter.getUser().setInstitutionId(institution.getId());
				userInstitutionParameter.getUser().save();
				flash.clear();
				validation.errors().clear();
				flash.success("Instituição '" + institution.getInstitution() + "' criada com sucesso. Aproveite!", "");
				Admin.enableUserConditions(userInstitutionParameter.getUser());
				Admin.index();
			}
		}
		render("@admin.firstStep");
	}

	private static boolean validateForm(Institution institution) {
		boolean ret = false;
		validation.required(institution.getInstitution()).message("Favor, insira o nome da Instituição.")
				.key("institution.institution");
		validation.required(institution.getEmail()).message("Favor, insira o e-mail.").key("institution.email");
		validation.email(institution.getEmail()).message("Favor, insira o e-mail no formato nome@provedor.com.br.")
				.key("institution.email");
		validation.required(institution.getLogo()).message("Favor, insira a logomarca.").key("institution.logo");
		validation.isTrue(Institution.verifyByEmail(institution.getEmail()) == null)
				.message("Já existe uma instituição com este e-mail.").key("institution.email");
		if (!Utils.isNullOrEmpty(institution.getCnpj())) {
			validation.isTrue(Utils.validateCPFOrCNPJ(institution.getCnpj())).message("CNPJ inválido.")
					.key("institution.cnpj");
			validation.isTrue(Institution.verifyByCnpj(institution.getCnpj()) == null)
					.message("Já existe uma Instituição com este CNPJ.").key("institution.cnpj");
		}
		validation.required(institution.getAddress()).message("Favor, digite o endereço.").key("institution.address");
		validation.required(institution.getNeighborhood()).message("Favor, informe o bairro.")
				.key("institution.neighborhood");
		validation.required(institution.getCep()).message("Favor, informe o CEP.").key("institution.cep");
		validation.required(institution.getPhone1()).message("Favor, informe o telefone.").key("institution.phone1");
		params.flash();
		validation.keep();
		if (!validation.hasErrors())
			ret = true;
		else {
			for (play.data.validation.Error error : validation.errors()) {
				System.out.println(error.getKey() + " " + error.message());
			}
		}
		return ret;
	}

	public static void follow(String orderCode) throws UnsupportedEncodingException, InterruptedException {
		String response = null;
		String status = null;
		if (orderCode != null) {
			OrderOfService orderOfService = OrderOfService.find("orderCode", orderCode).first();
			/* Validate object before saving */
			if (orderOfService == null) {
				List<Error> errors = validation.errors();
				response = "Código não encontrado! :(";
				status = "ERROR";
				render("includes/formFollow.html", response, status, errors);
			} else {
				response = "Redirecionando... :)";
				status = "SUCCESS";
				String clientName = orderOfService.getClient().getName();
				Institution institution = Institution.find("id", orderOfService.institutionId).first(); 
				String company = institution.getInstitution();
				List<Service> services = new ArrayList<Service>();
				List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue
						.find("orderOfServiceId = " + orderOfService.getId()).fetch();
				for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
					Service service = Service.find("id = " + orderOfServiceValue.getService().getId()).first();
					services.add(service);
				}
				Map<Service, List<OrderOfServiceStep>> mapOrderServiceSteps = new HashMap<Service, List<OrderOfServiceStep>>();
				for (Service service : services) {
					List<OrderOfServiceStep> orderOfServiceStep = OrderOfServiceStep
							.find("service_id = " + service.getId() + " and orderOfService_id = "
									+ orderOfService.getId() + " and isActive = true")
							.fetch();
					mapOrderServiceSteps.put(service, orderOfServiceStep);
				}
				render(clientName, company, orderOfService, mapOrderServiceSteps);
			}
		}
	}

}