package models;

import java.text.ParseException;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import controllers.CRUD.Hidden;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Client extends Model {
	@Required(message = "Campo obrigatório.")
	public String userName;

	@Required(message = "Campo obrigatório.")
	@Email
	public String mail;

	@Required(message = "Campo obrigatório.")
	public String phone;

	@Hidden
	public String postedAt;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getPostedAt() throws ParseException {
		if (this.postedAt == null) {
			setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
		}
		return postedAt;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
