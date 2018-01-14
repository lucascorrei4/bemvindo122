package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.ApplicationConfiguration;
import util.FacebookEventEnum;
import util.Utils;

@Entity
public class TheSystem extends Model {

	@Required(message = "Campo obrigatório.")
	public String title;

	@Lob
	@MaxSize(100000)
	public String headline;

	@Required(message = "Campo obrigatório.")
	public String title2;

	@Lob
	@MaxSize(100000)
	public String headline2;

	@Required(message = "Campo obrigatório.")
	public String title3;

	@Lob
	@MaxSize(100000)
	public String headline3;

	@Required(message = "Campo obrigatório.")
	public String titleDescription;

	@Lob
	@MaxSize(100000)
	public String description;

	@Lob
	@MaxSize(100000)
	public String complement;

	public String metatags;

	public Blob image1;

	public Blob image2;

	public Blob image3;

	public String tags;

	public String embed;

	public String friendlyUrl;

	public boolean highlight;

	public boolean showBottomNews;

	public boolean showLeadsForm;

	public boolean showTopMenu;
	
	public String actionButton;

	public String phrase;

	public String buttonAction;
	
	@Enumerated(EnumType.STRING)
	public FacebookEventEnum facebookEvent = FacebookEventEnum.ViewContent;

	@Hidden
	public String postedAt;

	public String shortenUrl;

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

	public String getMetatags() {
		return metatags;
	}

	public void setMetatags(String metatags) {
		this.metatags = metatags;
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

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public static TheSystem findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public String getShortenUrl() {
		if (Utils.isNullOrEmpty(this.shortenUrl) && !Utils.isNullOrZero(this.id) && !Utils.isNullOrEmpty(this.friendlyUrl)) {
			Parameter parameter = Parameter.getCurrentParameter();
			parameter.setGoogleShortnerUrlApiId(parameter.getGoogleShortnerUrlApiId() == null ? "AIzaSyCscCd-De7uL6UGXABT1g4I_rU1wMJRv8w" : parameter.getGoogleShortnerUrlApiId());
			setShortenUrl(Utils.getShortenUrl(parameter.getGoogleShortnerUrlApiId(), Parameter.getCurrentParameter().getSiteDomain() + "/o-sistema/".concat(String.valueOf(this.id)).concat("/").concat(this.getFriendlyUrl())));
		}
		return shortenUrl;
	}

	public void setShortenUrl(String shortenUrl) {
		this.shortenUrl = shortenUrl;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
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

	public String getTitle3() {
		return title3;
	}

	public void setTitle3(String title3) {
		this.title3 = title3;
	}

	public String getHeadline3() {
		return headline3;
	}

	public void setHeadline3(String headline3) {
		this.headline3 = headline3;
	}

	public Blob getImage3() {
		return image3;
	}

	public void setImage3(Blob image3) {
		this.image3 = image3;
	}

	public boolean isShowBottomNews() {
		return showBottomNews;
	}

	public void setShowBottomNews(boolean showBottomNews) {
		this.showBottomNews = showBottomNews;
	}

	public String getTitleDescription() {
		return titleDescription;
	}

	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
	}

	public boolean isShowLeadsForm() {
		return showLeadsForm;
	}

	public void setShowLeadsForm(boolean showLeadsForm) {
		this.showLeadsForm = showLeadsForm;
	}

	public String getActionButton() {
		return actionButton;
	}

	public void setActionButton(String actionButton) {
		this.actionButton = actionButton;
	}

	public boolean isShowTopMenu() {
		return showTopMenu;
	}

	public void setShowTopMenu(boolean showTopMenu) {
		this.showTopMenu = showTopMenu;
	}

	public String getPhrase() {
		if (Utils.isNullOrEmpty(this.phrase)) {
			setPhrase("");
		}
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getButtonAction() {
		return buttonAction;
	}

	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	public FacebookEventEnum getFacebookEvent() {
		return facebookEvent;
	}

	public void setFacebookEvent(FacebookEventEnum facebookEvent) {
		this.facebookEvent = facebookEvent;
	}

}
