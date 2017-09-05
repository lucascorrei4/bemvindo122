package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.Transient;

import controllers.Admin;
import controllers.CRUD.Hidden;
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

	public String basePrice = "0,00";

	public boolean isActive = true;
	
	@Transient
	public String nameBasePrice;

	@Transient
	public String aux;
	
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
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
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
			setPostedAt(Utils.getCurrentDateTime());
		}
		return postedAt;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}
	
	public String getNameBasePrice() {
		return title + " R$ " + basePrice;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

	public String getAux() {
		return aux;
	}

	public void setAux(String aux) {
		this.aux = aux;
	}

}
