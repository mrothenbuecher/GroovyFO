package de.finetech.groovy;

import groovy.lang.GroovyObject;
import groovy.lang.Script;
import groovy.transform.CompileStatic;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.EKS;
import de.abas.eks.jfop.remote.FO;
import de.abas.eks.jfop.remote.FOPSessionContext;
import de.abas.erp.db.DbContext;
import de.abas.jfop.base.buffer.BufferFactory;
import de.finetech.groovy.utils.GroovyFOBaseReadableMap;
import de.finetech.groovy.utils.GroovyFODBufferMap;
import de.finetech.groovy.utils.GroovyFOException;
import de.finetech.groovy.utils.GroovyFOFBufferMap;
import de.finetech.groovy.utils.GroovyFOWriteableMap;
import de.finetech.groovy.utils.datatypes.AbasDate;
import de.finetech.groovy.utils.datatypes.AbasPointer;
import de.finetech.utils.SelectionBuilder;

/**
 * 
 * Basisklasse von der jedes Script abgeleitet wird
 * 
 * @author Michael Rothenbücher, Finetech GmbH & Co. KG
 * 
 */
@CompileStatic
public abstract class AbasBaseScript extends Script implements GroovyObject {

	protected static enum PossibleDatatypes {
		INTEGER, DOUBLE, DOUBLED, DOUBLET, DOUBLEDT, BOOLEAN, ABASDATE, ABASPOINTER, STRING
	}

	protected static DecimalFormat df = new DecimalFormat("0.0000");

	// Temp Variablen um sich die letzten Selektion zu speichern
	private String hselection;
	private String dselection;
	private String[] lselection = new String[11];

	// reguläre Ausdrücke zum erkennen von Variablen arten
	private Pattern integerPattern = Pattern.compile("(I[0-9])|(IP.*)|(IN.*)|(K.*)");
	private Pattern doublePattern = Pattern.compile("(R.*)|(M.*)");
	private Pattern doubledPattern = Pattern.compile("(R.*D.*)|(M.*D.*)");
	private Pattern doubletPattern = Pattern.compile("(R.*T.*)|(M.*T.*)");
	private Pattern doubledtPattern = Pattern.compile("(R.*DT.*)|(M.*DT.*)");

	private Pattern boolPattern = Pattern.compile("(B)|(BOOL)");
	private Pattern pointerPattern = Pattern.compile("(P.*)|(ID.*)|(VP.*)|(VID.*)|(C.*)");
	private Pattern varPattern = Pattern.compile("([a-zA-Z]\\|[a-zA-Z0-9]*)");

	protected BufferFactory bfactory = BufferFactory.newInstance();

