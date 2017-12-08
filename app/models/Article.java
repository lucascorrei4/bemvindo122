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
import util.StatusInvoiceEnum;
import util.TypeAdsNewsEnum;
import util.Utils;

@Entity
public class Article extends Model {

	@Required(message = "Campo obrigatório.")
	public String titleSEO;
	@Required(message = "Campo obrigatório.")
	public String descriptionSEO;
	@Required(message = "Campo obrigatório.")
	public String canonicalURL;

	@Required(message = "Campo obrigatório.")
	public String title;

	@Lob
	@MaxSize(100000)
	public String headline;

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
	
	public boolean showCaptureForm = true;

	public String titleCaptureForm;

	public String tags;

	public String embed;

	@Enumerated(EnumType.STRING)
	public TypeAdsNewsEnum typeAds = TypeAdsNewsEnum.Default;

	public String directLink;
	
	@Enumerated(EnumType.STRING)
	public FacebookEventEnum facebookEvent = FacebookEventEnum.ViewContent;

	public String friendlyUrl;

	public boolean highlight;

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
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l : Admin.getLoggedUserInstitution().getInstitution().getId();
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

	public Blob getImage3() {
		return image3;
	}

	public void setImage3(Blob image3) {
		this.image3 = image3;
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

	public static Article findByFriendlyUrl(String friendlyUrl) {
		return find("byFriendlyUrl", friendlyUrl).first();
	}

	public String getShortenUrl() {
		if (Utils.isNullOrEmpty(this.shortenUrl) && !Utils.isNullOrZero(this.id) && !Utils.isNullOrEmpty(this.friendlyUrl)) {
			Parameter parameter = Parameter.getCurrentParameter();
			parameter.setGoogleShortnerUrlApiId(parameter.getGoogleShortnerUrlApiId() == null ? "AIzaSyCscCd-De7uL6UGXABT1g4I_rU1wMJRv8w" : parameter.getGoogleShortnerUrlApiId());
			setShortenUrl(Utils.getShortenUrl(parameter.getGoogleShortnerUrlApiId(), Parameter.getCurrentParameter().getSiteDomain() + "/artigos/".concat(String.valueOf(this.id)).concat("/").concat(this.getFriendlyUrl())));
		}
		return shortenUrl;
	}

	public void setShortenUrl(String shortenUrl) {
		this.shortenUrl = shortenUrl;
	}

	public String getComplement() {
		return Utils.isNullOrEmpty(this.complement) ? complement : Utils.normalizeString(complement);
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getTitleCaptureForm() {
		return titleCaptureForm;
	}

	public void setTitleCaptureForm(String titleCaptureForm) {
		this.titleCaptureForm = titleCaptureForm;
	}

	public TypeAdsNewsEnum getTypeAds() {
		return typeAds;
	}

	public void setTypeAds(TypeAdsNewsEnum typeAds) {
		this.typeAds = typeAds;
	}

	public String getDirectLink() {
		return directLink;
	}

	public void setDirectLink(String directLink) {
		this.directLink = directLink;
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

	public boolean isShowCaptureForm() {
		return showCaptureForm;
	}

	public void setShowCaptureForm(boolean showCaptureForm) {
		this.showCaptureForm = showCaptureForm;
	}

	public FacebookEventEnum getFacebookEvent() {
		return facebookEvent;
	}

	public void setFacebookEvent(FacebookEventEnum facebookEvent) {
		this.facebookEvent = facebookEvent;
	}

}
