package controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.MailList;
import models.SequenceMail;
import models.SequenceMailQueue;
import util.Utils;

public class SequenceMailController {

	public static void addLeadToSalesFunnel(MailList mailList, String siteDomain) {
		if (!Utils.isNullOrEmpty(mailList.getUrl())) {
			String url = returnCleanURL(mailList.getUrl(), siteDomain);
			List<SequenceMail> sequenceMailList = SequenceMail.find("url = '" + url + "' order by sequence asc").fetch();
			if (Utils.isNullOrEmpty(sequenceMailList)) {
				return;
			}
			addLeadToSalesFunnel(mailList, sequenceMailList);
		}
	}
	
	private static String returnCleanURL(String url, String siteDomain) {
		String newUrl = "";
		newUrl = url.replace("/#main", "").replace("/?utm_term", "");
		if (newUrl.charAt(newUrl.length() - 1) == '/') {
			newUrl = newUrl.substring(0, newUrl.length() - 1);
		}
		/* If not, the url is not the index page, so, I remove the domain part */
		if (!newUrl.equals(siteDomain)) {
			newUrl = newUrl.replace(siteDomain, "");
		}
		return newUrl;
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
