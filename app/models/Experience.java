package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import controllers.Admin;
import controllers.CRUD.Hidden;
import enumeration.ExperienceEnum;
import enumeration.GenderEnum;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Experience extends Model {

	@Required(message = "Campo obrigatório.")
	@Lob
	@MaxSize(100000)
	public String msg;
	
	@Enumerated(EnumType.STRING)
	public ExperienceEnum experienceType = ExperienceEnum.Entrada;
	
	@ManyToOne
	public Service serviceVinculation;

	public Integer sendInterval = 1;
	
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
		return msg;
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

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public ExperienceEnum getExperienceType() {
		return experienceType;
	}

	public void setExperienceType(ExperienceEnum experienceType) {
		this.experienceType = experienceType;
	}

	public Integer getSendInterval() {
		return sendInterval;
	}

	public void setSendInterval(Integer sendInterval) {
		this.sendInterval = sendInterval;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Service getServiceVinculation() {
		return serviceVinculation;
	}

	public void setServiceVinculation(Service serviceVinculation) {
		this.serviceVinculation = serviceVinculation;
	}

}
