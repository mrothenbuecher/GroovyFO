package de.finetech.groovy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

import groovy.lang.Script;
import de.abas.eks.jfop.remote.EKS;
import de.abas.eks.jfop.remote.FO;

/**
 * 
 * @author Michael Kürbis, Finetech GmbH & Co. KG
 * 
 */
public abstract class AbasBaseScript extends Script {

	private final static String pipe = Pattern.quote("|");

	// Temp variablen um sich die letzten Selektion zu speichern
	private String hselection;
	private String[] lselection = new String[11];

	private HashMap<String, String> variableTypes = new HashMap<String, String>();

	//private Pattern stringPattern = Pattern.compile("(PS.*)|(ID.*)|(GL.*)|(T.*)|(N.*)|(BT.*)|(BG.*)|(ST.*)|(ST.*)|(SW.*)");
	private Pattern integerPattern = Pattern.compile("(I.*)|(GRN.*)|(K.*)");
	private Pattern realPattern = Pattern.compile("(R.*)|(M.*)");
	
	
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
		// Integer
		if (!abasType.startsWith("ID") && integerPattern.matcher(abasType).matches()) {
			if (value == null || value.isEmpty())
				return 0;
			return Integer.parseInt(value);
		}
		// Real
		if (realPattern.matcher(abasType).matches()) {
			if (value == null || value.isEmpty())
				return 0.0d;
			return Double.parseDouble(value);
		}
		// bool
		if (abasType.matches("B")) {
			value = value.toLowerCase();
			return value.matches("ja")
					|| value.matches("yes")
					|| value.matches("true");
		}
		// Strings
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
		if (this.variableTypes.containsKey(variable)) {
			return this.variableTypes.get(variable);
		} else {
			String type = EKS.getValue("F", "typeof(" + variable + ")");
			this.variableTypes.put(variable, type);
			return type;
		}
	}

	public boolean hole(String cmd) {
		return EKS.hole(cmd);
	}

	/**
	 * Nutzt die Methode {@link #hole(String, String)}
	 * 
	 * @param db
	 * @param builder
	 * @return
	 */
	public boolean hole(String db, SelectionBuilder builder) {
		return this.hole(db, builder.toString());
	}

	/**
	 * sollte der Selektionstring Identisch mit einer vorher gehenden Abfragen
	 * sein so wird nur der nächste Datensatz geholt
	 * 
	 * @param db
	 *            - Datenbank von der Selektiert werden soll
	 * @param selection
	 *            - abas Selektionstring,
	 * @return liefert wahr falls die Selektion einen Datensatz holen konnte
	 */
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

	/**
	 * Nutzt die Methode {@link #lade(int, String, String)}
	 * 
	 * @param puffer
	 * @param db
	 * @param builder
	 * @return
	 */
	public boolean lade(int puffer, String db, SelectionBuilder builder) {
		return this.lade(puffer, db, builder.toString());
	}

	/**
	 * 
	 * sollte der Selektionstring und der Puffer Identisch mit einer vorher
	 * gehenden Abfragen sein so wird nur der nächste Datensatz geholt
	 * 
	 * @param puffer
	 *            - lade puffer 1-9
	 * @param db
	 *            - Datenbank von der abgefragt werden soll
	 * @param selection
	 *            - abas Selektionsstring
	 * @return liefert wahr falls die Selektion einen Datensatz holen konnte
	 */
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

	/**
	 * 
	 * @param var
	 *            - Muss die form haben M|asd od. G|asd usw
	 * @return Wert der Variablen
	 */
	public Object getValue(String var) {
		String[] foo = var.split(pipe);
		String buffer = foo[0];
		String varname = foo[1];
		return this.getValue(var, EKS.getValue(buffer, varname));
	}

	/**
	 * 
	 * @param var
	 *            = variable der ein wert zugewiesen werden soll (Format
	 *            Puffer|varname)
	 * @param value
	 *            = Formel welche abas seitig interpretiert werden soll
	 * @return
	 */
	public Object formel(String var, String value) {
		EKS.formel(var + "=" + value);
		return this.getValue(var);
	}

	/**
	 * 
	 * @param var
	 *            = variable der ein wert zugewiesen werden soll (Format
	 *            Puffer|varname)
	 * @param value
	 *            = Zeichenkette welche der variablen zugewiesen werden soll
	 * @return
	 */
	public Object fo(String var, String value) {
		EKS.formel(var + "=\"" + value + "\"");
		return this.getValue(var);
	}

	public Object fo(String var, int value) {
		EKS.formel(var + "=" + value);
		return this.getValue(var);
	}

	public Object fo(String var, double value) {
		EKS.formel(var + "=" + new DecimalFormat("0.0000").format(value));
		return this.getValue(var);
	}

	public Object fo(String var, boolean value) {
		// FIXME Sprach unabhängigkeit
		EKS.formel(var + "=" + (value ? "TRUE" : "FALSE"));
		return this.getValue(var);
	}

	public void box(String title, String content) {
		FO.box(title, content);
	}

	public boolean mehr() {
		String mehr = EKS.Gvar("mehr").toLowerCase();
		// FIXME Sprachunterstützung
		return mehr != null
				&& (mehr.equals("ja") || mehr.equals("true") || mehr
						.equals("yes"));
	}

	public void kom(String kommando) {
		EKS.kommando(kommando);
	}

	public void komSofort(String kommando) {
		EKS.kommando("-SOFORT " + kommando);
	}

	public void komWarten(String kommando) {
		EKS.kommando("-WARTEN " + kommando);
	}

	/**
	 * Definition genau einer Nutzervariablen
	 * 
	 * @param def
	 *            bsp.: "GD2 xvon"
	 * @return liefert die variablen bezeichnung zurück mit puffer bsp.: U|xvon
	 */
	public String art(String def) {
		EKS.art(def);
		return "U|" + def.trim().split(" ")[1];
	}

	/**
	 * Definition genau einer Nutzervariablen
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            bsp.: "xvon"
	 * @return liefert die variablen bezeichnung zurück mit puffer bsp.: U|xvon
	 */
	public String art(String type, String def) {
		EKS.art(type + " " + def);
		return "U|" + def;
	}

	/**
	 * Definition von n Nutzervariablen eines Types
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            die variablen bezeichnungen als array
	 * @return liefert die variablen bezeichnung zurück mit puffer bsp.: U|xvon
	 */
	public String[] art(String type, String[] def) {
		String[] ba = new String[def.length];
		int i = 0;
		for (String foo : def) {
			EKS.art(type + " " + foo);
			ba[i++] = "U|" + foo;
		}

		return ba;
	}

	public void println(String cmd) {
		EKS.println(cmd);
	}

	public void println(int cmd) {
		EKS.println(Integer.toString(cmd));
	}

	public void println(double cmd) {
		EKS.println(Double.toString(cmd));
	}

	public void println(boolean cmd) {
		EKS.println(Boolean.toString(cmd));
	}

	public void farbe(String cmd) {
		EKS.farbe(cmd);
	}

	public void hfarbe(String color, String field) {
		if(color == null || color.isEmpty()){
			color = "-1 -1 -1";
		}
		EKS.farbe("-HINTERGRUND " + color + " " + field);
	}

	public void hfarbe(String color, String field, int row) {
		if(color == null || color.isEmpty()){
			color = "-1 -1 -1";
		}
		EKS.farbe("-HINTERGRUND " + color + " " + field + " " + row);
	}

	public void hfarbe(String color, int row) {
		if(color == null || color.isEmpty()){
			color = "-1 -1 -1";
		}
		EKS.farbe("-HINTERGRUND " + color + " " + row);
	}

	public void vfarbe(String color, String field) {
		if(color == null || color.isEmpty()){
			color = "-1 -1 -1";
		}
		EKS.farbe("-VORDERGRUND " + color + " " + field);
	}

	public void vfarbe(String color, String field, int row) {
		if(color == null || color.isEmpty()){
			color = "-1 -1 -1";
		}
		EKS.farbe("-VORDERGRUND " + color + " " + field + " " + row);
	}

	public void vfarbe(String color, int row) {
		if(color == null || color.isEmpty()){
			color = "-1 -1 -1";
		}
		EKS.farbe("-VORDERGRUND " + color + " " + row);
	}

	protected String colorToString(Color c) {
		if (c == null)
			return "-1 -1 -1";
		else
			return c.getRed() + " " + c.getGreen() + " " + c.getBlue();
	}

	public void vfarbe(Color c, int row) {
		this.vfarbe(this.colorToString(c), row);
	}

	public void vfarbe(Color c, String field) {
		this.vfarbe(this.colorToString(c), field);
	}

	public void vfarbe(Color c, String field, int row) {
		this.vfarbe(this.colorToString(c), field, row);
	}

	public void hfarbe(Color c, int row) {
		this.hfarbe(this.colorToString(c), row);
	}

	public void hfarbe(Color c, String field) {
		this.hfarbe(this.colorToString(c), field);
	}

	public void hfarbe(Color c, String field, int row) {
		this.hfarbe(this.colorToString(c), field, row);
	}

}
