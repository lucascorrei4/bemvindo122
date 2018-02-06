package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
import org.apache.commons.lang.StringEscapeUtils;

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
import play.vfs.VirtualFile;

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
			return param.split("=", -1)[1];
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

	public static String getShortenUrl(String shortenApiId, String url) {
		return UrlShortener.shorten(shortenApiId, url);
	}

	public static String normalizeString(String str) {
		str = str.replaceAll("%%", "%");
		str = str.replaceAll("&#37;&#37;", "&#37;");
		return str.replace("%", "&#37;");
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
		String mail = "<p><span style=\"font-size:18px\"><span style=\"font-family:Arial,Helvetica,sans-serif\">Como vai?&nbsp;Aqui &eacute; o Lucas Correia.<br /> <a href=\"https://acompanheseupedido.com/pv/sistema-para-pos-venda-online-permite-que-seus-clientes-acompanhem-os-pedidos-ou-servicos-em-andamento\" target=\"_blank\">Clique aqui para adquirir o sistema</a>. <br /> Lucas, voc&ecirc; chegou aqui atrav&eacute;s da p&aacute;gina que tinha um v&iacute;deo chamado&nbsp;<strong><em>Encantar o cliente traz retorno ($)</em></b><em>.</em><br /> Muito obrigado por ter deixado o seu email<em>.</em><br /> <br /> <b>Veja s&oacute; estes&nbsp;n&uacute;meros e curiosidades sobre atendimento ao cliente:</b><br /> <br /> -&nbsp;70% da experi&ecirc;ncia de compra de um cliente baseia-se na forma como ele &eacute; tratado, depois &eacute; considerado o produto e pre&ccedil;o. (McKinsey);<br /> <br /> - 12 experi&ecirc;ncias de atendimento positivas compensam apenas uma negativa. (Ruby Newell);<br /> <br /> -&nbsp;9 em cada 10 clientes n&atilde;o se importariam em pagar mais por um produto ou servi&ccedil;o se tivessem um atendimento melhor. (H. Interactive);<br /> <br /> - Segundo a SUSESP, o atendimento ao cliente baseia-se em 51% de reclama&ccedil;&otilde;es, 5% de an&aacute;lise de processo, 3% de consultas, 9% de informa&ccedil;&otilde;es gerais e 32% de outros assuntos;<br /> <br /> -&nbsp;64% dos consumidores estariam dispostos a compartilhar suas informa&ccedil;&otilde;es para receber orienta&ccedil;&otilde;es mais adequadas. (Amdocs);<br /> <br /> - 68% dos clientes recorrem a meios diferentes do telefone. (Amdocs);<br /> <br /> - 38 milh&otilde;es de clientes procuram atendimento pelo Twitter. (Zendesk).<br /> <br /> Poucas empresas tem a consci&ecirc;ncia de que o cliente deve ser tratado como uma majestade, em um tapete vermelho.<br /> <br /> N&atilde;o h&aacute; perda. Apenas ganhos. Ent&atilde;o...<br /> <br /> Eu te disse no v&iacute;deo que eu teria <strong>respostas</b> para as seguintes perguntas:<br /> <br /> &nbsp;- Como fazer <strong>p&oacute;s venda com 1 clique</b> sem a necessidade de fazer liga&ccedil;&otilde;es inoportunas?<br /> <br /> &nbsp;- Como <strong>ter em m&atilde;os</b> todos os clientes que j&aacute; passaram pelo seu neg&oacute;cio?<br /> <br /> &nbsp;- Como seus clientes podem <strong>acompanhar os pedidos ou servi&ccedil;os</b> em andamento?<br /> <br /> &nbsp;- Como gerar <strong>Ordens de Servi&ccedil;o Online</b>?<br /> <br /> As respostas se encontram em um <strong>&uacute;nico lugar</b>:<br /> <br /> uma ferramenta que eu desenvolvi <em>depois que o dono de uma assist&ecirc;ncia t&eacute;cnica</em> abriu o cora&ccedil;&atilde;o e reclamou de algumas dificuldades:<br /> <br /> Isso aconteceu quando levei meu celular com a tela trincada para consertar.<br /> <br /> Fui at&eacute; l&aacute; com um amigo do trabalho.<br /> Cheguei, expliquei o defeito do aparelho&nbsp;e entreguei a um funcion&aacute;rio da loja.&nbsp;<br /> <br /> Enquanto trabalhava, o dono da assist&ecirc;ncia t&eacute;cnica me ouviu comentando coisas do meu trabalho com um amigo e perguntou:<br /> <br /> <strong>Lucas, voc&ecirc; &eacute; programador?</b><br /> Eu disse: <strong>Sim</b>.<br /> <br /> Ele comentou que precisava de um <strong>sistema para controlar os clientes e os pedidos</b>.<br /> <br /> Disse tamb&eacute;m que <strong>n&atilde;o aguentava&nbsp;mais</b> os clientes ligando&nbsp;para saber se o servi&ccedil;o j&aacute; estava pronto.<br /> <br /> Ele queria paz e organiza&ccedil;&atilde;o para trabalhar.<br /> <br /> Ouvi atentamente. Refleti muito na necessidade.<br /> <br /> Na hora me veio na cabe&ccedil;a uma solu&ccedil;&atilde;o ou um aplicativo online, <strong>nas nuvens</b>, onde os empreendedores/prestadores de servi&ccedil;o pudessem acessar de qualquer lugar do mundo.<br /> <br /> Pensei tamb&eacute;m na possibilidade&nbsp;de os clientes acompanharem em casa os pedidos e servi&ccedil;os em andamento.&nbsp;<br /> <br /> Pensei tamb&eacute;m em <strong>p&oacute;s venda</b>. Para manter a clientela sempre fiel, aquecida e por perto.<br /> <br /> Fui pra casa e a partir desse dia, trabalhei em muitas madrugadas e finais de semana.&nbsp;<br /> <br /> <strong>E atingi o meu objetivo</b>.<br /> <br /> Criei um <strong>Sistema para P&oacute;s Venda Online&nbsp;e Acompanhamento de Servi&ccedil;os</b>,<br /> com foco total&nbsp;no relacionamento com o cliente.&nbsp;<br /> <br /> O sistema possui um Script de P&oacute;s venda&nbsp;que permite fazer P&oacute;s venda com 1 clique.<br /> <br /> O nome dele &eacute; <strong>Acompanhe Seu Pedido</b>&nbsp;e funciona nas nuvens, ou seja, &eacute; totalmente online e pode ser acessado por smartphones ou computadores.<br /> <br /> <strong>Com o sistema&nbsp;&eacute; facilmente poss&iacute;vel: </b><br /> <br /> &nbsp;- O sistema permite entrar em contato rapidamente com o cliente por Whatsapp, e-mail&nbsp;ou notifica&ccedil;&atilde;o push;<br /> &nbsp;- Que seus clientes acompanhem os pedidos ou servi&ccedil;os em andamento de qualquer lugar do mundo;<br /> &nbsp;- Eles acompanham pelo smartphone, tablet ou pelo computador;<br /> &nbsp;- Permite gerar Ordem de Servi&ccedil;o Online e permite&nbsp;gerar um c&oacute;digo para o cliente ter acesso restrito a determinada Ordem de Servi&ccedil;o;<br /> &nbsp;- Obter a previs&atilde;o de faturamento por m&ecirc;s;<br /> &nbsp;- E o melhor: <strong>P&oacute;s-Venda com 1 clique!!!</b>&nbsp;Esta funcionalidade permite mostrar ao seu cliente que voc&ecirc; se importa com a qualidade dos&nbsp;servi&ccedil;os&nbsp;que oferece. ( P&oacute;s venda &eacute; poderoso e poucos fazem ou sabem fazer. )<br /> <br /> <strong>Voc&ecirc; tem interesse no sistema?</b>&nbsp;Se sim, leia atentamente abaixo.<br /> <br /> Eu <strong>n&atilde;o</b> deixo o acesso ao sistema aberto para qualquer empresa que tenha interesse.<br /> <br /> A libera&ccedil;&atilde;o &eacute; feita para um n&uacute;mero <em>limitad&iacute;ssimo </em>de interessados.&nbsp;<br /> <br /> Por 2 motivos que envolvem respeito:<br /> <br /> 1. Treinamento: eu e minha equipe de suporte queremos prestar o mais cuidadoso&nbsp;treinamento.&nbsp;<br /> <br /> Estaremos em contato com muita prestatividade e paci&ecirc;ncia para que voc&ecirc; e sua equipe tenham&nbsp;r&aacute;pida habilidade com o sistema.<br /> <br /> 2. Suporte: n&oacute;s n&atilde;o tapamos o sol com a peneira, n&atilde;o conseguimos abra&ccedil;ar o mundo e temos o p&eacute; no ch&atilde;o para assumir que n&atilde;o conseguir&iacute;amos atender milhares de empresas.<br /> <br /> N&oacute;s tamb&eacute;m odiamos deixar pessoas aguardando atendimento&nbsp;como fazem muitas empresas por a&iacute;.&nbsp;<br /> <br /> <strong>Como vai funcionar a libera&ccedil;&atilde;o do sistema e o custo para obter?</b></span></span></p> <p><span style=\"font-size:18px\"><span style=\"font-family:Arial,Helvetica,sans-serif\">Vou abrir o carrinho de compras nos pr&oacute;ximos dias. O carrinho normalmente fecha automaticamente quando atingimos o&nbsp;n&uacute;mero m&aacute;ximo de empresas que <strong>podemos atender</b>.<br /> <br /> Ou seja, quando este <strong>limite</b> &eacute; atingido, a p&aacute;gina de vendas sai do ar <strong>e s&oacute; abrimos o carrinho novamente depois</b> que os novos clientes se mostrarem aptos para utilizarem bem o sistema.</span></span></p> <p><span style=\"font-size:18px\"><span style=\"font-family:Arial,Helvetica,sans-serif\">O custo do sistema <strong>&eacute; irris&oacute;rio</b> - te garanto - e, por enquanto,<br /> <strong>n&atilde;o</b> cobramos taxa de licen&ccedil;a de ades&atilde;o ou taxa de treinamento.<br /> <br /> Trabalhamos com uma assinatura <strong>anual</b> (com desconto) onde <strong>voc&ecirc; pode dividir em 12 vezes</b> e tamb&eacute;m temos uma assinatura <strong>mensal</b>.</span></span><br /> <br /> <span style=\"font-size:18px\"><span style=\"font-family:Arial,Helvetica,sans-serif\">Fica a seu crit&eacute;rio.<br /> <br /> <strong>Muito obrigado por confiar.</b><br /> &Eacute; um prazer te servir.<br /> <br /> Lucas Correia<em>,</em><br /> <em>Arquiteto de Software<br /> Criador dos sistemas <strong>howtodo</b>&nbsp;e <strong>Acompanhe Seu Pedido</b></em></span></span></p> <br><br><img src=\"https://acompanheseupedido.com/hrpx/57\" /><br><br>Caso não queira mais receber nossos e-mails, <a href=\"https://acompanheseupedido.com/desinscrever-se/bHVrc19ldmFuZ2VsaXN0YUBob3RtYWlsLmNvbQ==\" target=\"_blank\">clique aqui</a> para descadastrar-se de nossa lista de forma segura.<br><br>E-mail enviado por <strong><a href=\"https://acompanheseupedido.com\" target=\"_blank\">Acompanhe Seu Pedido - Pós Venda e Acompanhamento de Pedidos e Serviços</a></b>.";
		System.out.println(validateHtmlEmail(mail, 1l));
	}

	public static String escapeSpecialCharacters(String text) {
		return StringEscapeUtils.escapeHtml(text);
	}

	public static File getVirtualFile() {
		VirtualFile vf = VirtualFile.fromRelativePath("/public/images/apple76x76.png");
		File f = vf.getRealFile();
		return f;
	}

	public static String unsubscribeHTML(String siteDomain, String mail, long sequenceMailQueueId) {
		return "<br><br><img src=\"" + siteDomain + "/hrpx/" + sequenceMailQueueId + "\" alt=\"Img bar\" /><br><br>Caso não queira mais receber nossos e-mails, <a href=\"" + siteDomain + "/desinscrever-se/" + Utils.encode(mail)
				+ "\" target=\"_blank\">clique aqui</a> para descadastrar-se de nossa lista de forma segura.";
	}

	public static String sentCredits(String siteTitle, String siteDomain) {
		return "<br><br>E-mail enviado por <b><a href=\"" + siteDomain + "\" target=\"_blank\">" + siteTitle + "</a></b>.";
	}

	public static String validateHtmlEmail(String bodyHTML, long sequenceMailQueueId) {
		String headers = "<!DOCTYPE html>";
		String tagsBegin = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html charset=UTF-8\" /></head><body>";
		String content = bodyHTML.replaceAll("<p>", "<p style=\"margin:0;\">").replaceAll("<strong>", "<b>").replaceAll("</strong>", "</b>").replaceAll("\" target=", "?mid=" + sequenceMailQueueId + "\" target=");
		String tagsEnd = "</body></html>";
		String htmlMail = headers.concat(tagsBegin).concat(content).concat(tagsEnd);
		return htmlMail;
	}

	public static boolean testUrl(String url) throws IOException {
		URL u = new URL(url);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		int code = huc.getResponseCode();
		return 200 == code;
	}

}
