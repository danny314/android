package com.mc.pha.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.mc.pha.beans.RestaurantMenuItem;
import com.mc.pha.util.CalorieEstimator;
import com.mc.pha.util.PHAConstants;

/**
 * This is an asynchronous task that retrieves menus for a restaurant using its venueId.
 * It parses the JSON response and returns a map containing the menu information.
 * Map key - FOOD_REC[1-5] 
 * Map value - List containing at most 3 items from the menu.
 * 
 * If the menu is not available the list contains a single entry 'Menu Not Available Online'
 *  
 * @author puneet
 *
 */
public class RetrieveMenusTask extends AsyncTask<String, Integer, Map<String,List<RestaurantMenuItem>>> {
	
	private AsyncMenuResponseHandler delegate;
	
	private String recNumber;

	public AsyncMenuResponseHandler getDelegate() {
		return this.delegate;
	}

	public void setDelegate(AsyncMenuResponseHandler _delegate) {
		this.delegate = _delegate;
	}

	@Override
	protected Map<String,List<RestaurantMenuItem>> doInBackground(String... params) {
	    StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    //Log.d(PHAConstants.PHA_DEBUG_TAG, "Received params for menu task " + params[0]);
	    
	    this.recNumber = params[0];
	    HttpGet httpGet = new HttpGet(params[1]);
	    String restaurantName = params[2];
	    Integer maxCalories = Integer.parseInt(params[3]);

	    BufferedReader reader = null;
	    
	    try {
	      HttpResponse response = client.execute(httpGet);
	      StatusLine statusLine = response.getStatusLine();
	      int statusCode = statusLine.getStatusCode();
	      Log.d(PHAConstants.PHA_DEBUG_TAG, "Received response code " + statusCode);
	      
	      if (statusCode == 200) {
	        HttpEntity entity = response.getEntity();
	        InputStream content = entity.getContent();
	        reader = new BufferedReader(new InputStreamReader(content));
	        String line = reader.readLine();
	        
	        while (line != null) {
	          builder.append(line);
	          line = reader.readLine();
	        }
	      } else {
	        Log.e(PHAConstants.PHA_DEBUG_TAG, "Failed to get menu information for " + restaurantName);
	      }
	    } catch (ClientProtocolException e) {
			Log.e(PHAConstants.PHA_DEBUG_TAG,e.getMessage());
	    } catch (IOException e) {
			Log.e(PHAConstants.PHA_DEBUG_TAG,e.getMessage());
	    }
	    finally {
	        try {
	        	if (reader != null) {
			        reader.close();
	        	}
	        } catch (IOException e) {
	        	Log.e(PHAConstants.PHA_DEBUG_TAG,e.getMessage());
	        }
	    }	    
	    
	    //Parse JSON
	    Map<String,List<RestaurantMenuItem>> menuItemsMap = new HashMap<String,List<RestaurantMenuItem>>();
        List<RestaurantMenuItem> menuItems = new ArrayList<RestaurantMenuItem>();
        
	    JSONObject obj;
		try {
			obj = new JSONObject(builder.toString());
			JSONObject response = obj.getJSONObject("response").getJSONObject("menu").getJSONObject("menus");
	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed upto menus");
			
	        JSONArray itemsArray = response.getJSONArray("items");
	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed upto items");

	        //This is the number of menus available for this restaurant (e.g lunch menu, dinner menu, appetizer menu, drinks menu, entee menu, desserts menu)
            int menuCount = itemsArray.length();
            
            if (menuCount == 0) {
                Log.d(PHAConstants.PHA_DEBUG_TAG, "No menu available for " + restaurantName);
                menuItems.add(RestaurantMenuItem.getUnavailableMenuItem());
                menuItemsMap.put(this.recNumber + PHAConstants.REC_RESTAURANT_SEPARATOR + restaurantName, menuItems);
            	return menuItemsMap;
            }
            //Log.d(PHAConstants.PHA_DEBUG_TAG, menuCount + " menus available");
            
            //Right now we are just using the very first menu since we have limited space.
            menuCount = 1;
            
            int recommendationCount = 0;
            
            JSONObject itemObj = null;
            
            for (int i = 0; i < menuCount; i++) {
            	itemObj = new JSONObject(itemsArray.getString(i));
    	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed up to itemObj");
            	JSONObject entries = itemObj.getJSONObject("entries");
    	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed up to entries");
            	
    	        JSONArray menuTypeArray =  entries.getJSONArray("items");
    	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed up to menu type array");
    	        
    	        int menuSectionCount = entries.getInt("count");
    	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Menu section count = " + menuSectionCount);
    	        
    	        for (int j=0; j < menuSectionCount; j++) {
                	String sectionName = menuTypeArray.getJSONObject(j).getString("name");
        	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Reading items in the section " + sectionName);
        	        
                	JSONObject menuEntries =  menuTypeArray.getJSONObject(j).getJSONObject("entries");
        	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed up to menu entries");

        	        int itemCount = menuEntries.getInt("count");
        	        Log.d(PHAConstants.PHA_DEBUG_TAG, "Section " +  sectionName + " has " + itemCount  + " entries");
    	        	
        	        JSONArray menuItemArray = menuEntries.getJSONArray("items");
        	        
        	        //Log.d(PHAConstants.PHA_DEBUG_TAG, "Parsed up to menu entries");
        	        
        	        for (int k = 0; k < itemCount && recommendationCount < getMaxRecommendations(); k++) {
                    	String menuItemName = menuItemArray.getJSONObject(k).getString("name");
                    	Integer caloriesInDish = CalorieEstimator.getCalorieEstimate(menuItemName);
                    	if (caloriesInDish <= maxCalories) {
                        	menuItems.add(new RestaurantMenuItem(menuItemName, caloriesInDish));
                    	} else {
                    		Log.d(PHAConstants.PHA_DEBUG_TAG, menuItemName + " has " + caloriesInDish + " calories. Skipping...");
                    	}
                    	++recommendationCount;
        	        }
    	        }
            }
            if (menuItems.isEmpty()) {
            	menuItems.add(RestaurantMenuItem.getNoRecommendationMenuItem());
            }
            menuItemsMap.put(this.recNumber + PHAConstants.REC_RESTAURANT_SEPARATOR + restaurantName, menuItems);
            
		} catch (JSONException e) {
            Log.e(PHAConstants.PHA_DEBUG_TAG, "Could not parse malformed JSON");
            menuItems.add(RestaurantMenuItem.getUnavailableMenuItem());
            menuItemsMap.put(this.recNumber + PHAConstants.REC_RESTAURANT_SEPARATOR + restaurantName, menuItems);
		}	    
	    return menuItemsMap;	
	}

	@Override
	protected void onPostExecute(Map<String,List<RestaurantMenuItem>> menuResults) {
        getDelegate().processMenuResults(menuResults);
    }
	
	private Integer getMaxRecommendations() {
		String maxRecommendationsPerRestaurantStr = getDelegate().getPhaProperties().getPHAProperty("com.mc.max.rec.per.restaurant");
		int maxRecommendationsPerRestaurant = 3;
		if (maxRecommendationsPerRestaurantStr != null) {
			try {
				maxRecommendationsPerRestaurant = Integer.parseInt(maxRecommendationsPerRestaurantStr);
			} catch (NumberFormatException nfe) {
        		Log.d(PHAConstants.PHA_DEBUG_TAG, "Invalid value " + maxRecommendationsPerRestaurantStr + " specified for com.mc.max.rec.per.restaurant in pha.conf");
			}
		}
		return maxRecommendationsPerRestaurant;
	}

}
