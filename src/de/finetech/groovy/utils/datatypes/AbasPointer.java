package de.finetech.groovy.utils.datatypes;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.GroovyFOException;
import de.finetech.groovy.utils.GroovyFOVariable;

/**
 * 
 * @author MKürbis
 *
 * Verweis Darstellung
 */
public class AbasPointer extends GroovyFOVariable<String> {

	public AbasPointer(String varname, AbasBaseScript script) {
		super(varname, script);
	}

	@Override
	public int compareTo(Object arg0) {
		return this.toString().compareTo(arg0.toString());
	}

	@Override
	public String getValue() {
		return FO.getValue("F", "expr("+this.varname+")");
	}

	/**
	 * Zugriff auf Variablen eines Verweises
	 * 
	 * M|vorgang^id z.Bsp. in groovy fo dann 
	 * 
	 * m.vorgang^"id"
	 * 
	 * @param var
	 * @return
	 * @throws FOPException
	 * @throws GroovyFOException
	 */
	public Object xor(String var) throws FOPException, GroovyFOException{
			Object o = this.script.getComputedValue(this.getVariablename()+"^"+var);
			return o;
	}
	
}
