package controllers.howtodo;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.howtodo.MailList;
import models.howtodo.Parameter;
import models.howtodo.SequenceMail;
import models.howtodo.SequenceMailQueue;
import util.Utils;

public class SequenceMailController {

	public static void addLeadToSalesFunnel(MailList mailList) throws ParseException {
		Parameter parameter = Parameter.getCurrentParameter();
		if (!Utils.isNullOrEmpty(mailList.getUrl())) {
			String url = returnCleanURL(mailList.getUrl(), parameter.getSiteDomain());
			List<SequenceMail> sequenceMailList = SequenceMail.find("isActive = true and url = '" + url + "' order by sequence asc").fetch();
			if (Utils.isNullOrEmpty(sequenceMailList)) {
				return;
			}
			addLeadToSalesFunnel(mailList, sequenceMailList, parameter);
		}
	}

	private static String returnCleanURL(String url, String siteDomain) {
		String newUrl = "";
		newUrl = url.replace("/#main", "").replace("/?utm_term", "");
		if (newUrl.charAt(newUrl.length() - 1) == '/') {
			newUrl = newUrl.substring(0, newUrl.length() - 1);
		}
		/*
		 * If not, the url is not the index page, so, I remove the domain part
		 */
		if (!newUrl.equals(siteDomain)) {
			newUrl = newUrl.replace(siteDomain, "");
		}
		return newUrl;
	}

	public static void addLeadToSalesFunnel(MailList mailList, List<SequenceMail> sequenceMailList, Parameter parameter) throws ParseException {
		SequenceMailQueue queue = null;
		for (int i = 0; i < sequenceMailList.size(); i++) {
			Long sequenceMailId = sequenceMailList.get(i).id;
			queue = new SequenceMailQueue();
			queue = SequenceMailQueue.find("name = '" + mailList.getName() + "' and mail = '" + mailList.getMail() + "' and sequenceMail_id = " + sequenceMailId).first();
			if (queue == null) {
				queue = new SequenceMailQueue();
				if (sequenceMailList.get(i).isSendSpecificDay()) {
					if (sequenceMailList.get(i).isSendSpecificDayAndTime() && !Utils.isNullOrEmpty(sequenceMailList.get(i).getSpecificDateTime())) {
						queue.setJobDate(Utils.parseStringDateTimeToDate(sequenceMailList.get(i).getSpecificDateTime()));
					} else if (sequenceMailList.get(i).isSendSpecificDay())  {
						Calendar c = Calendar.getInstance();
						c.set(Calendar.DAY_OF_WEEK, Integer.valueOf(sequenceMailList.get(i).getDayOfWeek().getValue()));
						c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(sequenceMailList.get(i).getHourOfDay().getValue().substring(0, 2)));
						c.set(Calendar.MINUTE, Integer.valueOf(sequenceMailList.get(i).getHourOfDay().getValue().substring(3, 5)));
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);
						queue.setJobDate(c.getTime());
					}
				} else {
					if (sequenceMailList.get(i).sequence == 1) {
						Calendar calendar = Utils.getBrazilCalendar();
						calendar.set(Calendar.SECOND, 0);
						queue.setJobDate(calendar.getTime());
					} else if ((sequenceMailList.get(i).sequence > 1) && (parameter.getMailSendInterval() == 1)) {
						Calendar calendar = Utils.getBrazilCalendar();
						calendar.set(Calendar.SECOND, 0);
						calendar.add(Calendar.DATE, (sequenceMailList.get(i).sequence - 1));
						queue.setJobDate(calendar.getTime());
					} else if ((sequenceMailList.get(i).sequence > 1) && (parameter.getMailSendInterval() > 1)) {
						Calendar calendar = Utils.getBrazilCalendar();
						calendar.set(Calendar.SECOND, 0);
						calendar.add(Calendar.DATE, ((sequenceMailList.get(i).sequence - 1) + parameter.getMailSendInterval()));
						queue.setJobDate(calendar.getTime());
					}
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
