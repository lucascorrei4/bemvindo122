package models;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Min;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.Utils;
import controllers.Admin;
import controllers.Application;
import controllers.CRUD.Hidden;

@Entity
public class Institution extends Model {
	
	@Required
	public String institution;

	public Blob logo;

	public String website;

	public String slogan;

	@Required
	@Min(1)
	public long countryId;

	@Required
	@Min(1)
	public long stateId;

	@Required
	@Min(1)
	public long cityId;

	public String address;
	public String complement;
	public String neighborhood;
	public String cep;

	public String cnpj;
	@Required
	@Email
	@Unique
	public String email;
	@Required
	@Phone
	public String phone1;
	@Phone
	public String phone2;
	@Phone
	public String phone3;

	@As("-16.570043, -49.313314")
	public String localizationGPS;

	@Hidden
	public String googleApiKey;
	
	@Hidden
	public String googleMapsAddress;

	@Hidden
	public long userId;

	@Hidden
	public long publishedBy;
	
	@Hidden
	@Temporal(TemporalType.TIMESTAMP)
	@As("yyyy-MM-dd HH:mm:ss")
	public Date licenseDate = new Date();

	@Hidden
	public String institutionKey;

	public String urlParameter;

	public Date getLicenseDate() {
		return licenseDate;
	}

	public void setLicenseDate(Date licenseDate) {
		this.licenseDate = licenseDate;
	}

	public String toString() {
		return institution;
	}

	@Hidden
	public String postedAt;

	@PrePersist
	public void prePersist() {
		setPostedAt(Utils.getCurrentDateTime());
	}

//	@PostLoad
//	public void postLoad() {
//		if (Admin.getLoggedUserInstitution().getUser() != null) {
//			Institution institution = Institution.find("userId = " + Admin.getLoggedUserInstitution().getUser().getId()).first();
//			User user = User.findById(institution.getUserId());
//			user.setInstitutionId(institution.getId());
//			user.save();
//		}
//	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLocalizationGPS() {
		return localizationGPS;
	}

	public void setLocalizationGPS(String localizationGPS) {
		this.localizationGPS = localizationGPS;
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

	public String getGoogleApiKey() {
		return googleApiKey;
	}

	public void setGoogleApiKey(String googleApiKey) {
		this.googleApiKey = googleApiKey;
	}

	public String getGoogleMapsAddress() {
		return googleMapsAddress;
	}

	public void setGoogleMapsAddress(String googleMapsAddress) {
		this.googleMapsAddress = googleMapsAddress;
	}

	public String getIframeCode() {
		return getGoogleMapsAddress() + "&key=" + getGoogleApiKey();
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

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public Blob getLogo() {
		return logo;
	}

	public void setLogo(Blob logo) {
		this.logo = logo;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public long getUserId() {
		return Admin.getLoggedUserInstitution().getUser().getId();
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public String getCountryName() {
		Country country = Country.verifyById(getCountryId());
		return country.getName();
	}

	public String getStateName() {
		State state = State.verifyById(getStateId());
		return state.getName();
	}

	public String getCityName() {
		City city = City.verifyById(getCityId());
		return city.getName();
	}

	public long getPublishedId() {
		return Admin.getLoggedUserInstitution().getUser().getId();
	}

	public void setPublishedId(long publishedBy) {
		this.publishedBy = publishedBy;
	}

	public static Institution verifyByCnpj(String cnpj) {
		return find("byCnpj", cnpj).first();
	}

	public static Institution verifyByEmail(String email) {
		return find("byEmail", email).first();
	}

	public static Institution verifyById(Long institutionId) {
		return find("byId", institutionId).first();
	}

	public String getInstitutionKey() {
		if (this.institutionKey == null) {
			setInstitutionKey(Utils.randomKey());
		}
		return institutionKey;
	}

	public void setInstitutionKey(String institutionKey) {
		this.institutionKey = institutionKey;
	}

	public long getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(long publishedBy) {
		this.publishedBy = publishedBy;
	}

	public String getCityStateCountry() {
		return getCityName().concat(" - ").concat(getStateName()).concat("/").concat(getCountryName());
	}

	public String getAddressComplement() {
		// TODO Auto-generated method stub
		return getAddress().concat(getComplement());
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

	public String getUrlParameter() {
		return urlParameter;
	}

	public void setUrlParameter(String urlParameter) {
		this.urlParameter = urlParameter;
	}
	
	public String getLicenseDateParsed() throws ParseException {
		return Utils.formatDate(licenseDate);
	}

}
