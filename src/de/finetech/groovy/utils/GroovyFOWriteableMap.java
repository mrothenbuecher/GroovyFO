package de.finetech.groovy.utils;

import de.abas.eks.jfop.FOPException;
import de.abas.jfop.base.buffer.WriteableBuffer;
import de.finetech.groovy.AbasBaseScript;

public class GroovyFOWriteableMap<T extends WriteableBuffer> extends
		GroovyFOReadableMap<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2863196830703951276L;

	public GroovyFOWriteableMap(T buffer, AbasBaseScript script) {
		super(buffer, script);
	}
	
	@Override
	public Object put(String key, Object value) {
		try {
			// FIXME muss besser gehen
			if (value instanceof Integer) {
				return script.fo(key, (Integer) value);
			} else if (value instanceof Double) {
				return script.fo(key, (Double) value);
			} else if (value instanceof Boolean) {
				Boolean b = (Boolean) value;
				return script.fo(key, b.booleanValue() ? "G|TRUE":"G|FALSE"  );
			} else if (value instanceof GroovyFOVariable) {
				return script.fo(key, value.toString());
			} else {
				// wenn der �bergebene Wert mit einem Hochkomma "'" beginnt wird der
				// wert so �bergeben das abas ihn interpretiert
				String val = value.toString();
				if (val.startsWith("'")) {
					val = val.substring(1);
					script.formula(buffer + "|" + key, val);
				} else {
					return script.fo(key, val);
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