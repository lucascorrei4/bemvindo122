package models.howtodo;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;
import util.howtodo.FromEnum;
import util.howtodo.TypeContentPageEnum;

@Entity
public class MailList extends Model {
	public String name;

	@Required(message = "Campo obrigatório.")
	public String mail;
	
	@Enumerated(EnumType.STRING)
	public FromEnum origin = FromEnum.HomePageTop;

	@Required(message = "Campo obrigatório.")
	public String url;

	@Enumerated(EnumType.STRING)
	public TypeContentPageEnum typeContentPage = TypeContentPageEnum.VideoContent;
	
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
		return "Nome: " + name + "; Origem: " + (Utils.isNullOrEmpty(url) ? "Não informada" : url);
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

	public FromEnum getOrigin() {
		return origin;
	}

	public void setOrigin(FromEnum origin) {
		this.origin = origin;
	}

	public TypeContentPageEnum getTypeContentPage() {
		return typeContentPage;
	}

	public void setTypeContentPage(TypeContentPageEnum typeContentPage) {
		this.typeContentPage = typeContentPage;
	}

}
