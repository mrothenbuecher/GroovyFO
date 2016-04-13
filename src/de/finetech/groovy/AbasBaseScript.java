package de.finetech.groovy;

import groovy.lang.Script;
import groovy.transform.CompileStatic;

import java.awt.Color;
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
import de.finetech.groovy.utils.GroovyFOException;
import de.finetech.groovy.utils.GroovyFOReadableMap;
import de.finetech.groovy.utils.GroovyFOWriteableMap;
import de.finetech.groovy.utils.datatypes.AbasDate;
import de.finetech.groovy.utils.datatypes.AbasPointer;
import de.finetech.utils.SelectionBuilder;

/**
 * 
 * @author Michael Kürbis, Finetech GmbH & Co. KG
 * 
 */
@CompileStatic
public abstract class AbasBaseScript extends Script {

	// Temp Variablen um sich die letzten Selektion zu speichern
	private String hselection;
	private String[] lselection = new String[11];

	// private Pattern stringPattern =
	// Pattern.compile("(PS.*)|(ID.*)|(GL.*)|(T.*)|(N.*)|(BT.*)|(BG.*)|(ST.*)|(ST.*)|(SW.*)");
	private Pattern integerPattern = Pattern
			.compile("(I[0-9])|(IP.*)|(IN.*)|(K.*)");
	private Pattern realPattern = Pattern.compile("(R.*)|(M.*)");
	private Pattern boolPattern = Pattern.compile("(B)|(BOOL)");
	private Pattern pointerPattern = Pattern
			.compile("(P.*)|(ID.*)|(VP.*)|(VID.*)|C.*");

	private Pattern varPattern = Pattern.compile("([a-zA-Z]\\|[a-zA-Z0-9]*)");

