package util.howtodo;

public enum TypeContentPageEnum {
	VideoContent("Página com Vídeo", "Video"), TextContent("Página com Texto", "Text"), NotDefined("Não definido", "NotDefined");

	String label;
	String value;

	TypeContentPageEnum(String label, String value) {
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

	public static TypeContentPageEnum getNameByValue(String value) {
		for (int i = 0; i < TypeContentPageEnum.values().length; i++) {
			if (value.equals(TypeContentPageEnum.values()[i].value))
				return TypeContentPageEnum.values()[i];
		}
		return null;
	}

	public static TypeContentPageEnum getValueByName(String label) {
		for (int i = 0; i < TypeContentPageEnum.values().length; i++) {
			if (label.equals(TypeContentPageEnum.values()[i].label))
				return getNameByValue(TypeContentPageEnum.values()[i].value);
		}
		return null;
	}
	
	public static boolean isVideoContent(String value) {
		return value.equals(TypeContentPageEnum.VideoContent.getValue());
	}

	public static boolean isTextContent(String value) {
		return value.equals(TypeContentPageEnum.TextContent.getValue());
	}

}
