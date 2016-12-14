package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class StatusMail extends Model {

	@Lob
	public String message;

	public String subject;

	public String destination;

	public String sendDate;

	public String clientName;

	public boolean sent;
	public boolean read;

	public int msgId;

	@Hidden
	public long institutionId;

	@Hidden
	public String postedAt;
	
	public String toString() {
		return destination.concat(" ").concat(clientName);
	}

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
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

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public Date getSendDateConverted() throws ParseException {
		return Utils.parseDate(sendDate, "yyyy-MM-dd'T'HH:mm");
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
