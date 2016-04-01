package de.finetech.groovy.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.AbasBaseScript;

public class GroovyFOMap implements Map<String,Object>, Cloneable, Serializable {

	private static final long serialVersionUID = 4146145334512673667L;
	private String buffer = null;
	private AbasBaseScript script;
	
	public void clear() {}
	
	public GroovyFOMap(String buffer, AbasBaseScript script){
		this.script = script;
		this.buffer = buffer;
	}
	
	public boolean containsKey(Object key) {
		String value = FO.getValue("F", "defined(" + buffer+"|"+key + ")").toLowerCase();
		return value.equals("true") || value.equals("ja");
	}

	public boolean containsValue(java.lang.Object value) {return false;}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {return null;}

	public Object get(Object key) {
		if(this.containsKey(key))
			return script.getValue(buffer+"|"+key);
		return null;
	}

	public boolean isEmpty() {
		return false;
	}

	// TODO prüfen ob es eine möglichkeit gibt alle Variablen eines Puffers zu ermitteln
	public Set<String> keySet() {
		return null;
	}
	// TODO siehe oben
	public int size() {
		return 0;
	}
	
	public Object put(String key, Object value) {
		if(this.containsKey(key)){
			String val = value.toString();
			// wenn der übergebene Wert mit einem Hochkomma "'" beginnt wird der wert so übergeben das abas ihn interpretiert 
			if(val.startsWith("'")){
				val = val.substring(1);
				script.formula(buffer+"|"+key, val);
			}else{
				script.fo(buffer+"|"+key, val);
			}
		}
		return null;
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO implementieren
	}

	public Object remove(java.lang.Object key) {return null;}

	public Collection<Object> values() {return null;}

	
}
