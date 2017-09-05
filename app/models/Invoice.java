package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.mysql.jdbc.Util;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.PlansEnum;
import util.StatusEnum;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.Utils;

@Entity
public class Invoice extends Model {

	@Required
	public String obs;

	@Temporal(TemporalType.TIMESTAMP)
	public Date memberSinceDate = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	public Date invoiceGenerationDate = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	public Date invoiceDueDate = new Date();

	@Enumerated(EnumType.STRING)
	public StatusInvoiceEnum statusInvoice = StatusInvoiceEnum.Current;

	@Enumerated(EnumType.STRING)
	public StatusPaymentEnum statusPayment = StatusPaymentEnum.Free;

	@Enumerated(EnumType.STRING)
	public PlansEnum plan = PlansEnum.SPO01;

	public Float smsValue = 0f;

	public Float smsUnitPrice = 0f;

	public long smsQtd = 0l;

	public Float value = 0f;

	@Hidden
	public long userId;

	@Hidden
	public long institutionId;

	@Hidden
	public String postedAt;

	public boolean isActive = true;

	public String toString() {
		return obs;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setPostedAt(String postedAt) {
		this.postedAt = postedAt;
	}

	public String getPostedAt() throws ParseException {
		if (this.postedAt == null) {
			setPostedAt(Utils.getCurrentDateTime());
		}
		return postedAt;
	}

	public long getInstitutionId() {
		return Admin.getLoggedUserInstitution().getInstitution() == null ? 0l
				: Admin.getLoggedUserInstitution().getInstitution().getId();
	}

	public void setInstitutionId(long institutionId) {
		this.institutionId = institutionId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getMemberSinceDate() {
		return memberSinceDate;
	}

	public void setMemberSinceDate(Date memberSinceDate) {
		this.memberSinceDate = memberSinceDate;
	}

	public Date getInvoiceGenerationDate() {
		return invoiceGenerationDate;
	}

	public void setInvoiceGenerationDate(Date invoiceGenerationDate) {
		this.invoiceGenerationDate = invoiceGenerationDate;
	}

	public Date getInvoiceDueDate() {
		return invoiceDueDate;
	}

	public void setInvoiceDueDate(Date invoiceDueDate) {
		this.invoiceDueDate = invoiceDueDate;
	}

	public Float getValue() {
		value = value + getSmsValue();
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public StatusPaymentEnum getStatusPayment() {
		return statusPayment;
	}

	public void setStatusPayment(StatusPaymentEnum statusPayment) {
		this.statusPayment = statusPayment;
	}

	public StatusInvoiceEnum getStatusInvoice() {
		return statusInvoice;
	}

	public void setStatusInvoice(StatusInvoiceEnum statusInvoice) {
		this.statusInvoice = statusInvoice;
	}

	public Float getSmsValue() {
		setSmsValue(smsUnitPrice * smsQtd);
		return smsValue;
	}

	public void setSmsValue(Float smsValue) {
		this.smsValue = smsValue;
	}

	public long getSmsQtd() {
		return smsQtd;
	}

	public void setSmsQtd(long smsQtd) {
		this.smsQtd = smsQtd;
	}

	public Float getSmsUnitPrice() {
		return smsUnitPrice;
	}

	public void setSmsUnitPrice(Float smsUnitPrice) {
		this.smsUnitPrice = smsUnitPrice;
	}
	
	public String getTotalCurrency() {
		return Utils.getCurrencyValue(getValue());
	}

	public String getSMSTotalCurrency() {
		return Utils.getCurrencyValue(getSmsValue());
	}

	public String getSMSUnitPriceCurrency() {
		return Utils.getCurrencyValue(getSmsUnitPrice());
	}
	
	public String getPostedAtParsed() throws ParseException {
		return Utils.parseStringDateTime(postedAt);
	}
	
	public PlansEnum getPlan() {
		return plan;
	}

	public void setPlan(PlansEnum plan) {
		this.plan = plan;
	}

}
