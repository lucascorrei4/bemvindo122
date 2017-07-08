package util;

public enum PlansEnum {
	SPO01("SPO-01", "SPO01"), SPO02("SPO-02", "SPO02"), BETA("BETA", "BETA");

	String label;
	String value;

	PlansEnum(String label, String value) {
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

	public static PlansEnum getNameByValue(String value) {
		for (int i = 0; i < PlansEnum.values().length; i++) {
			if (value.equals(PlansEnum.values()[i].value))
				return PlansEnum.values()[i];
		}
		return null;
	}

	public static PlansEnum getValueByName(String label) {
		for (int i = 0; i < PlansEnum.values().length; i++) {
			if (label.equals(PlansEnum.values()[i].label))
				return getNameByValue(PlansEnum.values()[i].value);
		}
		return null;
	}
	
	public static boolean isPlanSPO01(String value) {
		return value.equals(PlansEnum.SPO01.getValue());
	}

	public static boolean isPlanSPO02(String value) {
		return value.equals(PlansEnum.SPO02.getValue());
	}

	public static boolean isPlanBETA(String value) {
		return value.equals(PlansEnum.BETA.getValue());
	}
	
	public static void main(String[] args) {
		System.out.println("SPO02".equals(PlansEnum.SPO02.getValue()));
	}

}
