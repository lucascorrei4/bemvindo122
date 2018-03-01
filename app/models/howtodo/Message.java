package models.howtodo;

import java.text.ParseException;

import javax.persistence.Entity;

import controllers.CRUD.Hidden;
import models.Institution;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Message extends Model {
	@Required(message = "Campo obrigatório.")
	public String name;

	@Required(message = "Campo obrigatório.")
	public String lastName;

	@Required(message = "Campo obrigatório.")
	public String title;

	@Required(message = "Campo obrigatório.")
	public String description;

	public String mail;

	public String phone;

	@Hidden
	public String postedAt;

	@Hidden
	public long institutionId;

	public Institution institution;

	public String toString() {
		return title;
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public Institution getInstitution() {
		return Institution.find("byId", institutionId).first();
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

}
