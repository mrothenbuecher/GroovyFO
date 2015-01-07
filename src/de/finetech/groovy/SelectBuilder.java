package de.finetech.groovy;

public class SelectBuilder {

	private boolean isDe = true;

	private String selectionType = "";
	private String variables = "";
	private String selectionOption = "";

	
	public static SelectBuilder SelectBuilder(){
		return new SelectBuilder();
	}
	
	
	/**
	 * Standard ist die dialoglose Selektion
	 */
	public SelectBuilder() {
		this.selectionType = "$,";
	}

	/**
	 * 
	 * @param lang
	 *            - sprache "de" ansonsten wird englisch angenommen
	 */
	public SelectBuilder(String lang) {
		this.selectionType = "$,";
		isDe = lang.toLowerCase().equals("de");
	}

	/**
	 * 
	 * @param hidden
	 *            - true für dialoglose Selektion, false für Dialog
	 */
	public SelectBuilder(boolean hidden) {
		if (hidden) {
			this.selectionType = "$,";
		} else {
			this.selectionType = "%,,";
		}
	}

	/**
	 * 
	 * @param hidden
	 *            - true für die Dialoglose Selektion, false für Dialog
	 * @param selection
	 *            - Nummer der Selektion
	 */
	public SelectBuilder(boolean hidden, int selection) {
		if (hidden) {
			this.selectionType = "$" + selection + ",";
		} else {
			this.selectionType = "%" + selection + ",,";
		}
	}

	/**
	 * 
	 * @param hidden
	 *            - true für die Dialoglose Selektion, false für Dialog
	 * @param selection
	 *            - Suchwort oder Nummer der Selektion
	 */
	public SelectBuilder(boolean hidden, String selection) {
		if (hidden) {
			this.selectionType = "$" + selection + ",";
		} else {
			this.selectionType = "%" + selection + ",,";
		}
	}

	public SelectBuilder setLanguage(String lang) {
		isDe = lang.toLowerCase().equals("de");
		return this;
	}

