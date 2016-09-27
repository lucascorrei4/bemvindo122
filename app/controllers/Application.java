package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.MoipNotification;
import models.Service;
import models.User;
import play.data.validation.Valid;
import play.mvc.Controller;
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

	public static void saveQuickAccount(@Valid User user, String confirmPassword) {
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
			service.setPaymentForm(String.valueOf(moipNotification.getForma_pagamento()));
			service.setPaymentType(moipNotification.getTipo_pagamento());
			service.setPaid(true);
			service.merge();
			return HttpServletResponse.SC_OK;
		}
		return HttpServletResponse.SC_PAYMENT_REQUIRED;
	}
	
	public static void saveQuickAccount(String json) throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String order = null;
		/* Get body content from client request */
		String[] fields = request.params.data.get("body");
		String decodedFields = URLDecoder.decode(fields[0], "UTF-8");
		Gson gson = new GsonBuilder().create();
		/* Parse form content to JSON element */
		String jsonParam = transformQueryParamToJson(decodedFields);
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
			response = "Preencha os campos obrigatórios!";
			status = "ERROR";
			render("includes/newquickaccount.html", user, response, status);
		} else {
			user.setAdmin(true);
			user.setInstitutionId(0l);
			user.save();
			response = "Pronto! Cadastro criado com sucesso! Faça o login abaixo!";
			status = "SUCCESS";
			render("includes/newquickaccount.html", user, response, status, order);
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
	
	private static String transformQueryParamToJson(String queryParam) {
		StringTokenizer st = new StringTokenizer(queryParam, "&");
		String json = "{";
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			String replaceKey = str.replace("user.", "");
			int indexKey = replaceKey.indexOf("=");
			String key = replaceKey.substring(0, indexKey);
			String value = replaceKey.substring(indexKey + 1, replaceKey.length());
			value = (Utils.isNullOrEmpty(value) ? "" : new String(value).replace("+", " ").trim());
			json = json.concat("\"").concat(key).concat("\"").concat(":").concat("\"").concat(value).concat("\"");
			if (st.hasMoreTokens()) {
				json = json.concat(",");
			}
		}
		json = json.concat("}");
		return json;
	}
	
}