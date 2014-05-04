package com.mc.pha.async;

import java.util.List;
import java.util.Map;

import com.mc.pha.beans.RestaurantMenuItem;
import com.mc.pha.util.PHAProperties;

/**
 * Handler to enable reporting of asynchronous menu results back to the main UI thread.
 * 
 * @author puneet
 *
 */
public interface AsyncMenuResponseHandler {
	
	 public void processMenuResults(Map<String,List<RestaurantMenuItem>> menuResults);
	 
	 public PHAProperties getPhaProperties();
}
