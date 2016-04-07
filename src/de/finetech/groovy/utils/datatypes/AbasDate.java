package de.finetech.groovy.utils.datatypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.GroovyFOException;
import de.finetech.groovy.utils.GroovyFOVariable;

public class AbasDate extends GroovyFOVariable<String> {

	private AbasBaseScript script;

	private static Pattern pattern = Pattern.compile("(GD.*)|" + "(GW.*)|"
			+ "(Z)|" + "(J2)|(GJ)|" + "(GP.*)|"
			+ "(DATUM)|(WOCHE)|(TERMIN)|(ZEIT)");

	private static SimpleDateFormat sdfmt = new SimpleDateFormat();
	protected Calendar cal;

	/**
	 * @param type
	 * @return
	 */
	public static boolean isDate(String type) {
		return pattern.matcher(type.toUpperCase()).matches();
	}

	/**
	 * 
	 * @param type
	 *            - abas type bsp.: GD, GD2, ...
	 * @param expr
	 *            - Variable mit Puffer! bsp M|von, U|xvon
	 * @param script
	 *            - script welches den abastype anlegt
	 * @throws GroovyFOException
	 * @throws ParseException
	 */
	public AbasDate(String type, String expr, String value,
			AbasBaseScript script) throws GroovyFOException, ParseException {
		super(expr, script);
		this.script = script;
		this.type = type;

		// versuch der Geschwindigkeitssteigerung durch überführung in java
		// standard Datentyp für Daten
		if (value != null && !value.isEmpty()) {
			this.cal = Calendar.getInstance();
			if (type.equals("GD") || type.equals("GD0")) {
				cal.setTime(this.getValue("dd.MM.yyyy", value));
			} else if (type.equals("GD2")) {
				cal.setTime(this.getValue("dd.MM.yy", value));
			} else if (type.equals("GD7")) {
				cal.setTime(this.getValue("yyyy-MM-dd", value));
			} else if (type.equals("GD8")) {
				cal.setTime(this.getValue("yyyyMMdd", value));
			} else if (type.equals("GD13")) {
				cal.setTime(this.getValue("yyyy-MM-dd HH:mm:ss", value));
			} else if (type.equals("GD14")) {
				if (value.length() != 8)
					cal.setTime(this.getValue("yyyyMMddHHmmss", value));
				else
					cal.setTime(this.getValue("yyyyMMdd", value));
			} else if (type.equals("GW2")) {
				cal.setTime(this.getValue("ww/yy", value));
			} else if (type.equals("GW4")) {
				cal.setTime(this.getValue("ww/yyyy", value));
			} else if (type.equals("Z")) {
				cal.setTime(this.getValue("HH:mm", value));
			} else if (type.equals("J2")) {
				cal.setTime(this.getValue("yy", value));
			} else if (type.equals("J4")) {
				cal.setTime(this.getValue("yyyy", value));
			} else if (type.equals("GJ")) {
				if (value.length() == 4)
					cal.setTime(this.getValue("yyyy", value));
				else
					cal.setTime(this.getValue("yy", value));
			} else {
				// bei allen anderen Typen wird das vorgehen nicht unterstützt
				// sondern der Vergleich wird abas überlassen
				cal = null;
			}
		}
		if (!AbasDate.isDate(type))
			throw new GroovyFOException(type
					+ " is not a abas datetype for date/time/duration");
	}

	private Date getValue(String pattern, String value) throws ParseException {
		sdfmt.applyPattern(pattern);
		return sdfmt.parse(value);
	}

	public Object plus(int i) throws FOPException, GroovyFOException {
		String expr = this.getVariablename() + "+" + i;
		return script.getComputedValue(expr);
	}

	public Object next() throws FOPException, GroovyFOException {
		String expr = this.getVariablename() + "+1";
		return script.getComputedValue(expr);
	}

	public Object previous() throws FOPException, GroovyFOException {
		String expr = this.getVariablename() + "-1";
		return script.getComputedValue(expr);
	}

	public Object minus(int i) throws FOPException, GroovyFOException {
		return this.plus(-i);
	}

	// FIXME GD19+GP = GD19 usw siehe hilfe
	/**
	 * abasdate - abasdate
	 * 
	 * @param i
	 * @return
	 * @throws FOPException
	 * @throws GroovyFOException
	 */
	public Object minus(AbasDate i) throws FOPException, GroovyFOException {
		String expr = this.getVariablename() + "-" + i.getVariablename();
		return script.getComputedValue(expr);
	}

	/**
	 * 
	 * @param i
	 *            mögliche Werte 1 oder 7
	 * @return Datum // i
	 * @throws GroovyFOException
	 */
	public Object mod(int i) throws GroovyFOException {
		switch (i) {
		case 1:
		case 7:
			String expr = this.getVariablename() + "//" + i;
			return script.getComputedValue(expr);
		}
		throw new GroovyFOException("operation // " + i + " not supported on "
				+ this.getVariablename() + " of type " + this.type);
	}

	public String getSortable() throws FOPException, GroovyFOException {
		String expr = this.getVariablename() + ":8";
		return script.getComputedValue(expr).toString();
	}

	public Object and(int i) throws FOPException, GroovyFOException {
		String expr = this.getVariablename() + "&" + i;
		return script.getComputedValue(expr);
	}

	@Override
	public String getValue() {
		return FO.getValue("F", "expr(" + this.varname + ")");
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof AbasDate) {
			try {
				AbasDate date = (AbasDate) arg0;
				// wenn beide Daten über einen Calendar verfügen mache den Vergleich damit
				if (this.cal != null && date.cal != null) {
					return this.cal.compareTo(date.cal);
				} else {
					// ansonsten lasse abas den vergleich machen (langsam!)
					if ((Boolean) script.getComputedValue(this.varname + " > "
							+ date.getVariablename()))
						return 1;
					else if ((Boolean) script.getComputedValue(this.varname
							+ " < " + date.getVariablename()))
						return -1;
					else
						return 0;
				}
			} catch (Exception ex) {
				return -10;
			}

		} else {
			if (arg0 == null)
				return -10;
			return this.getValue().compareTo(arg0.toString());
		}
	}
}
