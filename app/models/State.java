package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import play.db.jpa.Model;

@Entity
public class State extends Model {
	public String acronym;

	public String name;

	public long countryId;

	@GenericGenerator(name = "gen", strategy = "sequency")
	@GeneratedValue(generator = "gen")
	public Long getId() {
		return id;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCountryId() {
		return countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	public static State verifyById(long id) {
		return find("byId", id).first();
	}

}
