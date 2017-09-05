package controllers;

import java.util.Date;
import java.util.List;

import models.MailList;
import models.SequenceMail;
import models.SequenceMailQueue;
import util.Utils;

public class SequenceMailController {

	public static void addLeadToSalesFunnel(MailList mailList) {
		List<SequenceMail> sequenceMailList = SequenceMail.find("url = '" + mailList.getUrl() + "' order by sequence asc").fetch();
		if (Utils.isNullOrEmpty(sequenceMailList)) {
			return;
		}
		if (!leadIsInSalesFunnel(mailList)) {
			addLeadToSalesFunnel(mailList, sequenceMailList);
		}
	}

	public static void addLeadToSalesFunnel(MailList mailList, List<SequenceMail> sequenceMailList) {
		for (SequenceMail sequenceMail : sequenceMailList) {
			SequenceMailQueue queue = new SequenceMailQueue();
			queue.setJobDate(Utils.addDays(new Date(), sequenceMail.getSequence()));
			queue.setName(mailList.getName());
			queue.setMail(mailList.getMail());
			queue.setSequenceMail(sequenceMail);
			queue.setPostedAt(Utils.getCurrentDateTime());
			queue.setSent(false);
			queue.willBeSaved = true;
			queue.save();
		}
	}

	public static boolean leadIsInSalesFunnel(MailList mailList) {
		List<SequenceMailQueue> sequenceMailQueue = SequenceMailQueue.find("mail", mailList.getMail()).fetch();
		if (Utils.isNullOrEmpty(sequenceMailQueue)) {
			return false;
		}
		return true;
	}

}
