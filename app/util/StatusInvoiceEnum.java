package util;

public enum StatusInvoiceEnum {
	Won("Vencida", "won"), Current("Vigente", "current"), Canceled("Cancelada", "canceled");

	String label;
	String value;

	StatusInvoiceEnum(String label, String value) {
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

	public static StatusInvoiceEnum getNameByValue(String value) {
		for (int i = 0; i < StatusInvoiceEnum.values().length; i++) {
			if (value.equals(StatusInvoiceEnum.values()[i].value))
				return StatusInvoiceEnum.values()[i];
		}
		return null;
	}

	public static StatusInvoiceEnum getValueByName(String label) {
		for (int i = 0; i < StatusInvoiceEnum.values().length; i++) {
			if (label.equals(StatusInvoiceEnum.values()[i].label))
				return getNameByValue(StatusInvoiceEnum.values()[i].value);
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(StatusInvoiceEnum.getNameByValue("free").getLabel());
	}

}
