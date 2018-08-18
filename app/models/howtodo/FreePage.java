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
public class FreePage extends Model {

	@Required(message = "Campo obrigatório.")
	public String titleSEO;
	@Required(message = "Campo obrigatório.")
	public String descriptionSEO;
	public boolean noFollow = false;

	@Required(message = "Campo obrigatório.")
	public String mainTitle;
	@Lob
	@MaxSize(10000000)
	public String description;

	public boolean abTestVideoOfText = false;
	@Lob
	@MaxSize(10000000)
	public String optionalDescription;

	@Hidden
	public boolean alternateVideoText = false;

	public Blob backgroundImage;
	public String backgroundColor;

	public boolean showCaptureForm;
	public boolean showNumberPhone = false;
	public String messageNumberPhone;
	public String buttonMainTitle;
	public String redirectTo;

	public boolean showFacebookComments = false;

	@Lob
	@MaxSize(10000000)
	public String subtitle1;

	@Lob
	@MaxSize(10000000)
	public String descriptionInactivePage;

	public Blob image1;
	public Blob image2;
	public Blob image3;
	public Blob image4;
	public Blob image5;

	public FreePageTemplatesEnum templateStyle = FreePageTemplatesEnum.FreeStyleTheme;

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

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}

	public FreePageTemplatesEnum getTemplateStyle() {
		return templateStyle;
	}

	public void setTemplateStyle(FreePageTemplatesEnum templateStyle) {
		this.templateStyle = templateStyle;
	}

	public boolean isAbTestVideoOfText() {
		return abTestVideoOfText;
	}

	public void setAbTestVideoOfText(boolean abTestVideoOfText) {
		this.abTestVideoOfText = abTestVideoOfText;
	}

	public String getOptionalDescription() {
		return Utils.isNullOrEmpty(this.optionalDescription) ? optionalDescription : Utils.normalizeString(optionalDescription);
	}

	public void setOptionalDescription(String optionalDescription) {
		this.optionalDescription = optionalDescription;
	}

	public boolean isAlternateVideoText() {
		return alternateVideoText;
	}

	public void setAlternateVideoText(boolean alternateVideoText) {
		this.alternateVideoText = alternateVideoText;
	}

	public boolean isShowFacebookComments() {
		return showFacebookComments;
	}

	public void setShowFacebookComments(boolean showFacebookComments) {
		this.showFacebookComments = showFacebookComments;
	}

	public boolean isShowNumberPhone() {
		return showNumberPhone;
	}

	public void setShowNumberPhone(boolean showNumberPhone) {
		this.showNumberPhone = showNumberPhone;
	}

	public String getDescriptionInactivePage() {
		return descriptionInactivePage;
	}

	public void setDescriptionInactivePage(String descriptionInactivePage) {
		this.descriptionInactivePage = descriptionInactivePage;
	}

	public String getMessageNumberPhone() {
		return messageNumberPhone;
	}

	public void setMessageNumberPhone(String messageNumberPhone) {
		this.messageNumberPhone = messageNumberPhone;
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

}
