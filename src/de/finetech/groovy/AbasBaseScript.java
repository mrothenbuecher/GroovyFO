package de.finetech.groovy;

import de.abas.eks.jfop.remote.EKS;
import de.abas.eks.jfop.remote.FO;
import groovy.lang.Script;

/**
 * 
 * @author Michael Kürbis, Finetech GmbH & Co. KG
 * 
 */
public abstract class AbasBaseScript extends Script {

	public Object h(String varName) {
		return EKS.Hvar(varName);
	}

	public Object m(String varName) {
		return EKS.Mvar(varName);
	}

	public Object d(String varName) {
		return EKS.Dvar(varName);
	}

	public Object g(String varName) {
		return EKS.Gvar(varName);
	}

	public boolean hole(String cmd) {
		return EKS.hole(cmd);
	}

	public boolean hole(String db, SelectBuilder builder) {
		return this.hole(db, builder.toString());
	}

	public boolean hole(String db, String selection) {
		return EKS.hole(db + " " + selection);
	}

	public boolean lade(String cmd) {
		return EKS.lade(cmd);
	}

	public boolean lade(int puffer, String cmd) {
		return EKS.lade(puffer + " " + cmd);
	}

	public boolean lade(int puffer, String db, SelectBuilder builder) {
		return this.lade(puffer, db, builder.toString());
	}

	public boolean lade(int puffer, String db, String selection) {
		return EKS.lade(puffer + " " + db + " " + selection);
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

	public boolean mehr() {
		String mehr = EKS.Gvar("mehr");
		// FIXME Sprachunterstützung
		return mehr != null
				&& (mehr.toLowerCase().equals("ja") || mehr.toCharArray()
						.equals("true"));
	}

	public void println(String cmd) {
		EKS.println(cmd);
	}

}
