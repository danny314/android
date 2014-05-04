package com.mc.pha.beans;

import com.mc.pha.util.PHAConstants;

public class RestaurantMenuItem {
	
	private String menuItemName;
	
	private Integer calories;
	
	public RestaurantMenuItem(String menuItemName, Integer calories) {
		this.menuItemName = menuItemName;
		this.calories = calories;
	}

	public RestaurantMenuItem(String menuItemName) {
		this.menuItemName = menuItemName;
	}

	public String getMenuItemName() {
		return menuItemName;
	}

	public void setMenuItemName(String menuItemName) {
		this.menuItemName = menuItemName;
	}

	public Integer getCalories() {
		return calories;
	}

	public void setCalories(Integer calories) {
		this.calories = calories;
	}
	
	public static RestaurantMenuItem getUnavailableMenuItem() {
		return new RestaurantMenuItem(PHAConstants.MENU_NOT_AVAILABLE);
	}

	public static RestaurantMenuItem getNoRecommendationMenuItem() {
		return new RestaurantMenuItem(PHAConstants.NO_RECOMMENDATIONS);
	}

	@Override
	public String toString() {
		return "RestaurantMenuItem [menuItemName=" + menuItemName
				+ ", calories=" + calories + "]";
	}

}
