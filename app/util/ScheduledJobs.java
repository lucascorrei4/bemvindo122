package util;

import java.util.Calendar;
import java.util.List;

import controllers.Admin;
import controllers.MailController;
import controllers.SequenceMailController;
import models.BodyMail;
import models.MailList;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.SequenceMail;
import models.SequenceMailQueue;
import models.StatusMail;
import play.jobs.Job;
import play.jobs.On;

//** Fire at 8am (noon) every day **/ 
//@On("0 0 8 * * ?")
@On("0 57 0 ? * *")
public class ScheduledJobs extends Job {
	
	public void doJob() {
		verifyIfLeadIsNotInSalesFunnel();
		sendMailToLead();
	}
	
	private void verifyIfLeadIsNotInSalesFunnel() {
		List<MailList> mailList = new MailList().find("isActive = true order by postedAt desc").fetch();
		for (MailList mL : mailList) {
			SequenceMailController.addLeadToSalesFunnel(mL);
		}
	}

	public void sendMailToLead() {
		Calendar calendar = Utils.getBrazilCalendar();
		List<SequenceMailQueue> sequenceMailQueueList = SequenceMailQueue.find("jobDate", calendar.getTime()).fetch();
		for (SequenceMailQueue sequenceMailQueue : sequenceMailQueueList) {
			sendEmailToLead(sequenceMailQueue);
		}
	}
	
	private static void sendEmailToLead(SequenceMailQueue sequenceMailQueue) {
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
			sender.setCompany("Seu Pedido Online");
			sender.setFrom("contato@seupedido.online");
			sender.setKey("Sales Funnel");
			/* SendTo object */
			BodyMail bodyMail = new BodyMail();
			bodyMail.setTitle1(sequenceMailQueue.getSequenceMail().getDescription());
			bodyMail.setTitle2("");
			bodyMail.setFooter1("");
			bodyMail.setImage1(parameter.getLogoUrl());
			bodyMail.setBodyHTML(MailController.getHTMLTemplateSimple(bodyMail));
			if (mailController.sendHTMLMail(sendTo, sender, bodyMail, null)) {
				Admin.sendMailToMeWithCustomInfo("E-mail disparado ao lead!", "Nome: " + sequenceMailQueue.getName().concat(" - E-mail: ").concat(sequenceMailQueue.getMail()));
			}
		}
	}
}
