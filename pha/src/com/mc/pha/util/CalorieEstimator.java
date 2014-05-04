package com.mc.pha.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.util.Log;

/**
 * This is a class to simulate calories in a dish and max calories allowed in a dish for it to be recommended
 * @author puneet
 *
 */
public class CalorieEstimator {
	
	private static Map<String,CalorieRange> calorieMap = new LinkedHashMap<String,CalorieRange>();
	
	static {
		//Healthy items
		calorieMap.put("salad", new CalorieRange(200,450));
		calorieMap.put("veggie", new CalorieRange(200,450));
		calorieMap.put("fruit", new CalorieRange(200,300));
		calorieMap.put("vegetable", new CalorieRange(200,300));
		calorieMap.put("sandwich", new CalorieRange(400,800));
		calorieMap.put("soup", new CalorieRange(300,500));
		calorieMap.put("yogurt", new CalorieRange(200,300));
		calorieMap.put("chicken", new CalorieRange(400,600));
		calorieMap.put("fish", new CalorieRange(500,800));
		calorieMap.put("turkey", new CalorieRange(400,600));
		
		//Unhealthy items
		calorieMap.put("cheese", new CalorieRange(400,700));
		calorieMap.put("burger", new CalorieRange(500,800));
		calorieMap.put("fried", new CalorieRange(500,900));
		calorieMap.put("fries", new CalorieRange(400,800));
		calorieMap.put("cream", new CalorieRange(400,800));
		calorieMap.put("queso", new CalorieRange(400,700));
		calorieMap.put("pizza", new CalorieRange(600,1000));
		calorieMap.put("butter", new CalorieRange(400,600));
		calorieMap.put("pork", new CalorieRange(500,700));
		calorieMap.put("beef", new CalorieRange(500,700));
		calorieMap.put("bacon", new CalorieRange(400,800));
		calorieMap.put("chocolate", new CalorieRange(400,800));
		
		//drinks
		calorieMap.put("coffee", new CalorieRange(30,60));
		calorieMap.put("espresso", new CalorieRange(5,20));
		calorieMap.put("americano", new CalorieRange(20,60));
		calorieMap.put("macchiato", new CalorieRange(20,50));
		calorieMap.put("latte", new CalorieRange(200,300));
		calorieMap.put("cappuccino", new CalorieRange(150,200));
		calorieMap.put("beer", new CalorieRange(200,300));
		calorieMap.put("diet", new CalorieRange(0,10));
		calorieMap.put("pepsi", new CalorieRange(200,300));
		calorieMap.put("coke", new CalorieRange(150,200));
		calorieMap.put("tea", new CalorieRange(50,150));
		
		//No Match
		calorieMap.put(PHAConstants.NO_CALORIE_MATCH, new CalorieRange(400,700));
	}

	public static Integer getCalorieEstimate(String itemName) {

		Iterator<String> iterator = calorieMap.keySet().iterator();
		CalorieRange cr = null;
		
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (itemName.toLowerCase().contains(key)) {
				Log.d(PHAConstants.PHA_DEBUG_TAG,"Found " + itemName + " in the database.");
				cr = calorieMap.get(key);
			}
		}
		
		if (cr == null) {
			Log.d(PHAConstants.PHA_DEBUG_TAG,"No match found for " + itemName + ". Using default calorie estimate");
			cr = calorieMap.get(PHAConstants.NO_CALORIE_MATCH);
		}
		
		return  getCaloriesInRange(cr.getMin(),cr.getMax());
	}
	
	public static Integer getRandomMaxCalories() {
		return getCaloriesInRange(500,900);
	}

	private static class CalorieRange {
		private int min;
		private int max;
		
		CalorieRange(int min, int max) {
			this.min = min;
			this.max = max;
		}

		int getMin() {
			return min;
		}

		int getMax() {
			return max;
		}
	}
	
	private static Integer getCaloriesInRange(int min, int max) {
		return  min + (int) Math.round(Math.random() * (max - min));
	}
	
}
