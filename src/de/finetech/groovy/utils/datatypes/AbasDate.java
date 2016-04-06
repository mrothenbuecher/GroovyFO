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

	private int DATE = 0, DAY_OF_WEEK = 1, NAME = 2;

	private static Pattern pattern = Pattern
			.compile("\\bGD\\b|\\bGD0\\b|\\bGD2\\b|\\bGD7\\b|\\bGD8\\b|\\bGD13\\b|\\bGD14\\b|\\bGD19\\b|\\bGD20\\b|"
					+ "\\bGW2\\b|\\bGW4\\b|"
					+ "\\bZ\\b"
					+ "|\\bJ2\\b|\\bGJ\\b|"
					+ "\\bGP1\\b|\\bGP2\\b|\\bGP3\\b|\\bGP4\\b|"
					+ "\\bDATUM\\b|\\bWOCHE\\b|\\bTERMIN\\b|\\bZEIT\\b");
	private String[] tempvars = { "xfooaddate", "xfooadweekday",
			"xfooadweekdayname" };

	public static boolean isDate(String type) {
		type = type.toUpperCase();
		Matcher match = pattern.matcher(type);
		return match.find();
	}

	/**
	 * 
	 * @param type
	 *            - abas type bsp.: GD, GD2, ...
	 * @param var
	 *            - Variable mit Puffer! bsp M|von, U|xvon
	 * @param script
	 *            - script welches den abastype anlegt
	 * @throws GroovyFOException
	 */
	public AbasDate(String type, String var, AbasBaseScript script)
			throws GroovyFOException {
		super(var, script);
		this.script = script;
		this.type = type;
		this.tempvars[DATE] = this.script.art(this.type, tempvars[DATE] + type);
		this.tempvars[DAY_OF_WEEK] = this.script.art("INT",
				tempvars[DAY_OF_WEEK]);
		this.tempvars[NAME] = this.script.art("TEXT", tempvars[NAME]);
		if (!AbasDate.isDate(type))
			throw new GroovyFOException(type
					+ " is not a abas datetype for date/time/duration");
	}

	// FIXME GD19+GP = GD19 usw siehe hilfe

	public Object plus(int i) throws FOPException, GroovyFOException {
		return this.script.formel(tempvars[0], this.getVariablename() + " + "
				+ i);
	}

	public Object next() throws FOPException, GroovyFOException {
		return this.script.formel(this.getVariablename(),
				this.getVariablename() + " + " + 1);
	}

	public Object previous() throws FOPException, GroovyFOException {
		return this.script.formel(this.getVariablename(),
				this.getVariablename() + " -1 " + 1);
	}

	public Object minus(int i) throws FOPException, GroovyFOException {
		return this.plus(-i);
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
			return this.script.formel(tempvars[NAME], this.getVariablename()
					+ " // " + i);
		case 7:
			return this.script.formel(tempvars[DAY_OF_WEEK],
					this.getVariablename() + " // " + i);
		}
		throw new GroovyFOException("operation // " + i + " not supported on "
				+ this.getVariablename() + " of type " + this.type);
	}

	public String getSortable() throws FOPException, GroovyFOException {
		return this.script
				.formel(tempvars[NAME], this.getVariablename() + ":8")
				.toString();
	}

	public Object and(int i) throws FOPException, GroovyFOException {
		return this.script.formel(tempvars[0], this.getVariablename() + " & "
				+ i);
	}

	@Override
	public String getValue() {
		return FO.getValue(this.buffer, this.varname);
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof AbasDate) {
			AbasDate date = (AbasDate) arg0;
			try {
				String xbfoo = script.art("bool", "xboolabasdateFOO");
				if ((Boolean) script.formel(xbfoo,
						this.varname + " > " + date.getVariablename()))
					return 1;
				else if ((Boolean) script.formel(xbfoo, this.varname + " < "
						+ date.getVariablename()))
					return -1;
				else
					return 0;
			} catch (GroovyFOException e) {
				e.printStackTrace();
			}
		} else {
			if (arg0 == null)
				return -10;
			this.getValue().compareTo(arg0.toString());
		}
		return 0;
	}
}
