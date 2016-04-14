package de.finetech.utils.charts;

import java.util.HashMap;
import java.util.Map.Entry;

public class Constant {

	private boolean vertical = true;
	private String position;
	
	private HashMap<String, String> parameter = new HashMap<String,String>();
	
	public Constant(boolean vertical, String position){
		this.vertical = vertical;
		this.position = position;
	}
	
	public Constant setTitle(String title){
		//TODO pr�fen ob vorhanden usw.
		//TODO entfernen wenn null oder leer
		this.parameter.put("-title", title);
		return this;
	}
	
	public Constant setColor(String color){
		//TODO pr�fen ob vorhanden usw.
		//TODO entfernen wenn null oder leer
		this.parameter.put("-color", color);
		return this;
	}
	
	public Constant setRGBcolor(String color){
		//TODO pr�fen ob vorhanden usw.
		//TODO entfernen wenn null oder leer
		this.parameter.put("-rgbcolor", color);
		return this;
	}
	
	public Constant setLinewidth(int width){
		//TODO pr�fen ob vorhanden usw.
		//TODO entfernen wenn null oder leer
		this.parameter.put("-linewidth ", ""+width);
		return this;
	}
	
	@Override
	public String toString(){
		String val = "";
		if(vertical){
			val+="-constantx";
		}else{
			val+="-constanty";
		}
		val += " "+position;
		for(Entry<String,String> param: this.parameter.entrySet()){
			val+=" "+param.getKey()+" "+param.getValue();
		}
		return val;
	}
}
