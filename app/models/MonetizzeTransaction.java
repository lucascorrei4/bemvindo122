package models;

import java.text.ParseException;

import javax.persistence.Entity;

import controllers.CRUD.Hidden;
import play.db.jpa.Model;
import util.Utils;

@Entity
public class MonetizzeTransaction extends Model {

	public String body;
	public String urlSrc;
	public String urlUtmSource;
	public String urlUtmMedium;
	public String urlUtmContent;
	public String urlUtmCampaing;
	public String uniqueKey;
	public String productCode;
	public String productName;
	public String productKey;
	public String sellCode;
	public String sellPlan;
	public String sellStartDate;
	public String sellFinishDate;
	public String sellMeansOfPayment;
	public String sellFormOfPayment;
	public String sellRemainingWarranty;
	public String sellStatus;
	public String sellValue;
	public String sellQuantity;
	public String sellFreightType;
	public String sellFreight;
	public String sellReceivedValue;
	public String sellPalan;
	public String sellCoupon;
	public String sellBillet;
	public String sellDigitableLine;
	public String sellSource;
	public String sellUtmSource;
	public String sellUtmMedium;
	public String sellUtmContent;
	public String sellUtmCampaing;
	public String comissionName;
	public String comissionType;
	public String comissionValue;
	public String comissionPercent;
	public String customerName;
	public String customerMail;
	public String customerBirthDate;
	public String customerCGC;
	public String customerPhone;
	public String customerCEP;
	public String customerAddress;
	public String customerAddressNumber;
	public String customerAddressComplement;
	public String customerNeighborhood;
	public String customerCity;
	public String customerState;
	public String customerCountry;
	public String signatureStatus;
	public String signatureDate;
	public String signatureCode;

	@Hidden
	public String postedAt;

