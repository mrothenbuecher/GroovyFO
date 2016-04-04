package de.finetech.groovy;

import groovy.lang.Script;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.EKS;
import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.utils.AbasDate;
import de.finetech.groovy.utils.GroovyFOException;
import de.finetech.groovy.utils.GroovyFOMap;
import de.finetech.utils.SelectionBuilder;

/**
 * 
 * @author Michael K�rbis, Finetech GmbH & Co. KG
 * 
 */
public abstract class AbasBaseScript extends Script {

	public final static String PIPE_PATTERN = Pattern.quote("|");

	// Temp Variablen um sich die letzten Selektion zu speichern
	private String hselection;
	private String[] lselection = new String[11];

	// private Pattern stringPattern =
	// Pattern.compile("(PS.*)|(ID.*)|(GL.*)|(T.*)|(N.*)|(BT.*)|(BG.*)|(ST.*)|(ST.*)|(SW.*)");
	private Pattern integerPattern = Pattern.compile("(I.*)|(GRN.*)|(K.*)");
	private Pattern realPattern = Pattern.compile("(R.*)|(M.*)");
	private Pattern boolPattern = Pattern.compile("(B)|(BOOL)");

	// maps f�r den einfachen zugriff auf die Felder bsp. m.von
	protected GroovyFOMap d = new GroovyFOMap("d", this);
	protected GroovyFOMap D = d;
	protected GroovyFOMap e = new GroovyFOMap("e", this);
	protected GroovyFOMap E = e;
	protected GroovyFOMap f = new GroovyFOMap("f", this);
	protected GroovyFOMap F = f;
	protected GroovyFOMap g = new GroovyFOMap("g", this);
	protected GroovyFOMap G = g;
	protected GroovyFOMap h = new GroovyFOMap("h", this);
	protected GroovyFOMap H = h;
	protected GroovyFOMap l1 = new GroovyFOMap("1", this);
	protected GroovyFOMap L1 = l1;
	protected GroovyFOMap l2 = new GroovyFOMap("2", this);
	protected GroovyFOMap L2 = l2;
	protected GroovyFOMap l3 = new GroovyFOMap("3", this);
	protected GroovyFOMap L3 = l3;
	protected GroovyFOMap l4 = new GroovyFOMap("4", this);
	protected GroovyFOMap L4 = l4;
	protected GroovyFOMap l5 = new GroovyFOMap("5", this);
	protected GroovyFOMap L5 = l5;
	protected GroovyFOMap l6 = new GroovyFOMap("6", this);
	protected GroovyFOMap L6 = l6;
	protected GroovyFOMap l7 = new GroovyFOMap("7", this);
	protected GroovyFOMap L7 = l7;
	protected GroovyFOMap l8 = new GroovyFOMap("8", this);
	protected GroovyFOMap L8 = l8;
	protected GroovyFOMap l9 = new GroovyFOMap("9", this);
	protected GroovyFOMap L9 = l9;
	protected GroovyFOMap m = new GroovyFOMap("m", this);
	protected GroovyFOMap M = m;
	protected GroovyFOMap p = new GroovyFOMap("p", this);
	protected GroovyFOMap P = p;
	protected GroovyFOMap s = new GroovyFOMap("p", this);
	protected GroovyFOMap S = s;
	protected GroovyFOMap t = new GroovyFOMap("t", this);
	protected GroovyFOMap T = t;
	protected GroovyFOMap u = new GroovyFOMap("u", this);
	protected GroovyFOMap U = u;

	// zwischenspeicher um nicht immer F|typeof aufrufen zum�ssen, schl�ssel ist
	// der Variablenname mit vorangestelltem Puffer (m|foo), Wert ist der abas
	// Typ
	protected ConcurrentHashMap<String, String> variableTypes = new ConcurrentHashMap<String, String>();

	/**
	 * die interne standard Sprache des groovyFO ist Deutsch
	 */
	public AbasBaseScript() {
		FO.setCommandLanguage(FO.CMDLANG_GERMAN);
	}

	public void absatz(String cmd) {
		EKS.absatz(cmd);
	}

	// TODO Methoden startTransaction, commitTransaction, abortTransaction,
	public void action(String cmd) {
		aktion(cmd);
	}

	public boolean add(String cmd) {
		return EKS.dazu(cmd);
	}

	public void addRow() {
		plusZeile();
	}

	@Deprecated
	public void addZeile() {
		plusZeile();
	}

	public void aktion(String cmd) {
		EKS.aktion(cmd);
	}

