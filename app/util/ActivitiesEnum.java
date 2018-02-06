package util;

public enum ActivitiesEnum {
	CommonActivity("Atividade Comum", "CommonActivity", "exclamation-circle"), 
	PhoneCallToHim("Ligação para o cliente", "PhoneCallToHim", "phone"), 
	PhoneCallToUs("Ligação recebida", "PhoneCallToUs", "phone-square"), 
	VisitHim("Visita realizada no cliente", "VisitHim", "shake-hand-o"), 
	VisitMe("O cliente veio até nós", "VisitMe", "shake-hand-o"),
	CallAfterSale("Pós-venda realizado por telefone", "CallAfterSale", "heartbeat"), 
	ClientSendMessage("O cliente nos mandou mensagem", "ClientSendMessage", "reply"), 
	Problem("Algo ruim aconteceu", "Problem", "thumbs-o-down"), 
	OSStepUpdated("Etapa da OS atualizada (Registrado pelo sistema)", "OSStepUpdated", "list-alt"), 
	NewUser("Usuário nasceu no sistema (Registrado pelo sistema)", "NewUser", "user-circle-o"), 
	OS("Ordem de Serviço gerada (Registrado pelo sistema)", "OS", "list-alt"), 
	OSSearch("Cliente consultou a OS (Registrado pelo sistema)", "OSSearch", "search"), 
	Mail("E-mail enviado (Registrado pelo sistema)", "Mail", "envelope-o"), 
	WppSent("Mensagem via Whatsapp enviada (Registrado pelo sistema)", "WppSent", "mobile-phone"), 
	SystemAfterSale("Pós-venda realizado pelo sistema (Registrado pelo sistema)", "SystemAfterSale", "heartbeat"),
	SystemAfterSaleByMail("Pós-venda via e-mail. (Registrado pelo sistema)", "SystemAfterSaleByMail", "heartbeat"),
	SystemAfterSaleByWhatsApp("Pós-venda via WhatsApp. (Registrado pelo sistema)", "SystemAfterSaleByWhatsApp", "heartbeat"),
	SystemAfterSaleByPush("Pós-venda via Push realizado. (Registrado pelo sistema)", "SystemAfterSaleByPush", "heartbeat"),
	PushSent("Notificação Push enviada (Registrado pelo sistema)", "PushSent",	"send-o"), 
	OSRemoveAction("OS removida (Registrado pelo sistema)", "OSRemoveAction", "ban"),
	ClientEvaluationMail("Avalição enviada via e-mail. (Registrado pelo sistema)", "ClientEvaluationMail", "edit"),
	ClientEvaluationPush("Avalição enviada via Push. (Registrado pelo sistema)", "ClientEvaluationPush", "edit"),
	Default("Outro", "Default", "asterisc"); 

	String label;
	String value;
	String icon;

	ActivitiesEnum(String label, String value, String icon) {
		this.label = label;
		this.value = value;
		this.icon = icon;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public String getValue() {
		return this.value;
	}

	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}

	public static ActivitiesEnum getNameByValue(String value) {
		for (int i = 0; i < ActivitiesEnum.values().length; i++) {
			if (value.equals(ActivitiesEnum.values()[i].value))
				return ActivitiesEnum.values()[i];
		}
		return null;
	}

	public static ActivitiesEnum getValueByName(String label) {
		for (int i = 0; i < ActivitiesEnum.values().length; i++) {
			if (label.equals(ActivitiesEnum.values()[i].label))
				return getNameByValue(ActivitiesEnum.values()[i].value);
		}
		return null;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
