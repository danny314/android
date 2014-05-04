package com.mc.pha.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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

import com.mc.pha.util.PHAConstants;

/**
 * This is an asynchronous task that retrieves restaurants based on current location.
 * It parses the JSON response and returns a map containing the restaurant information.
 * Map key - FOOD_REC[1-5]
 * Map value - Map containing a single entry with restaurant venueId as the key and restaurant name as the value.
 *  
 * @author puneet
 *
 */
public class RetrieveRestaurantsTask extends AsyncTask<String, Integer, Map<String,Map<String,String>>> {
	
	private AsyncRestaurantResponseHandler delegate;
	
	public AsyncRestaurantResponseHandler getDelegate() {
		return this.delegate;
	}

	public void setDelegate(AsyncRestaurantResponseHandler _delegate) {
		this.delegate = _delegate;
	}

	@Override
	protected Map<String,Map<String,String>> doInBackground(String... params) {
	    StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    String restaurantGetUrl = params[0];
	    
	    HttpGet httpGet = new HttpGet(restaurantGetUrl);
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
	        Log.e(PHAConstants.PHA_DEBUG_TAG, "Failed to get restaurant information. HTTP Response code " + statusCode);
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
	            e.printStackTrace();
	        }
	    }	    
	    //Parse JSON to extract restaurants and venue ids
	    Map<String,Map<String,String>> restaurantMap = new HashMap<String,Map<String,String>>();
        JSONObject obj;
		try {
			obj = new JSONObject(builder.toString());
			JSONObject response = obj.getJSONObject("response");
	        JSONArray resultsArray = response.getJSONArray("venues");
            
	        //Log.d(PHAConstants.PHA_DEBUG_TAG, String.valueOf(resultsArray));

            int iterationCount = resultsArray.length();
            StringBuilder recommendation = new StringBuilder();
            
            JSONObject venueObj = null;
            
            for (int i = 0; i < iterationCount; i++) {
        	    Map<String,String> venueIdNameMap = new HashMap<String,String>();
        	    
            	venueObj = new JSONObject(resultsArray.getString(i));
            	String restaurantName = venueObj.getString("name");
            	recommendation.append("\n").append(restaurantName).append("\t\t");

            	String venueId = venueObj.getString("id");
            	
            	venueIdNameMap.put(venueId, restaurantName);
            	restaurantMap.put(PHAConstants.FOOD_REC_PREFIX + (i+1), venueIdNameMap);
            }
		} catch (JSONException e) {
            Log.e(PHAConstants.PHA_DEBUG_TAG, "Could not parse malformed JSON");
		}	    
        Log.i(PHAConstants.PHA_DEBUG_TAG, restaurantMap.size() + " restaurants retrieved");
	    return restaurantMap;	
	}

	@Override
	protected void onPostExecute(Map<String,Map<String,String>> restaurantMap) {
        getDelegate().processRestaurantResults(restaurantMap);
    }

}
