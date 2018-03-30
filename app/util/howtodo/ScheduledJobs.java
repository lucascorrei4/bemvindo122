package util.howtodo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import controllers.howtodo.MailController;
import controllers.howtodo.SequenceMailController;
import models.SendTo;
import models.Sender;
import models.StatusMail;
import models.howtodo.BodyMail;
import models.howtodo.MailList;
import models.howtodo.Parameter;
import models.howtodo.SequenceMailQueue;
import play.jobs.Job;
import play.jobs.On;
import util.Utils;

// Fire every hour between 6AM and 22AM = 0 0 6-22 ? * * * 
// Fire every 5 minutes between 6AM and 22AM = 0 */5 6-22 ? * * * 
// Fire every 3 minutes 0 */3 * ? * * 
// @On("0 0 7-22 ? * * *")
//@On("0 */5 6-22 ? * * *")
@On("0 */5 * ? * *")
public class ScheduledJobs extends Job {

	public void doJob() throws ParseException {
		System.out.println("Running cron at " + Utils.dateNow());
		verifyIfLeadIsNotInSalesFunnel();
		sendMailToLead();
		System.out.println("Finished cron at " + Utils.dateNow());
	}

	private void verifyIfLeadIsNotInSalesFunnel() throws ParseException {
		List<MailList> mailList = new MailList().find("isActive = true order by postedAt desc").fetch();
		for (MailList mL : mailList) {
			SequenceMailController.addLeadToSalesFunnel(mL);
		}
	}

	public void sendMailToLead() {
		/* Brazilian time 15 minutes ago */
		Calendar cal = Utils.getBrazilCalendar();
	    cal.add(Calendar.MINUTE, -15);
	    Timestamp dateTimeOlder15Minutes = new Timestamp(cal.getTimeInMillis());
	    /* Brazilian time now */
	    cal = Utils.getBrazilCalendar();
	    Timestamp dateTimeNow = new Timestamp(cal.getTimeInMillis());
		List<SequenceMailQueue> sequenceMailQueueList = SequenceMailQueue.find("sent = false and jobDate BETWEEN '" + dateTimeOlder15Minutes + "' AND '" + dateTimeNow + "'").fetch();
		for (SequenceMailQueue sequenceMailQueue : sequenceMailQueueList) {
			if (sendEmailToLead(sequenceMailQueue)) {
				sequenceMailQueue.setSent(true);
				sequenceMailQueue.willBeSaved = true;
				sequenceMailQueue.merge();
			}
		}
	}

	private static boolean sendEmailToLead(SequenceMailQueue sequenceMailQueue) {
		Parameter parameter = Parameter.all().first();
		if (!Utils.isNullOrEmpty(sequenceMailQueue.getMail()) && Utils.validateEmail(sequenceMailQueue.getMail()) && !parameter.getSiteDomain().contains("localhost:90")) {
			MailController mailController = new MailController();
			/* SendTo object */
			SendTo sendTo = new SendTo();
			sendTo.setDestination(sequenceMailQueue.getMail());
			sendTo.setName(sequenceMailQueue.getName());
			sendTo.setSex("");
			sendTo.setStatus(new StatusMail());
			/* Sender object */
			Sender sender = new Sender();
			sender.setCompany(Utils.isNullOrEmpty(parameter.getMailSenderName()) ? parameter.getSiteTitle() : parameter.getMailSenderName());
			sender.setFrom(Utils.isNullOrEmpty(parameter.getMailSenderFrom()) ? parameter.getSiteMail() : parameter.getMailSenderFrom());
			sender.setKey("Sales Funnel");
			/* SendTo object */
			BodyMail bodyMail = new BodyMail();
			bodyMail.setTitle1("");
			bodyMail.setTitle2("");
			bodyMail.setFooter1("");
			bodyMail.setImage1(parameter.getSiteDomain() + "/logo");
			String firstName = sequenceMailQueue.getName().indexOf(" ") > -1 ? sequenceMailQueue.getName().substring(0, sequenceMailQueue.getName().indexOf(" ")) : sequenceMailQueue.getName();
			/* replace @lead@ = Name of lead */
			/* replace /uid/ = Adding encoded mail to complete search lead URL in opinion search page like: https://acompanheseupedido.com/pesquisa/cid/cpg_01/uid/{mail-encoded} */
			String bodyHTML = sequenceMailQueue.getSequenceMail().getDescription().replace("@lead@", firstName).replace("/uid/", "/uid/" + Utils.encode(sequenceMailQueue.getMail())).concat(Utils.unsubscribeHTMLSendPulse(parameter.getSiteDomain(), sequenceMailQueue.getMail(), sequenceMailQueue.id))
					.concat(Utils.sentCredits(parameter.getSiteTitle(), parameter.getSiteDomain()));
			bodyMail.setBodyHTML(bodyHTML);
			String subject = sequenceMailQueue.getSequenceMail().getTitle().replace("@lead@", firstName);
			if (mailController.sendHTMLMail(sendTo, sender, bodyMail, subject, sequenceMailQueue, parameter)) {
//				mailController.sendMailToMeWithCustomInfo("E-mail disparado ao lead!", "Nome: " + sequenceMailQueue.getName() + " - E-mail: " + sequenceMailQueue.getMail());
				return true;
			}
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		System.out.println("http://localhost:9002".contains("localhost:90"));
		
	}

}
