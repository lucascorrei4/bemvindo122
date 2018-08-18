package models.howtodo;

import java.text.ParseException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import com.sun.xml.internal.bind.v2.model.core.ID;

import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.Utils;
import util.howtodo.FacebookEventEnum;
import util.howtodo.FreePageTemplatesEnum;

@Entity
public class Gallery extends Model {

	@Required(message = "Campo obrigatório.")
	public String title;
	@Lob
	@MaxSize(10000000)
	public String description;

	public Blob image1;
	public Blob image2;
	public Blob image3;
	public Blob image4;
	public Blob image5;
	public Blob image6;
	public Blob image7;
	public Blob image8;
	public Blob image9;
	public Blob image10;
	public Blob image11;
	public Blob image12;
	public Blob image13;
	public Blob image14;
	public Blob image15;

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

	public String getTitle() {
		return Utils.isNullOrEmpty(this.title) ? title : Utils.normalizeString(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return Utils.isNullOrEmpty(this.description) ? description : Utils.normalizeString(description);
	}

	public void setDescription(String description) {
		this.description = description;
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


	public static Gallery findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public Blob getImage1() {
		return image1;
	}

	public void setImage1(Blob image1) {
		this.image1 = image1;
	}

	public Blob getImage2() {
		return image2;
	}

	public void setImage2(Blob image2) {
		this.image2 = image2;
	}

	public Blob getImage3() {
		return image3;
	}

	public void setImage3(Blob image3) {
		this.image3 = image3;
	}

	public Blob getImage4() {
		return image4;
	}

	public void setImage4(Blob image4) {
		this.image4 = image4;
	}

	public Blob getImage5() {
		return image5;
	}

	public void setImage5(Blob image5) {
		this.image5 = image5;
	}

	public Blob getImage6() {
		return image6;
	}

	public void setImage6(Blob image6) {
		this.image6 = image6;
	}

	public Blob getImage7() {
		return image7;
	}

	public void setImage7(Blob image7) {
		this.image7 = image7;
	}

	public Blob getImage8() {
		return image8;
	}

	public void setImage8(Blob image8) {
		this.image8 = image8;
	}

	public Blob getImage9() {
		return image9;
	}

	public void setImage9(Blob image9) {
		this.image9 = image9;
	}

	public Blob getImage10() {
		return image10;
	}

	public void setImage10(Blob image10) {
		this.image10 = image10;
	}

	public Blob getImage11() {
		return image11;
	}

	public void setImage11(Blob image11) {
		this.image11 = image11;
	}

	public Blob getImage12() {
		return image12;
	}

	public void setImage12(Blob image12) {
		this.image12 = image12;
	}

	public Blob getImage13() {
		return image13;
	}

	public void setImage13(Blob image13) {
		this.image13 = image13;
	}

	public Blob getImage14() {
		return image14;
	}

	public void setImage14(Blob image14) {
		this.image14 = image14;
	}

	public Blob getImage15() {
		return image15;
	}

	public void setImage15(Blob image15) {
		this.image15 = image15;
	}

}
