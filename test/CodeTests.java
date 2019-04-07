import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CodeTests {
	public static void main(String[] args) throws MessagingException {
		sendMail();
	}

	public static void sendMail() throws MessagingException {
		String host = "jp1585.temporary-domain.net";
		String user = "seupedid";
		String pass = "f$8LBdX%5R";
		String to = "lucascorreiaevangelista@gmail.com";
		String from = "notificacoes@acompanheseupedido.com";
		String subject = "Test subject";
		String messageText = "Test body";
		boolean sessionDebug = true;

		Properties props = System.getProperties();
		props.put("mail.host", host);
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.port", 26);
		// Uncomment 5 SMTPS-related lines below and comment out 2 SMTP-related
		// lines above and 1 below if you prefer to use SSL
		// props.put("mail.transport.protocol", "smtps");
		// props.put("mail.smtps.auth", "true");
		// props.put("mail.smtps.port", "465");
		// props.put("mail.smtps.ssl.trust", host);
		Session mailSession = Session.getDefaultInstance(props, null);
		mailSession.setDebug(sessionDebug);
		Message msg = new MimeMessage(mailSession);
		msg.setFrom(new InternetAddress(from));
		InternetAddress[] address = { new InternetAddress(to) };
		msg.setRecipients(Message.RecipientType.TO, address);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setText(messageText);
		Transport transport = mailSession.getTransport("smtp");
		// Transport transport = mailSession.getTransport("smtps");
		transport.connect(host, user, pass);
		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();
	}

}
