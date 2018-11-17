package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import controllers.Admin;
import controllers.CRUD.Hidden;
import enumeration.GenderEnum;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Client extends Model {
	@Required(message = "Campo obrigatório.")
	public String name;

	@Required(message = "Campo obrigatório.")
	public String lastName;
	
	public Company company;

	@Email
	@Unique
	@Required(message = "Campo obrigatório.")
	public String mail;

	@Required(message = "Campo obrigatório.")
	public String phone;

	public String phone2;

	public String birthdate;
	
	@Enumerated(EnumType.STRING)
	public GenderEnum gender = GenderEnum.M;
	
	@Lob
	@MaxSize(100000)
	public String obs;

	@Hidden
	public String postedAt;

	@Hidden
	public long institutionId;

	public Institution institution;

	public boolean isActive = true;

	public String toString() {
		return name.concat(" ").concat(lastName);
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		if (this.name == null) {
			setName("");
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		if (this.lastName == null) {
			setLastName("");
		}
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public Institution getInstitution() {
		if (this.institution == null && this.institutionId != 0) {
			Institution institution = Institution.find("id", institutionId).first();
			setInstitution(institution);
		}
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public GenderEnum getGender() {
		return gender;
	}

	public void setGender(GenderEnum gender) {
		this.gender = gender;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
