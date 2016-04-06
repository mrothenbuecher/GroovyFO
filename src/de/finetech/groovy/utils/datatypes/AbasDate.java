package de.finetech.groovy.utils.datatypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.GroovyFOException;
import de.finetech.groovy.utils.GroovyFOVariable;

public class AbasDate extends GroovyFOVariable<String> {

	private AbasBaseScript script;

	private static Pattern pattern = Pattern
			.compile("\\bGD\\b|\\bGD0\\b|\\bGD2\\b|\\bGD7\\b|\\bGD8\\b|\\bGD13\\b|\\bGD14\\b|\\bGD19\\b|\\bGD20\\b|"
					+ "\\bGW2\\b|\\bGW4\\b|"
					+ "\\bZ\\b"
					+ "|\\bJ2\\b|\\bGJ\\b|"
					+ "\\bGP1\\b|\\bGP2\\b|\\bGP3\\b|\\bGP4\\b|"
					+ "\\bDATUM\\b|\\bWOCHE\\b|\\bTERMIN\\b|\\bZEIT\\b");

	public static boolean isDate(String type) {
		type = type.toUpperCase();
		Matcher match = pattern.matcher(type);
		return match.find();
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
	 */
	public AbasDate(String type, String expr, AbasBaseScript script)
			throws GroovyFOException {
		super(expr, script);
		this.script = script;
		this.type = type;
		if (!AbasDate.isDate(type))
			throw new GroovyFOException(type
					+ " is not a abas datetype for date/time/duration");
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
				if ((Boolean) script.getComputedValue(this.varname + " > "
						+ date.getVariablename()))
					return 1;
				else if ((Boolean) script.getComputedValue(this.varname + " < "
						+ date.getVariablename()))
					return -1;
				else
					return 0;
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
