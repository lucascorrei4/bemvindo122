package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.FromEnum;
import util.Utils;

@Entity
public class MailList extends Model {
	public String name;

	@Required(message = "Campo obrigatório.")
	public String mail;
	
	@Enumerated(EnumType.STRING)
	public FromEnum origin = FromEnum.HomePageTop;

	@Required(message = "Campo obrigatório.")
	public String url;
	
	public boolean isActive = true;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Hidden
	public String postedAt;

	public String toString() {
		return url;
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
	
	public static MailList verifyByEmail(String mail) {
		return find("byMail", mail).first();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
