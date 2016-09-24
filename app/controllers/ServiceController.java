package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import models.Service;
import models.Client;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import util.Utils;

@CRUD.For(models.Service.class)
public class ServiceController extends CRUD {

	public static void saveChalkBoardChildren(String json) throws UnsupportedEncodingException {
		String error = null;
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
		Client client = saveClient(jsonElement);
		/* Create object parsing JSON element */
		Service chalkBoardChildren = new Service();
		chalkBoardChildren.setClientId(client.getId());
		chalkBoardChildren = gson.fromJson(jsonElement, Service.class);
		chalkBoardChildren.id = 0l;
		chalkBoardChildren.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
		chalkBoardChildren.willBeSaved = true;
		/* Validate object before saving */
		if (!validateObjectToSave(chalkBoardChildren)) {
			error = "Preencha os campos obrigatórios!";
			status = "ERROR";
			render("includes/formchildren.html", chalkBoardChildren, error, status);
		} else {
			/* model1 = girls ; model2 = boys */
			String modelType = chalkBoardChildren.getModel() == "model1" ? "FEM" : "MAS";
			chalkBoardChildren.setOrderCode("CCH".concat(modelType).concat(String.valueOf(Utils.generateRandomId())));
			chalkBoardChildren.merge();
			error = "Recebemos o seu pedido e estamos aguardando o pagamento para efetivação!";
			status = "SUCCESS";
			order = chalkBoardChildren.getOrderCode();
			render("includes/formchildren.html", chalkBoardChildren, error, status, order);
		}
	}
	
	private static Client saveClient(JsonObject jsonElement) {
		String mail = jsonElement.get("mail").getAsString();
		Client client = Client.find("byMail", mail).first();
		if (client == null) {
			client = new Client();
			client.setUserName(jsonElement.get("userName").getAsString());
			client.setMail(mail);
			client.setPhone(jsonElement.get("phone").getAsString());
			client.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
			client.willBeSaved = true;
			client.merge();
		} else {
			client.setUserName(jsonElement.get("userName").getAsString());
			client.setMail(mail);
			client.setPhone(jsonElement.get("phone").getAsString());
			client.setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
			client.willBeSaved = true;
			client.merge();
		}
		return Client.find("byMail", mail).first();
	}

	private static boolean validateObjectToSave(Service chalkBoardChildren) {
		validation.clear();
		validation.valid(chalkBoardChildren);
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
			String replaceKey = str.replace("chalkBoardChildren.", "");
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

	public static void list(int page, String search, String searchFields, String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order,
				(String) request.args.get("where"));
		Long count = type.count(search, searchFields, (String) request.args.get("where"));
		Long totalCount = type.count(null, null, (String) request.args.get("where"));
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
		}
	}
	
	public static void main(String[] args) {
		
	}

}
