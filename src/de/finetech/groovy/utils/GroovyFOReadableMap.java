package de.finetech.groovy.utils;

import java.text.ParseException;

import de.abas.eks.jfop.FOPException;
import de.abas.jfop.base.buffer.ReadableBuffer;
import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.datatypes.TypGuesser.PossibleDatatypes;

/**
 * 
 * @author Michael Rothenb�cher, Finetech GmbH & Co. KG
 *
 * @param <T>
 */
public class GroovyFOReadableMap<T extends ReadableBuffer> extends GroovyFOBaseReadableMap<T> {

	private static final long serialVersionUID = 9140667579366484095L;

	public GroovyFOReadableMap(T buffer, AbasBaseScript script) {
		super(buffer, script);
	}

	public Object get(Object key) {
		try {
			// buffer.
			String skey = key.toString();

			PossibleDatatypes abasType = script.getType(buffer.getQualifiedFieldName(skey));

			switch (abasType) {
			case INTEGER:
				return buffer.getIntegerValue(skey);
			case DOUBLE:
			case DOUBLEDT:
			case DOUBLET:
			case DOUBLED:
				return buffer.getDoubleValue(skey);
			case BOOLEAN:
				return buffer.getBooleanValue(skey);
			default:
				return script.getValueByType(abasType, buffer.getQualifiedFieldName(skey), buffer.getStringValue(skey));
				//return script.getValue(buffer.getQualifiedFieldName(skey), buffer.getStringValue(skey));
			}
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
