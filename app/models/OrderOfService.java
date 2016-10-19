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
import play.db.jpa.Model;
import util.Utils;

@Entity
public class OrderOfService extends Model {
	@ManyToOne
	public Client client;

	@ManyToMany
	@OrderBy("title ASC")
	public List<Service> services;

	@Hidden
	public long institutionId;

	public Float qtd = 0f;

	public Float discount = 0f;

	public Float unitPrice = 0f;

	@Hidden
	public Float totalPrice = 0f;

	public String orderCode;

	@Hidden
	public String postedAt;

	public boolean isActive = true;
	
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

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public Float getQtd() {
		return qtd;
	}

	public void setQtd(Float qtd) {
		this.qtd = qtd;
	}

	public Float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Float getTotalPrice() {
		setTotalPrice((unitPrice * qtd) - discount);
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getOrderCode() {
		if (this.orderCode == null) {
			String initials = Admin.getLoggedInstitution().getInstitution().replaceAll(" ", "").toUpperCase()
					.substring(0, 2).concat(Admin.getLoggedInstitution().getId().toString());
			setOrderCode(initials.concat(String.valueOf(Utils.generateRandomId())));
		}
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

}
