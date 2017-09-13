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

import models.Article;
import models.BodyMail;
import models.Institution;
import models.MailList;
import models.Message;
import models.MoipNotification;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.Service;
import models.StatusMail;
import models.StatusSMS;
import models.TheSystem;
import models.User;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.mvc.Before;
import play.mvc.Controller;
import util.FromEnum;
import util.UserInstitutionParameter;
import util.Utils;

public class Application extends Controller {

	public static void indexOld() {
		List<Article> listArticles = Article.find("isActive = true order by postedAt desc").fetch(6);
		render(listArticles);
	}

	public static void index() {
		List<Article> listArticles = Article.find("isActive = true order by postedAt desc").fetch(4);
		List<Article> listArticles12 = listArticles.subList(0, 2);
		List<Article> listArticles34 = listArticles.subList(2, listArticles.size());
		List<TheSystem> listTheSystems = TheSystem.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		render(listTheSystems, listArticles12, listArticles34);
	}

	public static void generateServiceCode() {
		render();
	}

	public static void newAccount() {
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		render(theSystem, bottomNews);
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
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		render(theSystem, bottomNews);
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
				user.setPostedAt(Utils.getCurrentDateTime());
				user.setInstitutionId(0);
				user.save();
				flash.clear();
				validation.errors().clear();
				flash.success("Cadastro realizado com sucesso! Você está quase lá, " + user.getName() + "! Para entrar, preencha os campos abaixo. :)", "");
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
		validation.email(user.getEmail()).message("Favor, insira o seu e-mail no formato nome@provedor.com.br.").key("user.email");
		validation.isTrue(User.verifyByEmail(user.getEmail()) == null).message("Já existe um usuário com este e-mail.").key("user.email");
		validation.required(user.getPassword()).message("Favor, insira uma senha.").key("user.password");
		validation.required(confirmPassword).message("Favor, digite novamente a senha.").key("confirmPassword");
		validation.isTrue(validatePassword(user.getPassword(), confirmPassword)).message("As senhas digitadas devem ser iguais.").key("confirmPassword");
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
		String body = params.get("body", String.class);
		String decodedParams = URLDecoder.decode(body, "UTF-8");
		String[] bodyParam = decodedParams.split("&");
		String name = Utils.getValueFromUrlParam(bodyParam[0]);
		String lastName = Utils.getValueFromUrlParam(bodyParam[1]);
		String phone = Utils.getValueFromUrlParam(bodyParam[2]);
		String mail = Utils.getValueFromUrlParam(bodyParam[3]);
		String password = Utils.getValueFromUrlParam(bodyParam[4]);
		String repeatePassword = Utils.getValueFromUrlParam(bodyParam[5]);
		String responseQuickAccount = "";
		String statusQuickAccount = "";
		/* Get body content from client request */
		User user = new User();
		user.id = 0l;
		user.setName(name);
		user.setLastName(lastName);
		user.setPhone1(phone);
		user.setEmail(mail);
		user.setPassword(password);
		user.setRepeatPassword(repeatePassword);
		user.willBeSaved = true;
		/* Validate object before saving */
		if (!validateForm(user)) {
			responseQuickAccount = "Favor, preencha todos os campos corretamente!";
			statusQuickAccount = "ERROR";
			render("includes/newQuickAccount.html", user, responseQuickAccount, statusQuickAccount);
		} else if (user != null && !validatePassword(user.getPassword(), user.getRepeatPassword())) {
			params.flash();
			validation.keep();
			statusQuickAccount = "ERROR";
			responseQuickAccount = "As senhas que você digitou não são iguais. :(";
			render("includes/newQuickAccount.html", user, responseQuickAccount, statusQuickAccount);
		} else {
			user.setPassword(Utils.encode(user.password));
			user.setAdmin(true);
			user.setPostedAt(Utils.getCurrentDateTime());
			user.setInstitutionId(0l);
			user.setActive(true);
			user.setFromMonetizze(false);
			user.merge();
			responseQuickAccount = "Ótimo. Seu cadastro foi criado com sucesso. :)";
			statusQuickAccount = "SUCCESS";
			user = new User();
			render("includes/newQuickAccount.html", user, responseQuickAccount, statusQuickAccount);
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
		validation.email(user.getEmail()).message("Favor, insira o seu e-mail no formato nome@provedor.com.br.").key("user.email");
		validation.isTrue(User.verifyByEmail(user.getEmail()) == null).message("Já existe um usuário com este e-mail.").key("user.email");
		validation.required(user.getPassword()).message("Favor, insira uma senha.").key("user.password");
		validation.required(user.getRepeatPassword()).message("Favor, digite novamente a senha.").key("confirmPassword");
		validation.isTrue(validatePassword(user.getPassword(), user.getRepeatPassword())).message("As senhas digitadas devem ser iguais.").key("confirmPassword");
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
				User connectedUser = userInstitutionParameter.getUser();
				Admin.globals();
				Parameter parameter = Parameter.all().first();
				render("@Admin.firstStep", institution, connectedUser, parameter);
				return;
			} else {
				// Links the user to institution
				institution.setUserId(userInstitutionParameter.getUser().getId());
				institution.setPublishedId(userInstitutionParameter.getUser().getId());
				institution.setPostedAt(Utils.getCurrentDateTime());
				// Grants one month free to user
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, 1);
				institution.setLicenseDate(calendar.getTime());
				institution.save();
				// Links the institution to user
				User user = User.verifyByEmail(userInstitutionParameter.getUser().getEmail());
				user.setInstitutionId(institution.getId());
				user.save();
				userInstitutionParameter.setUser(user);
				userInstitutionParameter.setInstitution(institution);
				flash.clear();
				validation.errors().clear();
				flash.success("Instituição '" + institution.getInstitution() + "' criada com sucesso. Aproveite!", "");
				Security.setCurrentSessionParameters(userInstitutionParameter.getUser());
				Admin.index();
				Admin.sendMailToMe(userInstitutionParameter, "Mais uma empresa cadastrada!");
			}
		}
		render("@Admin.firstStep");
	}

	private static boolean validateForm(Institution institution) {
		boolean ret = false;
		validation.required(institution.getInstitution()).message("Favor, insira o nome da Instituição.").key("institution.institution");
		validation.required(institution.getEmail()).message("Favor, insira o e-mail.").key("institution.email");
		validation.email(institution.getEmail()).message("Favor, insira o e-mail no formato nome@provedor.com.br.").key("institution.email");
		validation.required(institution.getLogo()).message("Favor, insira a logomarca.").key("institution.logo");
		validation.isTrue(Institution.verifyByEmail(institution.getEmail()) == null).message("Já existe uma instituição com este e-mail.").key("institution.email");
		// if (!Utils.isNullOrEmpty(institution.getCnpj())) {
		// validation.isTrue(Utils.validateCPFOrCNPJ(institution.getCnpj())).message("CNPJ
		// inválido.")
		// .key("institution.cnpj");
		// validation.isTrue(Institution.verifyByCnpj(institution.getCnpj()) ==
		// null)
		// .message("Já existe uma Instituição com este
		// CNPJ.").key("institution.cnpj");
		// }
		validation.required(institution.getAddress()).message("Favor, digite o endereço.").key("institution.address");
		validation.required(institution.getNeighborhood()).message("Favor, informe o bairro.").key("institution.neighborhood");
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
				List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + orderOfService.getId()).fetch();
				for (OrderOfServiceValue orderOfServiceValue : orderOfServiceValues) {
					Service service = Service.find("id = " + orderOfServiceValue.getService().getId()).first();
					services.add(service);
				}
				Map<Service, List<OrderOfServiceStep>> mapOrderServiceSteps = new HashMap<Service, List<OrderOfServiceStep>>();
				for (Service service : services) {
					List<OrderOfServiceStep> orderOfServiceStep = OrderOfServiceStep.find("service_id = " + service.getId() + " and orderOfService_id = " + orderOfService.getId() + " and isActive = true").fetch();
					if (mapOrderServiceSteps.containsKey(service)) {
						service.setAux("new");
					}
					mapOrderServiceSteps.put(service, orderOfServiceStep);
				}
				render(clientName, company, orderOfService, mapOrderServiceSteps);
			}
		}
	}

	public static void modalPass() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String value = params.get("value", String.class);
		String email = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		if (!Utils.isNullOrEmpty(email) && Utils.validateEmail(email) && User.verifyByEmail(email) != null) {
			User user = User.verifyByEmail(email);
			MailController mailController = new MailController();
			/* SendTo object */
			SendTo sendTo = new SendTo();
			sendTo.setDestination(email);
			sendTo.setName(user.getName());
			sendTo.setSex("");
			sendTo.setStatus(new StatusMail());
			/* Sender object */
			Sender sender = new Sender();
			sender.setCompany("Seu Pedido Online");
			sender.setFrom("contato@seupedido.online");
			sender.setKey("resetpass");
			/* SendTo object */
			BodyMail bodyMail = new BodyMail();
			bodyMail.setTitle1("Oi, " + user.getName() + "! Veja abaixo:");
			bodyMail.setTitle2("Seu Pedido Online - Nova senha");
			bodyMail.setFooter1("http://seupedido.online/nova-senha/" + Utils.encode(user.getEmail()));
			bodyMail.setImage1("http://seupedido.online/public/images/logo-admin.png");
			bodyMail.setBodyHTML(MailController.getHTMLTemplateResetPass(bodyMail));
			if (mailController.sendHTMLMail(sendTo, sender, bodyMail, null)) {
				status = "SUCCESS";
				response = "E-mail enviado com sucesso! Favor, verifique sua caixa de entrada, spam ou lixeira.";
			} else {
				status = "ERROR";
				response = "Houve um problema ao enviar. :(";
			}
		} else {
			status = "ERROR";
			response = "E-mail não encontrado ou digitado incorretamente! :(";
		}
		render(response, status);
	}

	public static void newPass(String id) throws Throwable {
		String mail = Utils.decode(id);
		User user = User.verifyByEmail(mail);
		if (user == null) {
			Application.index();
		} else {
			render(user);
		}
	}

	public static void confirmPass() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String body = params.get("body", String.class);
		String[] params = body.split("&");
		String pass1 = Utils.getValueFromUrlParam(params[0]);
		String pass2 = Utils.getValueFromUrlParam(params[1]);
		String ref = Utils.getValueFromUrlParam(params[2]);
		User user = User.verifyByEmail(Utils.decode(Utils.decodeUrl(ref)));
		if (user == null) {
			status = "ERROR";
			response = "Houve um problema. :(";
			render("Application/newPass.html", response, status, user);
		}
		if (validatePassword(pass1, pass2)) {
			user.setPassword(Utils.encode(pass1));
			user.save();
			status = "SUCCESS";
			response = "Nova senha criada com sucesso. Estamos voltando para a página de login. Ok?";
		} else {
			status = "ERROR";
			response = "As senhas não são iguais. :(";
		}
		render("Application/newPass.html", response, status, user);
	}

	public static void saveMessage(String json) throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		/* Get body content from client request */
		String[] fields = request.params.data.get("body");
		String decodedFields = URLDecoder.decode(fields[0], "UTF-8");
		Gson gson = new GsonBuilder().create();
		/* Parse form content to JSON element */
		String jsonParam = Utils.transformQueryParamToJson(decodedFields, "message.");
		JsonParser parser = new JsonParser();
		JsonObject jsonElement = (JsonObject) parser.parse(jsonParam);
		jsonElement.addProperty("id", Long.valueOf(0));
		/* Save Client */
		/* Create object parsing JSON element */
		Message message = new Message();
		message = gson.fromJson(jsonElement, Message.class);
		message.id = 0l;
		message.willBeSaved = true;
		/* Validate object before saving */
		if (!validateObjectToSave(message)) {
			List<Error> errors = validation.errors();
			response = "Favor, preencha todos os campos corretamente!";
			status = "ERROR";
			render("includes/newQuickContact.html", message, response, status, errors);
		} else {
			message.setPostedAt(Utils.getCurrentDateTime());
			message.setInstitutionId(0l);
			message.merge();
			response = "Muito obrigado pela mensagem!";
			status = "SUCCESS";
			render("includes/newQuickContact.html", message, response, status);
		}
	}

	private static boolean validateObjectToSave(Message message) {
		validation.clear();
		validation.valid(message);
		validation.email(message.getMail()).message("Favor, insira o seu e-mail no formato nome@provedor.com.br.").key("message.mail");
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

	public static void followAdmin() {
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		render(theSystem);
	}

	public static void saveMailList() throws UnsupportedEncodingException {
		String resp = null;
		String status = null;
		String body = params.get("body", String.class);
		String decodedParams = URLDecoder.decode(body, "UTF-8");
		String[] params = decodedParams.split("&");
		String name = Utils.getValueFromUrlParam(params[0]);
		String mail = Utils.getValueFromUrlParam(params[1]);
		String origin = Utils.getValueFromUrlParam(params[2]);
		String url = Utils.getValueFromUrlParam(params[3]);
		MailList mailList = new MailList();
		mailList.id = 0l;
		if (Utils.isNullOrEmpty(name)) {
			mailList.setName("");
		} else {
			mailList.setName(name);
		}
		mailList.setName(name);
		mailList.setMail(mail);
		mailList.origin = FromEnum.getNameByValue(origin);
		mailList.setUrl(url);
		/* Making validations */
		validation.clear();
		validation.valid(mailList);
		validation.email(mailList.getMail()).message("Favor, insira o seu e-mail no formato nome@provedor.com.br.").key("mailList.mail1");
		if (validation.hasErrors()) {
			status = "ERROR";
			resp = "Favor, insira o seu e-mail no formato nome@provedor.com.br.";
		} else {
			status = "SUCCESS";
			resp = "E-mail incluído com sucesso. Gratidão.";
			mailList.setPostedAt(Utils.getCurrentDateTime());
			mailList.merge();
		}

		/* Render page based on origin */
		switch (FromEnum.getNameByValue(origin)) {
		case HomePageTop:
			render("includes/formNewsletterTop.html", status, resp);
			break;
		case HomePageBottom:
			render("includes/formNewsletterBottom.html", status, resp);
			break;
		case NewsPage:
			render("includes/formNewsletterTips.html", status, resp);
			break;
		case CapturePageTop:
			render("includes/formCapturePageTop.html", status, resp);
			break;
		case CapturePageBottom:
			render("includes/formCapturePageBottom.html", status, resp);
			break;
		}
	}

	public static void thankLead() {
		Parameter parameter = Parameter.all().first();
		List<Article> listArticles = Article.find("isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		render(bottomNews, parameter, theSystem);
	}

}