	public String toString() {
		return this.productName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUrlSrc() {
		return urlSrc;
	}

	public void setUrlSrc(String urlSrc) {
		this.urlSrc = urlSrc;
	}

	public String getUrlUtmSource() {
		return urlUtmSource;
	}

	public void setUrlUtmSource(String urlUtmSource) {
		this.urlUtmSource = urlUtmSource;
	}

	public String getUrlUtmMedium() {
		return urlUtmMedium;
	}

	public void setUrlUtmMedium(String urlUtmMedium) {
		this.urlUtmMedium = urlUtmMedium;
	}

	public String getUrlUtmContent() {
		return urlUtmContent;
	}

	public void setUrlUtmContent(String urlUtmContent) {
		this.urlUtmContent = urlUtmContent;
	}

	public String getUrlUtmCampaing() {
		return urlUtmCampaing;
	}

	public void setUrlUtmCampaing(String urlUtmCampaing) {
		this.urlUtmCampaing = urlUtmCampaing;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getComissionValue() {
		return comissionValue;
	}

	public void setComissionValue(String comissionValue) {
		this.comissionValue = comissionValue;
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

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String getSellCode() {
		return sellCode;
	}

	public void setSellCode(String sellCode) {
		this.sellCode = sellCode;
	}

	public String getSellStartDate() {
		return sellStartDate;
	}

	public void setSellStartDate(String sellStartDate) {
		this.sellStartDate = sellStartDate;
	}

	public String getSellFinishDate() {
		return sellFinishDate;
	}

	public void setSellFinishDate(String sellFinishDate) {
		this.sellFinishDate = sellFinishDate;
	}

	public String getSellMeansOfPayment() {
		return sellMeansOfPayment;
	}

	public void setSellMeansOfPayment(String sellMeansOfPayment) {
		this.sellMeansOfPayment = sellMeansOfPayment;
	}

	public String getSellFormOfPayment() {
		return sellFormOfPayment;
	}

	public void setSellFormOfPayment(String sellFormOfPayment) {
		this.sellFormOfPayment = sellFormOfPayment;
	}

	public String getSellRemainingWarranty() {
		return sellRemainingWarranty;
	}

	public void setSellRemainingWarranty(String sellRemainingWarranty) {
		this.sellRemainingWarranty = sellRemainingWarranty;
	}

	public String getSellStatus() {
		return sellStatus;
	}

	public void setSellStatus(String sellStatus) {
		this.sellStatus = sellStatus;
	}

	public String getSellValue() {
		return sellValue;
	}

	public void setSellValue(String sellValue) {
		this.sellValue = sellValue;
	}

	public String getSellQuantity() {
		return sellQuantity;
	}

	public void setSellQuantity(String sellQuantity) {
		this.sellQuantity = sellQuantity;
	}

	public String getSellFreightType() {
		return sellFreightType;
	}

	public void setSellFreightType(String sellFreightType) {
		this.sellFreightType = sellFreightType;
	}

	public String getSellFreight() {
		return sellFreight;
	}

	public void setSellFreight(String sellFreight) {
		this.sellFreight = sellFreight;
	}

	public String getSellReceivedValue() {
		return sellReceivedValue;
	}

	public void setSellReceivedValue(String sellReceivedValue) {
		this.sellReceivedValue = sellReceivedValue;
	}

	public String getSellPalan() {
		return sellPalan;
	}

	public void setSellPalan(String sellPalan) {
		this.sellPalan = sellPalan;
	}

	public String getSellCoupon() {
		return sellCoupon;
	}

	public void setSellCoupon(String sellCoupon) {
		this.sellCoupon = sellCoupon;
	}

	public String getSellBillet() {
		return sellBillet;
	}

	public void setSellBillet(String sellBillet) {
		this.sellBillet = sellBillet;
	}

	public String getSellDigitableLine() {
		return sellDigitableLine;
	}

	public void setSellDigitableLine(String sellDigitableLine) {
		this.sellDigitableLine = sellDigitableLine;
	}

	public String getSellSource() {
		return sellSource;
	}

	public void setSellSource(String sellSource) {
		this.sellSource = sellSource;
	}

	public String getComissionName() {
		return comissionName;
	}

	public void setComissionName(String comissionName) {
		this.comissionName = comissionName;
	}

	public String getComissionType() {
		return comissionType;
	}

	public void setComissionType(String comissionType) {
		this.comissionType = comissionType;
	}

	public String getComissionPercent() {
		return comissionPercent;
	}

	public void setComissionPercent(String comissionPercent) {
		this.comissionPercent = comissionPercent;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMail() {
		return customerMail;
	}

	public void setCustomerMail(String customerMail) {
		this.customerMail = customerMail;
	}

	public String getCustomerBirthDate() {
		return customerBirthDate;
	}

	public void setCustomerBirthDate(String customerBirthDate) {
		this.customerBirthDate = customerBirthDate;
	}

	public String getCustomerCGC() {
		return customerCGC;
	}

	public void setCustomerCGC(String customerCGC) {
		this.customerCGC = customerCGC;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerCEP() {
		return customerCEP;
	}

	public void setCustomerCEP(String customerCEP) {
		this.customerCEP = customerCEP;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddressNumber() {
		return customerAddressNumber;
	}

	public void setCustomerAddressNumber(String customerAddressNumber) {
		this.customerAddressNumber = customerAddressNumber;
	}

	public String getCustomerAddressComplement() {
		return customerAddressComplement;
	}

	public void setCustomerAddressComplement(String customerAddressComplement) {
		this.customerAddressComplement = customerAddressComplement;
	}

	public String getCustomerNeighborhood() {
		return customerNeighborhood;
	}

	public void setCustomerNeighborhood(String customerNeighborhood) {
		this.customerNeighborhood = customerNeighborhood;
	}

	public String getCustomerCity() {
		return customerCity;
	}

	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}

	public String getCustomerState() {
		return customerState;
	}

	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}

	public String getCustomerCountry() {
		return customerCountry;
	}

	public void setCustomerCountry(String customerCountry) {
		this.customerCountry = customerCountry;
	}

	public String getSellPlan() {
		return sellPlan;
	}

	public void setSellPlan(String sellPlan) {
		this.sellPlan = sellPlan;
	}

	public String getSellUtmSource() {
		return sellUtmSource;
	}

	public void setSellUtmSource(String sellUtmSource) {
		this.sellUtmSource = sellUtmSource;
	}

	public String getSellUtmMedium() {
		return sellUtmMedium;
	}

	public void setSellUtmMedium(String sellUtmMedium) {
		this.sellUtmMedium = sellUtmMedium;
	}

	public String getSellUtmContent() {
		return sellUtmContent;
	}

	public void setSellUtmContent(String sellUtmContent) {
		this.sellUtmContent = sellUtmContent;
	}

	public String getSellUtmCampaing() {
		return sellUtmCampaing;
	}

	public void setSellUtmCampaing(String sellUtmCampaing) {
		this.sellUtmCampaing = sellUtmCampaing;
	}

	public String getSignatureStatus() {
		return signatureStatus;
	}

	public void setSignatureStatus(String signatureStatus) {
		this.signatureStatus = signatureStatus;
	}

	public String getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}

	public String getSignatureCode() {
		return signatureCode;
	}

	public void setSignatureCode(String signatureCode) {
		this.signatureCode = signatureCode;
	}

}
