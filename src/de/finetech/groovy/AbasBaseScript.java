package de.finetech.groovy;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

import groovy.lang.Script;
import de.abas.eks.jfop.remote.EKS;
import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.utils.GroovyFOException;

/**
 * 
 * @author Michael Kürbis, Finetech GmbH & Co. KG
 * 
 */
public abstract class AbasBaseScript extends Script {

	private final static String pipe = Pattern.quote("|");

	// Temp Variablen um sich die letzten Selektion zu speichern
	private String hselection;
	private String[] lselection = new String[11];

	// private Pattern stringPattern =
	// Pattern.compile("(PS.*)|(ID.*)|(GL.*)|(T.*)|(N.*)|(BT.*)|(BG.*)|(ST.*)|(ST.*)|(SW.*)");
	private Pattern integerPattern = Pattern.compile("(I.*)|(GRN.*)|(K.*)");
	private Pattern realPattern = Pattern.compile("(R.*)|(M.*)");

	// zwischenspeicher um nicht immer F|typeof aufrufen zumüssen, schlüssel ist
	// der Variablenname mit vorangestelltem Puffer (m|foo), Wert ist der abas Typ
	private HashMap<String, String> variableTypes = new HashMap<String, String>();

	/**
	 * die interne standard Sprache des groovyFO ist Deutsch
	 */
	public AbasBaseScript() {
		FO.setCommandLanguage(FO.CMDLANG_GERMAN);
	}

	public boolean add(String cmd) {
		return dazu(cmd);
	}

	public void addRow() {
		FO.mache("maske zeile +O");
	}

	@Deprecated
	public void addZeile() {
		plusZeile();
	}

	/**
	 * Definition genau einer NutzerVariablen
	 * 
	 * @param def
	 *            bsp.: "GD2 xvon"
	 * @return liefert die Variablen bezeichnung zurÃ¼ck mit puffer bsp.: U|xvon
	 * @throws Exception
	 */
	public String art(String def) throws GroovyFOException {
		String[] split = def.trim().split(" ");
		if (split.length != 2)
			throw new GroovyFOException(
					"ungültige Parameteranzahl / invalid parameter number");
		return art(split[0], split[1]);
	}

	/**
	 * Definition genau einer NutzerVariablen
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            bsp.: "xvon"
	 * @return liefert die Variablen bezeichnung zurÃ¼ck mit puffer bsp.: U|xvon
	 */
	public String art(String type, String def) {
		// FIXME vorher prüfen ob die Variable schon existiert
		// FIXME in Type Map aufnehmen
		EKS.art(type + " " + def);
		return "U|" + def;
	}

	/**
	 * Definition von n NutzerVariablen eines Types
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            die Variablen bezeichnungen als array
	 * @return liefert die Variablen bezeichnung zurÃ¼ck mit puffer bsp.: U|xvon
	 */
	public String[] art(String type, String... def) {
		String[] ba = new String[def.length];
		int i = 0;
		for (String foo : def) {
			EKS.art(type + " " + foo);
			// FIXME vorher prüfen ob die Variable schon existiert
			// FIXME in Type Map aufnehmen
			ba[i++] = "U|" + foo;
		}

		return ba;
	}

	public void bcolor(Color c, int row) {
		this.hfarbe(this.colorToString(c), row);
	}

	public void bcolor(Color c, String field) {
		this.hfarbe(this.colorToString(c), field);
	}

	public void bcolor(Color c, String field, int row) {
		this.hfarbe(this.colorToString(c), field, row);
	}

	public void bcolor(String color, int row) {
		this.hfarbe(color, row);
	}

	public void bcolor(String color, String field) {
		this.hfarbe(color, field);
	}

	public void bcolor(String color, String field, int row) {
		this.hfarbe(color, field, row);
	}

	public void bcolour(Color c, int row) {
		this.hfarbe(this.colorToString(c), row);
	}

	public void bcolour(Color c, String field) {
		this.hfarbe(this.colorToString(c), field);
	}

	public void bcolour(Color c, String field, int row) {
		this.hfarbe(this.colorToString(c), field, row);
	}

	public void bcolour(String color, int row) {
		this.hfarbe(color, row);
	}

	public void bcolour(String color, String field) {
		this.hfarbe(color, field);
	}

	public void bcolour(String color, String field, int row) {
		this.hfarbe(color, field, row);
	}

	public void box(String title, String content) {
		FO.box(title, content);
	}

