package util;

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

// Fire every hour between 7AM and 22AM 0 0 7-22 ? * * * 
// Fire every 3 minutes 0 */3 * ? * * 
@On("0 0 7-22 ? * * *")
public class ScheduledJobs extends Job {

	public void doJob() {
		System.out.println("Running cron at " + Utils.dateNow());
		verifyIfLeadIsNotInSalesFunnel();
		sendMailToLead();
		System.out.println("Finished cron at " + Utils.dateNow());
	}

	private void verifyIfLeadIsNotInSalesFunnel() {
		List<MailList> mailList = new MailList().find("isActive = true order by postedAt desc").fetch();
		for (MailList mL : mailList) {
			SequenceMailController.addLeadToSalesFunnel(mL);
		}
	}

	public void sendMailToLead() {
		Calendar calendar = Utils.getBrazilCalendar();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		List<SequenceMailQueue> sequenceMailQueueList = SequenceMailQueue.find("date(jobDate) = CURRENT_DATE and sent = false").fetch();
		for (SequenceMailQueue sequenceMailQueue : sequenceMailQueueList) {
			sendEmailToLead(sequenceMailQueue);
			sequenceMailQueue.setSent(true);
			sequenceMailQueue.willBeSaved = true;
			sequenceMailQueue.merge();
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
			bodyMail.setTitle1("");
			bodyMail.setTitle2("");
			bodyMail.setFooter1("");
			bodyMail.setImage1(parameter.getLogoUrl());
			String firstName = sequenceMailQueue.getName().indexOf(" ") > -1 ? sequenceMailQueue.getName().substring(0, sequenceMailQueue.getName().indexOf(" ")) : sequenceMailQueue.getName();
			bodyMail.setBodyHTML(sequenceMailQueue.getSequenceMail().getDescription().replace("@lead@", firstName));
			String subject = sequenceMailQueue.getSequenceMail().getTitle().replace("@lead@", firstName);
			if (mailController.sendHTMLMail(sendTo, sender, bodyMail, subject)) {
				mailController.sendMailToMeWithCustomInfo("E-mail disparado ao lead!", "Nome: " + sequenceMailQueue.getName() + " - E-mail: " + sequenceMailQueue.getMail());
			}
		}
	}

	public static void main(String[] args) {
		String name = "Lucas Correia";
		System.out.println(name.indexOf(" ") > -1 ? name.substring(0, name.indexOf(" ")) : name);
	}
}
