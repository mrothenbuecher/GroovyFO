package de.finetech.groovy.utils;

import de.abas.eks.jfop.remote.FO;
import de.finetech.groovy.AbasBaseScript;

public abstract class GroovyFOVariable<V> implements Comparable<Object>  {

	protected String buffer, varname, type;
	protected AbasBaseScript script;
	
	/**
	 * 
	 * @param varname U|von M|von H|von ...
	 * @param script
	 */
	public GroovyFOVariable(String varname,AbasBaseScript script ){
		String[] foo = varname.split(AbasBaseScript.PIPE_PATTERN);
		this.buffer = foo[0];
		this.varname = foo[1];
		this.script  = script;
	}
	
	/**
	 * 
	 * @param buffer H,M,D,1,....
	 * @param varname xvon, von, fooo
	 * @param script
	 */
	public GroovyFOVariable(String buffer, String varname, AbasBaseScript script){
		this.buffer = buffer;
		this.varname = varname;
		this.script  = script;
	}
	
	public abstract V getValue();
	
	public String getType(){
		if(type == null){
			this.type = FO.getValue("F", "typeof("+this.getVariablename()+")");
		}
		return type;
	}
	
	public String getVariablename(){
		return this.buffer+"|"+this.varname;
	}
	
	@Override
	public String toString(){
		return this.getValue().toString();
	}
	
}
