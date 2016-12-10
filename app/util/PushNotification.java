package util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class PushNotification {

	public static String authId;
	public static String appId;

	@SuppressWarnings("static-access")
	public PushNotification(String authId, String appId) {
		super();
		this.authId = authId;
		this.appId = appId;
	}

	public HttpURLConnection getHttpURLConnection() {
		URL url;
		HttpURLConnection con = null;
		try {
			url = new URL("https://onesignal.com/api/v1/notifications");
			con = (HttpURLConnection) url.openConnection();
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Authorization", "Basic " + getAuthId());
			con.setRequestMethod("POST");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return con;
	}

	public void sendPushAllUsers(String message) {
		try {
			String jsonResponse;
			HttpURLConnection con = getHttpURLConnection();
			String strJsonBody = "{" + "\"app_id\": \"" + getAppId() + "\"," + "\"included_segments\": [\"All\"],"
					+ "\"data\": {\"Enviar\": \"Teste Android Devices!\"}," + "\"contents\": {\"en\": \"" + message
					+ "\"}" + "}";
			byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(sendBytes);
			int httpResponse = con.getResponseCode();
			System.out.println("httpResponse: " + httpResponse);
			if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
				Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			} else {
				Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}
			System.out.println("jsonResponse:\n" + jsonResponse);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public String sentToUserBySpecificTag(String strJsonBody) {
		String jsonResponse = null;
		try {
			HttpURLConnection con = getHttpURLConnection();
			byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(sendBytes);
			int httpResponse = con.getResponseCode();
			System.out.println("httpResponse: " + httpResponse);
			if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
				Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				jsonResponse = new StringBuffer(jsonResponse).insert(1, "\"response\":" + httpResponse + ",").toString();
				scanner.close();
			} else {
				Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return jsonResponse;
	}

	public static String getTags(Map<String, String> tags) {
		Iterator<?> tagIterator = tags.entrySet().iterator();
		StringBuilder sB = new StringBuilder();
		String message = null;
		while (tagIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, String> tag = (Entry<String, String>) tagIterator.next();
			String key = tag.getKey();
			String value = tag.getValue();
			if (!"message".equals(key)) {
				sB.append("{\"field\": \"tag\", \"key\":\"" + key + "\", \"relation\":\"=\", \"value\":\"" + value
						+ "\"}");
				if (tagIterator.hasNext()) {
					sB.append(", {\"operator\": \"AND\"}, ");
				}
			} else {
				message = value;
			}
		}
		String strJsonBody = "{" + "\"app_id\": \"" + getAppId() + "\"," + "\"filters\": [" + sB.toString()
				+ "], \"data\": {\"foo\": \"bar\"}," + "\"contents\": {\"en\": \"" + message + "\"}" + "}";
		return strJsonBody;
	}

	public static String getAppId() {
		return appId;
	}

	public static String getAuthId() {
		return authId;
	}

}
