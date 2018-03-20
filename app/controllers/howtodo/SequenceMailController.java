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

	public static void addLeadToSalesFunnel(MailList lead, List<SequenceMail> sequenceMailList, Parameter parameter) throws ParseException {
		SequenceMailQueue queue = null;
		boolean addToQueue = false;
		for (int i = 0; i < sequenceMailList.size(); i++) {
			Long sequenceMailId = sequenceMailList.get(i).id;
			queue = new SequenceMailQueue();
			/* Exclude Who Will Receive Other Mails From Same URL Queue */
			if (sequenceMailList.get(i).isExcludeWhoDontReceiveOthersMails()) {
				queue = findMailsNotSentYet(lead.getMail());
				if (queue == null) {
					queue = findMailsAlreadyInQueue(lead.getMail(), sequenceMailId);
				} else {
					continue;
				}
			} else {
				queue = findMailsAlreadyInQueue(lead.getMail(), sequenceMailId);
			}
			if (queue == null) {
				addToQueue = true;
			} else {
				addToQueue = false;
			}
			if (addToQueue) {
				queue = new SequenceMailQueue();
				if (sequenceMailList.get(i).isSendSpecificDay()) {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.DAY_OF_WEEK, Integer.valueOf(sequenceMailList.get(i).getDayOfWeek().getValue()));
					c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(sequenceMailList.get(i).getHourOfDay().getValue().substring(0, 2)));
					c.set(Calendar.MINUTE, Integer.valueOf(sequenceMailList.get(i).getHourOfDay().getValue().substring(3, 5)));
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);
					queue.setJobDate(c.getTime());
				} else if (sequenceMailList.get(i).isSendSpecificDayAndTime() && !Utils.isNullOrEmpty(sequenceMailList.get(i).getSpecificDateTime())) {
					Date scheduledDate = Utils.parseStringDateTimeToDate(sequenceMailList.get(i).getSpecificDateTime());
					if (scheduledDate.after(Utils.getBrazilCalendar().getTime())) {
						queue.setJobDate(scheduledDate);
					} else {
						continue;
					}
				} else {
					if (sequenceMailList.get(i).sequence == 1) {
						Calendar calendar = Utils.getBrazilCalendar();
						calendar.set(Calendar.SECOND, 0);
						queue.setJobDate(calendar.getTime());
					} else if ((sequenceMailList.get(i).sequence > 1) && (parameter.getMailSendInterval() == 1)) {
						Calendar calendar = Utils.getBrazilCalendar();
						calendar.set(Calendar.HOUR_OF_DAY, parameter.getStandarHourToSendMails());
						calendar.set(Calendar.SECOND, 0);
						calendar.add(Calendar.DATE, (sequenceMailList.get(i).sequence - 1));
						queue.setJobDate(calendar.getTime());
					} else if ((sequenceMailList.get(i).sequence > 1) && (parameter.getMailSendInterval() > 1)) {
						Calendar calendar = Utils.getBrazilCalendar();
						calendar.set(Calendar.HOUR_OF_DAY, parameter.getStandarHourToSendMails());
						calendar.set(Calendar.SECOND, 0);
						calendar.add(Calendar.DATE, ((sequenceMailList.get(i).sequence - 1) + parameter.getMailSendInterval()));
						queue.setJobDate(calendar.getTime());
					}
				}
				queue.setName(lead.getName());
				queue.setMail(lead.getMail());
				queue.setSequenceMail(sequenceMailList.get(i));
				queue.setPostedAt(Utils.getCurrentDateTime());
				queue.setSent(false);
				queue.willBeSaved = true;
				queue.save();
			}
		}
	}

	public static void main(String[] args) throws ParseException {
		Date scheduledDate = Utils.parseStringDateTimeToDate("09/03/2018 11:33:00");
		if (scheduledDate.after(Utils.getBrazilCalendar().getTime())) {
			System.out.println(scheduledDate + " é futuro");
		} else {
			System.out.println(scheduledDate + " é passado");
		}
	}

	private static SequenceMailQueue findMailsAlreadyInQueue(String mail, Long sequenceMailId) {
		SequenceMailQueue queue = SequenceMailQueue.find("mail = '" + mail + "' and sequenceMail_id = " + sequenceMailId).first();
		return queue;
	}

	private static SequenceMailQueue findMailsNotSentYet(String mail) {
		SequenceMailQueue queue = SequenceMailQueue.find("sent = false and mail = '" + mail + "'").first();
		return queue;
	}

}
