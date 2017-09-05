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

	public Float qtd = 0f;

	public Float discount = 0f;

	public Float unitPrice = 0f;

	public Float subTotal = 0f;

	public Float totalPrice = 0f;
	
	public String toString() {
		return null;
	}

	public Float getDiscount() {
		return discount;
	}

	public String getDiscountCurrency() {
		return Utils.getCurrencyValue(discount);
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
		if (service != null) {
			unitPrice = Float.valueOf(service.basePrice.replace(",", "."));
		}
		return unitPrice;
	}

	public String getUnitPriceCurrency() {
		return Utils.getCurrencyValue(unitPrice);
	}

	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Float getTotalPrice() {
		setTotalPrice((unitPrice * qtd) - discount);
		return totalPrice;
	}

	public String getTotalPriceCurrency() {
		return Utils.getCurrencyValue(totalPrice);
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Float getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(Float subTotal) {
		this.subTotal = subTotal;
	}
	
	public String getSubTotalPriceCurrency() {
		return Utils.getCurrencyValue(subTotal);
	}

	public String getSubTotalCurrency() {
		return Utils.getCurrencyValue(subTotal);
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
