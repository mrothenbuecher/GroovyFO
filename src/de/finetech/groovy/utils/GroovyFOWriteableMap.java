package de.finetech.groovy.utils;

import de.abas.eks.jfop.FOPException;
import de.abas.jfop.base.buffer.WriteableBuffer;
import de.finetech.groovy.AbasBaseScript;

public class GroovyFOWriteableMap<T extends WriteableBuffer> extends GroovyFOReadableMap<T> {

	public GroovyFOWriteableMap(T buffer, AbasBaseScript script) {
		super(buffer, script);
	}

	@Override
	public Object put(String key, Object value) {
		String val = value.toString();
		// wenn der übergebene Wert mit einem Hochkomma "'" beginnt wird der
		// wert so übergeben das abas ihn interpretiert
		try {
			// FIXME muss besser gehen
			if (val.startsWith("'")) {
				val = val.substring(1);
				script.formula(buffer + "|" + key, val);
			} else {
				if (value instanceof Integer) {
					script.fo(buffer + "|" + key, ((Integer) value).intValue());
				} else if (value instanceof Double) {
					script.fo(buffer + "|" + key,
							((Double) value).doubleValue());
				} else if (value instanceof GroovyFOVariable) {
					script.fo(buffer + "|" + key, ((GroovyFOVariable<?>) value)
							.getValue().toString());
				} else {
					script.fo(buffer + "|" + key, val);
				}
			}
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (GroovyFOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
