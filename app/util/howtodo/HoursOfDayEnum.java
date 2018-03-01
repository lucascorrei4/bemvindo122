package util.howtodo;

public enum HoursOfDayEnum {

	hh05mm00("05:00", "05:00"), 
	hh06mm00("06:00", "06:00"), 
	hh07mm00("07:00", "07:00"), 
	hh08mm00("08:00", "08:00"), 
	hh09mm00("09:00", "09:00"), 
	hh10mm00("10:00", "10:00"), 
	hh11mm00("11:00", "11:00"), 
	hh12mm00("12:00", "12:00"), 
	hh13mm00("13:00", "13:00"), 
	hh14mm00("14:00", "14:00"), 
	hh15mm00("15:00", "15:00"), 
	hh16mm00("16:00", "16:00"), 
	hh17mm00("17:00", "17:00"), 
	hh18mm00("18:00", "18:00"), 
	hh19mm00("19:00", "19:00"), 
	hh20mm00("20:00", "20:00"), 
	hh21mm00("21:00", "21:00"), 
	hh22mm00("22:00", "22:00"), 
	hh23mm00("23:00", "23:00"), 
	hh00mm00("00:00", "00:00"); 

	String label;
	String value;

	HoursOfDayEnum(String label, String value) {
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