	// maps für den einfachen zugriff auf die Felder bsp. m.von
	// FIXME dazu puffer
	protected GroovyFODBufferMap d = new GroovyFODBufferMap(this);;
	protected GroovyFODBufferMap D = d;
	protected GroovyFOWriteableMap a = new GroovyFOWriteableMap(bfactory.getParentScreenBuffer(), this);
	protected GroovyFOWriteableMap A = a;
	protected GroovyFOBaseReadableMap e = new GroovyFOBaseReadableMap(bfactory.getEnvBuffer(), this);
	protected GroovyFOBaseReadableMap E = e;
	protected GroovyFOWriteableMap g = new GroovyFOWriteableMap(bfactory.getGlobalTextBuffer(), this);
	protected GroovyFOWriteableMap G = g;
	protected GroovyFOFBufferMap f = new GroovyFOFBufferMap(this);
	protected GroovyFOFBufferMap F = f;
	protected GroovyFOWriteableMap h = new GroovyFOWriteableMap(bfactory.getSelectBuffer(), this);
	protected GroovyFOWriteableMap H = h;
	protected GroovyFOWriteableMap l = new GroovyFOWriteableMap(bfactory.getSelectBarBuffer(), this);
	protected GroovyFOWriteableMap L = l;
	protected GroovyFOWriteableMap l0 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(0), this);
	protected GroovyFOWriteableMap L0 = l0;
	protected GroovyFOWriteableMap l1 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(1), this);
	protected GroovyFOWriteableMap L1 = l1;
	protected GroovyFOWriteableMap l2 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(2), this);
	protected GroovyFOWriteableMap L2 = l2;
	protected GroovyFOWriteableMap l3 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(3), this);
	protected GroovyFOWriteableMap L3 = l3;
	protected GroovyFOWriteableMap l4 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(4), this);
	protected GroovyFOWriteableMap L4 = l4;
	protected GroovyFOWriteableMap l5 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(5), this);
	protected GroovyFOWriteableMap L5 = l5;
	protected GroovyFOWriteableMap l6 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(6), this);
	protected GroovyFOWriteableMap L6 = l6;
	protected GroovyFOWriteableMap l7 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(7), this);
	protected GroovyFOWriteableMap L7 = l7;
	protected GroovyFOWriteableMap l8 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(8), this);
	protected GroovyFOWriteableMap L8 = l8;
	protected GroovyFOWriteableMap l9 = new GroovyFOWriteableMap(bfactory.getLoadBuffer(9), this);
	protected GroovyFOWriteableMap L9 = l9;
	protected GroovyFOWriteableMap m = new GroovyFOWriteableMap(bfactory.getScreenBuffer(), this);
	protected GroovyFOWriteableMap M = m;
	protected GroovyFOWriteableMap p = new GroovyFOWriteableMap(bfactory.getPrintBuffer(), this);
	protected GroovyFOWriteableMap P = p;
	protected GroovyFOWriteableMap s = new GroovyFOWriteableMap(bfactory.getCharactBarBuffer(), this);
	protected GroovyFOWriteableMap S = s;
	protected GroovyFOWriteableMap u = new GroovyFOWriteableMap(bfactory.getUserTextBuffer(), this);

	protected GroovyFOWriteableMap U = u;

	// zwischenspeicher um nicht immer F|typeof aufrufen zumüssen, schlüssel ist
	// der Variablenname mit vorangestelltem Puffer (m|foo), Wert ist der abas
	// Typ
	protected ConcurrentHashMap<String, PossibleDatatypes> variableTypes = new ConcurrentHashMap<String, PossibleDatatypes>();
	// zwischenspeicher um nicht immer neue Objekte erzeugen zu müssen
	protected ConcurrentHashMap<String, AbasDate> variables = new ConcurrentHashMap<String, AbasDate>();

	protected FOPSessionContext arg0;

	protected String[] args;

	protected DbContext dbContext;

	/**
	 * die interne standard Sprache des groovyFO ist Deutsch
	 */
	public AbasBaseScript() {
	}

	/**
	 * .absatz cmd
	 * 
	 * @param cmd
	 */
	public void absatz(String cmd) {
		EKS.absatz(cmd);
	}

	// TODO Methoden startTransaction, commitTransaction, abortTransaction,
	public void action(String cmd) {
		this.aktion(cmd);
	}

	public boolean add(String cmd) {
		return this.dazu(cmd);
	}

	public void addRow() {
		this.plusZeile();
	}

	@Deprecated
	public void addZeile() {
		this.plusZeile();
	}

	/**
	 * .aktion cmd
	 * 
	 * @param cmd
	 */
	public void aktion(String cmd) {
		EKS.aktion(cmd);
	}

	/**
	 * Methode wird immer ausgeführt nach Ende des Scriptes
	 * 
	 */
	public Object always() {
		return null;
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
			throw new GroovyFOException("ungültige Parameteranzahl / invalid parameter number");
		return art(split[0], split[1]);
	}

	/**
	 * Definition genau einer Nutzervariablen ( U Puffer)
	 * 
	 * .art GD xfoo
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT","P2:1" usw.
	 * @param def
	 *            Bezeichnung der Variablen bsp.: "xvon"
	 * @return liefert die Variablenbezeichnung zurück mit Puffer bsp.: U|xvon
	 * 
	 * @throws GroovyFOException
	 */
	public String art(String type, String def) throws GroovyFOException {
		if (!u.containsKey(def)) {
			FO.art(type + " " + def);
			this.variableTypes.put("U|" + def, this.getClassOfType(type));
		} else {
			// TODO prüfen ob die Typen übereinstimmen
			// if (!this.variableTypes.get("U|" + def).equals(type)) {
			// throw new GroovyFOException("different types same name");
			// }
		}
		return "U|" + def;
	}

	/**
	 * Definition von n Nutzervariablen eines Types (U Puffer)
	 * 
	 * .art GD xfoo xba
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
		this.zuweisen(cmd);
	}

	public void assign(String key, boolean value) {
		this.zuweisen(key + "=" + (value ? "G|TRUE" : "G|false"));
	}

	public void assign(String key, double value) {
		zuweisen(key + "=" + df.format(value));
	}

	public void assign(String key, int value) {
		this.zuweisen(key + "=" + Integer.toString(value));
	}

	public void assign(String key, String value) {
		this.zuweisen(key + "=" + value);
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

	public void box(String cmd) {
		FO.box(cmd);
	}

	public void box(String title, String content) {
		FO.box(title, content.split("\n"));
	}

	public void bringe() {
		this.bringe("");
	}

	public void bringe(String cmd) {
		EKS.bringe(cmd);
	}

	public void browser(String cmd) {
		FO.browser(cmd);
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

	public void datei(String cmd) {
		EKS.datei(cmd);
	}

	public boolean dazu(String cmd) {
		if(this.dselection == null || !this.dselection.equals(cmd)){
			this.resetMap("d");
			this.dselection = cmd;
		}
		return EKS.dazu(cmd);
	}

	public void delete(String cmd) {
		this.loesche(cmd);
	}

	public void down(String cmd) {
		this.unten(cmd);
	}

	public void drucke(String cmd) {
		EKS.drucke(cmd);
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
		this.fehler(cmd);
	}

	public Object expr(String expr) throws GroovyFOException, ParseException {
		return this.getComputedValue(expr);
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
		this.datei(cmd);
	}

	public void flattersatz() {
		FO.flattersatz("");
	}

	public Object fo(String var, AbasDate value) throws FOPException, GroovyFOException {
		FO.formel(var + "=\"" + value.toString() + "\"");
		return value;
		// return this.getValue(var, value);
	}

	public Object fo(String var, AbasPointer value) throws FOPException, GroovyFOException {
		FO.formel(var + "=\"" + value.toString() + "\"");
		// return this.getValue(var);
		return value;
	}

	public Object fo(String var, boolean value) throws FOPException, GroovyFOException {
		FO.formel(var + "=" + (value ? "G|TRUE" : "G|FALSE"));
		// return this.getValue(var);
		return value;
	}

	public Object fo(String var, double value) throws FOPException, GroovyFOException {
		FO.formel(var + "=" + df.format(value));
		// return this.getValue(var);
		return value;
	}

	public Object fo(String var, int value) throws FOPException, GroovyFOException {
		FO.formel(var + "=" + Integer.toString(value));
		// return this.getValue(var);
		return value;
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
	public Object fo(String var, String value) throws FOPException, GroovyFOException {
		FO.formel(var + "=\"" + value.replaceAll("\"", "\"+'DBLQUOTE'+\"") + "\"");
		// return this.getValue(var);
		return value;
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
	 * @throws ParseException
	 */
	public Object formel(String var, String value) throws FOPException, GroovyFOException, ParseException {
		FO.formel(var + "=" + value);
		return this.getComputedValue(var);
	}

	public Object formula(String var, String value) throws FOPException, GroovyFOException, ParseException {
		return this.formel(var, value);
	}

	public void gedruckt(String cmd) {
		EKS.gedruckt(cmd);
	}

	protected PossibleDatatypes getClassOfType(String abasType) {
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
		if (pointerPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.ABASPOINTER;
		}
		// Strings
		return PossibleDatatypes.STRING;
	}

	/**
	 * lässt abas den Werberechnen
	 * 
	 * @param expr
	 *            - U|von, U|von-U|bis, usw...
	 * @return
	 * @throws GroovyFOException
	 * @throws ParseException
	 */
	public Object getComputedValue(String expr) throws GroovyFOException, ParseException {
		String result = FO.getValue("F", "expr(" + expr + ")");
		PossibleDatatypes type = this.getClassOfType(FO.getValue("F", "typeof(F|expr(" + expr + "))"));
		return this.getValueByType(type, expr, result);
	}

	public boolean getMehr() {
		return this.mehr();
	}

	public boolean getMore() {
		return this.mehr();
	}

	/**
	 * liefert den abas Typ der Variable
	 * 
	 * @param variable
	 *            mit vorangestellten Puffer buchstaben bsp.: H|id
	 * @return
	 */
	protected PossibleDatatypes getType(String variable) {
		variable = variable.toLowerCase();
		if (this.variableTypes.containsKey(variable)) {
			return this.variableTypes.get(variable);
		} else {
			// FIXME vorher prüfen ob die Variable existiert!
			PossibleDatatypes type = this.getClassOfType(FO.getValue("F", "typeof(" + variable + ")"));
			this.variableTypes.put(variable, type);
			return type;
		}
	}

	public Object getValue(String varname) throws GroovyFOException, ParseException {
		return this.getComputedValue(varname);
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
	 * @throws ParseException
	 */
	public Object getValue(String varname, String value) throws GroovyFOException, ParseException {
		// Mapping der einzelnen abas Variablenarten auf Standard Typen
		PossibleDatatypes abasType = this.getType(varname);
		return this.getValueByType(abasType, varname, value);
	}

	public Object getValueByType(PossibleDatatypes abasType, String expr, String value)
			throws GroovyFOException, ParseException {
		value = value.trim();
		switch (abasType) {
		case INTEGER:
			if (value == null || value.isEmpty())
				return 0;
			return Integer.parseInt(value);
		case DOUBLE:
		case DOUBLEDT:
		case DOUBLET:
		case DOUBLED:
			if (value == null || value.isEmpty())
				return 0.0d;
			if (abasType == PossibleDatatypes.DOUBLEDT || abasType == PossibleDatatypes.DOUBLET
					|| abasType == PossibleDatatypes.DOUBLED) {
				// FIXME was mit anderen Trennzeichen
				if (abasType == PossibleDatatypes.DOUBLEDT || abasType == PossibleDatatypes.DOUBLET) {
					value = value.replaceAll("\\.", "");
				}
				if (abasType == PossibleDatatypes.DOUBLEDT || abasType == PossibleDatatypes.DOUBLED) {
					value = value.replace(',', '.');
				}
			}
			return Double.parseDouble(value);
		case BOOLEAN:
			return isTrue(value);

		case ABASPOINTER:
			return new AbasPointer(expr, this);
		case ABASDATE:
			boolean isVar = varPattern.matcher(expr).matches();
			if (isVar) {
				if (this.variables.containsKey(expr)) {
					return this.variables.get(expr);
				} else {
					AbasDate date = new AbasDate(expr, value, this);
					this.variables.put(expr, date);
					return date;
				}
			}
			return new AbasDate(expr, value, this);
		default:
			return value;
		}
	}

	public void help(String cmd) {
		this.hilfe(cmd);
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
		if (this.hselection == null || !this.hselection.equals(cmd)){
			this.resetMap("h");
			this.hselection = cmd;
		}
		return FO.hole(cmd);
	}

	/**
	 * sollte der Selektionstring Identisch mit einer vorher gehenden Abfragen
	 * sein so wird nur der nächste Datensatz geholt
	 * 
	 * Ist beim Selektionsstring weder % noch $ angegeben wird die
	 * Maskenloseselektion gewählt $,,
	 * 
	 * @param db
	 *            - Datenbank von der Selektiert werden soll
	 * @param selection
	 *            - abas Selektionstring,
	 * @return liefert wahr falls die Selektion einen Datensatz holen konnte
	 */
	public boolean hole(String db, Object selection) {
		if (this.hselection != null && this.hselection.equals(selection)) {
			return FO.hole(db);
		} else {
			this.hselection = selection.toString();
			this.resetMap("h");
			return FO.hole(db + " "+handleSelectionString(hselection));
		}
	}

	private String handleSelectionString(String selection){
		// identnummer
		/*
		if(selection.matches("\\d+")){
			return "\""+selection+"\"";
		}
		// Datenbank Id (123,123,123)
		if(selection.matches("\\((\\d+){1}(,\\d+){2,3}\\)")){
			
		}
		*/
		// mit hoher Wahrscheinlichkeit selektion
		if(selection.contains("=") || selection.contains(";")){
			// maskenlose
			if(!selection.startsWith("$") && selection.startsWith("%")){
				selection = "$"+selection;
			}
		}
		return "\""+selection+"\"";
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

	public void in(String fopName) {
		this.eingabe(fopName);
	}

	public void input(String fopName) {
		this.eingabe(fopName);
	}

	public boolean isTrue(String value) {
		value = value.toLowerCase();
		return value != null && !value.isEmpty() && value.matches("ja") || value.matches("yes")
				|| value.matches("true");
	}

	public void justified() {
		this.blocksatz("");
	}

	public void justified(String cmd) {
		this.blocksatz(cmd);
	}

	public void kom(String kommando) {
		this.kommando(kommando);
	}

	public void kommando(String kommando) {
		EKS.kommando(kommando);
	}

	public void kommandoWarten(String kommando) {
		this.kom("-WARTEN " + kommando);
	}

	public void komWarten(String kommando) {
		this.kom("-WARTEN " + kommando);
	}

	public void kopieren(String cmd) {
		EKS.kopieren(cmd);
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
		if(this.lselection[puffer] == null || !this.lselection[puffer].equals(cmd)){
			this.resetMap(Integer.toString(puffer));
			this.lselection[puffer] = cmd;
		}
		return FO.lade(puffer + " " + cmd);
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
	public boolean lade(int puffer, String db, Object selection) {
		if (this.lselection[puffer] != null && this.lselection[puffer].equals(selection)) {
			return FO.lade(puffer + " " + db);
		} else {
			this.resetMap(Integer.toString(puffer));
			this.lselection[puffer] = selection.toString();
			return FO.lade(puffer + " " + db + " "+this.handleSelectionString(this.lselection[puffer]));
		}
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

	public boolean lade(String cmd) {
		// .lade X ....
		this.resetMap(cmd.split(" ")[0]);
		return FO.lade(cmd);
	}

	public void laenge(String cmd) {
		EKS.laenge(cmd);
	}

	public void left(String cmd) {
		this.links(cmd);
	}

	public void length(String cmd) {
		this.laenge(cmd);
	}

	public void lesen(String cmd) {
		EKS.lesen(cmd);
	}

	public void line(String cmd) {
		this.zeile(cmd);
	}

	public void links(String cmd) {
		EKS.links(cmd);
	}

	public boolean load(int puffer, String cmd) {
		return this.lade(puffer + " " + cmd);
	}

	public boolean load(int puffer, String db, SelectionBuilder builder) {
		return this.lade(puffer, db, builder.toString());
	}

	public boolean load(int puffer, String db, String selection) {
		return this.lade(puffer, db, selection);
	}

	public boolean load(String cmd) {
		return this.lade(cmd);
	}

	public void loesche(String cmd) {
		EKS.loesche(cmd);
	}

	public void mache(String cmd) {
		EKS.mache(cmd);
	}

	public void make(String cmd) {
		this.mache(cmd);
	}

	public boolean mehr() {
		String mehr = "ja";
		// ist mehr definiert?
		// auf ruf ist F|defined(G|mehr)
		if ((Boolean) F.get("defined").call("G|mehr")) {
			mehr = FO.Gvar("mehr");
		} else {
			mehr = FO.Gvar("success");
		}
		return isTrue(mehr);
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

	public void merke(String cmd) {
		EKS.merke(cmd);
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

	/**
	 * Methode wird beim Auftretten einer unbehandelten Ausnahme ausgeführt
	 */
	public Object onerror(Object ex) {
		if (ex instanceof Exception) {
			Exception e = (Exception) ex;
			e.printStackTrace();
			println("Unbehandelte Ausnahme: \n" + getStacktrace(e));
		}
		return null;
	}

	private String getStacktrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public void output(String cmd) {
		this.ausgabe(cmd);
	}

	public void page(String cmd) {
		this.seite(cmd);
	}

	public void para(String cmd) {
		this.absatz(cmd);
	}

	public void plusZeile() {
		FO.mache("maske zeile +O");
	}

	public void print(String cmd) {
		this.drucke(cmd);
	}

	public void printed(String cmd) {
		this.gedruckt(cmd);
	}

	public void println(boolean cmd) {
		this.println(Boolean.toString(cmd));
	}

	public void println(double cmd) {
		this.println(Double.toString(cmd));
	}

	public void println(int cmd) {
		this.println(Integer.toString(cmd));
	}

	public void println(Object cmd) {
		FO.println(cmd.toString());
	}

	public void println(String cmd) {
		cmd = cmd.replaceAll("\"", "'DBLQUOTE'");
		// String kürzen
		if (cmd.length() > 2999)
			FO.println(cmd.substring(0, 2999));
		else
			FO.println(cmd);
	}

	public void protection(String cmd) {
		this.schutz(cmd);
	}

	public void read(String cmd) {
		this.lesen(cmd);
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
		this.merke(cmd);
	}

	/**
	 * wenn sich der Bezug eines Laden/Holen/dazu buffers ändert müssen alle
	 * Felder aus der Map zum merken des Datentypes entfernt werden
	 * 
	 * @param buffer
	 *            - Buchstabe des Puffers
	 */
	private void resetMap(String buffer) {
		if (buffer != null && !buffer.isEmpty()) {
			buffer = buffer.toLowerCase();
			// gemerkte Variablentypen für den Buffer entfernen
			for (Entry<String, PossibleDatatypes> entry : this.variableTypes.entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(buffer)) {
					this.variableTypes.remove(entry.getKey());
				}
			}
			// gemerkte Variablen für den Buffer entfernen
			for (Entry<String, AbasDate> entry : this.variables.entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(buffer)) {
					this.variables.remove(entry.getKey());
				}
			}
		}
	}

	public void rewrite() {
		this.bringe("");
	}

	public void rewrite(String cmd) {
		this.bringe(cmd);
	}

	public void right(String cmd) {
		this.rechts(cmd);
	}

	@Override
	public Object run() {
		Object o = null;
		try {
			o = runCode();
		} catch (Exception e) {
			this.onerror(e);
		} finally {
			this.always();
		}
		return o;
	}

	public abstract Object runCode();

	public void schutz(String cmd) {
		EKS.schutz(cmd);
	}

	public void seite(String cmd) {
		EKS.seite(cmd);
	}

	public boolean select(String cmd) {
		return this.hole(cmd);
	}

	public boolean select(String db, SelectionBuilder builder) {
		return this.hole(db, builder);
	}

	public boolean select(String db, String selection) {
		return hole(db, selection);
	}

	public void seperator(String cmd) {
		this.trenner(cmd);
	}

	public void set(String cmd) {
		this.setze(cmd);
	}

	public void setze(String cmd) {
		EKS.setze(cmd);
	}

	public void sort(String cmd) {
		this.sortiere(cmd);
	}

	public void sortiere(String cmd) {
		EKS.sortiere(cmd);
	}

	public boolean success() {
		return this.mehr();
	}

	public void tabellensatz(String cmd) {
		EKS.tabellensatz(cmd);
	}

	public void tablerecord(String cmd) {
		this.tabellensatz(cmd);
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
		this.oben(cmd);
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
		this.zeige(cmd);
	}

	public void window(String cmd) {
		this.fenster(cmd);
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

	public void zuweisen(String key, boolean value) {
		EKS.zuweisen(key = (value ? "G|TRUE" : "G|false"));
	}

	public void zuweisen(String key, double value) {
		EKS.zuweisen(key + "=" + Double.toString(value));
	}

	public void zuweisen(String key, int value) {
		EKS.zuweisen(key + "=" + Integer.toString(value));
	}

	public void zuweisen(String key, String value) {
		EKS.zuweisen(key + "=" + value);
	}

	public Object propertyMissing(String name) {
		return name;
	}

	protected void finalize() throws Throwable {
		try {
			hselection = null;
			lselection = null;
			integerPattern = null;
			doublePattern = null;
			boolPattern = null;
			pointerPattern = null;
			varPattern = null;
			d = null;
			D = null;
			a = null;
			A = null;
			e = null;
			E = null;

			f = null;
			F = null;

			g = null;
			G = null;
			h = null;
			H = null;
			l = null;
			L = null;
			l0 = null;
			L0 = null;
			l1 = null;
			L1 = null;
			l2 = null;
			L2 = null;
			l3 = null;
			L3 = null;
			l4 = null;
			L4 = null;
			l5 = null;
			L5 = null;
			l6 = null;
			L6 = null;
			l7 = null;
			L7 = null;
			l8 = null;
			L8 = null;
			l9 = null;
			L9 = null;
			m = null;
			M = null;
			p = null;
			P = null;
			s = null;
			S = null;
			u = null;
			U = null;
			variableTypes = null;
			variables = null;
			arg0 = null;
			args = null;
			dbContext = null;
		} finally {
			super.finalize();
		}
	}

}
