package models;

public class BodyMail {
	public String template;
	public String title1;
	public String subTitle1;
	public String title2;
	public String paragraph1;
	public String paragraph2;
	public String paragraph3;
	public String paragraph4;
	public String paragraph5;
	public String title3;
	public String paragraph6;
	public String paragraph7;
	public String paragraph8;
	public String paragraph9;
	public String paragraph10;
	public String footer1;
	public String footer2;
	public String footer3;
	public String image1;
	public String image2;
	public String image3;
	public String image4;
	public String attachment;
	public String bodyHTML;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"template\":\"" + template + "\",");
		sb.append("\"title1\":\"" + title1 + "\",");
		sb.append("\"subTitle1\":\"" + subTitle1 + "\",");
		sb.append("\"title2\":\"" + title2 + "\",");
		sb.append("\"paragraph1\":\"" + paragraph1 + "\",");
		sb.append("\"paragraph2\":\"" + paragraph2 + "\",");
		sb.append("\"paragraph3\":\"" + paragraph3 + "\",");
		sb.append("\"paragraph4\":\"" + paragraph4 + "\",");
		sb.append("\"paragraph5\":\"" + paragraph5 + "\",");
		sb.append("\"title3\":\"" + title3 + "\",");
		sb.append("\"paragraph6\":\"" + paragraph6 + "\",");
		sb.append("\"paragraph7\":\"" + paragraph7 + "\",");
		sb.append("\"paragraph8\":\"" + paragraph8 + "\",");
		sb.append("\"paragraph9\":\"" + paragraph9 + "\",");
		sb.append("\"paragraph10\":\"" + paragraph10 + "\",");
		sb.append("\"footer1\":\"" + footer1 + "\",");
		sb.append("\"footer2\":\"" + footer2 + "\",");
		sb.append("\"footer3\":\"" + footer3 + "\",");
		sb.append("\"image1\":\"" + image1 + "\",");
		sb.append("\"image2\":\"" + image2 + "\",");
		sb.append("\"image3\":\"" + image3 + "\",");
		sb.append("\"image4\":\"" + image4 + "\",");
		sb.append("\"attachment\":\"" + attachment + "\"");
		sb.append("}");

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new BodyMail().toString());
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
	}

	public String getSubTitle1() {
		return subTitle1;
	}

	public void setSubTitle1(String subTitle1) {
		this.subTitle1 = subTitle1;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public String getParagraph1() {
		return paragraph1;
	}

	public void setParagraph1(String paragraph1) {
		this.paragraph1 = paragraph1;
	}

	public String getParagraph2() {
		return paragraph2;
	}

	public void setParagraph2(String paragraph2) {
		this.paragraph2 = paragraph2;
	}

	public String getParagraph3() {
		return paragraph3;
	}

	public void setParagraph3(String paragraph3) {
		this.paragraph3 = paragraph3;
	}

	public String getParagraph4() {
		return paragraph4;
	}

	public void setParagraph4(String paragraph4) {
		this.paragraph4 = paragraph4;
	}

	public String getParagraph5() {
		return paragraph5;
	}

	public void setParagraph5(String paragraph5) {
		this.paragraph5 = paragraph5;
	}

	public String getTitle3() {
		return title3;
	}

	public void setTitle3(String title3) {
		this.title3 = title3;
	}

	public String getParagraph6() {
		return paragraph6;
	}

	public void setParagraph6(String paragraph6) {
		this.paragraph6 = paragraph6;
	}

	public String getParagraph7() {
		return paragraph7;
	}

	public void setParagraph7(String paragraph7) {
		this.paragraph7 = paragraph7;
	}

	public String getParagraph8() {
		return paragraph8;
	}

	public void setParagraph8(String paragraph8) {
		this.paragraph8 = paragraph8;
	}

	public String getParagraph9() {
		return paragraph9;
	}

	public void setParagraph9(String paragraph9) {
		this.paragraph9 = paragraph9;
	}

	public String getParagraph10() {
		return paragraph10;
	}

	public void setParagraph10(String paragraph10) {
		this.paragraph10 = paragraph10;
	}

	public String getFooter1() {
		return footer1;
	}

	public void setFooter1(String footer1) {
		this.footer1 = footer1;
	}

	public String getFooter2() {
		return footer2;
	}

	public void setFooter2(String footer2) {
		this.footer2 = footer2;
	}

	public String getFooter3() {
		return footer3;
	}

	public void setFooter3(String footer3) {
		this.footer3 = footer3;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getImage3() {
		return image3;
	}

	public void setImage3(String image3) {
		this.image3 = image3;
	}

	public String getImage4() {
		return image4;
	}

	public void setImage4(String image4) {
		this.image4 = image4;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getBodyHTML() {
		return bodyHTML;
	}

	public void setBodyHTML(String bodyHTML) {
		this.bodyHTML = bodyHTML;
	}
}
