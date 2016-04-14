package de.finetech.utils.charts;

import java.util.HashMap;
import java.util.Map.Entry;

public class Stripe {

	private boolean vertical = true;
	private String start, end;
	
	private HashMap<String, String> parameter = new HashMap<String,String>();
	
	public Stripe(boolean vertical, String start, String end){
		this.vertical = vertical;
		this.start = start;
		this.end = end;
	}
	
	public Stripe setColor(String color){
		//TODO prüfen ob vorhanden usw.
		//TODO entfernen wenn null oder leer
		this.parameter.put("-color", color);
		return this;
	}
	
	public Stripe setRGBcolor(String color){
		//TODO prüfen ob vorhanden usw.
		//TODO entfernen wenn null oder leer
		this.parameter.put("-rgbcolor", color);
		return this;
	}
	
	@Override
	public String toString(){
		String val = "";
		if(vertical){
			val+="-stripex";
		}else{
			val+="-stripey";
		}
		val += " "+start+" "+end;
		for(Entry<String,String> param: this.parameter.entrySet()){
			val+=" "+param.getKey()+" "+param.getValue();
		}
		return val;
	}
}