	/**
	 * Definition genau einer NutzerVariablen
	 * 
	 * @param def
	 *            bsp.: "GD2 xvon"
	 * @return liefert die Variablen bezeichnung zurück mit puffer bsp.: U|xvon
	 * @throws Exception
	 */
	public String art(String def) throws GroovyFOException {
		String[] split = def.trim().split(" ");
		if (split.length != 2)
			throw new GroovyFOException(
					"ung�ltige Parameteranzahl / invalid parameter number");
		return art(split[0], split[1]);
	}

	/**
	 * Definition genau einer NutzerVariablen
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            Bezeichnung der Variablen bsp.: "xvon"
	 * @return liefert die Variablen bezeichnung zurück mit puffer bsp.: U|xvon
	 * @throws GroovyFOException
	 */
	public String art(String type, String def) throws GroovyFOException {
		String defined = FO.getValue("F", "defined(" + def + ")").toLowerCase();
		if (!(defined.equals("ja") || defined.equals("true") || defined
				.equals("yes"))) {
			FO.art(type + " " + def);
			this.variableTypes.put("U|" + def, type);
		} else {
			// pr�fen ob die Typen �bereinstimmen
			if (!this.variableTypes.get("U|" + def).equals(type)) {
				throw new GroovyFOException("different types same name");
			}
		}
		return "U|" + def;
	}

	/**
	 * Definition von n NutzerVariablen eines Types
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            die Variablen bezeichnungen als array
	 * @return liefert die Variablen bezeichnung zurück mit puffer bsp.: U|xvon
	 * @throws GroovyFOException
	 */
	public String[] art(String type, String... def) throws GroovyFOException {
		String[] ba = new String[def.length];
		int i = 0;
		for (String foo : def) {
			ba[i++] = art(type, foo);
		}

		return ba;
	}

	public void assign(String cmd) {
		zuweisen(cmd);
	}

	/**
	 * @deprecated laut abas hilfe hier .uebersetzen nutzen
	 * @param cmd
	 */
	@Deprecated
	public void atext(String cmd) {
		FO.atext(cmd);
	}

