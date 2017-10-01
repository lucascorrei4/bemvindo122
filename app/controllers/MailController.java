
package controllers;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import models.BodyMail;
import models.Parameter;
import models.SendTo;
import models.Sender;
import models.StatusMail;
import util.ApplicationConfiguration;
import util.MailTemplates;
import util.Utils;

public class MailController {
	public static Logger logger = Logger.getLogger(MailController.class);

	public boolean sendHTMLMail(SendTo sendTo, Sender sender, BodyMail bodyMail, String subject) {
		final String userName = ApplicationConfiguration.getInstance().getMailUserName();
		final String password = ApplicationConfiguration.getInstance().getMailPassword();
		String hostName = ApplicationConfiguration.getInstance().getMailHostName();
		if (validateMailCredentials(userName, password, hostName)) {
			Properties properties = new Properties();
			properties.put("mail.transport.protocol", "smtp");
			properties.put("mail.smtp.host", hostName);
			properties.put("mail.smtp.socketFactory.port", "465");
			properties.put("mail.smtp.port", "25");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			/* Trying connect do smtp server */
			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			};
			/* Parse and send content */
			try {
				Session session = Session.getInstance(properties, auth);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(sender.from, sender.company, "utf-8"));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(sendTo.destination));
				if (Utils.isNullOrEmpty(subject)) {
					message.setSubject(Utils.removeHTML(bodyMail.title2));
				} else {
					message.setSubject(Utils.removeHTML(subject));
				}
				String htmlMessage = bodyMail.getBodyHTML();
				Multipart multipart = new MimeMultipart();
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				mimeBodyPart.setContent(htmlMessage, "text/html");
				multipart.addBodyPart(mimeBodyPart);
				message.setContent(multipart);
				message.saveChanges();
				Transport.send(message);
				logger.info("Success sending email to " + sendTo.destination + ".");
			} catch (MessagingException e) {
				logger.error("Problem sending email to " + sendTo.destination + ". Error: " + e.getMessage() + ". Cause: " + e.getCause());
				return false;
			} catch (UnsupportedEncodingException e) {
				logger.error("Problem sending email to " + sendTo.destination + ". Error: " + e.getMessage() + ". Cause: " + e.getCause());
				return false;
			}
		} else {
			logger.error("Problem validate mail credentials (username, password, hostname). Verify project configuration properties.");
		}
		return true;
	}

	private boolean validateMailCredentials(String userName, String password, String hostName) {
		if (Utils.isNullOrEmpty(userName) || Utils.isNullOrEmpty(password) || Utils.isNullOrEmpty(hostName)) {
			return false;
		}
		return true;
	}

	public void sendMailToMeWithCustomInfo(String message, String info) {
		Parameter parameter = Parameter.all().first();
		MailController mailController = new MailController();
		/* SendTo object */
		SendTo sendTo = new SendTo();
		sendTo.setDestination("lucascorreiaevangelista@gmail.com");
		sendTo.setName("Eu mesmo");
		sendTo.setSex("");
		sendTo.setStatus(new StatusMail());
		/* Sender object */
		Sender sender = new Sender();
		sender.setCompany("Seu Pedido Online");
		sender.setFrom("contato@seupedido.online");
		sender.setKey("");
		/* SendTo object */
		BodyMail bodyMail = new BodyMail();
		bodyMail.setTitle1(message);
		bodyMail.setTitle2(info);
		bodyMail.setParagraph1("Info: " + info);
		bodyMail.setParagraph2("");
		bodyMail.setParagraph3("");
		bodyMail.setFooter1("");
		bodyMail.setImage1(parameter.getLogoUrl());
		bodyMail.setBodyHTML(MailTemplates.getHTMLTemplateSimple(bodyMail));
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null);
	}
}