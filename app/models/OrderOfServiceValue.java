package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import controllers.CRUD.Hidden;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class OrderOfServiceValue extends Model {

	@OneToOne
	public Service service;

	@Hidden
	public long orderOfServiceId;

	@Hidden
	public long institutionId;

	public String reference;

	public String orderCode;
	
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String toString() {
		return null;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public long getOrderOfServiceId() {
		return orderOfServiceId;
	}

	public void setOrderOfServiceId(long orderOfServiceId) {
		this.orderOfServiceId = orderOfServiceId;
	}

	public long getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}


}
