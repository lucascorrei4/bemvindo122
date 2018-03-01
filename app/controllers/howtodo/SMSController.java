package controllers.howtodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import models.StatusSMS;
import util.ApplicationConfiguration;
import util.Utils;

public class SMSController {

	public static Logger logger = Logger.getLogger(SMSController.class);

	public String STR_URL_SEND_SMS = ApplicationConfiguration.getInstance().getSMSApiUrl().concat("SendSMS?");
	public String STR_URL_STATUS_SMS = ApplicationConfiguration.getInstance().getSMSApiUrl().concat("MsgStatus?");
	public String STR_URL_QUERY_BALANCE = ApplicationConfiguration.getInstance().getSMSApiUrl().concat("QueryBalance?");
	public String STR_SMS_USER_ID = ApplicationConfiguration.getInstance().getSMSUserId();
	public String STR_SMS_PWD = ApplicationConfiguration.getInstance().getSMSPwd();
	public String STR_SMS_API_KEY = ApplicationConfiguration.getInstance().getSMSApiKey();

	public String sendSMS(String destination, String sender, String message, StatusSMS status) {
		String strUrl = null;
		try {
			String from = "DEFAULT";
			String to = destination;
			String msg = message;
			strUrl = STR_URL_SEND_SMS.concat("userid=").concat(STR_SMS_USER_ID).concat("&pwd=").concat(STR_SMS_PWD)
					.concat("&apikey=").concat(STR_SMS_API_KEY).concat("&from=").concat(from).concat("&to=55")
					.concat(to).concat("&msg=").concat(URLEncoder.encode(msg, "UTF-8"));
			/* Preparing connection URL */
			URL url = new URL(strUrl);
			/* Trying connect do SMS API Server */
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			/* Verifying response code status is 200 */
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String aux = "";
				String output = "";
				while ((aux = br.readLine()) != null) {
					output = output.concat(aux);
				}
				/* Reading return */
				String responseStatus = output.substring(output.indexOf("<Status>"), output.indexOf("</Status>"))
						.replaceAll("<Status>", "");
				status.message = msg;
				status.destination = destination;
				if ("SUCCESS".equals(responseStatus)) {
					status.smsSent = true;
					status.sendDate = Utils.getCurrentDateTime();
					status.msgId = Long.valueOf(output.substring(output.indexOf("<MsgId>"), output.indexOf("</MsgId>"))
							.replaceAll("<MsgId>", ""));
					return "SUCCESS";
				} else if ("FAILED".equals(responseStatus)) {
					status.smsSent = false;
					status.msgId = 0;
					return "FAILED";
				}
			} else {
				logger.error("Error sending SMS from " + from + " to " + to + ". HTTP Code: " + conn.getResponseCode());
				return "ERROR";
			}
			conn.disconnect();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao enviar SMS: " + e.getMessage()
					.concat(" Detalhe: " + String.valueOf(e.getLocalizedMessage()).concat(" URL: ").concat(strUrl)));
		}
		return "";
	}

	public void getStatusSMS(int msgId) {
		try {
			URL url = new URL(STR_URL_STATUS_SMS.concat("Userid=").concat(STR_SMS_USER_ID).concat("&pwd=")
					.concat(STR_SMS_PWD).concat("&APIKEY=").concat(STR_SMS_API_KEY).concat("&MSGID=")
					.concat(String.valueOf(msgId)));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	public String getQueryBalance() {
		try {
			URL url = new URL(STR_URL_QUERY_BALANCE.concat("Userid=").concat(STR_SMS_USER_ID).concat("&pwd=")
					.concat(STR_SMS_PWD).concat("&APIKEY=").concat(STR_SMS_API_KEY));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String aux = "";
			String output = "";
			while ((aux = br.readLine()) != null) {
				output = output.concat(aux);
			}
			conn.disconnect();
			return output.substring(output.indexOf("<AccountBalance>"), output.indexOf("</AccountBalance>"))
					.replaceAll("<AccountBalance>", "");
		} catch (Exception e) {
			e.getStackTrace();
		}
		return "";
	}

	public static void mains(String[] args) {
		// 64018048 22h30 64018065 22H37
		// new SendSMSController().getStatusSMS(64016211);
		System.out.println(new SMSController().getQueryBalance());
	}

	public static int getQtdSMSByDate(long institutionId, String dateBegin, String dateEnd) throws ParseException {
		int qtd = 0;
		Date date = Utils.parseDate("2016-11-02T01:12", "yyyy-MM-dd'T'HH:mm");
		List<StatusSMS> listSMS = StatusSMS.find("institutionId = " + institutionId + " and sendDate > '" + dateBegin
				+ "' and sendDate < '" + dateEnd + "'").fetch();
		qtd = listSMS.size();
		return qtd;
	}

	public static void main(String[] args) throws ParseException {
		Date date = Utils.parseDate("2016-11-02T01:12", "yyyy-MM-dd'T'HH:mm");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
		c.add(Calendar.DATE, -i - 7);
		Date start = c.getTime();
		c.add(Calendar.DATE, 6);
		Date end = c.getTime();
		System.out.println(start + " - " + end);
	}

}
