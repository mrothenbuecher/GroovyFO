package de.finetech.utils.charts;

public enum ChartType {
	PIE("PIE"), BAR("BAR"), LINES("LINES"), AREA("AREA"), CURVE("CURVE"), CURVEAREA(
			"CURVEAREA"), STEP("STEP"), BUBBLE("BUBBLE"), OPENHIGHLOWCLOSE(
			"OPENHIGHLOWCLOSE"), GANTT("GANTT");

	private String value;

	private ChartType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
