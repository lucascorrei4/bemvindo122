package controllers;

import java.util.List;

import models.City;
import models.Country;
import models.State;
import play.mvc.Controller;

public class CEPController extends Controller {
	public static void getStatesByCountryId(String countryId) {
		List<State> listStates = State.find("countryId = " + countryId + " order by name asc").fetch();
		render("@includes.selectStates", listStates);
	}

	public static void getCitiesByCountryIdAndStateId(String stateId) {
		List<City> listCities = City.find("stateId = " + stateId + " order by name asc").fetch();
		render("@includes.selectCities", listCities);
	}

	public static Country getCountryById(String id) {
		return Country.findById(id);
	}

	public static State getStateById(String id) {
		return State.findById(id);
	}

	public static City getCityById(Object id) {
		return City.findById(id);
	}
}
