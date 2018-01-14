package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.Lob;

import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.ApplicationConfiguration;
import util.Utils;

@Entity
public class SimplePage extends Model {

	@Required(message = "Campo obrigatório.")
	public String title;

	@Lob
	@MaxSize(100000)
	public String headline;

	public String title2;

	public String metatags;

	@Lob
	@MaxSize(100000)
	public String headline2;

	@Lob
	@MaxSize(100000)
	public String description;

	public String buttonTitle;

	public String buttonAction;

	public String buttonActionCheckout;

	public Blob image1;

	public String tags;

	public String embed;

	public String friendlyUrl;

	public String messagePreFormLead;

	@Hidden
	public String postedAt;

	public String shortenUrl;

	public boolean isActive = true;
	
	public boolean showLeadsForm = true;

	public boolean autoPlayVideo = false;

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

	public String getHeadline() {
		return Utils.isNullOrEmpty(this.headline) ? headline : Utils.normalizeString(headline);
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public Blob getImage1() {
		return image1;
	}

	public void setImage1(Blob image1) {
		this.image1 = image1;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getEmbed() {
		return embed;
	}

	public void setEmbed(String embed) {
		this.embed = embed;
	}

	public String getFriendlyUrl() {
		if (Utils.isNullOrEmpty(this.friendlyUrl) && !Utils.isNullOrEmpty(this.title)) {
			setFriendlyUrl(Utils.stringToUrl(this.title.trim()));
		}
		return friendlyUrl;
	}

	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}

	public static SimplePage findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public String getShortenUrl() {
		if (Utils.isNullOrEmpty(this.shortenUrl) && !Utils.isNullOrZero(this.id) && !Utils.isNullOrEmpty(this.friendlyUrl)) {
			Parameter parameter = Parameter.getCurrentParameter();
			parameter.setGoogleShortnerUrlApiId(parameter.getGoogleShortnerUrlApiId() == null ? "AIzaSyCscCd-De7uL6UGXABT1g4I_rU1wMJRv8w" : parameter.getGoogleShortnerUrlApiId());
			setShortenUrl(Utils.getShortenUrl(parameter.getGoogleShortnerUrlApiId(), Parameter.getCurrentParameter().getSiteDomain() + "/continue/".concat(String.valueOf(this.id)).concat("/").concat(this.getFriendlyUrl())));
		}
		return shortenUrl;
	}


	public void setShortenUrl(String shortenUrl) {
		this.shortenUrl = shortenUrl;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public String getHeadline2() {
		return headline2;
	}

	public void setHeadline2(String headline2) {
		this.headline2 = headline2;
	}

	public String getButtonTitle() {
		return buttonTitle;
	}

	public void setButtonTitle(String buttonTitle) {
		this.buttonTitle = buttonTitle;
	}

	public String getButtonAction() {
		return buttonAction;
	}

	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	public String getMetatags() {
		return metatags;
	}

	public void setMetatags(String metatags) {
		this.metatags = metatags;
	}

	public boolean isShowLeadsForm() {
		return showLeadsForm;
	}

	public void setShowLeadsForm(boolean showLeadsForm) {
		this.showLeadsForm = showLeadsForm;
	}

	public String getMessagePreFormLead() {
		return messagePreFormLead;
	}

	public void setMessagePreFormLead(String messagePreFormLead) {
		this.messagePreFormLead = messagePreFormLead;
	}

	public boolean isAutoPlayVideo() {
		return autoPlayVideo;
	}

	public void setAutoPlayVideo(boolean autoPlayVideo) {
		this.autoPlayVideo = autoPlayVideo;
	}

	public String getButtonActionCheckout() {
		return buttonActionCheckout;
	}

	public void setButtonActionCheckout(String buttonActionCheckout) {
		this.buttonActionCheckout = buttonActionCheckout;
	}

}