	// maps für den einfachen zugriff auf die Felder bsp. m.von
	protected GroovyFOWriteableMap d = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getParentScreenBuffer(), this);
	protected GroovyFOWriteableMap D = d;
	protected GroovyFOWriteableMap a = d;
	protected GroovyFOWriteableMap A = d;
	protected GroovyFOReadableMap e = new GroovyFOReadableMap(BufferFactory
			.newInstance().getEnvBuffer(), this);
	protected GroovyFOReadableMap E = e;
	// protected GroovyFOMap f = new
	// GroovyFOMap(BufferFactory.newInstance().get, this);
	// protected GroovyFOMap F = f;
	protected GroovyFOWriteableMap g = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getGlobalTextBuffer(), this);
	protected GroovyFOWriteableMap G = g;
	protected GroovyFOWriteableMap h = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getSelectBuffer(), this);
	protected GroovyFOWriteableMap H = h;
	protected GroovyFOWriteableMap l1 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(1), this);
	protected GroovyFOWriteableMap L1 = l1;
	protected GroovyFOWriteableMap l2 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(2), this);
	protected GroovyFOWriteableMap L2 = l2;
	protected GroovyFOWriteableMap l3 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(3), this);
	protected GroovyFOWriteableMap L3 = l3;
	protected GroovyFOWriteableMap l4 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(4), this);
	protected GroovyFOWriteableMap L4 = l4;
	protected GroovyFOWriteableMap l5 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(5), this);
	protected GroovyFOWriteableMap L5 = l5;
	protected GroovyFOWriteableMap l6 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(6), this);
	protected GroovyFOWriteableMap L6 = l6;
	protected GroovyFOWriteableMap l7 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(7), this);
	protected GroovyFOWriteableMap L7 = l7;
	protected GroovyFOWriteableMap l8 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(8), this);
	protected GroovyFOWriteableMap L8 = l8;
	protected GroovyFOWriteableMap l9 = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getLoadBuffer(9), this);
	protected GroovyFOWriteableMap L9 = l9;
	protected GroovyFOWriteableMap m = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getScreenBuffer(), this);
	protected GroovyFOWriteableMap M = m;
	protected GroovyFOWriteableMap p = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getPrintBuffer(), this);
	protected GroovyFOWriteableMap P = p;
	protected GroovyFOWriteableMap s = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getCharactBarBuffer(), this);
	protected GroovyFOWriteableMap S = s;
	// protected GroovyFOMap t = new
	// GroovyFOMap(BufferFactory.newInstance().getTextBuffer(), this);
	// protected GroovyFOMap T = t;
	protected GroovyFOWriteableMap u = new GroovyFOWriteableMap(BufferFactory
			.newInstance().getUserTextBuffer(), this);
	protected GroovyFOWriteableMap U = u;

	// zwischenspeicher um nicht immer F|typeof aufrufen zumüssen, schlüssel ist
	// der Variablenname mit vorangestelltem Puffer (m|foo), Wert ist der abas
	// Typ
	protected ConcurrentHashMap<String, PossibleDatatypes> variableTypes = new ConcurrentHashMap<String, PossibleDatatypes>();

	// zwischenspeicher um nicht immer neue Objekte erzeugen zu müssen
	protected ConcurrentHashMap<String, AbasDate> variables = new ConcurrentHashMap<String, AbasDate>();

	protected FOPSessionContext arg0;
	protected String[] arg1;

	protected DbContext dbContext;

	protected static DecimalFormat df = new DecimalFormat("0.0000");

	protected static enum PossibleDatatypes {
		INTEGER, DOUBLE, BOOLEAN, ABASDATE, ABASPOINTER, STRING
	};
	
	/**
	 * die interne standard Sprache des groovyFO ist Deutsch
	 */
	public AbasBaseScript() {
		// println ("Session context not defined? "+arg0 == null );
		// DbContext dbContext = arg0.getDbContext();
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
	 *            Bezeichnung der Variablen bsp.: "xvon"
	 * @return liefert die Variablen bezeichnung zurÃ¼ck mit puffer bsp.: U|xvon
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

	protected PossibleDatatypes getClassOfType(String abasType) {
		if (integerPattern.matcher(abasType).matches()) {
			return PossibleDatatypes.INTEGER;
		}
		// Real
		if (realPattern.matcher(abasType).matches()) {
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
	 * Definition von n NutzerVariablen eines Types
	 * 
	 * @param type
	 *            Variablenart "GD", "TEXT" usw.
	 * @param def
	 *            die Variablen bezeichnungen als array
	 * @return liefert die Variablen bezeichnung zurÃ¼ck mit puffer bsp.: U|xvon
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

	public void assign(String key, int value) {
		zuweisen(key + "=" + Integer.toString(value));
	}

	public void assign(String key, double value) {
		zuweisen(key + "=" + df.format(value));
	}

	public void assign(String key, boolean value) {
		zuweisen(key + "=" + (value ? "G|TRUE" : "G|false"));
	}

	public void assign(String key, String value) {
		zuweisen(key + "=" + value);
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

	public Object expr(String expr) throws GroovyFOException {
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
		datei(cmd);
	}

	public void flattersatz() {
		FO.flattersatz("");
	}

	public Object fo(String var, AbasDate value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=\"" + value.toString() + "\"");
		return value;
		//return this.getValue(var, value);
	}

	public Object fo(String var, AbasPointer value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=\"" + value.toString() + "\"");
		//return this.getValue(var);
		return value;
	}

	public Object fo(String var, boolean value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + (value ? "G|TRUE" : "G|FALSE"));
		//return this.getValue(var);
		return value;
	}

	public Object fo(String var, double value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + df.format(value));
		//return this.getValue(var);
		return value;
	}

	public Object fo(String var, int value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + Integer.toString(value));
		//return this.getValue(var);
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
	public Object fo(String var, String value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=\"" + value.replaceAll("\"", "\"+'DBLQUOTE'+\"")
				+ "\"");
		//return this.getValue(var);
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
	 */
	public Object formel(String var, String value) throws FOPException,
			GroovyFOException {
		FO.formel(var + "=" + value);
		return this.getComputedValue(var);
	}

	public Object formula(String var, String value) throws FOPException,
			GroovyFOException {
		return this.formel(var, value);
	}

	public void gedruckt(String cmd) {
		EKS.gedruckt(cmd);
	}

	/**
	 * lässt abas den Werberechnen
	 * 
	 * @param expr
	 *            - U|von, U|von-U|bis, usw...
	 * @return
	 * @throws GroovyFOException
	 */
	public Object getComputedValue(String expr) throws GroovyFOException {
		String result = FO.getValue("F", "expr(" + expr + ")");
		PossibleDatatypes type = this.getClassOfType(FO.getValue("F", "typeof(F|expr("
				+ expr + "))"));
		return this.getValueByType(type, expr, result);
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
			PossibleDatatypes type = this.getClassOfType(FO.getValue("F", "typeof("
					+ variable + ")"));
			this.variableTypes.put(variable, type);
			return type;
		}
	}
	
	public Object getValue(String varname) throws GroovyFOException {
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
	 */
	public Object getValue(String varname, String value)
			throws GroovyFOException {
		// Mapping der einzelnen abas Variablenarten auf Standard Typen
		PossibleDatatypes abasType = this.getType(varname);
		return this.getValueByType(abasType, varname, value);
	}

	public Object getValueByType(PossibleDatatypes abasType, String expr, String value)
			throws GroovyFOException {
		value = value.trim();
		switch(abasType){
			case INTEGER:
				if (value == null || value.isEmpty())
					return 0;
				return Integer.parseInt(value);
			case DOUBLE:
				if (value == null || value.isEmpty())
					return 0.0d;
				return Double.parseDouble(value);
			case BOOLEAN:
				return isTrue(value);
			case ABASPOINTER:
				return new AbasPointer(expr, this);
			case ABASDATE:
				try {
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
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			default:
				return value;
		}
	}

	public void help(String cmd) {
		hilfe(cmd);
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
	 * sein so wird nur der nächste Datensatz geholt
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
			return FO.hole(db + " \"" + selection + "\"");
		}
	}

	public void in(String fopName) {
		eingabe(fopName);
	}

	public void input(String fopName) {
		eingabe(fopName);
	}

	public boolean isTrue(String value) {
		value = value.toLowerCase();
		return value != null && !value.isEmpty() && value.matches("ja")
				|| value.matches("yes") || value.matches("true");
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
	 * 
	 * @param puffer
	 *            - Nummer des lade Puffers für den das Kommando ausgeführt
	 *            werden soll
	 * @param cmd
	 *            - Kommando das ausgeführt werden soll
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
		if (this.lselection[puffer] != null
				&& this.lselection[puffer].equals(selection)) {
			return FO.lade(puffer + " " + db);
		} else {
			this.resetMap(Integer.toString(puffer));
			this.lselection[puffer] = selection.toString();
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

	public void mache(String cmd) {
		EKS.mache(cmd);
	}

	public void make(String cmd) {
		mache(cmd);
	}

	public boolean mehr() {
		String mehr = FO.Gvar("mehr");
		// FIXME SprachunterstÃ¼tzung
		return isTrue(mehr);
	}

	public boolean getMehr() {
		return mehr();
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

	public boolean getMore() {
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

	public void println(Object cmd) {
		FO.println(cmd.toString());
	}

	public void println(String cmd) {
		cmd = cmd.replaceAll("\"", "'DBLQUOTE'");
		if (cmd.length() > 2999)
			FO.println(cmd.substring(0, 2999));
		else
			FO.println(cmd);
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
	 * wenn sich der Bezug eines Laden/Holen/dazu buffers ändert müssen alle
	 * Felder aus der Map zum merken des Datentypes entfernt werden
	 * 
	 * @param buffer
	 */
	private void resetMap(String buffer) {
		if (buffer != null && !buffer.isEmpty()) {
			buffer = buffer.toLowerCase();
			for (Entry<String, PossibleDatatypes> entry : this.variableTypes.entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(buffer)) {
					this.variableTypes.remove(entry.getKey());
				}
			}
			for (Entry<String, AbasDate> entry : this.variables.entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(buffer)) {
					this.variables.remove(entry.getKey());
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

	public void zuweisen(String key, int value) {
		EKS.zuweisen(key + "=" + Integer.toString(value));
	}

	public void zuweisen(String key, double value) {
		EKS.zuweisen(key + "=" + Double.toString(value));
	}

	public void zuweisen(String key, boolean value) {
		EKS.zuweisen(key = (value ? "G|TRUE" : "G|false"));
	}

	public void zuweisen(String key, String value) {
		EKS.zuweisen(key + "=" + value);
	}

}
