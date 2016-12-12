package models;

public class StatusMail {
	public boolean sent;
	public boolean read;

	public String sendDate;
	public int msgId;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"sent\":" + sent + ",");
		sb.append("\"read\":" + read + ",");
		sb.append("\"sendDate\":\"" + sendDate + "\",");
		sb.append("\"msgId\":" + msgId + "");
		sb.append("}");
		
		return sb.toString();
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
}
