package enumeration;

public enum GenderEnum {

	M("Masculino", 1), F("Feminino", 2);

	String label;
	int value;

	GenderEnum(String label, int value) {
		this.value = value;
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public int getValue() {
		return this.value;
	}

	public String getLabel() {
		return label;
	}
}
