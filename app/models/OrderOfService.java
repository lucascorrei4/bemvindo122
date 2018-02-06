package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.ServiceOrderOfServiceSteps;
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

	@Lob
	public String obs;

	@Hidden
	public String postedAt;

	public boolean isActive = true;
	
	@Hidden
	public boolean isThanked = false;

	@Transient
	public Float totalOrderOfService = 0f;
	
	public int grade;
	
	@Lob
	@MaxSize(100000)
	public String clientEvaluation;

	public boolean evaluated = false;

	@Transient
	public String currentStatus;

	public String toString() {
		return orderCode;
	}

	@Transient
	Map<Service, List<OrderOfServiceStep>> mapOrderServiceSteps = new HashMap<Service, List<OrderOfServiceStep>>();

	@Transient
	public List<ServiceOrderOfServiceSteps> serviceOrderOfServiceSteps = new ArrayList<ServiceOrderOfServiceSteps>();
	
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
			setPostedAt(Utils.getCurrentDateTime());
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

	public boolean isThanked() {
		return isThanked;
	}

	public void setThanked(boolean isThanked) {
		this.isThanked = isThanked;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}

	public Float getTotalOrderOfService() {
		return totalOrderOfService;
	}

	public void setTotalOrderOfService(Float totalOrderOfService) {
		this.totalOrderOfService = totalOrderOfService;
	}
	
	public String getTotalPriceCurrency() {
		return Utils.getCurrencyValue(totalOrderOfService);
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public List<ServiceOrderOfServiceSteps> getServiceOrderOfServiceSteps() {
		return serviceOrderOfServiceSteps;
	}

	public void setServiceOrderOfServiceSteps(List<ServiceOrderOfServiceSteps> serviceOrderOfServiceSteps) {
		this.serviceOrderOfServiceSteps = serviceOrderOfServiceSteps;
	}
	
	public Institution getInstitutionById(long institutionId) {
		return (Institution) Institution.findById(Long.valueOf(institutionId));
	}

	public String getClientEvaluation() {
		return clientEvaluation;
	}

	public void setClientEvaluation(String clientEvaluation) {
		this.clientEvaluation = clientEvaluation;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}
	

}
