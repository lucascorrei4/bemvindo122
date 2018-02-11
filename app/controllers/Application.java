package controllers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.CRUD.ObjectType;
import models.Article;
import models.BodyMail;
import models.Client;
import models.FreePage;
import models.HighlightProduct;
import models.Institution;
import models.LeadSearchAnswer;
import models.LeadSearchQuestion;
import models.MailList;
import models.Message;
import models.MoipNotification;
import models.OrderOfService;
import models.OrderOfServiceStep;
import models.OrderOfServiceValue;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.SequenceMailQueue;
import models.Service;
import models.StatusMail;
import models.Step;
import models.TheSystem;
import models.User;
import play.data.binding.Binder;
import play.data.validation.Error;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.vfs.VirtualFile;
import util.FromEnum;
import util.MailTemplates;
import util.ServiceOrderOfServiceSteps;
import util.UserInstitutionParameter;
import util.Utils;

public class Application extends Controller {

	public static void index() {
		List<Article> listArticles = Article.find("isActive = true order by postedAt desc").fetch(4);
		List<Article> listArticles12 = listArticles.subList(0, 2);
		List<Article> listArticles34 = listArticles.subList(2, listArticles.size());
		List<TheSystem> listTheSystems = TheSystem.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		Parameter parameter = Parameter.all().first();
		render(listTheSystems, listArticles12, listArticles34, parameter);
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

	public static void follow(String cod) throws UnsupportedEncodingException, InterruptedException {
		String response = null;
		String status = null;
		String clientName = null;
		boolean restrictedAccess = false;
		boolean codeNotFound = false;
		OrderOfServiceValue orderOfServiceValue = null;
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(2, listArticles.size());
		Parameter parameter = getCurrentParameter();
		if (!Utils.isNullOrEmpty(cod)) {
			OrderOfService orderOfService = OrderOfService.find("orderCode", cod).first();
			if (orderOfService == null) {
				orderOfServiceValue = OrderOfServiceValue.find("orderCode", cod).first();
				if (orderOfServiceValue != null) {
					restrictedAccess = true;
				}
			}
			/* Validate object before saving */
			if (orderOfService == null && orderOfServiceValue == null) {
				List<Error> errors = validation.errors();
				response = "Código não encontrado! :(";
				status = "ERROR";
				codeNotFound = true;
				render(codeNotFound, response, status, errors, bottomNews, parameter);
			} else {
				response = "Redirecionando... :)";
				status = "SUCCESS";
				if (orderOfService == null) {
					orderOfService = OrderOfService.findById(orderOfServiceValue.getOrderOfServiceId());
					orderOfService.setOrderCode(orderOfServiceValue.getOrderCode());
				}
				clientName = orderOfService.getClient().getName();
				Institution institution = Institution.find("id", orderOfService.institutionId).first();
				String company = institution.getInstitution();
				List<ServiceOrderOfServiceSteps> serviceOrderOfServiceSteps = new ArrayList<ServiceOrderOfServiceSteps>();
				ServiceOrderOfServiceSteps serviceOrderOfServiceStep = null;
				if (!restrictedAccess) {
					List<OrderOfServiceValue> orderOfServiceValues = OrderOfServiceValue.find("orderOfServiceId = " + orderOfService.getId()).fetch();
					for (OrderOfServiceValue orderServValue : orderOfServiceValues) {
						configureOrderOfServiceSteps(orderOfService, orderServValue, serviceOrderOfServiceSteps, serviceOrderOfServiceStep);
					}
					updateServicesReferences(orderOfService);
				} else {
					configureOrderOfServiceSteps(orderOfService, orderOfServiceValue, serviceOrderOfServiceSteps, serviceOrderOfServiceStep);
					updateServicesReferences(orderOfService);
				}
				render(clientName, company, orderOfService, serviceOrderOfServiceSteps, bottomNews, parameter);
			}
		} else if (cod == "") {
			List<Error> errors = validation.errors();
			response = "Favor, insira um código válido.";
			status = "ERROR";
			codeNotFound = true;
			render(codeNotFound, response, status, errors, bottomNews, parameter);
		} else {
			render(clientName, bottomNews, parameter);
		}

	}

	private static void configureOrderOfServiceSteps(OrderOfService orderOfService, OrderOfServiceValue orderServValue, List<ServiceOrderOfServiceSteps> serviceOrderOfServiceSteps, ServiceOrderOfServiceSteps serviceOrderOfServiceStep) {
		List<OrderOfServiceStep> orderOfServiceStep = OrderOfServiceStep.find("service_id = " + orderServValue.getService().getId() + " and orderOfService_id = " + orderOfService.getId() + " and isActive = true order by id asc").fetch();
		List<OrderOfServiceStep> orderOfServiceStepNew = new ArrayList<OrderOfServiceStep>();
		List<Step> stepsOfService = Step.find("service_id = " + orderServValue.getService().getId() + " and isActive = true").fetch();
		int qtdStepOfCurrentService = stepsOfService.size();
		if (orderOfServiceStep.size() > stepsOfService.size()) {
			for (OrderOfServiceStep orderSteps : orderOfServiceStep) {
				if (serviceOrderOfServiceSteps.isEmpty() || stepsHadBeenNotInsertedInList(orderSteps, serviceOrderOfServiceSteps)) {
					orderOfServiceStepNew.add(orderSteps);
				}
				if (orderOfServiceStepNew.size() == qtdStepOfCurrentService) {
					serviceOrderOfServiceStep = new ServiceOrderOfServiceSteps();
					serviceOrderOfServiceStep.setService(orderServValue.getService());
					serviceOrderOfServiceStep.setOrderOfServiceSteps(orderOfServiceStepNew);
					serviceOrderOfServiceSteps.add(serviceOrderOfServiceStep);
					orderOfServiceStepNew = new ArrayList<OrderOfServiceStep>();
				}
			}
		} else {
			serviceOrderOfServiceStep = new ServiceOrderOfServiceSteps();
			serviceOrderOfServiceStep.setService(orderServValue.getService());
			serviceOrderOfServiceStep.setOrderOfServiceSteps(orderOfServiceStep);
			serviceOrderOfServiceSteps.add(serviceOrderOfServiceStep);
		}
	}

	private static void updateServicesReferences(OrderOfService orderOfService) {
		for (ServiceOrderOfServiceSteps serviceOrderOfServiceSteps : orderOfService.getServiceOrderOfServiceSteps()) {
			if (!Utils.isNullOrEmpty(serviceOrderOfServiceSteps.getOrderOfServiceSteps()) && serviceOrderOfServiceSteps.getOrderOfServiceSteps().iterator().next() != null) {
				String reference = serviceOrderOfServiceSteps.getOrderOfServiceSteps().iterator().next().getReference();
				serviceOrderOfServiceSteps.setReference(Utils.isNullOrEmpty(reference) ? "Não referenciado." : reference);
			} else {
				serviceOrderOfServiceSteps.setReference("Não referenciado.");
			}
		}
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
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(2, listArticles.size());
		render(theSystem, bottomNews);
	}

	public static void thankLead() {
		Parameter parameter = Parameter.all().first();
		List<Article> listArticles = Article.find("isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		render(bottomNews, parameter, theSystem);
	}

	public static void getImageHighlightProduct(long id) {
		final HighlightProduct hightlightProduct = HighlightProduct.findById(id);
		notFoundIfNull(hightlightProduct);
		if (hightlightProduct.getImage() != null) {
			renderBinary(hightlightProduct.getImage().get());
			return;
		}
	}

	public static void getLogo() {
		Parameter parameter = getCurrentParameter();
		if (parameter.getLogo().exists()) {
			renderBinary(parameter.getLogo().get(), "img-logo-" + parameter.id, "image/png", true);
			return;
		}
	}

	public static void getIcon() {
		Parameter parameter = getCurrentParameter();
		if (parameter.getIcon().exists()) {
			renderBinary(parameter.getIcon().get());
			return;
		}
	}

	public static void getHomeBackgroundImage() {
		Parameter parameter = getCurrentParameter();
		if (parameter.getHomeBackgroundImage().exists()) {
			renderBinary(parameter.getHomeBackgroundImage().get());
			return;
		}
	}

	/* Pixel tracking e-mail */
	public static void hrpx(long id) {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/hr-line-gray.png");
		File f = vf.getRealFile();
		SequenceMailQueue seqMail = SequenceMailQueue.findById(id);
		seqMail.setMailRead(true);
		seqMail.save();
		renderBinary(f);
	}

	public static void contact() {
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		Parameter parameter = getCurrentParameter();
		render(theSystem, bottomNews, parameter);
	}

	public static void about() {
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		Parameter parameter = getCurrentParameter();
		render(theSystem, bottomNews, parameter);
	}

	public static void privacyPolicy() {
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		Parameter parameter = getCurrentParameter();
		render(theSystem, bottomNews, parameter);
	}

	public static void termsConditions() {
		TheSystem theSystem = new TheSystem();
		theSystem.setShowTopMenu(true);
		List<Article> listArticles = Article.find("highlight = false and isActive = true order by postedAt desc").fetch(6);
		List<Article> bottomNews = listArticles.subList(0, 3);
		Parameter parameter = getCurrentParameter();
		render(theSystem, bottomNews, parameter);
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

		String partOf = null;
		String pageParameter = null;
		FreePage freePage = null;
		String redirectTo = null;
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
		case NewsletterFreePage:
			partOf = url.substring(url.indexOf("fp/"));
			pageParameter = partOf.split("/")[1];
			freePage = FreePage.findByFriendlyUrl(pageParameter);
			redirectTo = freePage.getRedirectTo();
			render("includes/formNewsLetterFreePage.html", status, resp, freePage, redirectTo);
			break;
		case NewsletterFreePageBootstrap:
			partOf = url.substring(url.indexOf("fp/"));
			pageParameter = partOf.split("/")[1];
			freePage = FreePage.findByFriendlyUrl(pageParameter);
			redirectTo = freePage.getRedirectTo();
			render("includes/formNewsLetterFreePageBootstrap.html", status, resp, freePage, redirectTo);
			break;
		}
	}

	private static Parameter getCurrentParameter() {
		return Parameter.all().first();
	}

	public static void unsubscribe(String mailLead) {
		Parameter parameter = getCurrentParameter();
		String mail = Utils.decode(mailLead);
		List<MailList> listMail = MailList.find("mail = '" + mail + "'").fetch();
		String lead = listMail.iterator().next().getName();
		for (MailList mL : listMail) {
			mL.setActive(false);
			mL.save();
		}
		render(lead, parameter);
	}

	public static void modalPass() throws UnsupportedEncodingException {
		String response = null;
		String status = null;
		String value = params.get("value", String.class);
		String email = Utils.removeAccent(URLDecoder.decode(value, "UTF-8"));
		if (!Utils.isNullOrEmpty(email) && Utils.validateEmail(email) && User.verifyByEmail(email) != null) {
			Parameter parameter = getCurrentParameter();
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
			sender.setCompany(Utils.isNullOrEmpty(parameter.getMailSenderName()) ? parameter.getSiteTitle() : parameter.getMailSenderName());
			sender.setFrom(Utils.isNullOrEmpty(parameter.getMailSenderFrom()) ? parameter.getSiteMail() : parameter.getMailSenderFrom());
			sender.setKey("resetpass");
			/* SendTo object */
			BodyMail bodyMail = new BodyMail();
			bodyMail.setTitle1("Oi, " + user.getName() + "! Veja abaixo:");
			bodyMail.setTitle2(parameter.getSiteTitle() + " - Nova senha");
			bodyMail.setFooter1(parameter.getSiteDomain() + "/nova-senha/" + Utils.encode(user.getEmail()));
			bodyMail.setImage1(parameter.getSiteDomain() + "/logo");
			bodyMail.setBodyHTML(MailTemplates.getHTMLTemplateResetPass(bodyMail, parameter));
			if (mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter)) {
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
		Parameter parameter = getCurrentParameter();
		String mail = Utils.decode(id);
		User user = User.verifyByEmail(mail);
		if (user == null) {
			Application.index();
		} else {
			render(user, parameter);
		}
	}

	public static void confirmPass() throws UnsupportedEncodingException {
		Parameter parameter = getCurrentParameter();
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
			render("Application/newPass.html", response, status, user, parameter);
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
		render("Application/newPass.html", response, status, user, parameter);
	}

	public static void leadSearch(String campaing, String lead) {
		boolean pageNotAvailable = true;
		Parameter parameter = getCurrentParameter();
		if (Utils.isNullOrEmpty(campaing)) {
			render(pageNotAvailable, parameter);
		} else if (Utils.isNullOrEmpty(lead)) {
			render(pageNotAvailable, parameter);
		} else {
			pageNotAvailable = false;
			LeadSearchQuestion question = LeadSearchQuestion.find("campaing = '" + campaing + "'").first();
			String leadDecoded = Utils.decode(lead);
			MailList mailList = MailList.verifyByEmail(leadDecoded);
			render(pageNotAvailable, parameter, question, mailList);
		}
	}
	
	public static void sendAnswer() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Parameter parameter = getCurrentParameter();
		ObjectType type = ObjectType.get(LeadSearchAnswerCRUD.class);
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		LeadSearchAnswer leadSearchAnswer = new LeadSearchAnswer();
		leadSearchAnswer = (LeadSearchAnswer) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", leadSearchAnswer);
		String campaing = params.get("object.leadSearchQuestion");
		String message = null;
		boolean pageNotAvailable = false;
		MailList mailList = MailList.verifyByEmail(params.get("object.mailList"));
		LeadSearchQuestion question = (LeadSearchQuestion) LeadSearchQuestion.find("campaing = '" + campaing + "'").first();
		validateSearchFields(pageNotAvailable, parameter, question, mailList, message, leadSearchAnswer);
		LeadSearchAnswer answer = getAnswerByQuestionLeadInstitutionId(question.id, mailList.id,question.institutionId);
		if (answer == null) {
			leadSearchAnswer.id = 0l;
		} else {
			leadSearchAnswer.id = answer.id;
		}
		leadSearchAnswer.setLeadSearchQuestion(question);
		leadSearchAnswer.setInstitutionId(question.getInstitutionId());
		leadSearchAnswer.setMailList(mailList);
		leadSearchAnswer.setPostedAt(Utils.getCurrentDateTime());
		leadSearchAnswer.setActive(true);
		leadSearchAnswer.setTestimonial("");
		leadSearchAnswer.willBeSaved = true;
		leadSearchAnswer.merge();
		answer = getAnswerByQuestionLeadInstitutionId(question.id, mailList.id, question.institutionId);
		boolean showThanksMessage = false;
		render("@leadSearchThanks", question, answer, parameter, mailList, showThanksMessage);
	}
	
	private static LeadSearchAnswer getAnswerByQuestionLeadInstitutionId(Long questionId, Long leadId, Long institutionId) {
		return (LeadSearchAnswer) LeadSearchAnswer.find("question_id = " + questionId + " and lead_id = " + leadId + " and institutionId = " + institutionId).first();
	}
	
	private static void validateSearchFields(boolean pageNotAvailable, Parameter parameter, LeadSearchQuestion question, MailList mailList, String message, LeadSearchAnswer leadSearchAnswer) {
		boolean error = false;
		if (Utils.isNullOrEmpty(leadSearchAnswer.getBusiness())) {
			error = true;
			message = "Por favor, informe o negócio que você trabalha.";
 		} else if (Utils.isNullOrEmpty(leadSearchAnswer.getProfession())) {
 			error = true;
 			message = "Por favor, informe sua profissão.";
 		} else if (Utils.isNullOrEmpty(leadSearchAnswer.getAnswer1())) {
 			error = true;
 			message = "Por favor, responda a questão #2.";
 		} else if (Utils.isNullOrEmpty(leadSearchAnswer.getAnswer2())) {
 			error = true;
 			message = "Por favor, responda a questão #3.";
 		} else if (Utils.isNullOrEmpty(leadSearchAnswer.getAnswer3())) {
 			error = true;
 			message = "Por favor, responda a questão #4.";
 		} else if (Utils.isNullOrEmpty(leadSearchAnswer.getAnswer4())) {
 			error = true;
 			message = "Por favor, responda a questão #5.";
 		} else if (Utils.isNullOrEmpty(leadSearchAnswer.getAnswer5())) {
 			error = true;
 			message = "Por favor, responda a questão #6.";
 		}
		if (error) {
			render("@leadSearch", pageNotAvailable, parameter, question, mailList, message, leadSearchAnswer);
			return;
		}
	}

	public static void leadSearchThanks(LeadSearchQuestion question, LeadSearchAnswer answer) {
		render(question, answer);
	}

	public static void saveTestimonial() {
		Parameter parameter = getCurrentParameter();
		boolean showThanksMessage = true;
		String answer = params.get("ansid");
		String testimonial = params.get("object.testimonial");
		boolean authorize = params.get("object.isAuthorize", Boolean.class);
		LeadSearchAnswer leadSearchAnswer = LeadSearchAnswer.findById(Long.valueOf(answer));
		leadSearchAnswer.setTestimonial(testimonial);
		leadSearchAnswer.setAuthorize(authorize);
		leadSearchAnswer.save();
		render("@leadSearchThanks", showThanksMessage, parameter);
	}
	
	public static void clientEvaluation(long id, String cod, String message) {
		Parameter parameter = getCurrentParameter();
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + cod + "' and institutionId = " + id).first();
		if (orderOfService == null) {
			index();
		}
		Institution institution = Institution.findById(orderOfService.institutionId);
		String clientName = orderOfService.getClient().getName();
		render(orderOfService, parameter, clientName, institution);
	}
	
	public static void saveEvaluation() {
		Parameter parameter = getCurrentParameter();
		String orderCode = params.get("orderCode");
		Long institutionId =  params.get("instid", Long.class);
		int grade = params.get("optionsRadios", Integer.class);
		String evaluation = params.get("evaluation");
		OrderOfService orderOfService = OrderOfService.find("orderCode = '" + orderCode + "' and institutionId = " + institutionId).first();
		if (Utils.isNullOrEmpty(evaluation)) {
			Institution institution = Institution.findById(orderOfService.institutionId);
			String clientName = orderOfService.getClient().getName();
			String message = "Por favor, deixe um pequeno e sincero comentário.";
			render("@clientEvaluation", orderOfService, parameter, clientName, institution, message);
		}
		orderOfService.setEvaluated(true);
		orderOfService.setGrade(grade);
		orderOfService.setClientEvaluation(evaluation);
		orderOfService.save();
		render("@clientEvaluationThanks", parameter);
	}
}
