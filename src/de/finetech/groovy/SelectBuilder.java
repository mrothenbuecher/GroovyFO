package de.finetech.groovy;

public class SelectBuilder {

	public final static int TYPE_HIDDEN = 0, TYPE_VISIBLE = 1; 
	
	private String selectionType = "";
	private String variables = "";
	private String selection = "";
	
	/**
	 * @param type - Art der Selektion TYPE_HIDDEN = dialoglos, TYPE_VISIBLE = Dialogselektion
	 */
	public SelectBuilder(int type){
		switch(type){
		case SelectBuilder.TYPE_HIDDEN:
			this.selectionType = "$";
			break;
		case SelectBuilder.TYPE_VISIBLE:
			this.selectionType =  "%";
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public SelectBuilder(int type, int selection){
		switch(type){
		case SelectBuilder.TYPE_HIDDEN:
			this.selectionType = "$"+selection+",";
			break;
		case SelectBuilder.TYPE_VISIBLE:
			this.selectionType =  "%"+selection+",,";
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public SelectBuilder(int type, String selection){
		switch(type){
		case SelectBuilder.TYPE_HIDDEN:
			this.selectionType = "$"+selection+",";
			break;
		case SelectBuilder.TYPE_VISIBLE:
			this.selectionType =  "%"+selection+",,";
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public SelectBuilder setGroup(String group){
		this.selection += "@Gruppe="+group+";";
		return this;
	}
	
	public String getSelectionString(){
		return toString();
	}
	
	@Override
	public String toString(){
		return this.selectionType+variables+selection;
	}
	
}
