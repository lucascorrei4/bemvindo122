package models;

import java.text.ParseException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.ivy.Main;

import com.mysql.jdbc.Util;

import controllers.Admin;
import controllers.CRUD.Hidden;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;
import util.StatusEnum;
import util.StatusInvoiceEnum;
import util.StatusPaymentEnum;
import util.Utils;

@Entity
public class Parameter extends Model {

	public Float smsPricePlan = 0f;

	public Float mininumSalary = 0f;

	public Float percentValuePlan = 0f;

	@Lob
	public String msgNewUsers;
	
	@Lob
	public String msgActiveUsers;
	
	@Hidden
	public Float currentPricePlan = 0f;

	public String toString() {
		return "Mensalidade atual: " + currentPricePlan;
	}

	public Float getCurrentPricePlan() {
		setCurrentPricePlan(mininumSalary * percentValuePlan / 100);
		return currentPricePlan;
	}

	public void setCurrentPricePlan(Float currentPricePlan) {
		this.currentPricePlan = currentPricePlan;
	}

	public Float getSmsPricePlan() {
		return smsPricePlan;
	}

	public void setSmsPricePlan(Float smsPricePlan) {
		this.smsPricePlan = smsPricePlan;
	}

	public Float getPercentValuePlan() {
		return percentValuePlan;
	}

	public void setPercentValuePlan(Float percentValuePlan) {
		this.percentValuePlan = percentValuePlan;
	}

	public Float getMininumSalary() {
		return mininumSalary;
	}

	public void setMininumSalary(Float mininumSalary) {
		this.mininumSalary = mininumSalary;
	}
	
	public static void main(String[] args) {
		System.out.println(816 * 10 /100);
	}

	public String getMsgNewUsers() {
		return msgNewUsers;
	}

	public void setMsgNewUsers(String msgNewUsers) {
		this.msgNewUsers = msgNewUsers;
	}

	public String getMsgActiveUsers() {
		return msgActiveUsers;
	}

	public void setMsgActiveUsers(String msgActiveUsers) {
		this.msgActiveUsers = msgActiveUsers;
	}

}
