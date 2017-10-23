package de.finetech.groovy.utils;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.abas.eks.jfop.FOPException;
import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.datatypes.FOFunction;

/**
 * D-Buffer
 * 
 * @author Michael Rothenb�cher, Finetech GmbH & Co. KG
 *
 */
public class GroovyFODBufferMap extends GroovyObjectSupport implements
		Map<String, Object>, Cloneable, Serializable, GroovyObject {

	private static final long serialVersionUID = 4338579360691818296L;
	
	protected AbasBaseScript script;

	public GroovyFODBufferMap(AbasBaseScript script) {
		this.script = script;
	}
	
	public Object get(Object key) {
		//FOFunction function = new FOFunction(key.toString(), script);
		try {
			// buffer.
			return script.getValue("d|"+key);
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

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}

}
