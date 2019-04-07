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
public class PrayOrderItem extends Model {

	@ManyToOne
	public PrayOrder prayOrder;

	@Required
	public String title;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

	public PrayOrder getPrayOrder() {
		return prayOrder;
	}

	public void setPrayOrder(PrayOrder prayOrder) {
		this.prayOrder = prayOrder;
	}

}
