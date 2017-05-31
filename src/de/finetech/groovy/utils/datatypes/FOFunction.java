package de.finetech.groovy.utils.datatypes;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;

import java.text.ParseException;
import java.util.Map;

import de.finetech.groovy.AbasBaseScript;
import de.finetech.groovy.utils.GroovyFOException;

/**
 * 
 * @author Michael Rothenb�cher, Finetech GmbH & Co. KG
 *
 *
 * Klasse repr�sentiert einen Functions aufruf wie
 * F|defined(o)
 */
public class FOFunction {

	protected String functionName;
	protected AbasBaseScript script;
	
	public FOFunction(String functionName, AbasBaseScript script){
		this.functionName = functionName;
		this.script = script;
	}
	
	public Object call(Object o) {
		return this.call(new Object[]{o});
	}
	
	public Object call(Object...objs){
		String parameter = "";
		if(objs != null){
			for(int i = 0; i<objs.length;i++) {
				Object o = objs[i];
				if(o != null){
						parameter += o.toString();
					// einzelnen Parameter mit komma trennen
					if(i < (objs.length-1)){
						parameter += ",";
					}
				}
			}
		}
		try {
			return script.getComputedValue("F|"+functionName+"("+parameter+")");
		} catch (GroovyFOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Object call() throws Exception {
		return this.call(new Object[]{null});
	}
	
}
