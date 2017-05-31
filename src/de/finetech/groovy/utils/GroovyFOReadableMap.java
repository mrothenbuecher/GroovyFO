package de.finetech.groovy.utils;

import java.text.ParseException;

import de.abas.eks.jfop.FOPException;
import de.abas.jfop.base.buffer.ReadableBuffer;
import de.finetech.groovy.AbasBaseScript;

public class GroovyFOReadableMap<T extends ReadableBuffer> extends
GroovyFOBaseReadableMap<T> {

	private static final long serialVersionUID = 9140667579366484095L;

	public GroovyFOReadableMap(T buffer, AbasBaseScript script) {
		super(buffer, script);
	}

	/**
	 * 
	 */
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

}
