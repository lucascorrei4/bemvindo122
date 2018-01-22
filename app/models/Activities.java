package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.ActivitiesEnum;
import util.Utils;

@Entity
public class Activities extends Model {
	
	@ManyToOne
	public Client client;

	@Required
	@Enumerated(EnumType.STRING)
	public ActivitiesEnum type = ActivitiesEnum.PhoneCallToHim;

	@Required
	public String title;

	@Required
	@Lob
	@MaxSize(100000)
	public String description;
	
	@ManyToOne
	public User collaborator;

	public boolean isGeneratedSale = false;

	@Required
	public String activityDate;

	public Blob image;
	
	@Hidden
	public String postedAt;

	public boolean isActive = true;

	public String toString() {
		return client.name + " " + client.lastName + " - " + title;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public ActivitiesEnum getType() {
		return type;
	}

	public void setType(ActivitiesEnum type) {
		this.type = type;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public boolean isGeneratedSale() {
		return isGeneratedSale;
	}

	public void setGeneratedSale(boolean isGeneratedSale) {
		this.isGeneratedSale = isGeneratedSale;
	}

	public User getCollaborator() {
		return collaborator;
	}

	public void setCollaborator(User collaborator) {
		this.collaborator = collaborator;
	}

	public String getActivityDate() {
		if (this.activityDate == null) {
			setActivityDate(Utils.formatDateSimple(new Date()));
		}
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}
}
