package models.howtodo;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.Lob;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class LeadSearchQuestion extends Model {

	public String campaing;
	@Lob
	@MaxSize(100000)
	public String question1;
	public String commentQuestion1;
	@Lob
	@MaxSize(100000)
	public String question2;
	public String commentQuestion2;
	@Lob
	@MaxSize(100000)
	public String question3;
	public String commentQuestion3;
	@Lob
	@MaxSize(100000)
	public String question4;
	public String commentQuestion4;
	@Lob
	@MaxSize(100000)
	public String question5;
	public String commentQuestion5;
	@Lob
	@MaxSize(100000)
	public String thanksEmbedVideo;
	public String thanksTitle;
	@Lob
	@MaxSize(100000)
	public String thanksDescription;

	@Hidden
	public String postedAt;

	public boolean annonymousSearch = false;
	public boolean isActive = true;
	
	@Hidden
	public long institutionId;

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l : Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public String toString() {
		return campaing;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public String getCampaing() {
		return campaing;
	}

	public void setCampaing(String campaing) {
		this.campaing = campaing;
	}

	public String getQuestion1() {
		return question1;
	}

	public void setQuestion1(String question1) {
		this.question1 = question1;
	}

	public String getQuestion2() {
		return question2;
	}

	public void setQuestion2(String question2) {
		this.question2 = question2;
	}

	public String getQuestion3() {
		return question3;
	}

	public void setQuestion3(String question3) {
		this.question3 = question3;
	}

	public String getQuestion4() {
		return question4;
	}

	public void setQuestion4(String question4) {
		this.question4 = question4;
	}

	public String getQuestion5() {
		return question5;
	}

	public void setQuestion5(String question5) {
		this.question5 = question5;
	}

	public String getCommentQuestion1() {
		return commentQuestion1;
	}

	public void setCommentQuestion1(String commentQuestion1) {
		this.commentQuestion1 = commentQuestion1;
	}

	public String getCommentQuestion2() {
		return commentQuestion2;
	}

	public void setCommentQuestion2(String commentQuestion2) {
		this.commentQuestion2 = commentQuestion2;
	}

	public String getCommentQuestion3() {
		return commentQuestion3;
	}

	public void setCommentQuestion3(String commentQuestion3) {
		this.commentQuestion3 = commentQuestion3;
	}

	public String getCommentQuestion4() {
		return commentQuestion4;
	}

	public void setCommentQuestion4(String commentQuestion4) {
		this.commentQuestion4 = commentQuestion4;
	}

	public String getCommentQuestion5() {
		return commentQuestion5;
	}

	public void setCommentQuestion5(String commentQuestion5) {
		this.commentQuestion5 = commentQuestion5;
	}

	public String getThanksDescription() {
		return thanksDescription;
	}

	public void setThanksDescription(String thanksDescription) {
		this.thanksDescription = thanksDescription;
	}

	public String getThanksEmbedVideo() {
		return thanksEmbedVideo;
	}

	public void setThanksEmbedVideo(String thanksEmbedVideo) {
		this.thanksEmbedVideo = thanksEmbedVideo;
	}

	public String getThanksTitle() {
		return thanksTitle;
	}

	public void setThanksTitle(String thanksTitle) {
		this.thanksTitle = thanksTitle;
	}

	public boolean isAnnonymousSearch() {
		return annonymousSearch;
	}

	public void setAnnonymousSearch(boolean annonymousSearch) {
		this.annonymousSearch = annonymousSearch;
	}

}
