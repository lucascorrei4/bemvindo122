package models;

import java.text.ParseException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class LeadSearchAnswer extends Model {

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="question_id")
	public LeadSearchQuestion leadSearchQuestion;
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name="lead_id")
	public MailList mailList;
	public String age;
	public String gender;
	public String profession;
	public String business;
	@Lob
	@MaxSize(100000)
	public String answer1;
	@Lob
	@MaxSize(100000)
	public String answer2;
	@Lob
	@MaxSize(100000)
	public String answer3;
	@Lob
	@MaxSize(100000)
	public String answer4;
	@Lob
	@MaxSize(100000)
	public String answer5;
	@Lob
	@MaxSize(100000)
	public String testimonial;

	@Hidden
	public String postedAt;

	public boolean isActive = true;

	public String toString() {
		return mailList.name;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Hidden
	public long institutionId;

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l : Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
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

	public LeadSearchQuestion getLeadSearchQuestion() {
		return leadSearchQuestion;
	}

	public void setLeadSearchQuestion(LeadSearchQuestion leadSearchQuestion) {
		this.leadSearchQuestion = leadSearchQuestion;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public String getAnswer4() {
		return answer4;
	}

	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}

	public String getAnswer5() {
		return answer5;
	}

	public void setAnswer5(String answer5) {
		this.answer5 = answer5;
	}

	public MailList getMailList() {
		return mailList;
	}

	public void setMailList(MailList mailList) {
		this.mailList = mailList;
	}

	public String getTestimonial() {
		return testimonial;
	}

	public void setTestimonial(String testimonial) {
		this.testimonial = testimonial;
	}

}
