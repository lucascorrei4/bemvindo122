package util;

public enum VideoHelpEnum {
	Index("Página Inicial", "https://www.youtube.com/embed/jek-QOfiXOo"), Client("Clientes", "https://www.youtube.com/embed/6Wb4C0RNP2A"), Services("Serviços",
			"https://www.youtube.com/embed/JGwWNGJdvx8"), OrderOfService("Ordens de Serviço", "https://www.youtube.com/embed/6Wb4C0RNP2A"), UpdateOrders("Atualizar Pedidos",
					"https://www.youtube.com/embed/VpnUXY4-6W8");

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
