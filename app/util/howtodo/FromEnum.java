package util.howtodo;

public enum FromEnum {
	HomePageTop("Página Inicial Topo", "homepagetop"), HomePageBottom("Página Inicial Rodapé", "homepagebottom"), DoubtsForm("Formulário de Dúvidas", "doubtsform"), UserForm("Formulário de Usuário", "userform"), FollowPage("Página de Acompanhamento", "followpage"), NewsPage("Página de Notícias",
			"newspage"), CapturePageTop("Página de Captura Topo",
					"capturepagetop"), CapturePageBottom("Página de Captura Rodapé", "capturepagebottom"), NewsletterFreePage("Página de Captura Página Livre", "newsletterfreepage"), NewsletterFreePageBootstrap("Página de Captura Página Livre Bootstrap", "newsletterfreepagebootstrap");

	String label;
	String value;

	FromEnum(String label, String value) {
		this.label = label;
		this.value = value;
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

	public static FromEnum getNameByValue(String value) {
		for (int i = 0; i < FromEnum.values().length; i++) {
			if (value.equals(FromEnum.values()[i].value))
				return FromEnum.values()[i];
		}
		return null;
	}

	public static FromEnum getValueByName(String label) {
		for (int i = 0; i < FromEnum.values().length; i++) {
			if (label.equals(FromEnum.values()[i].label))
				return getNameByValue(FromEnum.values()[i].value);
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(FromEnum.getNameByValue("homepagetop"));
	}

}
