package models.howtodo;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.Lob;

import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class MailBase extends Model {
	public String titlePersona;

	@MaxSize(1000000000)
	@Lob
	public String mailsPersona;

	@Hidden
	public String postedAt;
	
	public int qtd;

	public String getTitlePersona() {
		return titlePersona;
	}

	public void setTitlePersona(String titlePersona) {
		this.titlePersona = titlePersona;
	}

	public String getMailsPersona() {
		return mailsPersona;
	}

	public void setMailsPersona(String mailsPersona) {
		this.mailsPersona = mailsPersona;
	}

	public int getQtd() {
		if (!Utils.isNullOrEmpty(mailsPersona)) {
			setQtd(mailsPersona.split(",").length);
		}
		return qtd;
	}

	public void setQtd(int qtd) {
		this.qtd = qtd;
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
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

}
