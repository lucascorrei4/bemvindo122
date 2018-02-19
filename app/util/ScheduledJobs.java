package util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import controllers.MailController;
import controllers.SequenceMailController;
import models.BodyMail;
import models.MailList;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.SequenceMailQueue;
import models.StatusMail;
import play.jobs.Job;
import play.jobs.On;

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
		Parameter parameter = Parameter.all().first();
		List<MailList> mailList = new MailList().find("isActive = true order by postedAt desc").fetch();
		for (MailList mL : mailList) {
			SequenceMailController.addLeadToSalesFunnel(mL, parameter);
		}
	}

	public void sendMailToLead() {
		List<SequenceMailQueue> sequenceMailQueueList = SequenceMailQueue.find("jobDate = CURRENT_TIMESTAMP and sent = false").fetch();
		for (SequenceMailQueue sequenceMailQueue : sequenceMailQueueList) {
			if (sendEmailToLead(sequenceMailQueue)) {
				sequenceMailQueue.setSent(true);
				sequenceMailQueue.willBeSaved = true;
				sequenceMailQueue.merge();
			}
		}
	}

	private static boolean sendEmailToLead(SequenceMailQueue sequenceMailQueue) {
		if (!Utils.isNullOrEmpty(sequenceMailQueue.getMail()) && Utils.validateEmail(sequenceMailQueue.getMail())) {
			Parameter parameter = Parameter.all().first();
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
				mailController.sendMailToMeWithCustomInfo("E-mail disparado ao lead!", "Nome: " + sequenceMailQueue.getName() + " - E-mail: " + sequenceMailQueue.getMail());
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String name = "Lucas Correia";
		System.out.println(name.indexOf(" ") > -1 ? name.substring(0, name.indexOf(" ")) : name);
	}
}
