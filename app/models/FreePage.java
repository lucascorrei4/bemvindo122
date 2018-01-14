package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.FacebookEventEnum;
import util.Utils;

@Entity
public class FreePage extends Model {

	@Required(message = "Campo obrigatório.")
	public String titleSEO;
	@Required(message = "Campo obrigatório.")
	public String descriptionSEO;
	@Required(message = "Campo obrigatório.")
	public String canonicalURL;
	public boolean noFollow = false;

	@Required(message = "Campo obrigatório.")
	public String mainTitle;
	@Lob
	@MaxSize(10000000)
	public String description;

	public Blob backgroundImage;
	public String backgroundColor;
	
	public boolean showCaptureForm;
	public String buttonMainTitle;

	@Lob
	@MaxSize(10000000)
	public String subtitle1;
	
	@Enumerated(EnumType.STRING)
	public FacebookEventEnum facebookEvent = FacebookEventEnum.ViewContent;

	public String friendlyUrl;

	@Hidden
	public String postedAt;

	public String shortenUrl;

	public boolean isActive = true;
	
	public String toString() {
		return titleSEO;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getMainTitle() {
		return Utils.isNullOrEmpty(this.mainTitle) ? mainTitle : Utils.normalizeString(mainTitle);
	}

	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
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

	public String getFriendlyUrl() {
		if (!Utils.isNullOrEmpty(this.mainTitle)) {
			setFriendlyUrl(Utils.stringToUrl(Utils.removeHTML(this.mainTitle.trim())));
		}
		return friendlyUrl;
	}

	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}

	public static FreePage findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public String getShortenUrl() {
		if (Utils.isNullOrEmpty(this.shortenUrl) && !Utils.isNullOrZero(this.id) && !Utils.isNullOrEmpty(this.friendlyUrl)) {
			Parameter parameter = Parameter.getCurrentParameter();
			parameter.setGoogleShortnerUrlApiId(parameter.getGoogleShortnerUrlApiId() == null ? "AIzaSyCscCd-De7uL6UGXABT1g4I_rU1wMJRv8w" : parameter.getGoogleShortnerUrlApiId());
			setShortenUrl(Utils.getShortenUrl(parameter.getGoogleShortnerUrlApiId(), Parameter.getCurrentParameter().getSiteDomain() + "/fp/".concat(this.getFriendlyUrl())));
		}
		return shortenUrl;
	}
	
	public void setShortenUrl(String shortenUrl) {
		this.shortenUrl = shortenUrl;
	}

	public Blob getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Blob backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getSubtitle1() {
		return Utils.isNullOrEmpty(this.subtitle1) ? subtitle1 : Utils.normalizeString(subtitle1);
	}

	public void setSubtitle1(String subtitle1) {
		this.subtitle1 = subtitle1;
	}

	public String getTitleSEO() {
		return titleSEO;
	}

	public void setTitleSEO(String titleSEO) {
		this.titleSEO = titleSEO;
	}

	public String getDescriptionSEO() {
		return descriptionSEO;
	}

	public void setDescriptionSEO(String descriptionSEO) {
		this.descriptionSEO = descriptionSEO;
	}

	public String getCanonicalURL() {
		return canonicalURL;
	}

	public void setCanonicalURL(String canonicalURL) {
		this.canonicalURL = canonicalURL;
	}

	public boolean isNoFollow() {
		return noFollow;
	}

	public void setNoFollow(boolean noFollow) {
		this.noFollow = noFollow;
	}

	public FacebookEventEnum getFacebookEvent() {
		return facebookEvent;
	}

	public void setFacebookEvent(FacebookEventEnum facebookEvent) {
		this.facebookEvent = facebookEvent;
	}

	public String getButtonMainTitle() {
		return buttonMainTitle;
	}

	public void setButtonMainTitle(String buttonMainTitle) {
		this.buttonMainTitle = buttonMainTitle;
	}

	public boolean isShowCaptureForm() {
		return showCaptureForm;
	}

	public void setShowCaptureForm(boolean showCaptureForm) {
		this.showCaptureForm = showCaptureForm;
	}

}
