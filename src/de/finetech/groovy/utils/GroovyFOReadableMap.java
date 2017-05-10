package de.finetech.groovy.utils;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.abas.jfop.base.buffer.BaseReadableBuffer;
import de.finetech.groovy.AbasBaseScript;

public class GroovyFOReadableMap<T extends BaseReadableBuffer> extends
		GroovyObjectSupport implements Map<String, Object>, Cloneable,
		Serializable, GroovyObject {

	private static final long serialVersionUID = 4146145334512673667L;
	// private String buffer = null;
	protected AbasBaseScript script;
	protected T buffer;

	public void clear() {
	}

	public GroovyFOReadableMap(T buffer, AbasBaseScript script) {
		this.script = script;
		this.buffer = buffer;
	}

	public boolean containsKey(Object key) {
		return buffer.isVarDefined(key.toString());
	}

	public boolean containsValue(java.lang.Object value) {
		return false;
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return null;
	}

	// h.
	public Object get(Object key){
		try {
			// buffer.
			String skey = key.toString();
			return script.getValue(buffer.getQualifiedFieldName(skey),
					buffer.getStringValue(skey));
		} catch (FOPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GroovyFOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getType(Object key) {
		return buffer.getFieldType(key.toString());
	}

	public boolean isEmpty() {
		return false;
	}

	// TODO prüfen ob es eine möglichkeit gibt alle Variablen eines Puffers zu
	// ermitteln
	public Set<String> keySet() {
		return null;
	}

	// TODO siehe oben
	public int size() {
		return 0;
	}

	public Object put(String key, Object value) {
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

	public Object call(Object key) {
		return this.get(key.toString());
	}

	/*
	@Override
	public Object getProperty(String name) {
		Object o = getMetaClass().getProperty(this, name);
		if (o != null) {
			//FO.println("o not null 2 " + name);
			//FO.println(o);
			return o;
		} else {
			return name;
		}
	}
	 */
	// h|
	public Object or(String key) {
		return this.get(key);
	}

}
