package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.Admin;
import models.Institution;
import models.Service;
import models.User;
import play.mvc.Controller;

public class Utils extends Controller {
	public static final String STR_DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
	public static final String STR_BRAZIL_TIMEZONE = "America/Sao_Paulo";

	public static String formatToMoney(double price) {
		NumberFormat formatter = new DecimalFormat("R$ #0.00");
		return formatter.format(price);
	}

	public static String formatDate(Date postedAt) {
		String format = "dd/MM/yyyy HH'h'mm";
		SimpleDateFormat formatas = new SimpleDateFormat(format);
		String formattedDate = formatas.format(postedAt);
		return formattedDate;
	}

	public static String formatDateSimple(Date postedAt) {
		String format = "dd/MM/yyyy";
		SimpleDateFormat formatas = new SimpleDateFormat(format);
		String formattedDate = formatas.format(postedAt);
		return formattedDate;
	}

	public static boolean validateEmail(String email) {
		validation.email(email);
		if (!validation.hasErrors()) {
			return true;
		}
		return false;
	}

	public static String replaceSpace(String name) {
		return name.replace(" ", "-");
	}

	public static boolean isNullOrEmpty(String text) {
		if (text == null || text.equals(" ") || text.equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isNullOrEmpty(Object text) {
		if (text == null || text.equals(" ") || text.equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isNullOrZero(Long text) {
		if (text == null || text == 0) {
			return true;
		}
		return false;
	}

	public static String replaceBoolean(boolean value) {
		if (value == true)
			return "Sim";
		else
			return "Não";

	}

	public static String timeHourNow() {
		return new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
	}

	public static String splitSpacesAndLimitSubstring(String str, int limit) {
		String f[] = str.split(" ");
		String finalStr = "";
		for (String string : f) {
			if (!Utils.isNullOrEmpty(string)) {
				finalStr = finalStr + string;
			}
		}
		return finalStr.substring(0, limit);
	}

	public static String split(String regex, String str) {
		String f[] = str.split(regex);
		String finalStr = "";
		for (String string : f) {
			if (!Utils.isNullOrEmpty(string)) {
				finalStr = finalStr + string;
			}
		}
		return finalStr.trim();
	}

	public static String substringText(String text, int limitMinimum, int limitMaximum) {
		String string = null;
		if (!isNullOrEmpty(text) && text.length() > limitMaximum) {
			string = text.substring(limitMinimum, limitMaximum - 3);
			return string + "...";
		}
		return text;
	}

	public static Date parseDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	public static String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat(STR_DEFAULT_DATE_FORMAT);
		Calendar cal = getBrazilCalendar();
		return dateFormat.format(cal.getTime());
	}

	public static String getCurrentDateTimeByFormat(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = getBrazilCalendar();
		return dateFormat.format(cal.getTime());
	}

	public static void mains(String[] args) {
		int randomNum = 0;
		randomNum = 1 + (int) (Math.random() * 1000);
		System.out.println(randomNum);
	}

	public static Calendar getBrazilCalendar() {
		TimeZone tz = TimeZone.getTimeZone(STR_BRAZIL_TIMEZONE);
		TimeZone.setDefault(tz);
		Calendar calendar = GregorianCalendar.getInstance(tz);
		return calendar;
	}

	public static String randomKey() {
		return UUID.randomUUID().toString();
	}

	public static int generateRandomId() {
		Random r = new Random(System.currentTimeMillis());
		return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
	}

	public static String getJsonFileContent(File jsonFile) {
		try {
			String jsonContent = "";
			InputStream is = new FileInputStream(jsonFile);
			String UTF8 = "utf8";
			int BUFFER_SIZE = 8192;

			BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF8), BUFFER_SIZE);
			String str;

			while ((str = br.readLine()) != null) {
				jsonContent += str;
			}
			return jsonContent;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static JsonObject getJsonObject(String jsonContent, String objectName) {
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonContent).getAsJsonObject();
		JsonObject jsonObject = (JsonObject) obj.get(objectName);
		return jsonObject;
	}

	public static JsonArray getJsonArray(String jsonContent, String arrayName) {
		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(jsonContent).getAsJsonObject();
		JsonArray jsonArray = (JsonArray) obj.get(arrayName);
		return jsonArray;
	}

	private static String[] parseMapBody(String[] fields) {
		JsonParser parser = new JsonParser();
		JsonObject object = (JsonObject) parser.parse(fields[0]);
		Set<Map.Entry<String, JsonElement>> set = object.entrySet();
		int i = 0;
		String parsedBody = "";
		for (Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator(); iterator.hasNext(); i++) {
			Map.Entry<String, JsonElement> entry = iterator.next();
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			parsedBody = parsedBody.concat("chalkBoardChildren.").concat(key).concat("=").concat(
					Utils.isNullOrEmpty(value.getAsString()) ? "" : new String(value.getAsString()).replace(" ", "+"));
			if (i < (set.size() + 1)) {
				parsedBody = parsedBody.concat("&");
			}
		}
		String[] contentMap = new String[1];
		contentMap[0] = parsedBody;
		return contentMap;
	}

	private static Map<String, Object> parse(String json) {
		JsonParser parser = new JsonParser();
		JsonObject object = (JsonObject) parser.parse(json);
		Set<Map.Entry<String, JsonElement>> set = object.entrySet();
		Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
		Map<String, Object> map = new HashMap<String, Object>();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = iterator.next();
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if (!value.isJsonPrimitive()) {
				String v = new String();
				v = value.toString();
				if (Utils.isNullOrEmpty(v)) {
					v = new String();
				}
				map.put("chalkBoardChildren.".concat(key), parse(v));
			} else {
				map.put("chalkBoardChildren.".concat(key), new String(value.getAsString()));
			}
		}
		return map;
	}

	private static boolean validateForm(Service chalkBoardChildren) {
		boolean retorno = false;
		validation.valid(chalkBoardChildren);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			render("includes/formchildren.html", chalkBoardChildren);
			retorno = false;
		} else {
			retorno = true;
		}
		return retorno;
	}

