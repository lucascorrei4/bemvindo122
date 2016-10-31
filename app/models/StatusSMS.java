package models;

import javax.persistence.Entity;

import controllers.CRUD.Hidden;
import play.db.jpa.Model;

@Entity
public class StatusSMS extends Model {
	@Hidden
	public long institutionId;

	public boolean sent;
	public boolean read;

	public String message;
	public String destination;
	public String sendDate;

	public int msgId = 0;

	@Hidden
	public String postedAt;

	public String toStringJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"sent\":" + sent + ",");
		sb.append("\"read\":" + read + ",");
		sb.append("\"sendDate\":\"" + sendDate + "\",");
		sb.append("\"msgId\":" + msgId + "");
		sb.append("}");

		return sb.toString();
	}

	public long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
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

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public String getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}
}
