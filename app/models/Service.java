package models;

import java.text.ParseException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Service extends Model {

	@Required(message = "Campo obrigatório.")
	public String title;

	public String description;

	@Hidden
	public String postedAt;

	public boolean isActive = true;

	public String toString() {
		return title;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Hidden
	public long institutionId;

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l : Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPostedAt() throws ParseException {
		if (this.postedAt == null) {
			setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
		}
		return postedAt;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

}
