package models.howtodo;

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
import util.Utils;
import util.howtodo.FacebookEventEnum;

@Entity
public class SellPage extends Model {

	@Required(message = "Campo obrigatório.")
	public String titleSEO;
	@Required(message = "Campo obrigatório.")
	public String descriptionSEO;
	@Required(message = "Campo obrigatório.")
	public String canonicalURL;

	@Lob
	@MaxSize(10000000)
	@Required(message = "Campo obrigatório.")
	public String mainTitle;
	@Lob
	@MaxSize(10000000)
	public String videoDescription;
	public String embedVideo;

	public String buttonMainTitle;
	public String buttonActionMainTitle;

	public Blob backgroundImage;
	public String backgroundColor;

	@Lob
	@MaxSize(10000000)
	public String subtitle1;
	@Lob
	@MaxSize(10000000)
	public String description;

	public Blob imageProduct;
	@Lob
	@MaxSize(10000000)
	public String subtitle2;

	@Lob
	@MaxSize(10000000)
	public String htmlOffer1;
	@Lob
	@MaxSize(10000000)
	public String htmlOffer2;
	@Lob
	@MaxSize(10000000)
	public String htmlOffer3;
	@Lob
	@MaxSize(10000000)
	public String htmlOffer4;

	@Lob
	@MaxSize(10000000)
	public String warnings;

	public String urlCheckout;

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

	public static SellPage findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public String getShortenUrl() {
		if (Utils.isNullOrEmpty(this.shortenUrl) && !Utils.isNullOrZero(this.id) && !Utils.isNullOrEmpty(this.friendlyUrl)) {
			Parameter parameter = Parameter.getCurrentParameter();
			parameter.setGoogleShortnerUrlApiId(parameter.getGoogleShortnerUrlApiId() == null ? "AIzaSyCscCd-De7uL6UGXABT1g4I_rU1wMJRv8w" : parameter.getGoogleShortnerUrlApiId());
			setShortenUrl(Utils.getShortenUrl(parameter.getGoogleShortnerUrlApiId(), Parameter.getCurrentParameter().getSiteDomain() + "/pv/".concat(this.getFriendlyUrl())));
		}
		return shortenUrl;
	}


	public void setShortenUrl(String shortenUrl) {
		this.shortenUrl = shortenUrl;
	}

	public String getVideoDescription() {
		return Utils.isNullOrEmpty(this.videoDescription) ? videoDescription : Utils.normalizeString(videoDescription);
	}

	public void setVideoDescription(String videoDescription) {
		this.videoDescription = videoDescription;
	}

	public String getEmbedVideo() {
		return embedVideo;
	}

	public void setEmbedVideo(String embedVideo) {
		this.embedVideo = embedVideo;
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

	public Blob getImageProduct() {
		return imageProduct;
	}

	public void setImageProduct(Blob imageProduct) {
		this.imageProduct = imageProduct;
	}

	public String getSubtitle2() {
		return Utils.isNullOrEmpty(this.subtitle2) ? subtitle2 : Utils.normalizeString(subtitle2);
	}

	public void setSubtitle2(String subtitle2) {
		this.subtitle2 = subtitle2;
	}

	public String getHtmlOffer1() {
		return Utils.isNullOrEmpty(this.htmlOffer1) ? htmlOffer1 : Utils.normalizeString(htmlOffer1);
	}

	public void setHtmlOffer1(String htmlOffer1) {
		this.htmlOffer1 = htmlOffer1;
	}

	public String getHtmlOffer2() {
		return Utils.isNullOrEmpty(this.htmlOffer2) ? htmlOffer2 : Utils.normalizeString(htmlOffer2);
	}

	public void setHtmlOffer2(String htmlOffer2) {
		this.htmlOffer2 = htmlOffer2;
	}

	public String getHtmlOffer3() {
		return Utils.isNullOrEmpty(this.htmlOffer3) ? htmlOffer3 : Utils.normalizeString(htmlOffer3);
	}

	public void setHtmlOffer3(String htmlOffer3) {
		this.htmlOffer3 = htmlOffer3;
	}

	public String getHtmlOffer4() {
		return Utils.isNullOrEmpty(this.htmlOffer4) ? htmlOffer4 : Utils.normalizeString(htmlOffer4);
	}

	public void setHtmlOffer4(String htmlOffer4) {
		this.htmlOffer4 = htmlOffer4;
	}

	public String getButtonMainTitle() {
		return Utils.isNullOrEmpty(this.buttonMainTitle) ? buttonMainTitle : Utils.normalizeString(buttonMainTitle);
	}

	public void setButtonMainTitle(String buttonMainTitle) {
		this.buttonMainTitle = buttonMainTitle;
	}

	public String getButtonActionMainTitle() {
		return buttonActionMainTitle;
	}

	public void setButtonActionMainTitle(String buttonActionMainTitle) {
		this.buttonActionMainTitle = buttonActionMainTitle;
	}

	public String getUrlCheckout() {
		return urlCheckout;
	}

	public void setUrlCheckout(String urlCheckout) {
		this.urlCheckout = urlCheckout;
	}

	public String getWarnings() {
		return Utils.isNullOrEmpty(this.warnings) ? warnings : Utils.normalizeString(warnings);
	}

	public void setWarnings(String warnings) {
		this.warnings = warnings;
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

	public FacebookEventEnum getFacebookEvent() {
		return facebookEvent;
	}

	public void setFacebookEvent(FacebookEventEnum facebookEvent) {
		this.facebookEvent = facebookEvent;
	}

}