	public SelectBuilder normal(String var, String from, String to) {
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

	public SelectBuilder normalEx(String var, String from, String to) {
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

	public SelectBuilder normalExG(String var, String from, String to) {
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

	public SelectBuilder startWith(String var, String value) {
		this.variables += var + "=" + value + ";";
		return this;
	}

	public SelectBuilder startWithG(String var, String value) {
		this.variables += var + "~=" + value + ";";
		return this;
	}

	public SelectBuilder empty(String var) {
		this.variables += var + "=`;";
		return this;
	}

	public SelectBuilder filled(String var) {
		this.variables += var + "<>`;";
		return this;
	}

	public SelectBuilder similar(String var, String value) {
		this.variables += var + "==" + value + ";";
		return this;
	}

	public SelectBuilder unsimilar(String var, String value) {
		this.variables += var + "<>" + value + ";";
		return this;
	}

	public SelectBuilder similarG(String var, String value) {
		this.variables += var + "~" + value + ";";
		return this;
	}

	public SelectBuilder unsimilarG(String var, String value) {
		this.variables += var + "~<>" + value + ";";
		return this;
	}

	public SelectBuilder contains(String var, String value) {
		this.variables += var + "/" + value + ";";
		return this;
	}

	public SelectBuilder containsG(String var, String value) {
		this.variables += var + "~/" + value + ";";
		return this;
	}

	public SelectBuilder containsW(String var, String value) {
		this.variables += var + "//" + value + ";";
		return this;
	}

	public SelectBuilder containsGW(String var, String value) {
		this.variables += var + "~//" + value + ";";
		return this;
	}

	public SelectBuilder matchcode(String var, String value) {
		this.variables += var + "=`" + value + ";";
		return this;
	}

	public SelectBuilder matchcodeN(String var, String value) {
		this.variables += var + "<>`" + value + ";";
		return this;
	}

	public SelectBuilder matchcodeG(String var, String value) {
		this.variables += var + "~=`" + value + ";";
		return this;
	}

	public SelectBuilder matchcodeGN(String var, String value) {
		this.variables += var + "~<>`" + value + ";";
		return this;
	}

	public SelectBuilder expression(String var, String value) {
		this.variables += var + "/==" + value + ";";
		return this;
	}

	public SelectBuilder expressionN(String var, String value) {
		this.variables += var + "/<>" + value + ";";
		return this;
	}

	public SelectBuilder expressionG(String var, String value) {
		this.variables += var + "~/==" + value + ";";
		return this;
	}

	public SelectBuilder expressionGN(String var, String value) {
		this.variables += var + "~/<>" + value + ";";
		return this;
	}

	public SelectBuilder group(int value) {
		if (isDe)
			this.selectionOption += "@gruppe=" + value + ";";
		else
			this.selectionOption += "@group=" + value + ";";
		return this;
	}

	public SelectBuilder sort(String value) {
		this.selectionOption += "@sort=" + value + ";";
		return this;
	}

	public SelectBuilder order(String value) {
		if (isDe)
			this.selectionOption += "@ordnung=" + value + ";";
		else
			this.selectionOption += "@order=" + value + ";";
		return this;
	}

	public SelectBuilder optimalkey(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@optschl="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@optimalkey="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder link(boolean AND) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@verknuepfung="
					+ (AND == true ? "und" : "oder") + ";";
		else
			this.selectionOption += "@link=" + (AND == true ? "and" : "or")
					+ ";";
		return this;
	}

	public SelectBuilder range(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@bereich="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@range=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectBuilder direction(boolean forward) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@richtung="
					+ (forward == true ? "vorwärts" : "rückwärts") + ";";
		else
			this.selectionOption += "@direction="
					+ (forward == true ? "forward" : "backward") + ";";
		return this;
	}

	public SelectBuilder objectselection(String value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@objektwahl=" + value + ";";
		else
			this.selectionOption += "@objectselection=" + value + ";";
		return this;
	}

	public SelectBuilder read(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@lesen=" + (value == true ? "yes" : "no")
					+ ";";
		else
			this.selectionOption += "@read=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectBuilder autostart(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@autostart="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@autostart="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder database(int value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@datenbank=" + value + ";";
		else
			this.selectionOption += "@database=" + value + ";";
		return this;
	}

	public SelectBuilder rows(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@zeilen="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@rows=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectBuilder fprotection(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@fschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@fprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder gprotection(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@gschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@gprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder bprotection(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@bschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@bprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder note(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@merken="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@note=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectBuilder filingmode(String value) {
		if (isDe)
			this.selectionOption += "@ablageart=" + value + ";";
		else
			this.selectionOption += "@filingmode=" + value + ";";
		return this;
	}

	public SelectBuilder enterscreen(String value) {
		if (isDe)
			this.selectionOption += "@maskein=" + value + ";";
		else
			this.selectionOption += "@enterscreen=" + value + ";";
		return this;
	}

	public SelectBuilder screenvalidation(String value) {
		if (isDe)
			this.selectionOption += "@maskpruef=" + value + ";";
		else
			this.selectionOption += "@screenvalidation=" + value + ";";
		return this;
	}

	public SelectBuilder fieldfilled(String value) {
		if (isDe)
			this.selectionOption += "@feldfuell=" + value + ";";
		else
			this.selectionOption += "@fieldfilled=" + value + ";";
		return this;
	}

	public SelectBuilder hitvalidation(String value) {
		if (isDe)
			this.selectionOption += "@trefferpruef=" + value + ";";
		else
			this.selectionOption += "@hitvalidation=" + value + ";";
		return this;
	}

	public SelectBuilder title(String value) {
		if (isDe)
			this.selectionOption += "@titel=" + value + ";";
		else
			this.selectionOption += "@title=" + value + ";";
		return this;
	}

	public SelectBuilder dynprotection(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@dynschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@dynprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder stdsel(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@stdsel="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@stdsel=" + (value == true ? "yes" : "no")
					+ ";";
		return this;
	}

	public SelectBuilder templsel(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@rohlsel="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@templsel="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder initialvalues(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@initialwerte="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@initialvalues="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder lastname(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@letztername="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@lastname="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder noswd(String value) {
		if (isDe)
			this.selectionOption += "@numsuch=" + value + ";";
		else
			this.selectionOption += "@noswd=" + value + ";";
		return this;
	}

	public SelectBuilder opprotection(boolean value) {
		// FIXME Sprachunabhänigkeit
		if (isDe)
			this.selectionOption += "@opschutz="
					+ (value == true ? "ja" : "nein") + ";";
		else
			this.selectionOption += "@opprotection="
					+ (value == true ? "yes" : "no") + ";";
		return this;
	}

	public SelectBuilder maxhit(int value) {
		if (isDe)
			this.selectionOption += "@maxtreffer=" + value + ";";
		else
			this.selectionOption += "@maxhit=" + value + ";";
		return this;
	}

	public SelectBuilder maxordhit(int value) {
		if (isDe)
			this.selectionOption += "@maxordtreffer=" + value + ";";
		else
			this.selectionOption += "@maxordhit=" + value + ";";
		return this;
	}

	public SelectBuilder language(String value) {
		this.selectionOption += "@language=" + value + ";";
		return this;
	}

	public SelectBuilder englvar(boolean value) {
		// FIXME Sprachunabhänigkeit
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
