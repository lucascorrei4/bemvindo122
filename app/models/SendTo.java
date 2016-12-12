package models;

public class SendTo {
	public String name;
	public String destination;
	public String sex;
	
	public StatusMail status;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"" + name + "\",");
		sb.append("\"destination\":\"" + destination + "\",");
		sb.append("\"sex\":\"" + sex + "\",");
		sb.append("\"status\": ");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new SendTo().toString());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public StatusMail getStatus() {
		return status;
	}

	public void setStatus(StatusMail status) {
		this.status = status;
	}
}
