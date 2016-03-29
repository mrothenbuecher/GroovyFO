package de.finetech.groovy;

public class SelectionBuilder {

	private boolean isDe = true;

	private String selectionType = "";
	private String variables = "";
	private String selectionOption = "";

	
	public static SelectionBuilder build(){
		return new SelectionBuilder();
	}
	
	
	/**
	 * Standard ist die dialoglose Selektion
	 */
	public SelectionBuilder() {
		this.selectionType = "$,";
	}

	/**
	 * 
	 * @param lang
	 *            - sprache "de" ansonsten wird englisch angenommen
	 */
	public SelectionBuilder(String lang) {
		this.selectionType = "$,";
		isDe = lang.toLowerCase().equals("de");
	}

	/**
	 * 
	 * @param hidden
	 *            - true fÃ¼r dialoglose Selektion, false fÃ¼r Dialog
	 */
	public SelectionBuilder(boolean hidden) {
		if (hidden) {
			this.selectionType = "$,";
		} else {
			this.selectionType = "%,,";
		}
	}

	/**
	 * 
	 * @param hidden
	 *            - true fÃ¼r die Dialoglose Selektion, false fÃ¼r Dialog
	 * @param selection
	 *            - Nummer der Selektion
	 */
	public SelectionBuilder(boolean hidden, int selection) {
		if (hidden) {
			this.selectionType = "$" + selection + ",";
		} else {
			this.selectionType = "%" + selection + ",,";
		}
	}

	/**
	 * 
	 * @param hidden
	 *            - true fÃ¼r die Dialoglose Selektion, false fÃ¼r Dialog
	 * @param selection
	 *            - Suchwort oder Nummer der Selektion
	 */
	public SelectionBuilder(boolean hidden, String selection) {
		if (hidden) {
			this.selectionType = "$" + selection + ",";
		} else {
			this.selectionType = "%" + selection + ",,";
		}
	}

	public SelectionBuilder setLanguage(String lang) {
		isDe = lang.toLowerCase().equals("de");
		return this;
	}

	public SelectionBuilder normal(String var, String from, String to) {
		if ((from == null || from.isEmpty()) && (to != null && !to.isEmpty())) {
			this.variables += var + "=!" + to + ";";
		} else if ((to == null || to.isEmpty())
				&& (from != null && !from.isEmpty())) {
			this.variables += var + "=" + from + "!;";
		} else if ((to != null && !to.isEmpty())
				&& (from != null && !from.isEmpty())) {
			this.variables += var + "=" + from + "!" + to + ";";
		} else {
			// beide null, keine Aktion
		}
		return this;
	}

	public SelectionBuilder normalEx(String var, String from, String to) {
		if ((from == null || from.isEmpty()) && (to != null && !to.isEmpty())) {
			this.variables += var + "=!!" + to + ";";
		} else if ((to == null || to.isEmpty())
				&& (from != null && !from.isEmpty())) {
			this.variables += var + "=" + from + "!!;";
		} else if ((to != null && !to.isEmpty())
				&& (from != null && !from.isEmpty())) {
			this.variables += var + "=" + from + "!!" + to + ";";
		} else {
			// beide null, keine Aktion
		}
		return this;
	}

	public SelectionBuilder normalExG(String var, String from, String to) {
		if ((from == null || from.isEmpty()) && (to != null && !to.isEmpty())) {
			this.variables += var + "~=!!" + to + ";";
		} else if ((to == null || to.isEmpty())
				&& (from != null && !from.isEmpty())) {
			this.variables += var + "~=" + from + "!!;";
		} else if ((to != null && !to.isEmpty())
				&& (from != null && !from.isEmpty())) {
			this.variables += var + "~=" + from + "!!" + to + ";";
		} else {
			// beide null, keine Aktion
		}
		return this;
	}

	public SelectionBuilder startWith(String var, String value) {
		this.variables += var + "=" + value + ";";
		return this;
	}

	public SelectionBuilder startWithG(String var, String value) {
		this.variables += var + "~=" + value + ";";
		return this;
	}

	public SelectionBuilder empty(String var) {
		this.variables += var + "=`;";
		return this;
	}

	public SelectionBuilder filled(String var) {
		this.variables += var + "<>`;";
		return this;
	}

	public SelectionBuilder similar(String var, String value) {
		this.variables += var + "==" + value + ";";
		return this;
	}

	public SelectionBuilder unsimilar(String var, String value) {
		this.variables += var + "<>" + value + ";";
		return this;
	}

	public SelectionBuilder similarG(String var, String value) {
		this.variables += var + "~" + value + ";";
		return this;
	}

	public SelectionBuilder unsimilarG(String var, String value) {
		this.variables += var + "~<>" + value + ";";
		return this;
	}

	public SelectionBuilder contains(String var, String value) {
		this.variables += var + "/" + value + ";";
		return this;
	}

	public SelectionBuilder containsG(String var, String value) {
		this.variables += var + "~/" + value + ";";
		return this;
	}

	public SelectionBuilder containsW(String var, String value) {
		this.variables += var + "//" + value + ";";
		return this;
	}

	public SelectionBuilder containsGW(String var, String value) {
		this.variables += var + "~//" + value + ";";
		return this;
	}

	public SelectionBuilder matchcode(String var, String value) {
		this.variables += var + "=`" + value + ";";
		return this;
	}

	public SelectionBuilder matchcodeN(String var, String value) {
		this.variables += var + "<>`" + value + ";";
		return this;
	}

