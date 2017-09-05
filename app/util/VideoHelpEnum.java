package util;

public enum VideoHelpEnum {
	Index("Página Inicial", "https://www.youtube.com/embed/12CHIXHYPJM"), Client("Clientes", "https://www.youtube.com/embed/YyR1ZE11wOc"), Services("Serviços",
			"https://www.youtube.com/embed/Sdj2Tjf8JIs"), OrderOfService("Ordens de Serviço", "https://www.youtube.com/embed/12CHIXHYPJM"), UpdateOrders("Atualizar Pedidos",
					"https://www.youtube.com/embed/yQYE5MJhyTI");

	String label;
	String value;

	VideoHelpEnum(String label, String value) {
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

	public static VideoHelpEnum getNameByValue(String value) {
		for (int i = 0; i < VideoHelpEnum.values().length; i++) {
			if (value.equals(VideoHelpEnum.values()[i].value))
				return VideoHelpEnum.values()[i];
		}
		return null;
	}

	public static VideoHelpEnum getValueByName(String label) {
		for (int i = 0; i < VideoHelpEnum.values().length; i++) {
			if (label.equals(VideoHelpEnum.values()[i].label))
				return getNameByValue(VideoHelpEnum.values()[i].value);
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(VideoHelpEnum.getNameByValue("homepagetop"));
	}

}
