package models;

import java.text.ParseException;

import javax.persistence.ManyToOne;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;

public class ClientExperience extends Model {
	
	@Required(message = "Campo obrigatório.")
	@ManyToOne
	public Client client;
	public boolean isSendEntryExperience = false;
	public boolean isSendOutExperience = false;
	public boolean isSendPeakExperience = false;
	@Hidden
	public String postedAt;

	@Hidden
	public long institutionId;

	public Institution institution;

	public boolean isActive = true;
	
	public String toString() {
		return client.name + " "+ client.lastName;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isSendEntryExperience() {
		return isSendEntryExperience;
	}

	public void setSendEntryExperience(boolean isSendEntryExperience) {
		this.isSendEntryExperience = isSendEntryExperience;
	}

	public boolean isSendOutExperience() {
		return isSendOutExperience;
	}

	public void setSendOutExperience(boolean isSendOutExperience) {
		this.isSendOutExperience = isSendOutExperience;
	}

	public boolean isSendPeakExperience() {
		return isSendPeakExperience;
	}

	public void setSendPeakExperience(boolean isSendPeakExperience) {
		this.isSendPeakExperience = isSendPeakExperience;
	}
}
