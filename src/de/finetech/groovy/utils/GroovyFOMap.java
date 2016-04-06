package de.finetech.groovy.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.datatypes.AbasDate;

public class GroovyFOMap implements Map<String, Object>, Cloneable,
		Serializable {

	private static final long serialVersionUID = 4146145334512673667L;
	private String buffer = null;
	private AbasBaseScript script;

	public void clear() {
	}

	public GroovyFOMap(String buffer, AbasBaseScript script) {
		this.script = script;
		this.buffer = buffer;
	}

	public boolean containsKey(Object key) {
		String value = FO.getValue("F", "defined(" + buffer + "|" + key + ")")
				.toLowerCase();
		return value.equals("true") || value.equals("ja");
	}

	public boolean containsValue(java.lang.Object value) {
		return false;
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return null;
	}

	public Object get(Object key) {
		if (this.containsKey(key))
			try {
				return script.getValue(buffer + "|" + key);
			} catch (FOPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GroovyFOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}

	public boolean isEmpty() {
		return false;
	}

	// TODO pr�fen ob es eine m�glichkeit gibt alle Variablen eines Puffers zu
	// ermitteln
	public Set<String> keySet() {
		return null;
	}

	// TODO siehe oben
	public int size() {
		return 0;
	}

	public Object put(String key, Object value) {
		if (this.containsKey(key)) {
			String val = value.toString();
			// wenn der �bergebene Wert mit einem Hochkomma "'" beginnt wird der
			// wert so �bergeben das abas ihn interpretiert
			try {
				if (val.startsWith("'")) {
					val = val.substring(1);
					script.formula(buffer + "|" + key, val);
				} else {
					if (value instanceof Integer) {
						script.fo(buffer + "|" + key,
								((Integer) value).intValue());
					} else if (value instanceof Double) {
						script.fo(buffer + "|" + key,
								((Double) value).doubleValue());
					} else if (value instanceof GroovyFOVariable) {
						script.fo(buffer + "|" + key, ((GroovyFOVariable<?>) value).getValue().toString());
					} else {
						script.fo(buffer + "|" + key, val);
					}
				}
			} catch (FOPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GroovyFOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO implementieren
	}

	public Object remove(java.lang.Object key) {
		return null;
	}

	public Collection<Object> values() {
		return null;
	}
	
	public Object or(String key){
		return this.get(key);
	}

}
