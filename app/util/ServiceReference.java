package util;

import models.OrderOfService;
import models.Service;

public class ServiceReference {
	private Service service;
	private String reference;

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

}
