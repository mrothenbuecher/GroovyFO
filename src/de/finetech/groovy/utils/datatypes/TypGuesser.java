package de.finetech.groovy.utils.datatypes;

import java.util.regex.Pattern;

/**
 * 
 * @author Michael Rothenb�cher, Finetech GmbH & Co. KG
 *
 */
public class TypGuesser {

	public static enum PossibleDatatypes {
		INTEGER, DOUBLE, DOUBLED, DOUBLET, DOUBLEDT, BOOLEAN, ABASDATE, ABASPOINTER, STRING
	}
	
	// regul�re Ausdr�cke zum erkennen von Variablen arten
	private static Pattern integerPattern = Pattern.compile("(I[0-9]*)|(IP.*)|(IN.*)|(K.*)|(AN.*)");
	private static Pattern doublePattern = Pattern.compile("(R.*)|(M.*)");
	private static Pattern doubledPattern = Pattern.compile("(R.*D.*)|(M.*D.*)");
	private static Pattern doubletPattern = Pattern.compile("(R.*T.*)|(M.*T.*)");
	private static Pattern doubledtPattern = Pattern.compile("(R.*DT.*)|(M.*DT.*)");

	private static Pattern boolPattern = Pattern.compile("(B)|(BOOL)");
	private static Pattern pointerPattern = Pattern.compile("(P.*)|(ID.*)|(VP.*)|(VID.*)|(C.*)");
	
	/**
	 * 
	 * @param abasType - abas Variablenart Bspw: I3, GD2, GL30, T60 usw...
	 * @return
	 */
	public static PossibleDatatypes getClassOfType(String abasType) {
		if (integerPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.INTEGER;
		}
		// real tausender und dezimal
		if (doubledtPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.DOUBLEDT;
		}
		// real tausender
		if (doubletPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.DOUBLET;
		}
		// real dezimal trennzeichen
		if (doubledPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.DOUBLED;
		}
		// Real
		if (doublePattern.matcher(abasType).matches()) {
			return PossibleDatatypes.DOUBLE;
		}
		// bool
		if (boolPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.BOOLEAN;
		}
		if (AbasDate.isDate(abasType)) {
			return PossibleDatatypes.ABASDATE;
		}
		// Zeiger also
		if (pointerPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.ABASPOINTER;
		}
		// Strings
		return PossibleDatatypes.STRING;
	}
	
}
