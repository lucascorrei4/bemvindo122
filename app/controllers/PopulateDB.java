package controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.SystemUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import models.City;
import models.Country;
import models.Institution;
import models.State;
import models.User;
import play.Play;
import play.mvc.Controller;
import util.Utils;

public class PopulateDB extends Controller {
	public static final String STR_COUNTRIES_JSON_FILE_PATH = "conf\\populate\\countries.json";
	public static final String STR_STATES_JSON_FILE_PATH = "conf\\populate\\states.json";
	public static final String STR_CITIES_JSON_FILE_PATH = "conf\\populate\\cities.json";
	public static final String STR_USERS_JSON_FILE_PATH = "conf\\populate\\users.json";
	public static final String STR_INSTITUTIONS_JSON_FILE_PATH = "conf\\populate\\institutions.json";

	public static void populate() {
		String status = null;
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String jsonContent = null;
			// new PopulateDB().configureAutoIncrementValue();
			/* Populate countries */
			jsonContent = Utils.getJsonFileContent(Play.getFile(SystemUtils.IS_OS_LINUX
					? STR_COUNTRIES_JSON_FILE_PATH.replaceAll("\\", "/") : STR_COUNTRIES_JSON_FILE_PATH));
			new PopulateDB().populateCountries(jsonContent, gson);
			/* Populate states */
			jsonContent = Utils.getJsonFileContent(Play.getFile(SystemUtils.IS_OS_LINUX
					? STR_STATES_JSON_FILE_PATH.replaceAll("\\", "/") : STR_STATES_JSON_FILE_PATH));
			new PopulateDB().populateStates(jsonContent, gson);
			/* Populate cities */
			jsonContent = Utils.getJsonFileContent(Play.getFile(SystemUtils.IS_OS_LINUX
					? STR_CITIES_JSON_FILE_PATH.replaceAll("\\", "/") : STR_CITIES_JSON_FILE_PATH));
			new PopulateDB().populateCities(jsonContent, gson);
			/* Populate users */
			jsonContent = Utils.getJsonFileContent(Play.getFile(SystemUtils.IS_OS_LINUX
					? STR_USERS_JSON_FILE_PATH.replaceAll("\\", "/") : STR_USERS_JSON_FILE_PATH));
			new PopulateDB().populateUsers(jsonContent, gson);
			/* Populate institutions */
			// jsonContent =
			// Utils.getJsonFileContent(Play.getFile(STR_INSTITUTIONS_JSON_FILE_PATH));
			// new PopulateDB().populateInstitutions(jsonContent, gson);
			status = "Populou! :)";
			render(status);
		} catch (Exception e) {
			status = "Não populou corretamente! Verifique os índices do BD! :(";
			render(status);
			render();
		}
	}

	private void configureAutoIncrementValue() {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE Country AUTO_INCREMENT = 1 ; ");
		sb.append("ALTER TABLE State AUTO_INCREMENT = 1 ; ");
		sb.append("ALTER TABLE City AUTO_INCREMENT = 1 ; ");
		sb.append("ALTER TABLE User AUTO_INCREMENT = 1 ; ");
		Connection conn = play.db.DB.getConnection();
		Statement stm = null;
		try {
			stm = conn.createStatement();
			stm.execute(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void populateCountries(String jsonContent, Gson gson) {
		JsonArray jsonArray = Utils.getJsonArray(jsonContent, "countries");
		Country country = new Country();
		if (country.findAll().isEmpty()) {
			for (JsonElement elem : jsonArray) {
				country = gson.fromJson(elem, Country.class);
				country.willBeSaved = true;
				validation.valid(country);
				if (!validation.hasErrors()) {
					country.merge();
				}
			}
		}
	}

	private void populateStates(String jsonContent, Gson gson) {
		JsonArray jsonArray = Utils.getJsonArray(jsonContent, "states");
		State state = new State();
		if (state.findAll().isEmpty()) {
			for (JsonElement elem : jsonArray) {
				state = gson.fromJson(elem, State.class);
				state.willBeSaved = true;
				validation.valid(state);
				if (!validation.hasErrors()) {
					state.merge();
				}
			}
		}
	}

	private void populateCities(String jsonContent, Gson gson) {
		JsonArray jsonArray = Utils.getJsonArray(jsonContent, "cities");
		City city = new City();
		if (city.findAll().isEmpty()) {
			for (JsonElement elem : jsonArray) {
				city = gson.fromJson(elem, City.class);
				city.willBeSaved = true;
				validation.valid(city);
				if (!validation.hasErrors()) {
					city.merge();
				}
			}
		}
	}

	private void populateUsers(String jsonContent, Gson gson) {
		JsonArray jsonArray = Utils.getJsonArray(jsonContent, "users");
		User user = new User();
		if (user.findAll().isEmpty()) {
			for (JsonElement elem : jsonArray) {
				user = gson.fromJson(elem, User.class);
				user.id = 0l;
				user.setActive(true);
				user.willBeSaved = true;
				validation.valid(user);
				if (!validation.hasErrors()) {
					user.merge();
				}
			}
		}
	}

	private void populateInstitutions(String jsonContent, Gson gson) {
		JsonArray jsonArray = Utils.getJsonArray(jsonContent, "institutions");
		Institution institution = new Institution();
		if (institution.findAll().isEmpty()) {
			for (JsonElement elem : jsonArray) {
				institution = gson.fromJson(elem, Institution.class);
				institution.id = 0l;
				institution.willBeSaved = true;
				validation.valid(institution);
				if (!validation.hasErrors()) {
					institution.merge();
				}
			}
		}
	}
}
