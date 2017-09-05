package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import controllers.Admin;
import controllers.CRUD.Hidden;
import enumeration.GenderEnum;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class User extends Model {
	@Required
	public String name;

	@Required
	public String lastName;

	@Email
	@Required
	@Unique
	public String email;

	public String birthdate;

	public long countryId;

	public long stateId;

	public long cityId;

	@Enumerated(EnumType.STRING)
	public GenderEnum gender = GenderEnum.M;

	public String address;

	public String complement;

	public String neighborhood;

	public String cep;

	public String cpf;

	@Required
	@Phone
	public String phone1;

	@Phone
	public String phone2;

	@Phone
	public String phone3;

	@Required
	@Password
	@Hidden
	public String password;

	@Transient
	public String repeatPassword;

	@Hidden
	public boolean isAdmin;

	@Hidden
	public boolean isActive = true;

	@Hidden	
	public boolean isFromMonetizze;

	@Hidden
	public long institutionId;

	@Hidden
	public String postedAt;

	public User() {
	}

	public User(String email, String password, String lastName) {
		this.email = email;
		this.password = password;
		this.lastName = lastName;
	}

	public static User connect(String email, String password) {
		User user = find("byEmail", email).first();
		if (user == null) {
			return null;
		}
		String pass1 = user.password.trim();
		String pass2 = password.trim();
		return pass1.equals(pass2) ? user : null;
	}

	public static User verifyByEmail(String email) {
		return find("byEmail", email).first();
	}

	public static User verifyByCPF(String cpf) {
		return find("byCpf", cpf).first();
	}

	public static User verifyByInstitutionId(long id) {
		return find("byInstitutionId", id).first();
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone3() {
		return phone3;
	}

	public void setPhone3(String phone3) {
		this.phone3 = phone3;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public long getCountryId() {
		return countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	public long getStateId() {
		return stateId;
	}

	public void setStateId(long stateId) {
		this.stateId = stateId;
	}

	public long getCityId() {
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	
	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}
	
	public String getStateByStateId() {
		return State.verifyById(Long.valueOf(this.stateId)) == null ? "Não informado" : State.verifyById(Long.valueOf(this.stateId)).name;
	}

	public String getCityByCityId() {
		return City.verifyById(Long.valueOf(this.cityId)) == null ? "Não informado" : City.verifyById(Long.valueOf(this.cityId)).name;
	}
	
	public String getInstitutionByInstitutionId() {
		if (this.institutionId > 0) {
			return Institution.verifyById(Long.valueOf(this.institutionId)) == null ? "Não informado" : Institution.verifyById(this.institutionId).getInstitution();
		}
		return "Não vinculado";
	}

	public boolean isFromMonetizze() {
		return isFromMonetizze;
	}

	public void setFromMonetizze(boolean isFromMonetizze) {
		this.isFromMonetizze = isFromMonetizze;
	}

}