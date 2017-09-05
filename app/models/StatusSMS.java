package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.joda.time.DateTime;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class StatusSMS extends Model {

	@Lob
	public String message;

	public String destination;

	public String sendDate;

	public String clientName;

	public long msgId = 0;

	public boolean smsSent;
	public boolean smsRead;

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

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
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

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
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
	
	public Date getSendDateConverted() throws ParseException {
		return Utils.parseDate(sendDate, "yyyy-MM-dd'T'HH:mm");
	}

	public boolean isSmsRead() {
		return smsRead;
	}

	public void setSmsRead(boolean smsRead) {
		this.smsRead = smsRead;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

}
