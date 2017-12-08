package models;

import java.text.ParseException;

import javax.persistence.Entity;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class HighlightProduct extends Model {

	@Required(message = "Campo obrigatório.")
	public String title;

	@Required(message = "Campo obrigatório.")
	public String msgOffer;

	@Required(message = "Campo obrigatório.")
	public String link;

	public Blob image;

	public boolean isHighlight;

	@Hidden
	public String postedAt;

	public boolean isActive = true;


	public String toString() {
		return title;
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
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public String getTitle() {
		return Utils.isNullOrEmpty(this.title) ? title : Utils.normalizeString(title);
	}

	public void setTitle(String title) {
		this.title = title;
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

	public static HighlightProduct findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public String getMsgOffer() {
		return msgOffer;
	}

	public void setMsgOffer(String msgOffer) {
		this.msgOffer = msgOffer;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}
	
	public String getTitleNoHtml() {
		return Utils.removeHTML(this.title);
	}

	public boolean isHighlight() {
		return isHighlight;
	}

	public void setHighlight(boolean isHighlight) {
		this.isHighlight = isHighlight;
	}

}
