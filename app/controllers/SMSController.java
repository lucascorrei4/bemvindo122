package controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import util.ApplicationConfiguration;
import util.Utils;
import models.StatusSMS;

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
			strUrl = STR_URL_SEND_SMS.concat("userid=").concat(STR_SMS_USER_ID).concat("&pwd=").concat(STR_SMS_PWD).concat("&apikey=").concat(STR_SMS_API_KEY).concat("&from=").concat(from).concat("&to=55").concat(to).concat("&msg=")
					.concat(URLEncoder.encode(msg, "UTF-8"));
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
				String responseStatus = output.substring(output.indexOf("<Status>"), output.indexOf("</Status>")).replaceAll("<Status>", "");
				status.message = msg;
				status.destination = destination;
				if ("SUCCESS".equals(responseStatus)) {
					status.smsSent = true;
					status.sendDate = Utils.getCurrentDateTime();
					status.msgId = Long.valueOf(output.substring(output.indexOf("<MsgId>"), output.indexOf("</MsgId>")).replaceAll("<MsgId>", ""));
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
			throw new RuntimeException("Erro ao enviar SMS: " + e.getMessage().concat(" Detalhe: " + String.valueOf(e.getLocalizedMessage()).concat(" URL: ").concat(strUrl)));
		}
		return "";
	}

	public void getStatusSMS(int msgId) {
		try {
			URL url = new URL(STR_URL_STATUS_SMS.concat("Userid=").concat(STR_SMS_USER_ID).concat("&pwd=").concat(STR_SMS_PWD).concat("&APIKEY=").concat(STR_SMS_API_KEY).concat("&MSGID=").concat(String.valueOf(msgId)));
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
			URL url = new URL(STR_URL_QUERY_BALANCE.concat("Userid=").concat(STR_SMS_USER_ID).concat("&pwd=").concat(STR_SMS_PWD).concat("&APIKEY=").concat(STR_SMS_API_KEY));
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
			return output.substring(output.indexOf("<AccountBalance>"), output.indexOf("</AccountBalance>")).replaceAll("<AccountBalance>", "");
		} catch (Exception e) {
			e.getStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		// 64018048 22h30 64018065 22H37
		// new SendSMSController().getStatusSMS(64016211);
		 System.out.println(new SMSController().getQueryBalance());
	}
}