	public void ausgabe(String cmd) {
		EKS.ausgabe(cmd);
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

	public void belegen(String cmd) {
		EKS.belegen(cmd);
	}

	public void blocksatz() {
		FO.blocksatz("");
	}

	public void blocksatz(String cmd) {
		FO.blocksatz(cmd);
	}

	public void box(String title, String content) {
		FO.box(title, content);
	}

	public void bringe() {
		bringe("");
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

	public void copy(String cmd) {
		this.kopieren(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem Dazu Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes D
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object d(String varname) throws FOPException, GroovyFOException {
		return this.getValue("D|" + varname, FO.Dvar(varname));
	}

	public void datei(String cmd) {
		EKS.datei(cmd);
	}

	public void delete(String cmd) {
		loesche(cmd);
	}

	public void down(String cmd) {
		unten(cmd);
	}

	public void drucke(String cmd) {
		EKS.drucke(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem E Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes E
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object e(String varname) throws FOPException, GroovyFOException {
		return this.getValue("E|" + varname, FO.Evar(varname));
	}

	public void edit(String cmd) {
		editiere(cmd);
	}

	public void editiere(String cmd) {
		EKS.editiere(cmd);
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

	public void error(String cmd) {
		fehler(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem F Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes F
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object f(String varname) throws FOPException, GroovyFOException {
		return this.getValue("F|" + varname, FO.Gvar(varname));
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

	public void fehler(String cmd) {
		EKS.fehler(cmd);
	}

	public void fenster(String cmd) {
		EKS.fenster(cmd);
	}

	public void file(String cmd) {
		datei(cmd);
	}

	public void flattersatz() {
		FO.flattersatz("");
	}

	public Object fo(String var, boolean value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + (value ? "G|TRUE" : "G|FALSE"));
		return this.getValue(var);
	}

	public Object fo(String var, double value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + new DecimalFormat("0.0000").format(value));
		return this.getValue(var);
	}

	public Object fo(String var, int value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + value);
		return this.getValue(var);
	}

	public Object fo(String var, AbasDate value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=\"" + value.toString() + "\"");
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
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object fo(String var, String value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=\"" + value.replaceAll("\"", "\"+'DBLQUOTE'+\"")
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
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object formel(String var, String value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + value);
		return this.getValue(var);
	}

	public Object formula(String var, String value) throws FOPException,
			GroovyFOException {
		return this.formel(var, value);
	}

	/**
	 * liefert den Variablenwert aus dem G Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes G
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object g(String varname) throws FOPException, GroovyFOException {
		return this.getValue("G|" + varname, FO.Gvar(varname));
	}

	public void gedruckt(String cmd) {
		EKS.gedruckt(cmd);
	}

	/**
	 * liefert den abas Typ der Variable
	 * 
	 * @param variable
	 *            mit vorangestellten Puffer buchstaben bsp.: H|id
	 * @return
	 */
	protected String getType(String variable) {
		variable = variable.toLowerCase();
		if (this.variableTypes.containsKey(variable)) {
			return this.variableTypes.get(variable);
		} else {
			// FIXME vorher pr�fen ob die Variable existiert!
			String type = FO.getValue("F", "typeof(" + variable + ")");
			this.variableTypes.put(variable, type);
			return type;
		}
	}

	/**
	 * 
	 * @param var
	 *            - Muss die form haben M|asd od. G|asd usw
	 * @return Wert der Variablen
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object getValue(String var) throws FOPException, GroovyFOException {
		String[] foo = var.split(PIPE_PATTERN);
		String buffer = foo[0];
		String varname = foo[1];
		return this.getValue(var, FO.getValue(buffer, varname));
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
	 * @throws GroovyFOException
	 */
	protected Object getValue(String varname, String value)
			throws GroovyFOException {
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
		if (boolPattern.matcher(abasType).matches()) {
			value = value.toLowerCase();
			return value.matches("ja") || value.matches("yes")
					|| value.matches("true");
		}
		if (AbasDate.isDate(abasType)) {
			return new AbasDate(abasType, varname, this);
		}
		// Strings
		return value;
	}

	/**
	 * liefert den Variablenwert aus dem Hole Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes H
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object h(String varname) throws FOPException, GroovyFOException {
		return this.getValue("H|" + varname, FO.Hvar(varname));
	}

	public void help(String cmd) {
		hilfe(cmd);
	}

	/**
	 * setzt die Hintergrund farbe f�r eine ganze zeile
	 * 
	 * @param c
	 * @param row
	 */
	public void hfarbe(Color c, int row) {
		this.hfarbe(this.colorToString(c), row);
	}

	/**
	 * setzt die Hintergrundfarbe f�r ein Feld im Kopfbereich
	 * 
	 * @param c
	 * @param field
	 */
	public void hfarbe(Color c, String field) {
		this.hfarbe(this.colorToString(c), field);
	}

	/**
	 * setzt die Hintergrundfarbe f�r ein Feld in einer Zeile
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
		FO.farbe("-HINTERGRUND " + color + " " + row);
	}

	public void hfarbe(String color, String field) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		FO.farbe("-HINTERGRUND " + color + " " + field);
	}

	public void hfarbe(String color, String field, int row) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		FO.farbe("-HINTERGRUND " + color + " " + field + " " + row);
	}

	public void hilfe(String cmd) {
		EKS.hilfe(cmd);
	}

	public void hinweis(String hinweis) {
		EKS.hinweis(hinweis);
	}

	public boolean hole(String cmd) {
		this.resetMap("h");
		return FO.hole(cmd);
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
	 * sein so wird nur der n�chste Datensatz geholt
	 * 
	 * @param db
	 *            - Datenbank von der Selektiert werden soll
	 * @param selection
	 *            - abas Selektionstring,
	 * @return liefert wahr falls die Selektion einen Datensatz holen konnte
	 */
	public boolean hole(String db, String selection) {
		if (this.hselection != null && this.hselection.equals(selection)) {
			return FO.hole(db);
		} else {
			this.hselection = selection;
			this.resetMap("h");
			return FO.hole(db + " \"" + selection + "\"");
		}
	}

	public void in(String fopName) {
		eingabe(fopName);
	}

	public void input(String fopName) {
		eingabe(fopName);
	}

	public void justified() {
		blocksatz("");
	}

	public void justified(String cmd) {
		blocksatz(cmd);
	}

	public void kom(String kommando) {
		kommando(kommando);
	}

	public void kommando(String kommando) {
		EKS.kommando(kommando);
	}

	public void kommandoWarten(String kommando) {
		kom("-WARTEN " + kommando);
	}

	public void komWarten(String kommando) {
		kom("-WARTEN " + kommando);
	}

	public void kopieren(String cmd) {
		EKS.kopieren(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem lade 1 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 1
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object l1(String varname) throws FOPException, GroovyFOException {
		return this.getValue("1|" + varname, FO.getValue("1", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 2 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 2
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object l2(String varname) throws FOPException, GroovyFOException {
		return this.getValue("2|" + varname, FO.getValue("2", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 3 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 3
	 * 
	 * @return
	 */
	public Object l3(String varname) throws FOPException, GroovyFOException {
		return this.getValue("3|" + varname, FO.getValue("3", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 4 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 4
	 * 
	 * @return
	 * @throws GroovyFOException
	 * @throws FOPException
	 */
	public Object l4(String varname) throws FOPException, GroovyFOException {
		return this.getValue("4|" + varname, FO.getValue("4", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 5 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 5
	 * 
	 * @return
	 */
	public Object l5(String varname) throws FOPException, GroovyFOException {
		return this.getValue("5|" + varname, FO.getValue("5", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 6 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 6
	 * 
	 * @return
	 */
	public Object l6(String varname) throws FOPException, GroovyFOException {
		return this.getValue("6|" + varname, FO.getValue("6", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 7 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 7
	 * 
	 * @return
	 */
	public Object l7(String varname) throws FOPException, GroovyFOException {
		return this.getValue("7|" + varname, FO.getValue("7", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 8 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 8
	 * 
	 * @return
	 */
	public Object l8(String varname) throws FOPException, GroovyFOException {
		return this.getValue("8|" + varname, FO.getValue("8", varname));
	}

	/**
	 * liefert den Variablenwert aus dem lade 9 Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestellte 9
	 * 
	 * @return
	 */
	public Object l9(String varname) throws FOPException, GroovyFOException {
		return this.getValue("9|" + varname, FO.getValue("9", varname));
	}

	/**
	 * 
	 * @param puffer
	 *            - Nummer des lade Puffers f�r den das Kommando ausgef�hrt
	 *            werden soll
	 * @param cmd
	 *            - Kommando das ausgef�hrt werden soll
	 * @return
	 */
	public boolean lade(int puffer, String cmd) {
		this.resetMap(Integer.toString(puffer));
		return FO.lade(puffer + " " + cmd);
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
			return FO.lade(puffer + " " + db);
		} else {
			this.resetMap(Integer.toString(puffer));
			this.lselection[puffer] = selection;
			return FO.lade(puffer + " " + db + " \"" + selection + "\"");
		}
	}

	public boolean lade(String cmd) {
		// .lade X ....
		this.resetMap(cmd.split(" ")[0]);
		return FO.lade(cmd);
	}

	public void laenge(String cmd) {
		EKS.laenge(cmd);
	}

	public void left(String cmd) {
		links(cmd);
	}

	public void length(String cmd) {
		laenge(cmd);
	}

	public void lesen(String cmd) {
		EKS.lesen(cmd);
	}

	public void line(String cmd) {
		zeile(cmd);
	}

	public void links(String cmd) {
		EKS.links(cmd);
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

	public void loesche(String cmd) {
		EKS.loesche(cmd);
	}

	/**
	 * liefert den Variablenwert aus dem Masken Puffer
	 * 
	 * @param varname
	 *            Variablenname ohne vorangestelltes M
	 * 
	 * @return
	 */
	public Object m(String varname) throws FOPException, GroovyFOException {
		return this.getValue("M|" + varname, FO.Mvar(varname));
	}

	public void mache(String cmd) {
		EKS.mache(cmd);
	}

	public void make(String cmd) {
		mache(cmd);
	}

	public boolean mehr() {
		String mehr = FO.Gvar("mehr").toLowerCase();
		// FIXME Sprachunterstützung
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

	public int menu(String title, String[] options, int highlight,
			boolean noReplace) {
		return this.menue(title, options, highlight, noReplace);
	}

	public int menue(String title, String[] options) {
		return FO.menue(title, options);
	}

	public int menue(String title, String[] options, int highlight) {
		return FO.menue(title, options, 0, 0, highlight);
	}

	public int menue(String title, String[] options, int highlight,
			boolean noReplace) {
		return FO.menue(title, options, 0, 0, highlight, noReplace);
	}

	public void merke(String cmd) {
		EKS.merke(cmd);
	}

	public boolean more() {
		return mehr();
	}

	public void note(String hinweis) {
		hinweis(hinweis);
	}

	public void oben(String cmd) {
		EKS.oben(cmd);
	}

	public void occupy(String cmd) {
		belegen(cmd);
	}

	public void output(String cmd) {
		ausgabe(cmd);
	}

	public Object p(String varname) throws FOPException, GroovyFOException {
		return this.getValue("P|" + varname, FO.Pvar(varname));
	}

	public void page(String cmd) {
		seite(cmd);
	}

	public void para(String cmd) {
		absatz(cmd);
	}

	public void plusZeile() {
		FO.mache("maske zeile +O");
	}

	public void print(String cmd) {
		drucke(cmd);
	}

	public void printed(String cmd) {
		gedruckt(cmd);
	}

	public void println(boolean cmd) {
		println(Boolean.toString(cmd));
	}

	public void println(double cmd) {
		println(Double.toString(cmd));
	}

	public void println(int cmd) {
		println(Integer.toString(cmd));
	}

	public void println(String cmd) {
		cmd = cmd.replaceAll("\"", "'DBLQUOTE'");
		if (cmd.length() > 2999)
			FO.println(cmd.substring(0, 2999));
		else
			FO.println(cmd);
	}

	public void println(Object cmd) {
		FO.println(cmd.toString());
	}

	public void protection(String cmd) {
		schutz(cmd);
	}

	public void read(String cmd) {
		lesen(cmd);
	}

	public void rechts(String cmd) {
		EKS.rechts(cmd);
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

	public void reserve(String cmd) {
		merke(cmd);
	}

	/**
	 * wenn sich der Bezug eines Laden/Holen/dazu buffers �ndert m�ssen alle
	 * Felder aus der Map zum merken des Datentypes entfernt werden
	 * 
	 * @param buffer
	 */
	private void resetMap(String buffer) {
		if (buffer != null && !buffer.isEmpty()) {
			buffer = buffer.toLowerCase();
			for (Entry<String, String> entry : this.variableTypes.entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(buffer)) {
					this.variableTypes.remove(entry.getKey());
				}
			}
		}
	}

	public void rewrite() {
		bringe("");
	}

	public void rewrite(String cmd) {
		bringe(cmd);
	}

	public void right(String cmd) {
		rechts(cmd);
	}

	public Object s(String varname) throws FOPException, GroovyFOException {
		return this.getValue("S|" + varname, FO.Svar(varname));
	}

	public void schutz(String cmd) {
		EKS.schutz(cmd);
	}

	public void seite(String cmd) {
		EKS.seite(cmd);
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

	public void seperator(String cmd) {
		trenner(cmd);
	}

	public void set(String cmd) {
		setze(cmd);
	}

	public void setze(String cmd) {
		EKS.setze(cmd);
	}

	public void sort(String cmd) {
		sortiere(cmd);
	}

	public void sortiere(String cmd) {
		EKS.sortiere(cmd);
	}

	public boolean success() {
		return mehr();
	}

	public Object t(String varname) throws FOPException, GroovyFOException {
		return this.getValue("T|" + varname, FO.Tvar(varname));
	}

	public void tabellensatz(String cmd) {
		EKS.tabellensatz(cmd);
	}

	public void tablerecord(String cmd) {
		tabellensatz(cmd);
	}

	public void translate(String cmd) {
		uebersetzen(cmd);
	}

	public void trenner(String cmd) {
		EKS.trenner(cmd);
	}

	public String type(String def) throws GroovyFOException {
		return art(def);
	}

	public String type(String type, String def) throws GroovyFOException {
		return art(type, def);
	}

	public String[] type(String type, String... def) throws GroovyFOException {
		return art(type, def);
	}

	public Object u(String varname) throws FOPException, GroovyFOException {
		return this.getValue("U|" + varname, FO.Uvar(varname));
	}

	public void uebersetzen(String cmd) {
		EKS.uebersetzen(cmd);
	}

	public void unjustified() {
		FO.flattersatz("");
	}

	public void unjustified(String cmd) {
		FO.flattersatz(cmd);
	}

	public void unten(String cmd) {
		EKS.unten(cmd);
	}

	public void up(String cmd) {
		oben(cmd);
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
		FO.farbe("-VORDERGRUND " + color + " " + row);
	}

	public void vfarbe(String color, String field) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		FO.farbe("-VORDERGRUND " + color + " " + field);
	}

	public void vfarbe(String color, String field, int row) {
		if (color == null || color.isEmpty()) {
			color = "-1 -1 -1";
		}
		FO.farbe("-VORDERGRUND " + color + " " + field + " " + row);
	}

	public void view(String cmd) {
		zeige(cmd);
	}

	public void window(String cmd) {
		fenster(cmd);
	}

	public void zeige(String cmd) {
		EKS.zeige(cmd);
	}

	public void zeile(String cmd) {
		EKS.zeile(cmd);
	}

	public void zuweisen(String cmd) {
		EKS.zuweisen(cmd);
	}

}
