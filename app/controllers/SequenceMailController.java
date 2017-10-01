package controllers;

import java.util.Date;
import java.util.List;

import models.MailList;
import models.SequenceMail;
import models.SequenceMailQueue;
import util.Utils;

public class SequenceMailController {

	public static void addLeadToSalesFunnel(MailList mailList) {
		if (!Utils.isNullOrEmpty(mailList.getUrl())) {
			List<SequenceMail> sequenceMailList = SequenceMail.find("url = '" + mailList.getUrl() + "' or url = '" + mailList.getUrl().concat("#main") + "' order by sequence asc").fetch();
			if (Utils.isNullOrEmpty(sequenceMailList)) {
				return;
			}
			addLeadToSalesFunnel(mailList, sequenceMailList);
		}
	}

	public static void addLeadToSalesFunnel(MailList mailList, List<SequenceMail> sequenceMailList) {
		SequenceMailQueue queue = null;
		for (int i = 0; i < sequenceMailList.size(); i++) {
			Long sequenceMailId = sequenceMailList.get(i).id;
			queue = new SequenceMailQueue(); 
			queue = SequenceMailQueue
					.find("name = '" + mailList.getName() + "' and mail = '" + mailList.getMail() + "' and sequenceMail_id = " + sequenceMailId).first();
			if (queue == null) {
				queue = new SequenceMailQueue();
				if (sequenceMailList.get(i).sequence == 1) {
					queue.setJobDate(new Date());
				} else {
					queue.setJobDate(Utils.addDays(new Date(), sequenceMailList.get(i).sequence - 1));
				}
				queue.setName(mailList.getName());
				queue.setMail(mailList.getMail());
				queue.setSequenceMail(sequenceMailList.get(i));
				queue.setPostedAt(Utils.getCurrentDateTime());
				queue.setSent(false);
				queue.willBeSaved = true;
				queue.save();
			}
		}
	}

}
