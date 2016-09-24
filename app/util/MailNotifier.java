package util;

import models.User;
import play.Play;
import play.mvc.Mailer;

public class MailNotifier extends Mailer {
	public static String STR_URL_SEND_MAIL = Play.configuration.getProperty("send.mail.url");
	public static String STR_FAIL_MESSAGE = "Ops! :( Algo estranho aconteceu! Por favor, tente novamente!";

	public static void welcome(User user) {
		setSubject("Bem vindo %s", user.getName());
		addRecipient(user.email);
		setFrom("BemVindo <naoreponda@ebemvindo.com>");
		// EmailAttachment attachment = new EmailAttachment();
		// attachment.setDescription("A pdf document");
		// attachment.setPath(Play.getFile("rules.pdf").getPath());
		// addAttachment(attachment);
		send(user);
	}

	public static void lostPassword(User user) {
		String newpassword = user.password;
		setFrom("BemVindo <naoreponda@ebemvindo.com>");
		setSubject("Ops! Esqueceu sua senha?");
		addRecipient(user.email);
		send(user, newpassword);
	}

	public static void mailNotification(User user) {
		String newpassword = user.password;
		setFrom("BemVindo <naoreponda@ebemvindo.com>");
		setSubject("Ops! Esqueceu sua senha?");
		addRecipient(user.email);
		send(user, newpassword);
	}

}
