
package controllers.howtodo;

import java.io.IOException;
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

import models.SendTo;
import models.Sender;
import models.StatusMail;
import models.howtodo.BodyMail;
import models.howtodo.Parameter;
import models.howtodo.SequenceMailQueue;
import play.db.jpa.Blob;
import util.ApplicationConfiguration;
import util.Utils;

public class MailController {
	public static Logger logger = Logger.getLogger(MailController.class);
	
	public boolean sendHTMLMail(SendTo sendTo, Sender sender, BodyMail bodyMail, String subject, Parameter parameter) {
		final String userName = parameter.getMailHostUser();
		final String password = parameter.getMailHostPassword();
		String hostName = parameter.getMailHostName();
		if (validateMailCredentials(userName, password, hostName)) {
			Properties properties = getSmtpProperties(parameter, hostName);
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
				String htmlMessage = Utils.validateHtmlEmail(bodyMail.getBodyHTML(), 0l);
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
	
	private static Properties getSmtpProperties(Parameter parameter, String hostName){
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", hostName);
		properties.put("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.port", parameter.getMailHostPort() == null ? "25" : parameter.getMailHostPort());
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		return properties;
	}
	
	public boolean sendHTMLMail(SendTo sendTo, Sender sender, BodyMail bodyMail, String subject, SequenceMailQueue sequenceMailQueue, Parameter parameter) {
		final String userName = parameter.getMailHostUser();
		final String password = parameter.getMailHostPassword();
		String hostName = parameter.getMailHostName();
		if (validateMailCredentials(userName, password, hostName)) {
			Properties properties = getSmtpProperties(parameter, hostName);
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
				String htmlMessage = Utils.validateHtmlEmail(bodyMail.getBodyHTML(), sequenceMailQueue.id);
				Multipart multipart = new MimeMultipart();
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				mimeBodyPart.setContent(htmlMessage, "text/html");
				multipart.addBodyPart(mimeBodyPart);
				/* Finding attachment and adding to body message mail */
				Blob attachment = sequenceMailQueue.getSequenceMail().getAttachment(); 
				if (attachment.exists()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					try {
						attachPart.attachFile(attachment.getFile());
						attachPart.setFileName(attachment.getFile().getName());
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					multipart.addBodyPart(attachPart);
				}
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

	public static String getHTMLTemplate(BodyMail bodyMail, Parameter parameter) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<div id=\":1fb\" class=\"a3s aXjCH m14d93659b00650ae\">                                                                                                                                                                                                                                                                                       ");
		sb.append(
				"   <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"      <div>                                                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"         <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"            <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                             ");
		sb.append(
				"               <div>                                                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                  <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                          ");
		sb.append(
				"                     <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                        <div>                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"                           <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                              <div bgcolor=\"#EBEBEB\">                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                 <table bgcolor=\"#EBEBEB\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">                                                                                                                                                                                                                        ");
		sb.append(
				"                                    <tbody>                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                       <tr align=\"center\">                                                                                                                                                                                                                                                                                ");
		sb.append(
				"                                          <td align=\"center\">                                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                             <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                <tbody>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                         <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center; border: #e3e3e3 solid 1px; border-radius: 7px\" width=\"382\">                                                                                                                       ");
		sb.append(
				"                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                     <table bgcolor=\"#fff\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-radius: 7px\" width=\"380\">                                                                                                                                                             ");
		sb.append(
				"                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append(
				"                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                 <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"310\">                                                                                                                                              ");
		sb.append(
				"                                                                                    <tbody>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr align=\"center\">                                                                                                                                                                                                                                ");
		sb.append("                                                                                          <td style=\"text-align: center\"><img alt=\"" + parameter.getSiteTitle() + "\" class=\"m_3594026484717175708CToWUd CToWUd\" src=\"" + bodyMail.getImage1()
				+ "\"                                                                                                                                                    ");
		sb.append(
				"                                                                                             style=\"border-radius: 5%; border: 1px solid rgb(227, 227, 227); width: 80px; min-height: 80px\"></td>                                                                                                                                         ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\">" + parameter.getSiteDomain() + "</td>                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\">                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span style=\"color: #000000\"><span                                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><span                                                           ");
		sb.append("                                                                                                   class=\"il\">" + bodyMail.getTitle1()
				+ "</span></span></span>                                                                                                                                                             ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span style=\"color: #808000\"><span                                                                                                                                                                                                        ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><strong>"
				+ bodyMail.getTitle2() + "</strong>                    ");
		sb.append(
				"                                                                                                 </span></span><br> <br> <strong> <span                                                                                                                                                                    ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\">                                                                ");
		sb.append(
				"                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\">                                                                ");
		sb.append(
				"                                                                                                VEJA ABAIXO: </span>                                                                                                                                                                                                                      ");
		sb.append(
				"                                                                                                </span>                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                                                                </strong>                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; text-align: center\"><span><span                                       ");
		sb.append(
				"                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; float: left\">C&oacute;digo:                                           ");
		sb.append("                                                                                                <strong style=\"font-weight: bold\">" + bodyMail.getParagraph1()
				+ "</strong>                                                                                                                                                                                       ");
		sb.append(
				"                                                                                                </span> </span></span>                                                                                                                                                                                                                    ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                             <br />                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; text-align: center\"><span><span                                       ");
		sb.append(
				"                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; float: left\">Fase:                                                    ");
		sb.append("                                                                                                <strong style=\"font-weight: bold\">" + bodyMail.getParagraph2()
				+ "</strong>                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                </span> </span></span>                                                                                                                                                                                                                    ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                             <br />                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; text-align: center\"><span><span                                       ");
		sb.append(
				"                                                                                                   style=\"font-size: 17px; line-height: 26px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif; color: #636363; font-weight: lighter; float: left\">Obs.:                                                    ");
		sb.append("                                                                                                <strong style=\"font-weight: bold\">" + bodyMail.getParagraph3()
				+ "</strong>                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                </span> </span></span>                                                                                                                                                                                                                    ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("																							 <td height=\"20\" style=\"height: 40px; border-bottom-color: rgb(227, 227, 227); border-bottom-style: solid; border-bottom-width: 1px; font-style: italic;\">" + bodyMail.getFooter1()
				+ "</td>                                                      ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; border-bottom: #e3e3e3 solid 1px; padding: 5px\">Clique para acompanhar as etapas e                                                                                                          ");
		sb.append(
				"                                                                                             detalhes do seu pedido                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" bgcolor=\"#4098BC\" style=\"border-radius: 5px\">                                                                                                                                                                                ");
		sb.append(
				"                                                                                             <div>                                                                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                <a href=\"" + parameter.getSiteDomain() + "\"                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"background-color: #808000; border-radius: 5px; color: #ffffff; display: inline-block; font-family: sans-serif; font-size: 16px; font-weight: bold; line-height: 50px; text-align: center; text-decoration: none; width: 310px\"  ");
		sb.append(
				"                                                                                                   target=\"_blank\" data-saferedirecturl=\"" + parameter.getSiteDomain() + "\">" + parameter.getSiteDomain() + "</a>                                                                                                                                            ");
		sb.append(
				"                                                                                             </div>                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\"><strong>Acompanhamento de Pedidos e Servi&ccedil;os</strong>                                                                                                                  ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\"                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                             style=\"font-size: 11px; color: #cfcfcf; font-style: italic; text-align: center; padding-top: 5px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif\">&nbsp;</td>                                                ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                    </tbody>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                 </table>                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                     <table bgcolor=\"#F7F7F7\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"                                                                                                                                                                                                  ");
		sb.append(
				"                                                                        style=\"border-top: #e3e3e3 solid 1px; border-bottom-right-radius: 7px; border-bottom-left-radius: 7px\" width=\"380\">                                                                                                                                               ");
		sb.append(
				"                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                 <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"310\"></table>                                                                                                                                                                    ");
		sb.append(
				"                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr align=\"center\">                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"382\">                                                                                                                                                                                        ");
		sb.append(
				"                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                               <tr style=\"text-align: center\">                                                                                                                                                                                                                                            ");
		sb.append(
				"                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                </tbody>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                             </table>                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                          </td>                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                       </tr>                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"                                    </tbody>                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"                                 </table>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"                              </div>                                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                              <div>&nbsp;</div>                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"                           </div>                                                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                        </div>                                                                                                                                                                                                                                                                                                            ");
		sb.append(
				"                        <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                     </div>                                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                     <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                  </div>                                                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"               </div>                                                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"               <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"               <div class=\"m_3594026484717175708yj6qo\">&nbsp;</div>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"               <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"            </div>                                                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"            <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                            ");
		sb.append(
				"         </div>                                                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"      </div>                                                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"      <p>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"         <br>                                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"      </p>                                                                                                                                                                                                                                                                                                                                ");
		sb.append(
				"      <div class=\"yj6qo\"></div>                                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"      <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"   </div>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"   <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                                ");
		sb.append(
				"</div>                                                                                                                                                                                                                                                                                                                                    ");
		return sb.toString();
	}

	public static String getHTMLTemplateSimple(BodyMail bodyMail, Parameter parameter) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<div id=\":1fb\" class=\"a3s aXjCH m14d93659b00650ae\">                                                                                                                                                                                                                                                                                       ");
		sb.append(
				"   <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"      <div>                                                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"         <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"            <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                             ");
		sb.append(
				"               <div>                                                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                  <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                          ");
		sb.append(
				"                     <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                        <div>                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"                           <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                              <div bgcolor=\"#EBEBEB\">                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                 <table bgcolor=\"#EBEBEB\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">                                                                                                                                                                                                                        ");
		sb.append(
				"                                    <tbody>                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                       <tr align=\"center\">                                                                                                                                                                                                                                                                                ");
		sb.append(
				"                                          <td align=\"center\">                                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                             <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                <tbody>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                         <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center; border: #e3e3e3 solid 1px; border-radius: 7px\" width=\"382\">                                                                                                                       ");
		sb.append(
				"                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                     <table bgcolor=\"#fff\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-radius: 7px\" width=\"380\">                                                                                                                                                             ");
		sb.append(
				"                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append(
				"                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                 <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"310\">                                                                                                                                              ");
		sb.append(
				"                                                                                    <tbody>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr align=\"center\">                                                                                                                                                                                                                                ");
		sb.append("                                                                                          <td style=\"text-align: center\"><img alt=\"" + parameter.getSiteTitle() + "\" class=\"m_3594026484717175708CToWUd CToWUd\" src=\"" + bodyMail.getImage1()
				+ "\"                                                                                                                                                    ");
		sb.append(
				"                                                                                             style=\"border-radius: 5%; border: 1px solid rgb(227, 227, 227); width: 80px; min-height: 80px\"></td>                                                                                                                                         ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\">" + parameter.getSiteDomain() + "</td>                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\">                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span style=\"color: #000000\"><span                                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><span                                                           ");
		sb.append("                                                                                                   class=\"il\">" + bodyMail.getTitle1()
				+ "</span></span></span>                                                                                                                                                             ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span style=\"color: #808000\"><span                                                                                                                                                                                                        ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><strong>"
				+ bodyMail.getTitle2() + "</strong>                    ");
		sb.append(
				"                                                                                                 </span></span><br> <br> <strong> <span                                                                                                                                                                    ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\">                                                                ");
		sb.append(
				"                                                                                                </strong>                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("																							 <td height=\"20\" style=\"height: 40px; border-bottom-color: rgb(227, 227, 227); border-bottom-style: solid; border-bottom-width: 1px; font-style: italic;\">" + bodyMail.getFooter1()
				+ "</td>                                                      ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" bgcolor=\"#4098BC\" style=\"border-radius: 5px\">                                                                                                                                                                                ");
		sb.append(
				"                                                                                             <div>                                                                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                <a href=\"" + parameter.getSiteDomain() + "\"                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"background-color: #808000; border-radius: 5px; color: #ffffff; display: inline-block; font-family: sans-serif; font-size: 16px; font-weight: bold; line-height: 50px; text-align: center; text-decoration: none; width: 310px\"  ");
		sb.append(
				"                                                                                                   target=\"_blank\" data-saferedirecturl=\"" + parameter.getSiteDomain() + "\">" + parameter.getSiteDomain() + "</a>                                                                                                                                            ");
		sb.append(
				"                                                                                             </div>                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\"><strong>Acompanhamento de Pedidos e Servi&ccedil;os</strong>                                                                                                                  ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\"                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                             style=\"font-size: 11px; color: #cfcfcf; font-style: italic; text-align: center; padding-top: 5px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif\">&nbsp;</td>                                                ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                    </tbody>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                 </table>                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                     <table bgcolor=\"#F7F7F7\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"                                                                                                                                                                                                  ");
		sb.append(
				"                                                                        style=\"border-top: #e3e3e3 solid 1px; border-bottom-right-radius: 7px; border-bottom-left-radius: 7px\" width=\"380\">                                                                                                                                               ");
		sb.append(
				"                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                 <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"310\"></table>                                                                                                                                                                    ");
		sb.append(
				"                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr align=\"center\">                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"382\">                                                                                                                                                                                        ");
		sb.append(
				"                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                               <tr style=\"text-align: center\">                                                                                                                                                                                                                                            ");
		sb.append(
				"                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                </tbody>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                             </table>                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                          </td>                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                       </tr>                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"                                    </tbody>                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"                                 </table>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"                              </div>                                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                              <div>&nbsp;</div>                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"                           </div>                                                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                        </div>                                                                                                                                                                                                                                                                                                            ");
		sb.append(
				"                        <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                     </div>                                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                     <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                  </div>                                                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"               </div>                                                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"               <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"               <div class=\"m_3594026484717175708yj6qo\">&nbsp;</div>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"               <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"            </div>                                                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"            <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                            ");
		sb.append(
				"         </div>                                                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"      </div>                                                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"      <p>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"         <br>                                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"      </p>                                                                                                                                                                                                                                                                                                                                ");
		sb.append(
				"      <div class=\"yj6qo\"></div>                                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"      <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"   </div>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"   <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                                ");
		sb.append(
				"</div>                                                                                                                                                                                                                                                                                                                                    ");
		return sb.toString();
	}

	public static String getHTMLTemplateResetPass(BodyMail bodyMail, Parameter parameter) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<div id=\":1fb\" class=\"a3s aXjCH m14d93659b00650ae\">                                                                                                                                                                                                                                                                                       ");
		sb.append(
				"   <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"      <div>                                                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"         <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"            <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                             ");
		sb.append(
				"               <div>                                                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                  <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                          ");
		sb.append(
				"                     <div style=\"font-family: Arial; font-size: 14px\">                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                        <div>                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"                           <div style=\"overflow: hidden\">                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                              <div bgcolor=\"#EBEBEB\">                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                 <table bgcolor=\"#EBEBEB\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">                                                                                                                                                                                                                        ");
		sb.append(
				"                                    <tbody>                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                       <tr align=\"center\">                                                                                                                                                                                                                                                                                ");
		sb.append(
				"                                          <td align=\"center\">                                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                             <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                <tbody>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                         <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center; border: #e3e3e3 solid 1px; border-radius: 7px\" width=\"382\">                                                                                                                       ");
		sb.append(
				"                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                     <table bgcolor=\"#fff\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-radius: 7px\" width=\"380\">                                                                                                                                                             ");
		sb.append(
				"                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append(
				"                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                 <table bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"310\">                                                                                                                                              ");
		sb.append(
				"                                                                                    <tbody>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr align=\"center\">                                                                                                                                                                                                                                ");
		sb.append("                                                                                          <td style=\"text-align: center\"><img alt=\"" + parameter.getSiteTitle() + "\" class=\"m_3594026484717175708CToWUd CToWUd\" src=\"" + bodyMail.getImage1()
				+ "\"                                                                                                                                                    ");
		sb.append(
				"                                                                                             style=\"border-radius: 5%; border: 1px solid rgb(227, 227, 227); width: 80px; min-height: 80px\"></td>                                                                                                                                         ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\">" + parameter.getSiteDomain() + "</td>                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\">                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span style=\"color: #34495e\"><span                                                                                                                                                                                                        ");
		sb.append("                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><strong>"
				+ bodyMail.getTitle2() + "</strong>                    ");
		sb.append(
				"                                                                                                 </span></span><br> <br> <strong> <span                                                                                                                                                                    ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\">                                                                ");
		sb.append(
				"                                                                                                </span>                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                                                                </strong>                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                             <p>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                                                <span style=\"color: #000000\"><span                                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                   style=\"font-size: 20px; line-height: 26px; font-family: HelveticaNeue-Light, 'Helvetica Neue', Helvetica, Arial, sans-serif; font-weight: lighter; text-align: center\"><span                                                           ");
		sb.append("                                                                                                   class=\"il\">" + bodyMail.getTitle1()
				+ "</span></span></span>                                                                                                                                                             ");
		sb.append(
				"                                                                                             </p>                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append("                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; border-bottom: #e3e3e3 solid 1px; padding: 5px\"><a style=\"font-size:18px; \" href=\"" + bodyMail.getFooter1()
				+ "\" target=\"_blank\" data-saferedirecturl=\"" + bodyMail.getFooter1() + "\">Clique aqui para criar nova senha.</a>");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" bgcolor=\"#4098BC\" style=\"border-radius: 5px\">                                                                                                                                                                                ");
		sb.append(
				"                                                                                             <div>                                                                                                                                                                                                                                        ");
		sb.append(
				"                                                                                                <a href=\"" + parameter.getSiteDomain() + "\"                                                                                                                                                                                                     ");
		sb.append(
				"                                                                                                   style=\"background-color: #808000; border-radius: 5px; color: #ffffff; display: inline-block; font-family: sans-serif; font-size: 16px; font-weight: bold; line-height: 50px; text-align: center; text-decoration: none; width: 310px\"  ");
		sb.append(
				"                                                                                                   target=\"_blank\" data-saferedirecturl=\"" + parameter.getSiteDomain() + "\">" + parameter.getSiteDomain() + "</a>                                                                                                                                            ");
		sb.append(
				"                                                                                             </div>                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"20\" style=\"height: 20px; font-size: 11px; font-style: italic;\"><strong>Acompanhamento de Pedidos e Servi&ccedil;os</strong>                                                                                                                  ");
		sb.append(
				"                                                                                          </td>                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\"                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                             style=\"font-size: 11px; color: #cfcfcf; font-style: italic; text-align: center; padding-top: 5px; font-family: HelveticaNeue-Light, Helvetica Neue, Helvetica, Arial, sans-serif\">&nbsp;</td>                                                ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                       <tr>                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                                                          <td align=\"center\" height=\"40\" style=\"height: 40px\">&nbsp;</td>                                                                                                                                                                                 ");
		sb.append(
				"                                                                                       </tr>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                    </tbody>                                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                                 </table>                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                              <td align=\"center\" style=\"width: 35px\">&nbsp;</td>                                                                                                                                                                                                          ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                                                     <table bgcolor=\"#F7F7F7\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"                                                                                                                                                                                                  ");
		sb.append(
				"                                                                        style=\"border-top: #e3e3e3 solid 1px; border-bottom-right-radius: 7px; border-bottom-left-radius: 7px\" width=\"380\">                                                                                                                                               ");
		sb.append(
				"                                                                        <tbody>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                              ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                           <tr>                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                                                              <td align=\"center\">                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                                                 <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"310\"></table>                                                                                                                                                                    ");
		sb.append(
				"                                                                              </td>                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                           </tr>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                        </tbody>                                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                                     </table>                                                                                                                                                                                                                                                             ");
		sb.append(
				"                                                                  </td>                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr align=\"center\">                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                                                      <td align=\"center\">                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                                         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: center\" width=\"382\">                                                                                                                                                                                        ");
		sb.append(
				"                                                            <tbody>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                               <tr>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                               <tr style=\"text-align: center\">                                                                                                                                                                                                                                            ");
		sb.append(
				"                                                                  <td align=\"center\">&nbsp;</td>                                                                                                                                                                                                                                          ");
		sb.append(
				"                                                               </tr>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                            </tbody>                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                                                         </table>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                                                      </td>                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                   <tr>                                                                                                                                                                                                                                                                                   ");
		sb.append(
				"                                                      <td align=\"center\" height=\"30\" style=\"height: 30px\">&nbsp;</td>                                                                                                                                                                                                                     ");
		sb.append(
				"                                                   </tr>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                                </tbody>                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"                                             </table>                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                                          </td>                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"                                       </tr>                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"                                    </tbody>                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"                                 </table>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"                                 <div>&nbsp;</div>                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"                              </div>                                                                                                                                                                                                                                                                                                      ");
		sb.append(
				"                              <div>&nbsp;</div>                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"                           </div>                                                                                                                                                                                                                                                                                                         ");
		sb.append(
				"                        </div>                                                                                                                                                                                                                                                                                                            ");
		sb.append(
				"                        <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                        <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"                     </div>                                                                                                                                                                                                                                                                                                               ");
		sb.append(
				"                     <div>&nbsp;</div>                                                                                                                                                                                                                                                                                                    ");
		sb.append(
				"                  </div>                                                                                                                                                                                                                                                                                                                  ");
		sb.append(
				"               </div>                                                                                                                                                                                                                                                                                                                     ");
		sb.append(
				"               <p>&nbsp;</p>                                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"               <div class=\"m_3594026484717175708yj6qo\">&nbsp;</div>                                                                                                                                                                                                                                                                       ");
		sb.append(
				"               <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                         ");
		sb.append(
				"            </div>                                                                                                                                                                                                                                                                                                                        ");
		sb.append(
				"            <div class=\"m_3594026484717175708adL\">&nbsp;</div>                                                                                                                                                                                                                                                                            ");
		sb.append(
				"         </div>                                                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"      </div>                                                                                                                                                                                                                                                                                                                              ");
		sb.append(
				"      <p>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"         <br>                                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"      </p>                                                                                                                                                                                                                                                                                                                                ");
		sb.append(
				"      <div class=\"yj6qo\"></div>                                                                                                                                                                                                                                                                                                           ");
		sb.append(
				"      <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                             ");
		sb.append(
				"   </div>                                                                                                                                                                                                                                                                                                                                 ");
		sb.append(
				"   <div class=\"adL\"></div>                                                                                                                                                                                                                                                                                                                ");
		sb.append(
				"</div>                                                                                                                                                                                                                                                                                                                                    ");
		return sb.toString();
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
		sender.setCompany(Utils.isNullOrEmpty(parameter.getMailSenderName()) ? parameter.getSiteTitle() : parameter.getMailSenderName());
		sender.setFrom(Utils.isNullOrEmpty(parameter.getMailSenderFrom()) ? parameter.getSiteMail() : parameter.getMailSenderFrom());
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
		bodyMail.setBodyHTML(MailController.getHTMLTemplateSimple(bodyMail, parameter));
		mailController.sendHTMLMail(sendTo, sender, bodyMail, null, parameter);
	}
}