package models;

import java.text.ParseException;

import javax.persistence.Entity;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Company extends Model {

	public String name;

	@Hidden
	public long institutionId;

	public String getName() {
		return name;
	}
	
	@Hidden
	public String postedAt;

	public void setName(String name) {
		this.name = name;
	}

	public static Company verifyById(long id) {
		return find("byId", id).first();
	}
	
	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}
	
	public String getPostedAt() throws ParseException {
		if (this.postedAt == null) {
			setPostedAt(Utils.getCurrentDateTime());
		}
		return postedAt;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

}
