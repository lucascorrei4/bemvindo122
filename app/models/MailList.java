package models;

import java.text.ParseException;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;

import org.hibernate.id.insert.InsertSelectIdentityInsert;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import util.FromEnum;
import util.StatusEnum;
import util.Utils;

@Entity
public class MailList extends Model {
	public String name;

	@Required(message = "Campo obrigatório.")
	public String mail;
	
	@Enumerated(EnumType.STRING)
	public FromEnum from = FromEnum.HomePageTop;

	@Hidden
	public String postedAt;

	public String toString() {
		return mail;
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

}
