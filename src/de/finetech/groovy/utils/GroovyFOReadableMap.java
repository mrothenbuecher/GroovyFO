package de.finetech.groovy.utils;

import java.text.ParseException;

import de.abas.eks.jfop.FOPException;
import de.abas.jfop.base.buffer.ReadableBuffer;
import de.finetech.groovy.AbasBaseScript;

/**
 * 
 * @author Michael Rothenbücher, Finetech GmbH & Co. KG
 *
 * @param <T>
 */
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
			String skey = key.toString();
			return script.getValue(buffer.getQualifiedFieldName(skey),
					buffer.getStringValue(skey));
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (GroovyFOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
