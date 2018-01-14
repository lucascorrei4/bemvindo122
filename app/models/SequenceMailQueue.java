
package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.FromEnum;
import util.Utils;

@Entity
public class SequenceMailQueue extends Model {
	public String name;

	@Required(message = "Campo obrigatório.")
	public String mail;

	@ManyToOne
	public SequenceMail sequenceMail;

	public Date jobDate = new Date();

	public boolean sent = false;
	
	public boolean mailRead = false;

	@Hidden
	public String postedAt;

	public String toString() {
		return name;
	}

	public SequenceMail getSequenceMail() {
		return sequenceMail;
	}

	public void setSequenceMail(SequenceMail sequenceMail) {
		this.sequenceMail = sequenceMail;
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

	public static SequenceMailQueue verifyByEmail(String mail) {
		return find("byMail", mail).first();
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	public Date getJobDate() {
		return jobDate;
	}

	public void setJobDate(Date jobDate) {
		this.jobDate = jobDate;
	}

	public String getJobDateParsed() throws ParseException {
		return Utils.parseStringDateTime(Utils.getStringDateTime(jobDate));
	}

	public boolean isMailRead() {
		return mailRead;
	}

	public void setMailRead(boolean mailRead) {
		this.mailRead = mailRead;
	}
}