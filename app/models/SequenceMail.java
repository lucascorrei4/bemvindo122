package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.Lob;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class SequenceMail extends Model {
	public String url;

	public String title;
	
	@Lob
	@MaxSize(100000)
	public String description;

	public Blob attachment = null;

	public Integer sequence;

	public Blob getAttachment() {
		if (this.attachment == null) {
			setAttachment(new Blob());
		}
		return attachment;
	}

	public void setAttachment(Blob attachment) {
		this.attachment = attachment;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Hidden
	public String postedAt;

	@Hidden
	public long institutionId;

	public Institution institution;

	public boolean isActive = true;

	public String toString() {
		return title + " " + url;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

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

}
