package models;

import java.text.ParseException;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import util.StatusEnum;
import util.Utils;

@Entity
public class OrderOfServiceStep extends Model {
	@ManyToOne
	public OrderOfService orderOfService;

	@ManyToOne
	public Step step;

	@ManyToOne
	public Service service;

	@Enumerated(EnumType.STRING)
	public StatusEnum status = StatusEnum.NotStarted;

	public String obs;

	private String reference;
	
	public Blob image;

	@Hidden
	public String postedAt;

	public boolean isActive = true;

	@Hidden
	public long institutionId;
	
	public String orderCode;
	
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l : Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public OrderOfService getOrderOfService() {
		return orderOfService;
	}

	public void setOrderOfService(OrderOfService orderOfService) {
		this.orderOfService = orderOfService;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

}
