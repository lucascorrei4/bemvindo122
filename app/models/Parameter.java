package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.ivy.Main;

import com.mysql.jdbc.Util;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.StatusEnum;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.Utils;

@Entity
public class Parameter extends Model {

	public String siteName;
	public String siteDomain;
	public String siteMail;

	public String siteTitle;
	public String siteDescription;
	public String siteKeywords;

	public String siteFacebook;
	public String siteTwitter;
	public String siteTwitterShort;
	public String siteInstagram;
	public String siteInstagramShort;

	public String siteLogo;
	public String siteHeader;
	public String siteSlogan;

	public String siteBackGroundColor;
	public String siteMainTitleColor;

	public String siteIconFontAwesome;

	@Lob
	public String descriptionContactPage;
	@Lob
	public String descriptionAboutPage;
	@Lob
	public String descriptionPrivacyPolicyPage;
	@Lob
	public String descriptionTermsConditionsPage;

	public Float smsPricePlan = 0f;

	public Float mininumSalary = 0f;

	public Float percentValuePlan = 0f;

	@Lob
	public String msgNewUsers;

	@Lob
	public String msgActiveUsers;

	@Lob
	public String msgUpgrade;
	
	public String googleAnalyticsId;
	
	public String googleRemarketingId;

	public String messageHighlightProducts;
	
	public String googleTagManagerId;
	
	public String googleShortnerUrlApiId;

	public String facebookPixelId;

	public String getMsgUpgrade() {
		return msgUpgrade;
	}

	public void setMsgUpgrade(String msgUpgrade) {
		this.msgUpgrade = msgUpgrade;
	}

	@Lob
	public String logoUrl;

	@Lob
	public String tweet;

	@Hidden
	public Float currentPricePlan = 0f;

	@Lob
	public String embedThankLead;

	@Lob
	public String embedFirstStep;

	public String toString() {
		return "Mensalidade atual: " + currentPricePlan;
	}

	public Float getCurrentPricePlan() {
		setCurrentPricePlan(mininumSalary * percentValuePlan / 100);
		return currentPricePlan;
	}

	public void setCurrentPricePlan(Float currentPricePlan) {
		this.currentPricePlan = currentPricePlan;
	}

	public Float getSmsPricePlan() {
		return smsPricePlan;
	}

	public void setSmsPricePlan(Float smsPricePlan) {
		this.smsPricePlan = smsPricePlan;
	}

	public Float getPercentValuePlan() {
		return percentValuePlan;
	}

	public void setPercentValuePlan(Float percentValuePlan) {
		this.percentValuePlan = percentValuePlan;
	}

	public Float getMininumSalary() {
		return mininumSalary;
	}

	public void setMininumSalary(Float mininumSalary) {
		this.mininumSalary = mininumSalary;
	}

	public static void main(String[] args) {
		System.out.println(816 * 10 / 100);
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

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getSMSCurrency() {
		return Utils.getCurrencyValue(getSmsPricePlan());
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
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

	public static Parameter getCurrentParameter() {
		return (Parameter) Parameter.findAll().iterator().next();
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

}
