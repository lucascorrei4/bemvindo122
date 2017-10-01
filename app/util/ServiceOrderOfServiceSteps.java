package util;

import java.util.ArrayList;
import java.util.List;

import models.OrderOfServiceStep;
import models.Service;

public class ServiceOrderOfServiceSteps {
	private Service service;
	private List<OrderOfServiceStep> orderOfServiceSteps;
	private String reference;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<OrderOfServiceStep> getOrderOfServiceSteps() {
		if (Utils.isNullOrEmpty(this.orderOfServiceSteps)) {
			setOrderOfServiceSteps(new ArrayList<OrderOfServiceStep>());
		}
		return orderOfServiceSteps;
	}

	public void setOrderOfServiceSteps(List<OrderOfServiceStep> orderOfServiceSteps) {
		this.orderOfServiceSteps = orderOfServiceSteps;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
}
