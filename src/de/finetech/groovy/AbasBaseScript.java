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

	public String h(String varName){
		return EKS.Hvar(varName);
	}
	
	public String m(String varName){
		return EKS.Mvar(varName);
	}

	public String d(String varName){
		return EKS.Dvar(varName);
	}
	
	public String g(String varName){
		return EKS.Gvar(varName);
	}
	
	public boolean hole(String cmd){
		return EKS.hole(cmd);
	}
	
	public boolean lade(String cmd){
		return EKS.lade(cmd);
	}
	
	public boolean dazu(String cmd){
		return EKS.dazu(cmd);
	}
	
	public void mache(String cmd){
		 FO.mache(cmd);
	}
	
	public void addZeile(){
		 FO.mache("maske zeile +O");
	}
	
	public void removeZeile(){
		 FO.mache("maske zeile -O");
	}
	
	public void bringe(String cmd){
		EKS.bringe(cmd);
	}
	
	public void ein(String fopName){
		EKS.eingabe(fopName);
	}
	
	public boolean mehr(){
		String mehr = EKS.Gvar("mehr");
		//FIXME Sprachunterstützung
		return mehr != null && (mehr.toLowerCase().equals("ja") || mehr.toCharArray().equals("true"));
	}
	
	public void println(String cmd){
		EKS.println(cmd);
	}

}
