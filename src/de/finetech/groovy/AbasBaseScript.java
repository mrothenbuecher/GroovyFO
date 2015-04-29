package de.finetech.groovy;

import java.text.DecimalFormat;

import groovy.lang.Script;
import de.abas.eks.jfop.remote.EKS;
import de.abas.eks.jfop.remote.FO;

/**
 * 
 * @author Michael Kürbis, Finetech GmbH & Co. KG
 * 
 */
public abstract class AbasBaseScript extends Script {

	// Temp variablen um sich die letzten Selektion zu speichern
	private String hselection;
	private String[] lselection = new String[11];

	public Object h(String varName) {
		return this.getValue("H|" + varName, EKS.Hvar(varName));
	}

	public Object m(String varName) {
		return this.getValue("M|" + varName, EKS.Mvar(varName));
	}

	public Object d(String varName) {
		return this.getValue("D|" + varName, EKS.Dvar(varName));
	}

	private Object getValue(String varname, String value) {
		// Mapping der einzelnen abas Variablenarten auf Standard Typen
		String abasType = this.getType(varname).toUpperCase();
		// Strings
		if (abasType.startsWith("PS") || abasType.startsWith("GL")
				|| abasType.startsWith("T") || abasType.startsWith("N")
				|| abasType.startsWith("BT") || abasType.startsWith("BG")
				|| abasType.startsWith("ST") || abasType.startsWith("SW")) {
			return value;
		}
		// Integer
		if ((abasType.startsWith("I") && !abasType.startsWith("ID"))
				|| abasType.startsWith("K") || abasType.startsWith("GRN")) {
			return Integer.parseInt(value);
		}
		// Real
		if (abasType.startsWith("R") || abasType.startsWith("M")) {
			return Double.parseDouble(value);
		}
		// bool
		if (abasType.matches("B")) {
			return value.toLowerCase().matches("ja")
					|| value.toLowerCase().matches("yes")
					|| value.toLowerCase().matches("true");
		}
		// TODO Datum
		return value;
	}

	public Object g(String varName) {
		return this.getValue("G|" + varName, EKS.Gvar(varName));
	}

	public Object l1(String varName) {
		return this.getValue("1|" + varName, EKS.getValue("1", varName));
	}

	public Object l2(String varName) {
		return this.getValue("2|" + varName, EKS.getValue("2", varName));
	}

	public Object l3(String varName) {
		return this.getValue("3|" + varName, EKS.getValue("3", varName));
	}

	public Object l4(String varName) {
		return this.getValue("4|" + varName, EKS.getValue("4", varName));
	}

	public Object l5(String varName) {
		return this.getValue("5|" + varName, EKS.getValue("5", varName));
	}

	public Object l6(String varName) {
		return this.getValue("6|" + varName, EKS.getValue("6", varName));
	}

	public Object l7(String varName) {
		return this.getValue("7|" + varName, EKS.getValue("7", varName));
	}

	public Object l8(String varName) {
		return this.getValue("8|" + varName, EKS.getValue("8", varName));
	}

	public Object l9(String varName) {
		return this.getValue("9|" + varName, EKS.getValue("9", varName));
	}

	/**
	 * liefert den abas Typ der Variable
	 * 
	 * @param variable
	 * @return
	 */
	private String getType(String variable) {
		return EKS.getValue("F", "typeof(" + variable + ")");
	}

	public boolean hole(String cmd) {
		return EKS.hole(cmd);
	}

	public boolean hole(String db, SelectionBuilder builder) {
		return this.hole(db, builder.toString());
	}

	public boolean hole(String db, String selection) {
		if (this.hselection != null && this.hselection.equals(selection)) {
			return EKS.hole(db);
		} else {
			this.hselection = selection;
			return EKS.hole(db + " \"" + selection + "\"");
		}
	}

	public boolean lade(String cmd) {
		return EKS.lade(cmd);
	}

	public boolean lade(int puffer, String cmd) {
		return EKS.lade(puffer + " " + cmd);
	}

	public boolean lade(int puffer, String db, SelectionBuilder builder) {
		return this.lade(puffer, db, builder.toString());
	}

	public boolean lade(int puffer, String db, String selection) {
		if (this.lselection[puffer] != null
				&& this.lselection[puffer].equals(selection)) {
			return EKS.lade(puffer + " " + db);
		} else {
			this.lselection[puffer] = selection;
			return EKS.lade(puffer + " " + db + " \"" + selection + "\"");
		}
	}

	public boolean dazu(String cmd) {
		return EKS.dazu(cmd);
	}

	public void mache(String cmd) {
		FO.mache(cmd);
	}

	public void addZeile() {
		FO.mache("maske zeile +O");
	}

	public void removeZeile() {
		FO.mache("maske zeile -O");
	}

	public void bringe(String cmd) {
		EKS.bringe(cmd);
	}

	public void ein(String fopName) {
		EKS.eingabe(fopName);
	}

	public void fo(String var, String value) {
		EKS.formel(var + "=\"" + value + "\"");
	}

	public void fo(String var, int value) {
		EKS.formel(var + "=" + value);
	}

	public void fo(String var, double value) {
		EKS.formel(var + "=" + new DecimalFormat("0.0000").format(value));
	}

	public void fo(String var, boolean value) {
		//FIXME Sprach unabhängigkeit
		EKS.formel(var + "=" + (value ? "ja" : "nein"));
	}
	
	public void box(String title, String content){
		FO.box(title,content);
	}

	public boolean mehr() {
		String mehr = EKS.Gvar("mehr").toLowerCase();
		// FIXME Sprachunterstützung
		return mehr != null
				&& (mehr.equals("ja") || mehr.equals("true") || mehr
						.equals("yes"));
	}

	public void println(String cmd) {
		EKS.println(cmd);
	}

}
