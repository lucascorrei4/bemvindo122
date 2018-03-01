package util.howtodo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.apache.ivy.Main;
import org.apache.log4j.Logger;

public class UrlShortener {

	public static String shorten(String shortenApiId, String longUrl) {
		String googUrl = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=http://goo.gl/fbsS&key="
				+ shortenApiId;
		String shortUrl = "";

		try {
			URLConnection conn = new URL(googUrl).openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write("{\"longUrl\":\"" + longUrl + "\"}");
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;

			while ((line = rd.readLine()) != null) {
				if (line.indexOf("id") > -1) {
					// I'm sure there's a more elegant way of parsing
					// the JSON response, but this is quick/dirty =)
					shortUrl = line.substring(8, line.length() - 2);
					break;
				}
			}

			wr.close();
			rd.close();
		} catch (MalformedURLException ex) {
			Logger.getLogger(UrlShortener.class.getName());
		} catch (IOException ex) {
			Logger.getLogger(UrlShortener.class.getName());
		}

		return shortUrl;
	}

}
