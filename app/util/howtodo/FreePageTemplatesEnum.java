package util.howtodo;

public enum FreePageTemplatesEnum {
	BootstrapTheme("Tema Bootstrap", "BootstrapTheme"), FreeStyleTheme("Tema Livre", "FreeStyleTheme");

	String label;
	String value;

	FreePageTemplatesEnum(String label, String value) {
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

	public static FreePageTemplatesEnum getNameByValue(String value) {
		for (int i = 0; i < FreePageTemplatesEnum.values().length; i++) {
			if (value.equals(FreePageTemplatesEnum.values()[i].value))
				return FreePageTemplatesEnum.values()[i];
		}
		return null;
	}

	public static FreePageTemplatesEnum getValueByName(String label) {
		for (int i = 0; i < FreePageTemplatesEnum.values().length; i++) {
			if (label.equals(FreePageTemplatesEnum.values()[i].label))
				return getNameByValue(FreePageTemplatesEnum.values()[i].value);
		}
		return null;
	}
	
	public static boolean isBootstrapTheme(String value) {
		return value.equals(FreePageTemplatesEnum.BootstrapTheme.getValue());
	}

	public static boolean isFreeStyleTheme(String value) {
		return value.equals(FreePageTemplatesEnum.FreeStyleTheme.getValue());
	}

}