	public SelectionBuilder matchcodeG(String var, String value) {
		this.variables += var + "~=`" + value + ";";
		return this;
	}

	public SelectionBuilder matchcodeGN(String var, String value) {
		this.variables += var + "~<>`" + value + ";";
		return this;
	}

	public SelectionBuilder expression(String var, String value) {
		this.variables += var + "/==" + value + ";";
		return this;
	}

	public SelectionBuilder expressionN(String var, String value) {
		this.variables += var + "/<>" + value + ";";
		return this;
	}

	public SelectionBuilder expressionG(String var, String value) {
		this.variables += var + "~/==" + value + ";";
		return this;
	}

	public SelectionBuilder expressionGN(String var, String value) {
		this.variables += var + "~/<>" + value + ";";
		return this;
	}

	public SelectionBuilder group(int value) {
		if (isDe)
			this.selectionOption += "@gruppe=" + value + ";";
		else
			this.selectionOption += "@group=" + value + ";";
		return this;
	}

	public SelectionBuilder sort(String value) {
		this.selectionOption += "@sort=" + value + ";";
		return this;
	}

	public SelectionBuilder order(String value) {
		if (isDe)
			this.selectionOption += "@ordnung=" + value + ";";
		else
			this.selectionOption += "@order=" + value + ";";
		return this;
	}

	public SelectionBuilder optimalkey(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@optschl="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@optimalkey="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder link(boolean AND) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@verknuepfung="
					+ (AND == true ? "und" : "oder") + ";";
		else
			this.selectionOption += "@link=" + (AND == true ? "and" : "or")
					+ ";";
		return this;
	}

	public SelectionBuilder range(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@bereich="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@range=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectionBuilder direction(boolean forward) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@richtung="
					+ (forward == true ? "vorwÃ¤rts" : "rÃ¼ckwÃ¤rts") + ";";
		else
			this.selectionOption += "@direction="
					+ (forward == true ? "forward" : "backward") + ";";
		return this;
	}

	public SelectionBuilder objectselection(String value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@objektwahl=" + value + ";";
		else
			this.selectionOption += "@objectselection=" + value + ";";
		return this;
	}

	public SelectionBuilder read(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@lesen=" + (value == true ? "yes" : "no")
					+ ";";
		else
			this.selectionOption += "@read=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectionBuilder autostart(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@autostart="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@autostart="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder database(int value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@datenbank=" + value + ";";
		else
			this.selectionOption += "@database=" + value + ";";
		return this;
	}

	public SelectionBuilder rows(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@zeilen="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@rows=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectionBuilder fprotection(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@fschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@fprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder gprotection(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@gschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@gprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder bprotection(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@bschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@bprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder note(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@merken="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@note=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectionBuilder filingmode(String value) {
		if (isDe)
			this.selectionOption += "@ablageart=" + value + ";";
		else
			this.selectionOption += "@filingmode=" + value + ";";
		return this;
	}

	public SelectionBuilder enterscreen(String value) {
		if (isDe)
			this.selectionOption += "@maskein=" + value + ";";
		else
			this.selectionOption += "@enterscreen=" + value + ";";
		return this;
	}

	public SelectionBuilder screenvalidation(String value) {
		if (isDe)
			this.selectionOption += "@maskpruef=" + value + ";";
		else
			this.selectionOption += "@screenvalidation=" + value + ";";
		return this;
	}

	public SelectionBuilder fieldfilled(String value) {
		if (isDe)
			this.selectionOption += "@feldfuell=" + value + ";";
		else
			this.selectionOption += "@fieldfilled=" + value + ";";
		return this;
	}

	public SelectionBuilder hitvalidation(String value) {
		if (isDe)
			this.selectionOption += "@trefferpruef=" + value + ";";
		else
			this.selectionOption += "@hitvalidation=" + value + ";";
		return this;
	}

	public SelectionBuilder title(String value) {
		if (isDe)
			this.selectionOption += "@titel=" + value + ";";
		else
			this.selectionOption += "@title=" + value + ";";
		return this;
	}

	public SelectionBuilder dynprotection(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@dynschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@dynprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder stdsel(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@stdsel="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@stdsel=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectionBuilder templsel(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@rohlsel="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@templsel="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder initialvalues(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@initialwerte="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@initialvalues="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder lastname(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@letztername="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@lastname="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder noswd(String value) {
		if (isDe)
			this.selectionOption += "@numsuch=" + value + ";";
		else
			this.selectionOption += "@noswd=" + value + ";";
		return this;
	}

	public SelectionBuilder opprotection(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@opschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@opprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectionBuilder maxhit(int value) {
		if (isDe)
			this.selectionOption += "@maxtreffer=" + value + ";";
		else
			this.selectionOption += "@maxhit=" + value + ";";
		return this;
	}

	public SelectionBuilder maxordhit(int value) {
		if (isDe)
			this.selectionOption += "@maxordtreffer=" + value + ";";
		else
			this.selectionOption += "@maxordhit=" + value + ";";
		return this;
	}

	public SelectionBuilder language(String value) {
		this.selectionOption += "@language=" + value + ";";
		return this;
	}

	public SelectionBuilder englvar(boolean value) {
		// FIXME SprachunabhÃ¤nigkeit
		if (isDe)
			this.selectionOption += "@englvar="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@englvar="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public String getSelectionString() {
		return toString();
	}

	@Override
	public String toString() {
		return this.selectionType + variables + selectionOption;
	}

}
