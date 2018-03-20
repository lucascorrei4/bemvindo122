package models.howtodo;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import controllers.CRUD.Hidden;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class Parameter extends Model {
	@OneToOne
	public FreePage freePageIndex;

	public String siteName;
	public String siteDomain;
	public String siteMail;

	public String siteTitle;
	public String siteDescription;
	@Hidden
	public String siteKeywords;

	public String siteFacebook;
	public String siteTwitter;
	public String siteTwitterShort;
	public String siteInstagram;
	public String siteInstagramShort;

	@Hidden
	public String siteLogo;
	public Blob logo;
	public Blob icon;
	public String siteHeader;
	@Lob
	public String siteSlogan;
	public boolean leftAligmentLogo;
	public boolean logoRoundedBorder;

	public String siteBackGroundColor;
	public String siteMainTitleColor;
	public Blob homeBackgroundImage;
	public String footerInfo;

	public String siteIconFontAwesome;
	public String titleCaptureForm;

	public String messageHighlightProducts;

	@Lob
	public String descriptionContactPage;
	@Lob
	public String descriptionAboutPage;
	@Lob
	public String descriptionPrivacyPolicyPage;
	@Lob
	public String descriptionTermsConditionsPage;

	@Hidden
	@Lob
	public String logoUrl;

	@Lob
	public String tweet;

	public String googleAnalyticsId;

	public String googleRemarketingId;

	public String googleTagManagerId;

	public String googleShortnerUrlApiId;

	public String facebookPixelId;

	public String mailSenderName;
	public String mailSenderFrom;
	public String mailHostName;
	public String mailHostUser;
	public String mailHostPassword;
	public String mailHostPort;

	public Float smsPricePlan = 0f;

	public Float mininumSalary = 0f;

	public Float percentValuePlan = 0f;

	@Lob
	public String msgNewUsers;
	@Lob
	public String msgActiveUsers;
	@Lob
	public String msgUpgrade;
	@Hidden
	public Float currentPricePlan = 0f;
	@Lob
	public String embedThankLead;
	@Lob
	public String embedFirstStep;

	public int mailSendInterval = 1;
	public int standarHourToSendMails = 10;
	
	@Hidden
	public long institutionId;

	public String toString() {
		return "Portal: " + siteName;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getGoogleAnalyticsId() {
		return googleAnalyticsId;
	}

	public void setGoogleAnalyticsId(String googleAnalyticsId) {
		this.googleAnalyticsId = googleAnalyticsId;
	}

	public String getGoogleRemarketingId() {
		return googleRemarketingId;
	}

	public void setGoogleRemarketingId(String googleRemarketingId) {
		this.googleRemarketingId = googleRemarketingId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteDomain() {
		return siteDomain;
	}

	public void setSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
	}

	public String getSiteMail() {
		return siteMail;
	}

	public void setSiteMail(String siteMail) {
		this.siteMail = siteMail;
	}

	public String getSiteTitle() {
		return siteTitle;
	}

	public void setSiteTitle(String siteTitle) {
		this.siteTitle = siteTitle;
	}

	public String getSiteDescription() {
		return siteDescription;
	}

	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}

	public String getSiteKeywords() {
		return siteKeywords;
	}

	public void setSiteKeywords(String siteKeywords) {
		this.siteKeywords = siteKeywords;
	}

	public String getSiteFacebook() {
		return siteFacebook;
	}

	public void setSiteFacebook(String siteFacebook) {
		this.siteFacebook = siteFacebook;
	}

	public String getSiteTwitter() {
		return siteTwitter;
	}

	public void setSiteTwitter(String siteTwitter) {
		this.siteTwitter = siteTwitter;
	}

	public String getSiteTwitterShort() {
		return siteTwitterShort;
	}

	public void setSiteTwitterShort(String siteTwitterShort) {
		this.siteTwitterShort = siteTwitterShort;
	}

	public String getSiteLogo() {
		return siteLogo;
	}

	public void setSiteLogo(String siteLogo) {
		this.siteLogo = siteLogo;
	}

	public String getSiteHeader() {
		return siteHeader;
	}

	public void setSiteHeader(String siteHeader) {
		this.siteHeader = siteHeader;
	}

	public String getSiteSlogan() {
		return siteSlogan;
	}

	public void setSiteSlogan(String siteSlogan) {
		this.siteSlogan = siteSlogan;
	}

	public String getSiteInstagram() {
		return siteInstagram;
	}

	public void setSiteInstagram(String siteInstagram) {
		this.siteInstagram = siteInstagram;
	}

	public String getSiteInstagramShort() {
		return siteInstagramShort;
	}

	public void setSiteInstagramShort(String siteInstagramShort) {
		this.siteInstagramShort = siteInstagramShort;
	}

	public String getSiteBackGroundColor() {
		return siteBackGroundColor;
	}

	public void setSiteBackGroundColor(String siteBackGroundColor) {
		this.siteBackGroundColor = siteBackGroundColor;
	}

	public String getSiteMainTitleColor() {
		return siteMainTitleColor;
	}

	public void setSiteMainTitleColor(String siteMainTitleColor) {
		this.siteMainTitleColor = siteMainTitleColor;
	}

	public String getSiteIconFontAwesome() {
		return siteIconFontAwesome;
	}

	public void setSiteIconFontAwesome(String siteIconFontAwesome) {
		this.siteIconFontAwesome = siteIconFontAwesome;
	}

	public String getMessageHighlightProducts() {
		return messageHighlightProducts;
	}

	public void setMessageHighlightProducts(String messageHighlightProducts) {
		this.messageHighlightProducts = messageHighlightProducts;
	}

	public String getGoogleTagManagerId() {
		return googleTagManagerId;
	}

	public void setGoogleTagManagerId(String googleTagManagerId) {
		this.googleTagManagerId = googleTagManagerId;
	}

	public static Parameter getCurrentParameter() {
		return (Parameter) Parameter.findAll().iterator().next();
	}

	public String getDescriptionContactPage() {
		return Utils.isNullOrEmpty(this.descriptionContactPage) ? descriptionContactPage : Utils.normalizeString(descriptionContactPage);
	}

	public void setDescriptionContactPage(String descriptionContactPage) {
		this.descriptionContactPage = descriptionContactPage;
	}

	public String getDescriptionAboutPage() {
		return Utils.isNullOrEmpty(this.descriptionAboutPage) ? descriptionAboutPage : Utils.normalizeString(descriptionAboutPage);
	}

	public void setDescriptionAboutPage(String descriptionAboutPage) {
		this.descriptionAboutPage = descriptionAboutPage;
	}

	public String getDescriptionPrivacyPolicyPage() {
		return Utils.isNullOrEmpty(this.descriptionPrivacyPolicyPage) ? descriptionPrivacyPolicyPage : Utils.normalizeString(descriptionPrivacyPolicyPage);
	}

	public void setDescriptionPrivacyPolicyPage(String descriptionPrivacyPolicyPage) {
		this.descriptionPrivacyPolicyPage = descriptionPrivacyPolicyPage;
	}

	public String getDescriptionTermsConditionsPage() {
		return Utils.isNullOrEmpty(this.descriptionTermsConditionsPage) ? descriptionTermsConditionsPage : Utils.normalizeString(descriptionTermsConditionsPage);
	}

	public void setDescriptionTermsConditionsPage(String descriptionTermsConditionsPage) {
		this.descriptionTermsConditionsPage = descriptionTermsConditionsPage;
	}

	public String getGoogleShortnerUrlApiId() {
		return googleShortnerUrlApiId;
	}

	public void setGoogleShortnerUrlApiId(String googleShortnerUrlApiId) {
		this.googleShortnerUrlApiId = googleShortnerUrlApiId;
	}

	public String getFacebookPixelId() {
		return facebookPixelId;
	}

	public void setFacebookPixelId(String facebookPixelId) {
		this.facebookPixelId = facebookPixelId;
	}

	public Blob getLogo() {
		return logo;
	}

	public void setLogo(Blob logo) {
		this.logo = logo;
	}

	public boolean isLeftAligmentLogo() {
		return leftAligmentLogo;
	}

	public void setLeftAligmentLogo(boolean leftAligmentLogo) {
		this.leftAligmentLogo = leftAligmentLogo;
	}

	public long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public String getMailHostName() {
		return mailHostName;
	}

	public void setMailHostName(String mailHostName) {
		this.mailHostName = mailHostName;
	}

	public String getMailHostUser() {
		return mailHostUser;
	}

	public void setMailHostUser(String mailHostUser) {
		this.mailHostUser = mailHostUser;
	}

	public String getMailHostPassword() {
		return mailHostPassword;
	}

	public void setMailHostPassword(String mailHostPassword) {
		this.mailHostPassword = mailHostPassword;
	}

	public boolean isLogoRoundedBorder() {
		return logoRoundedBorder;
	}

	public void setLogoRoundedBorder(boolean logoRoundedBorder) {
		this.logoRoundedBorder = logoRoundedBorder;
	}

	public Blob getHomeBackgroundImage() {
		return homeBackgroundImage;
	}

	public void setHomeBackgroundImage(Blob homeBackgroundImage) {
		this.homeBackgroundImage = homeBackgroundImage;
	}

	public String getTitleCaptureForm() {
		return titleCaptureForm;
	}

	public void setTitleCaptureForm(String titleCaptureForm) {
		this.titleCaptureForm = titleCaptureForm;
	}

	public Blob getIcon() {
		return icon;
	}

	public void setIcon(Blob icon) {
		this.icon = icon;
	}

	public String getFooterInfo() {
		return footerInfo;
	}

	public void setFooterInfo(String footerInfo) {
		this.footerInfo = footerInfo;
	}

	public Float getSmsPricePlan() {
		return smsPricePlan;
	}

	public void setSmsPricePlan(Float smsPricePlan) {
		this.smsPricePlan = smsPricePlan;
	}

	public Float getMininumSalary() {
		return mininumSalary;
	}

	public void setMininumSalary(Float mininumSalary) {
		this.mininumSalary = mininumSalary;
	}

	public Float getPercentValuePlan() {
		return percentValuePlan;
	}

	public void setPercentValuePlan(Float percentValuePlan) {
		this.percentValuePlan = percentValuePlan;
	}

	public String getMsgNewUsers() {
		return msgNewUsers;
	}

	public void setMsgNewUsers(String msgNewUsers) {
		this.msgNewUsers = msgNewUsers;
	}

	public String getMsgActiveUsers() {
		return msgActiveUsers;
	}

	public void setMsgActiveUsers(String msgActiveUsers) {
		this.msgActiveUsers = msgActiveUsers;
	}

	public String getMsgUpgrade() {
		return msgUpgrade;
	}

	public void setMsgUpgrade(String msgUpgrade) {
		this.msgUpgrade = msgUpgrade;
	}

	public Float getCurrentPricePlan() {
		return currentPricePlan;
	}

	public void setCurrentPricePlan(Float currentPricePlan) {
		this.currentPricePlan = currentPricePlan;
	}

	public String getEmbedThankLead() {
		return embedThankLead;
	}

	public void setEmbedThankLead(String embedThankLead) {
		this.embedThankLead = embedThankLead;
	}

	public String getEmbedFirstStep() {
		return embedFirstStep;
	}

	public void setEmbedFirstStep(String embedFirstStep) {
		this.embedFirstStep = embedFirstStep;
	}

	public String getMailHostPort() {
		return mailHostPort;
	}

	public void setMailHostPort(String mailHostPort) {
		this.mailHostPort = mailHostPort;
	}

	public String getMailSenderFrom() {
		return mailSenderFrom;
	}

	public void setMailSenderFrom(String mailSenderFrom) {
		this.mailSenderFrom = mailSenderFrom;
	}

	public String getMailSenderName() {
		return mailSenderName;
	}

	public void setMailSenderName(String mailSenderName) {
		this.mailSenderName = mailSenderName;
	}

	public int getMailSendInterval() {
		return mailSendInterval;
	}

	public void setMailSendInterval(int mailSendInterval) {
		this.mailSendInterval = mailSendInterval;
	}

	public FreePage getFreePageIndex() {
		return freePageIndex;
	}

	public void setFreePageIndex(FreePage freePageIndex) {
		this.freePageIndex = freePageIndex;
	}

	public int getStandarHourToSendMails() {
		return standarHourToSendMails;
	}

	public void setStandarHourToSendMails(int standarHourToSendMails) {
		this.standarHourToSendMails = standarHourToSendMails;
	}


}
