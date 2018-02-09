package de.finetech.groovy.utils;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.datatypes.FOFunction;

/**
 * 
 * @author Michael Rothenbücher, Finetech GmbH & Co. KG
 *
 */
public class GroovyFOFBufferMap extends GroovyObjectSupport
		implements Map<String, FOFunction>, Cloneable, Serializable, GroovyObject {

	private static final long serialVersionUID = 4338579360691818296L;

	protected AbasBaseScript script;

	public void clear() {
	}

	public GroovyFOFBufferMap(AbasBaseScript script) {
		this.script = script;
	}

	public boolean containsKey(Object key) {
		return false;
	}

	public boolean containsValue(Object value) {
		return false;
	}

	public Set<java.util.Map.Entry<String, FOFunction>> entrySet() {
		return null;
	}

	public Object invokeMethod(String name, Object args) {
		return this.get(name).call((Object[]) args);
	}

	public FOFunction get(Object key) {
		FOFunction function = new FOFunction(key.toString(), script);
		return function;
	}

	public boolean isEmpty() {
		return false;
	}

	public Set<String> keySet() {
		return null;
	}

	// TODO siehe oben
	public int size() {
		return 0;
	}

	public FOFunction put(String key, FOFunction value) {
		return null;
	}

	public void putAll(Map<? extends String, ? extends FOFunction> m) {
	}

	public FOFunction remove(java.lang.Object key) {
		return null;
	}

	public Collection<FOFunction> values() {
		return null;
	}

}