	public static void saveChalkBoardChildren(String json) {
		String[] fields = request.params.data.get("body");
		String[] parsedBody = parseMapBody(fields);
		request.params.data.remove("body");
		request.params.data.put("body", parsedBody);
		Gson gson = new GsonBuilder().create();
		Service chalkBoardChildren = gson.fromJson(fields[0], Service.class);
		if (validateForm(chalkBoardChildren)) {
			render();
		}
	}

	public static boolean validateCPFOrCNPJ(String text) {
		if (Utils.isNullOrEmpty(text)) {
			return false;
		}
		String str = text.trim();
		str = str.replace(".", "");
		str = str.replace("-", "");
		str = str.replace("/", "");
		if (str.length() == 11) {
			if (CPFCNPJ.isValidCPF(str))
				return true;
		} else if (str.length() == 14) {
			if (CPFCNPJ.isValidCNPJ(str))
				return true;
		}
		return false;
	}

	public static String removeAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static String getCurrencyValue(Float value) {
		DecimalFormat format = new DecimalFormat("##,###,###,##0.00");
		format.setMinimumFractionDigits(2);
		format.setParseBigDecimal(true);
		return format.format(value);
	}

	public static String replacePhoneNumberCaracteres(String text) {
		String str = text.trim();
		str = str.replace(" ", "");
		str = str.replace("  ", "");
		str = str.replace("(", "");
		str = str.replace(")", "");
		str = str.replace("-", "");
		return str;
	}

	public static void main(String[] args) {
		System.out.println(removeAccent("João,"));
	}

	public static boolean validateCompanySession(String id) {
		Institution institution = Institution.findById(Long.valueOf(id).longValue());
		Institution loggedInstitution = Admin.getLoggedUserInstitution().getInstitution();
		if (institution != null) {
			if (institution.id == loggedInstitution.id) {
				return true;
			}
		}
		return false;
	}
	public static boolean validateUserSession(String id) {
		User user = User.findById(Long.valueOf(id).longValue());
		User loggedUser = Admin.getLoggedUserInstitution().getUser();
		if (user != null) {
			if (user.id == loggedUser.id) {
				return true;
			}
		}
		return false;
	}

}
