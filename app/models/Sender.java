package models;

public class Sender {
	public String key;
	public String company;
	public String from;
	public String receivedAt;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"key\":\"" + key + "\",");
		sb.append("\"company\":\"" + company + "\",");
		sb.append("\"from\":\"" + from + "\",");
		sb.append("\"postedAt\":\"" + receivedAt + "\"");
		sb.append("}");

		return sb.toString();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(String receivedAt) {
		this.receivedAt = receivedAt;
	}
}
