package util.howtodo;

import java.util.Calendar;

public enum DaysOfWeekEnum {

	Sunday("Domingo", String.valueOf(Calendar.SUNDAY)), 
	Monday("Segunda", String.valueOf(Calendar.MONDAY)), 
	Tuesday("Terça", String.valueOf(Calendar.TUESDAY)), 
	Wednesday("Quarta", String.valueOf(Calendar.WEDNESDAY)), 
	Thursday("Quinta", String.valueOf(Calendar.THURSDAY)), 
	Friday("Sexta", String.valueOf(Calendar.FRIDAY)), 
	Saturday("Sábado", String.valueOf(Calendar.SATURDAY));

	String label;
	String value;

	DaysOfWeekEnum(String label, String value) {
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
