package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class OrderOfService extends Model {
	@Required
	@ManyToOne
	public Client client;

	@Transient
	public List<Service> services = new ArrayList<Service>();

	@Hidden
	public long institutionId;

	@Transient
	public List<OrderOfServiceValue> orderOfServiceValue = new ArrayList<OrderOfServiceValue>();

	public String orderCode;

	@Hidden
	public String postedAt;

	public boolean isActive = true;

	public String toString() {
		return orderCode;
	}

	@Transient
	Map<Service, List<OrderOfServiceStep>> mapOrderServiceSteps = new HashMap<Service, List<OrderOfServiceStep>>();

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public long getInstitutionId() {
		if (this.institutionId == 0f) {
			setInstitutionId(Admin.getLoggedUserInstitution().getInstitution().getId());
		}
		return institutionId;
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getPostedAt() throws ParseException {
		if (this.postedAt == null) {
			setPostedAt(Utils.getCurrentDateTimeByFormat("dd/MM/yyyy HH:mm:ss"));
		}
		return postedAt;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public Map<Service, List<OrderOfServiceStep>> getMapOrderServiceSteps() {
		return mapOrderServiceSteps;
	}

	public void setMapOrderServiceSteps(Map<Service, List<OrderOfServiceStep>> mapOrderServiceSteps) {
		this.mapOrderServiceSteps = mapOrderServiceSteps;
	}

	public List<OrderOfServiceValue> getOrderOfServiceValue() {
		if (this.orderOfServiceValue == null) {
			setOrderOfServiceValue(new ArrayList<OrderOfServiceValue>());
		}
		return orderOfServiceValue;
	}

	public void setOrderOfServiceValue(List<OrderOfServiceValue> orderOfServiceValue) {
		this.orderOfServiceValue = orderOfServiceValue;
	}

}
