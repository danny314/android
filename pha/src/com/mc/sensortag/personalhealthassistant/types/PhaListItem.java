package com.mc.sensortag.personalhealthassistant.types;

/**
 * List item to store PHA activities or recommendations data and corresponding calories 
 * burned or consumed
 * 
 * @author Sadaf
 *
 */
public class PhaListItem {
	private String title;
	private String value;
	
	public PhaListItem(String title, String value){
		this.title = title;
		this.value = value;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return title + ": " + value;
	}
}