	public void bringe(String cmd) {
		EKS.bringe(cmd);
	}

	public void color(String cmd) {
		this.farbe(cmd);
	}

	protected String colorToString(Color c) {
		if (c == null)
			return "-1 -1 -1";
		else
			return c.getRed() + " " + c.getGreen() + " " + c.getBlue();
	}

	public void colour(String cmd) {
		this.farbe(cmd);
	}

	public void com(String kommando) {
		this.kom(kommando);
	}

	public void commando(String kommando) {
		this.com(kommando);
	}

	public void commandoWait(String kommando) {
		this.comWait(kommando);
	}

	public void comWait(String kommando) {
		this.komWarten(kommando);
	}

	/**
	 * liefert den Variablenwert aus dem Dazu Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes D
	 * 
	 * @return
	 */
	public Object d(String varname) {
		return this.getValue("D|" + varname, EKS.Dvar(varname));
	}

	public boolean dazu(String cmd) {
		return EKS.dazu(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem E Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes E
	 * 
	 * @return
	 */
	public Object e(String varname) {
		return this.getValue("E|" + varname, EKS.Evar(varname));
	}

	public void ein(String fopName) {
		EKS.eingabe(fopName);
	}

	public void eingabe(String fopName) {
		EKS.eingabe(fopName);
	}

	public void entfAlleZeilen() {
		FO.mache("maske zeile --");
	}

	public void entfZeile() {
		FO.mache("maske zeile -O");
	}

	public void farbe(String cmd) {
		EKS.farbe(cmd);
	}

	public void fcolor(Color c, int row) {
		this.vfarbe(this.colorToString(c), row);
	}

	public void fcolor(Color c, String field) {
		this.vfarbe(this.colorToString(c), field);
	}

	public void fcolor(Color c, String field, int row) {
		this.vfarbe(this.colorToString(c), field, row);
	}

	public void fcolor(String color, int row) {
		this.vfarbe(color, row);
	}

	public void fcolor(String color, String field) {
		this.vfarbe(color, field);
	}

	public void fcolor(String color, String field, int row) {
		this.vfarbe(color, field, row);
	}

	// grrr
	public void fcolour(Color c, int row) {
		this.vfarbe(this.colorToString(c), row);
	}

	public void fcolour(Color c, String field) {
		this.vfarbe(this.colorToString(c), field);
	}

	public void fcolour(Color c, String field, int row) {
		this.vfarbe(this.colorToString(c), field, row);
	}

	public void fcolour(String color, int row) {
		this.vfarbe(color, row);
	}

	public void fcolour(String color, String field) {
		this.vfarbe(color, field);
	}

	public void fcolour(String color, String field, int row) {
		this.vfarbe(color, field, row);
	}

	public Object fo(String var, boolean value) {
		// FIXME Sprach unabhÃ¤ngigkeit
		EKS.formel(var + "=" + (value ? "TRUE" : "FALSE"));
		return this.getValue(var);
	}

	public Object fo(String var, double value) {
		EKS.formel(var + "=" + new DecimalFormat("0.0000").format(value));
		return this.getValue(var);
	}

	public Object fo(String var, int value) {
		EKS.formel(var + "=" + value);
		return this.getValue(var);
	}

	/**
	 * 
	 * @param var
	 *            = variable der ein wert zugewiesen werden soll (Format
	 *            Puffer|varname)
	 * @param value
	 *            = Zeichenkette welche der Variablen zugewiesen werden soll
	 * @return
	 */
	public Object fo(String var, String value) {
		EKS.formel(var + "=\"" + value.replaceAll("\"", "\"+'DBLQUOTE'+\"")
				+ "\"");
		return this.getValue(var);
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
	 * liefert den Variablenwert aus dem G Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes G
	 * 
	 * @return
	 */
	public Object g(String varname) {
		return this.getValue("G|" + varname, EKS.Gvar(varname));
	}

	/**
	 * liefert den abas Typ der Variable
	 * 
	 * @param variable
	 *            mit vorangestellten Puffer buchstaben bsp.: H|id
	 * @return
	 */
	private String getType(String variable) {
		variable = variable.toLowerCase();
		if (this.variableTypes.containsKey(variable)) {
			return this.variableTypes.get(variable);
		} else {
			// FIXME vorher prüfen ob die Variable existiert!
			String type = EKS.getValue("F", "typeof(" + variable + ")");
			this.variableTypes.put(variable, type);
			return type;
		}
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
	 * liefert basierend auf dem abas internen Typ den Wert einer Variablen
	 * 
	 * Typen der Bezeichnung (I.*)|(GRN.*)|(K.*) trifft Integer Typen der
	 * Bezeichnung (R.*)|(M.*) trifft sind double Typen der Bezeichnung (B)
	 * trifft sind boolean alle anderen werden als String interpretiert
	 * 
	 * @param varname
	 *            Variablenname dessen Typ ermittelt werden soll
	 * @param value
	 *            wert dieser Variable als String
	 * 
	 * @return
	 */
	private Object getValue(String varname, String value) {
		// Mapping der einzelnen abas Variablenarten auf Standard Typen
		String abasType = this.getType(varname).toUpperCase();
		// Integer
		if (!abasType.startsWith("ID")
				&& integerPattern.matcher(abasType).matches()) {
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
			return value.matches("ja") || value.matches("yes")
					|| value.matches("true");
		}
		// Strings
		// TODO Datum
		return value;
	}

	/**
	 * liefert den Variablenwert aus dem Hole Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes H
	 * 
	 * @return
	 */
	public Object h(String varname) {
		return this.getValue("H|" + varname, EKS.Hvar(varname));
	}

	/**
	 * setzt die Hintergrund farbe für eine ganze zeile
	 * 
	 * @param c
	 * @param row
	 */
	public void hfarbe(Color c, int row) {
		this.hfarbe(this.colorToString(c), row);
	}

	/**
	 * setzt die Hintergrundfarbe für ein Feld im Kopfbereich
	 * 
	 * @param c
	 * @param field
	 */
	public void hfarbe(Color c, String field) {
		this.hfarbe(this.colorToString(c), field);
	}

	/**
	 * setzt die Hintergrundfarbe für ein Feld in einer Zeile
	 * 
	 * @param c
	 * @param field
	 * @param row
	 */
	public void hfarbe(Color c, String field, int row) {
		this.hfarbe(this.colorToString(c), field, row);
	}

	public void hfarbe(String color, int row) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		EKS.farbe("-HINTERGRUND " + color + " " + row);
	}

	public void hfarbe(String color, String field) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		EKS.farbe("-HINTERGRUND " + color + " " + field);
	}

	public void hfarbe(String color, String field, int row) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		EKS.farbe("-HINTERGRUND " + color + " " + field + " " + row);
	}

	public void hinweis(String hinweis) {
		EKS.hinweis(hinweis);
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

	public void in(String fopName) {
		EKS.eingabe(fopName);
	}

	public void input(String fopName) {
		EKS.eingabe(fopName);
	}

	public void kom(String kommando) {
		EKS.kommando(kommando);
	}

	public void komWarten(String kommando) {
		EKS.kommando("-WARTEN " + kommando);
	}

	/**
	 * liefert den Variablenwert aus dem lade 1 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 1
	 * 
	 * @return
	 */
	public Object l1(String varname) {
		return this.getValue("1|" + varname, EKS.getValue("1", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 2 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 2
	 * 
	 * @return
	 */
	public Object l2(String varname) {
		return this.getValue("2|" + varname, EKS.getValue("2", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 3 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 3
	 * 
	 * @return
	 */
	public Object l3(String varname) {
		return this.getValue("3|" + varname, EKS.getValue("3", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 4 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 4
	 * 
	 * @return
	 */
	public Object l4(String varname) {
		return this.getValue("4|" + varname, EKS.getValue("4", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 5 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 5
	 * 
	 * @return
	 */
	public Object l5(String varname) {
		return this.getValue("5|" + varname, EKS.getValue("5", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 6 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 6
	 * 
	 * @return
	 */
	public Object l6(String varname) {
		return this.getValue("6|" + varname, EKS.getValue("6", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 7 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 7
	 * 
	 * @return
	 */
	public Object l7(String varname) {
		return this.getValue("7|" + varname, EKS.getValue("7", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 8 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 8
	 * 
	 * @return
	 */
	public Object l8(String varname) {
		return this.getValue("8|" + varname, EKS.getValue("8", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 9 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 9
	 * 
	 * @return
	 */
	public Object l9(String varname) {
		return this.getValue("9|" + varname, EKS.getValue("9", varname));
	}

	/**
	 * 
	 * @param puffer
	 *            - Nummer des lade Puffers für den das Kommando ausgeführt
	 *            werden soll
	 * @param cmd
	 *            - Kommando das ausgeführt werden soll
	 * @return
	 */
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
	 * gehenden Abfragen sein so wird nur der nÃ¤chste Datensatz geholt
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

	public boolean lade(String cmd) {
		return EKS.lade(cmd);
	}

	public boolean load(int puffer, String cmd) {
		return lade(puffer + " " + cmd);
	}

	public boolean load(int puffer, String db, SelectionBuilder builder) {
		return this.lade(puffer, db, builder.toString());
	}

	public boolean load(int puffer, String db, String selection) {
		return lade(puffer, db, selection);
	}

	public boolean load(String cmd) {
		return lade(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem Masken Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes M
	 * 
	 * @return
	 */
	public Object m(String varname) {
		return this.getValue("M|" + varname, EKS.Mvar(varname));
	}

	public void mache(String cmd) {
		FO.mache(cmd);
	}

	public void make(String cmd) {
		mache(cmd);
	}

	public boolean mehr() {
		String mehr = EKS.Gvar("mehr").toLowerCase();
		// FIXME SprachunterstÃ¼tzung
		return mehr != null
				&& (mehr.equals("ja") || mehr.equals("true") || mehr
						.equals("yes"));
	}

	public int menu(String title, String[] options) {
		return this.menue(title, options);
	}

	public int menu(String title, String[] options, int highlight) {
		return this.menue(title, options, highlight);
	}

	public int menu(String title, String[] options, int highlight, boolean noReplace) {
		return this.menue(title, options, highlight, noReplace);
	}
	
	public int menue(String title, String[] options) {
		return FO.menue(title, options);
	}

	public int menue(String title, String[] options, int highlight) {
		return FO.menue(title, options, 0, 0, highlight);
	}
	
	public int menue(String title, String[] options, int highlight, boolean noReplace) {
		return FO.menue(title, options, 0, 0, highlight, noReplace);
	}

	public boolean more() {
		return mehr();
	}

	public void note(String hinweis) {
		this.hinweis(hinweis);
	}

	public Object p(String varname) {
		return this.getValue("P|" + varname, EKS.Pvar(varname));
	}

	public void plusZeile() {
		FO.mache("maske zeile +O");
	}

	public void print(boolean cmd) {
		EKS.println("-lfsuppress " + Boolean.toString(cmd));
	}

	public void print(double cmd) {
		EKS.println("-lfsuppress " + Double.toString(cmd));
	}

	public void print(int cmd) {
		EKS.println("-lfsuppress " + Integer.toString(cmd));
	}

	public void print(String cmd) {
		EKS.println("-lfsuppress " + cmd.replaceAll("\"", "'DBLQUOTE'"));
	}

	public void println(boolean cmd) {
		EKS.println(Boolean.toString(cmd));
	}

	public void println(double cmd) {
		EKS.println(Double.toString(cmd));
	}

	public void println(int cmd) {
		EKS.println(Integer.toString(cmd));
	}

	public void println(String cmd) {
		EKS.println(cmd.replaceAll("\"", "'DBLQUOTE'"));
	}

	public void removeAllRows() {
		this.entfAlleZeilen();
	}

	public void removeRow() {
		this.entfZeile();
	}

	@Deprecated
	public void removeZeile() {
		this.entfZeile();
	}

	public void rewrite(String cmd) {
		bringe(cmd);
	}

	public Object s(String varname) {
		return this.getValue("S|" + varname, EKS.Svar(varname));
	}

	public boolean select(String cmd) {
		return hole(cmd);
	}

	public boolean select(String db, SelectionBuilder builder) {
		return this.hole(db, builder);
	}

	public boolean select(String db, String selection) {
		return hole(db, selection);
	}

	public Object t(String varname) {
		return this.getValue("T|" + varname, EKS.Tvar(varname));
	}

	public String type(String def) throws GroovyFOException {
		return art(def);
	}

	public String type(String type, String def) {
		return art(type, def);
	}

	public String[] type(String type, String... def) {
		return art(type, def);
	}

	public Object u(String varname) {
		return this.getValue("U|" + varname, EKS.Uvar(varname));
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

	public void vfarbe(String color, int row) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		EKS.farbe("-VORDERGRUND " + color + " " + row);
	}

	public void vfarbe(String color, String field) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		EKS.farbe("-VORDERGRUND " + color + " " + field);
	}

	public void vfarbe(String color, String field, int row) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		EKS.farbe("-VORDERGRUND " + color + " " + field + " " + row);
	}

}
