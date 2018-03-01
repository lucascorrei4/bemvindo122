package util.howtodo;

public enum TypeAdsNewsEnum {
	Default("Padrão", "Default"), AdsTop("Ads Topo", "AdsTop"), AdsSidebarRight("Ads Lat. Direito", "AdsSidebarRight"), AdsBottom("Ads Rodapé", "AdsBottom");

	String label;
	String value;

	TypeAdsNewsEnum(String label, String value) {
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

	public static TypeAdsNewsEnum getNameByValue(String value) {
		for (int i = 0; i < TypeAdsNewsEnum.values().length; i++) {
			if (value.equals(TypeAdsNewsEnum.values()[i].value))
				return TypeAdsNewsEnum.values()[i];
		}
		return null;
	}

	public static TypeAdsNewsEnum getValueByName(String label) {
		for (int i = 0; i < TypeAdsNewsEnum.values().length; i++) {
			if (label.equals(TypeAdsNewsEnum.values()[i].label))
				return getNameByValue(TypeAdsNewsEnum.values()[i].value);
		}
		return null;
	}

	public static boolean isAdsTop(String value) {
		return value.equals(TypeAdsNewsEnum.AdsTop.getValue());
	}

	public static boolean isAdsSidebarRight(String value) {
		return value.equals(TypeAdsNewsEnum.AdsSidebarRight.getValue());
	}

	public static boolean isAdsBottom(String value) {
		return value.equals(TypeAdsNewsEnum.AdsBottom.getValue());
	}

	public static boolean isDefaultArticle(String value) {
		return value.equals(TypeAdsNewsEnum.Default.getValue());
	}

}
