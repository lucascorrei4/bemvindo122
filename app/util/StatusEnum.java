package util;

public enum StatusEnum {
	NotStarted("Não iniciada", "notstarted"), InProgress("Em progresso", "inprogress"), Finished("Finalizada", "finished"), Frozen("Congelada", "frozen");

	String label;
	String value;

	StatusEnum(String label, String value) {
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

	public static StatusEnum getNameByValue(String value) {
		for (int i = 0; i < StatusEnum.values().length; i++) {
			if (value.equals(StatusEnum.values()[i].value))
				return StatusEnum.values()[i];
		}
		return null;
	}

	public static StatusEnum getValueByName(String label) {
		for (int i = 0; i < StatusEnum.values().length; i++) {
			if (label.equals(StatusEnum.values()[i].label))
				return getNameByValue(StatusEnum.values()[i].value);
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(StatusEnum.getNameByValue("simple").getLabel());
	}

}
