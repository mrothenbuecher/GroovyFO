package de.finetech.groovy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AbasDate extends GregorianCalendar {

	private static final long serialVersionUID = -1498611589708068192L;

	public AbasDate(String type, String value) throws ParseException {
		// Montag ist der erste Tag der Woche
		this.setFirstDayOfWeek(Calendar.MONDAY);

		// Java 1.6 kompatbilität
		if (type.equals("GD") || type.equals("GD0")) {
			this.setTime(this.getValue("dd.MM.yyyy", value));
		} else if (type.equals("GD2")) {
			this.setTime(this.getValue("dd.MM.yy", value));
		} else if (type.equals("GD7")) {
			this.setTime(this.getValue("yyyy-MM-dd", value));
		} else if (type.equals("GD8")) {
			this.setTime(this.getValue("yyyyMMdd", value));
		} else if (type.equals("GD13")) {
			this.setTime(this.getValue("yyyy-MM-dd HH:mm:ss", value));
		} else if (type.equals("GD14")) {
			if (value.length() != 8)
				this.setTime(this.getValue("yyyyMMddHHmmss", value));
			else
				this.setTime(this.getValue("yyyyMMdd", value));
		} else if (type.equals("GW2")) {
			this.setTime(this.getValue("ww/yy", value));
		} else if (type.equals("GW4")) {
			this.setTime(this.getValue("ww/yyyy", value));
		} else if (type.equals("Z")) {
			this.setTime(this.getValue("HH:mm", value));
		} else if (type.equals("J2")) {
			this.setTime(this.getValue("yy", value));
		} else if (type.equals("J4")) {
			this.setTime(this.getValue("yyyy", value));
		} else if (type.equals("GJ")) {
			if (value.length() == 4)
				this.setTime(this.getValue("yyyy", value));
			else
				this.setTime(this.getValue("yy", value));
		}/* else if (type.equals("GP4")) {
			this.setTime(this.getValue("D'D'HH'h'mm'm'ss's'", value));
		}*/
		// TODO GD19 && GD20
		// FIXME Problem mit der Dauer Type GP
	}

	private Date getValue(String pattern, String value) throws ParseException {
		SimpleDateFormat sdfmt = new SimpleDateFormat();
		sdfmt.applyPattern(pattern);
		return sdfmt.parse(value);
	}

	public AbasDate plus(int i) {
		this.add(Calendar.DATE, i);
		return this;
	}

	public AbasDate minus(int i) {
		this.add(Calendar.DATE, -i);
		return this;
	}

	public Object mod(int i) {
		int day = this.get(Calendar.DAY_OF_WEEK);
		switch(i) {
			//TODO Sprach unabhängigkeit
			case 1:
				if(day == 0){
					return "Montag";
				}
				if(day == 1){
					return "Dienstag";
				}
				if(day == 2){
					return "Mittwoch";
				}
				if(day == 3){
					return "Donnerstag";
				}
				if(day == 4){
					return "Freitag";
				}
				if(day == 5){
					return "Samstag";
				}
				if(day == 6){
					return "Sonntag";
				}
			case 7:
				return day+1;
			default:
				return "";
		}
	}
}
