package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

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
import play.libs.URLs;
import play.mvc.Controller;

public class Utils extends Controller {
	public static final String STR_DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
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

	public static boolean isNullOrEmpty(List list) {
		if (list == null || list.isEmpty()) {
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

	public static Date getSimpleCurrentDateTime() {
		Calendar cal = getBrazilCalendar();
		return cal.getTime();
	}

	public static String getStringDateTime(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(STR_DEFAULT_DATE_FORMAT);
		Calendar c = getBrazilCalendar();
		c.setTime(date);
		return dateFormat.format(c.getTime());
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
			parsedBody = parsedBody.concat("chalkBoardChildren.").concat(key).concat("=").concat(Utils.isNullOrEmpty(value.getAsString()) ? "" : new String(value.getAsString()).replace(" ", "+"));
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

	public static String stringToUrl(String str) {
		return Utils.removeAccent(str).replace(" ", "-").replaceAll("[^a-zA-Z0-9&-]", "").toLowerCase();
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

	public static boolean validateCompanySession(String id) {
		Institution institution = Institution.findById(Long.valueOf(id).longValue());
		Institution loggedInstitution = Admin.getLoggedUserInstitution().getInstitution();
		if (institution != null && institution.id.equals(loggedInstitution.id)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean validateUserSession(String id) {
		User user = User.findById(Long.valueOf(id).longValue());
		User loggedUser = Admin.getLoggedUserInstitution().getUser();
		if (user != null && user.id.equals(loggedUser.id)) {
			return true;
		} else {
			return false;
		}
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = getBrazilCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static void mainss(String[] args) {
		System.out.println(new Date());
		System.out.println(addDays(new Date(), 30));
	}

	public static String transformQueryParamToJson(String queryParam, String prefix) {
		StringTokenizer st = new StringTokenizer(queryParam, "&");
		String json = "{";
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			String replaceKey = str.replace(prefix, "");
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

	public static String removeHTML(String str) {
		str = str.replaceAll("\\<[^>]*>", "");
		return str;
	}

	public static String dateNow() {
		Calendar currentDate = getBrazilCalendar();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	public static String parseStringDate(String strDate) throws ParseException {
		Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(strDate);
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
	}

	public static String parseStringDateTime(String strDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date d = sdf.parse(strDate);
		return formatDate(d);
	}

	public static String decode(String s) {
		return StringUtils.newStringUtf8(Base64.decodeBase64(s));
	}

	public static void mainssss(String[] args) throws UnsupportedEncodingException {
		String val1 = String.valueOf(Utils.encode(decodeUrl("teste654321")));
		String val2 = Utils.encode("teste654321");
		System.out.println(val1.equals(val2));
		System.out.println(val1.equalsIgnoreCase(val2));
	}

	public static String encode(String s) {
		return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
	}

	public static String getValueFromUrlParam(String param) {
		if (!isNullOrEmpty(param) && param.contains("=")) {
			return param.split("=",-1)[1];
		}
		return "";
	}

	public static String getValueParamByKey(String[] params, String key) {
		for (int i = 0; i < params.length; i++) {
			if (key.equals(params[i].split("=")[0])) {
				return getValueFromUrlParam(params[i]);
			}
		}
		return null;
	}

	public static String decodeUrl(String url) throws UnsupportedEncodingException {
		return URLDecoder.decode(url, "UTF-8");
	}

	public static String getShortenUrl(String url) {
		return UrlShortener.shorten(url);
	}

	public static String normalizeString(String str) {
		return str.replace("%", "%%");
	}

	public static String getFirstDayMonthDate() {
		Calendar calendar = getBrazilCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String getFirstDay = getStringDateTime(calendar.getTime());
		return getFirstDay;
	}

	public static String getLastDayMonthDate() {
		Calendar calendar = getBrazilCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String getLastDay = getStringDateTime(calendar.getTime());
		return getLastDay;
	}

	public static String getNameByWholeName(String name) {
		if (!isNullOrEmpty(name)) {
			name = name.substring(0, name.indexOf(" ") > -1 ? name.indexOf(" ") : name.length());
			return name;
		}
		return "";
	}

	public static String getLastNameByWholeName(String name) {
		if (!isNullOrEmpty(name)) {
			name = name.substring(name.indexOf(" ") > -1 ? name.indexOf(" ") + 1 : name.length());
			return name;
		}
		return "";
	}
	
	public static void main(String[] args) {
		System.out.println(getNameByWholeName("Lucas Correia Evangelista"));
		System.out.println(getLastNameByWholeName("Lucas Correia Evangelista"));
	}
}
