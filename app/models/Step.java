package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.mysql.jdbc.Util;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Step extends Model {

	@ManyToOne
	public Service service;

	@Required
	public String title;

	public String description;

	public float estimatedDuration = 0f;

	public int position = 0;

	public boolean isActive = true;

	@Hidden
	public long institutionId;

	@Hidden
	public String postedAt;

	@Transient
	public String titleParsed;

	public String toString() {
		return title;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
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

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

	public String getPostedAt() throws ParseException {
		if (this.postedAt == null) {
			setPostedAt(Utils.getCurrentDateTime());
		}
		return postedAt;
	}

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getTitleParsed() {
		String titleParsed = title.replace(" ", "-").toLowerCase();
		return Utils.removeAccent(titleParsed);
	}

	public void setTitleParsed(String titleParsed) {
		this.titleParsed = titleParsed;
	}

	public float getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(float estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

}
