package enumeration;

public enum ExperienceEnum {

	Entrada("Entrada", "entrada"), Saida("Saída", "saida"), Pico("Pico", "pico");

	String label;
	String value;

	ExperienceEnum(String label, String value) {
		this.value = value;
		this.label = label;
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
}
