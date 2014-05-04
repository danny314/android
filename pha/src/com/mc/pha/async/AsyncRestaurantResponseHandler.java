package com.mc.pha.async;

import java.util.Map;

/**
 * Handler to enable reporting of asynchronous restaurants results back to the main UI thread.
 * 
 * @author puneet
 *
 */
public interface AsyncRestaurantResponseHandler {
	
	 public void processRestaurantResults(Map<String,Map<String,String>> restaurantMap);
}
