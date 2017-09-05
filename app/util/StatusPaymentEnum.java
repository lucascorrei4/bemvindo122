package util;

public enum StatusPaymentEnum {
	Paid("Pago", "paid"), Pending("Pendente", "pending"), Free("Isento", "free"), Present("Aguardando", "present");

	String label;
	String value;

	StatusPaymentEnum(String label, String value) {
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

	public static StatusPaymentEnum getNameByValue(String value) {
		for (int i = 0; i < StatusPaymentEnum.values().length; i++) {
			if (value.equals(StatusPaymentEnum.values()[i].value))
				return StatusPaymentEnum.values()[i];
		}
		return null;
	}

	public static StatusPaymentEnum getValueByName(String label) {
		for (int i = 0; i < StatusPaymentEnum.values().length; i++) {
			if (label.equals(StatusPaymentEnum.values()[i].label))
				return getNameByValue(StatusPaymentEnum.values()[i].value);
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(StatusPaymentEnum.getNameByValue("free").getLabel());
	}

}
