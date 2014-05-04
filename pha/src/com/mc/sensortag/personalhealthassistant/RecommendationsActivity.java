package com.mc.sensortag.personalhealthassistant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.mc.pha.async.AsyncMenuResponseHandler;
import com.mc.pha.async.AsyncRestaurantResponseHandler;
import com.mc.pha.async.RetrieveMenusTask;
import com.mc.pha.async.RetrieveRestaurantsTask;
import com.mc.pha.beans.RestaurantMenuItem;
import com.mc.pha.dao.DailyStatusDAO;
import com.mc.pha.dao.ProfileDAO;
import com.mc.pha.util.PHAConstants;
import com.mc.pha.util.PHAProperties;
import com.mc.sensortag.personalhealthassistant.types.ListBaseAdapter;
import com.mc.sensortag.personalhealthassistant.types.PhaListItem;
import com.mc.sensortag.personalhealthassistant.types.SectionedListAdapter;

/**
 * Displays Recommendations based on current location. It also track the calories consumed by the user.
 * 
 * @author Sadaf, Puneet
 *
 */
public class RecommendationsActivity extends ListActivity implements AsyncRestaurantResponseHandler, AsyncMenuResponseHandler {
	
	Context context;
	
	private DailyStatusDAO dailyStatusDao;
	private ProfileDAO profileDao;
	private TextView name;
	private TextView todaysConsumption;
	private ListView recommendationsListView;

	/**
	 * Used for getting properties configured in assets/pha.conf
	 */
	private PHAProperties phaProperties;

	/**
	 * Map of restaurants returned based on location. The key is the recommendation number 'FOOD_REC[1-5]'.
	 * Value is a map containing only one entry with key as the Foursquare venueId and value as the name of the restaurant. 
	 */
	private Map<String,Map<String,String>> restaurantMap;
	
	/**
	 * Iterator for the restaurantMap. Used to get menu for a new restaurant if the current one does not have menu available on FourSquare API.
	 */
	private Iterator<Map.Entry<String,Map<String,String>>> restaurantIterator;
	
	private SectionedListAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommendations_activity);
		context = this;
		
		initializeUserName();
		initializeCaloriesConsumed();
		
		//Load configuration from assets/pha.conf
		phaProperties = new PHAProperties(getAssets());
		
		LocationClient locationClient = ((MainActivity) getParent()).getmLocationClient();
	    
		if (locationClient != null && locationClient.isConnected()) {
		    Location location = locationClient.getLastLocation();
    		String restaurantUrl = buildRestaurantUrl(location);
    		Log.d(PHAConstants.PHA_DEBUG_TAG,"restaurant url = " + restaurantUrl);
    		RetrieveRestaurantsTask restaurantTask = new RetrieveRestaurantsTask();
    		restaurantTask.setDelegate(this);
    		restaurantTask.execute(new String[] {restaurantUrl});
		    
		} else {
			Log.w(PHAConstants.PHA_DEBUG_TAG,"Waiting for location information...");
		}
		recommendationsListView = (ListView) findViewById(R.id.recommendationsListView);
		initializeRecommendationsList();
	}
	
	private void initializeUserName() {
		name = (TextView) findViewById(R.id.recommendationUserName);
		
		profileDao = new ProfileDAO(this);
		profileDao.open();

		Map<String, String> userProfile = profileDao.getUserProfile();
		if(userProfile != null){
			name.setText(userProfile.get("NAME"));
			Log.i(PHAConstants.PHA_DEBUG_TAG, "User name: " + userProfile.get("NAME") + "; Calories Burned : " + userProfile.get("WEIGHT"));
		} else {
			Log.i(PHAConstants.PHA_DEBUG_TAG, "User profile not was not found.");
		}
	}
	
	private void initializeCaloriesConsumed(){
		todaysConsumption = (TextView) findViewById(R.id.caloriesConsumed);
		
		//TODO: get total calories consumed today from the db and update the UI 
		dailyStatusDao = new DailyStatusDAO(this);
		dailyStatusDao.open();
		
		String today = null;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		
		updateCaloriesConsumptionUI();
		
		todaysConsumption.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				Log.i(PHAConstants.PHA_DEBUG_TAG, "**** Starting Calories Input Activity *****");
				Intent mCaloriesInputIntent = new Intent(context, CaloriesInputActivity.class);
				mCaloriesInputIntent.putExtra("ComingFrom", "Recommendation Activity");
				final int result = 1;
				startActivityForResult(mCaloriesInputIntent, result);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		String extraData = data.getStringExtra("ComingFrom");
		
		updateCaloriesConsumptionUI();
		//Log.i(PHAConstants.PHA_DEBUG_TAG, extraData);
		//todaysConsumption.setText(extraData);
	}
	
	private void updateCaloriesConsumptionUI() {
		Map<String, String> dailyStatus = dailyStatusDao.getDailyStatus();
		if(dailyStatus != null){
			Log.i(PHAConstants.PHA_DEBUG_TAG, "Calories retrieved from DB : " + dailyStatus.get(dailyStatusDao.CONSUMED));
			todaysConsumption.setText(dailyStatus.get(dailyStatusDao.CONSUMED));
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Today's record was not found.");
		}
	}

	private void initializeRecommendationsList() {
		this.adapter = new SectionedListAdapter(this);
		adapter.addSection("Retrieving recommendations...", new ListBaseAdapter(context, new ArrayList<PhaListItem>()));
		recommendationsListView.setAdapter(adapter);
	}

	public void onRecommendationSelection(View v){
		
		Integer caloriesConsumedToday = Integer.parseInt(dailyStatusDao.getDailyStatus().get(dailyStatusDao.CONSUMED));
		//get calories
		Button caloriesButton = (Button) v.findViewById(R.id.calories);
		String caloriesStr = caloriesButton.getText().toString();
		String today = null;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		int caloriesConsumedNow =  Integer.valueOf(caloriesStr.split(" ")[0]);
		dailyStatusDao.updateCaloriesConsumed(today, caloriesConsumedToday + caloriesConsumedNow, caloriesConsumedNow);
		updateCaloriesConsumptionUI();
		Toast.makeText(context, caloriesConsumedNow + " calories logged", 1000).show();
	}
	
	private String buildRestaurantUrl(Location location) {
		String clientId = phaProperties.getPHAProperty("com.mc.foursquare.client_id");
		String clientSecret= phaProperties.getPHAProperty("com.mc.foursquare.client_secret");
		String apiVersion = phaProperties.getPHAProperty("com.mc.foursquare.v");
		String restaurantSearchUrl = phaProperties.getPHAProperty("com.mc.foursquare.venue.search.url");
		String searchRadiusInMeters = phaProperties.getPHAProperty("com.mc.foursquare.radius");
		String foodCategoryId = phaProperties.getPHAProperty("com.mc.foursquare.food.categoryId");
		String resultLimit = phaProperties.getPHAProperty("com.mc.foursquare.limit");
		
		String uriStr = null;
		
		if (clientId != null && clientSecret != null && apiVersion != null) {
			Uri.Builder uri = Uri.parse(restaurantSearchUrl)
	                .buildUpon()
	                //.appendQueryParameter("ll", "38,-100")
	                .appendQueryParameter("ll", location.getLatitude() + ","+ location.getLongitude())
	                .appendQueryParameter("categoryId", foodCategoryId)
	                .appendQueryParameter("client_id", clientId)
	                .appendQueryParameter("client_secret", clientSecret)
	                .appendQueryParameter("v", apiVersion);
			
			if (searchRadiusInMeters != null) {
				uri.appendQueryParameter("radius", searchRadiusInMeters);
			}
		
			if (resultLimit != null) {
				uri.appendQueryParameter("limit", resultLimit);
			}

			uriStr = uri.build().toString();
			
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Missing four square client id, secret or api version");
		}
		return uriStr;
    	
    }
    
    private String buildMenuUrl(String venueId) {
		String clientId = phaProperties.getPHAProperty("com.mc.foursquare.client_id");
		String clientSecret= phaProperties.getPHAProperty("com.mc.foursquare.client_secret");
		String apiVersion = phaProperties.getPHAProperty("com.mc.foursquare.v");
		String uriStr = null;
		if (clientId != null && clientSecret != null && apiVersion != null) {
			//Menus url -- venue id 40b13b00f964a520b1f31ee3
			//venueId = "4a5689b8f964a52059b51fe3";
			Uri.Builder uri = Uri.parse("https://api.foursquare.com/v2/venues/" + venueId + "/menu")
	                .buildUpon()
	                .appendQueryParameter("client_id", clientId)
	                .appendQueryParameter("client_secret", clientSecret)
	                .appendQueryParameter("v", apiVersion);
	
			uriStr = uri.build().toString();
			//Log.i(PHAConstants.PHA_DEBUG_TAG,"Built url " + uri);
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG,"No Google Places API key found. Cannot fetch restaurants");
		}
		return uriStr;
    }
    
    public void processRestaurantResults(Map<String,Map<String,String>> restaurantMap){
    	this.restaurantMap = restaurantMap;
    	this.restaurantIterator = this.restaurantMap.entrySet().iterator();
        Log.d(PHAConstants.PHA_DEBUG_TAG, "Finished retrieving restaurant information");
        
		int numberOfRestaurants = restaurantMap.size();
		
		if (numberOfRestaurants == 0) {
			adapter.clear();
			adapter.addSection("No restaurants found nearby", new ListBaseAdapter(context, new ArrayList<PhaListItem>()));
			recommendationsListView.setAdapter(adapter);
	        return;
		}
		
		String numberOfRestaurantsStr = phaProperties.getPHAProperty("com.mc.max.restaurant");
		int maxRestaurants = 5;
		if (numberOfRestaurantsStr != null) {
			try {
				maxRestaurants = Integer.parseInt(numberOfRestaurantsStr);
			} catch (NumberFormatException nfe) {
				Log.e(PHAConstants.PHA_DEBUG_TAG, "Invalid value " + numberOfRestaurantsStr + " specified for com.mc.max.restaurant in pha.conf.");
			}
		}
		
		for (int i=0; i < maxRestaurants; i++) {
			if (restaurantIterator.hasNext()) {
				Map<String,String> restaurantEntry = restaurantIterator.next().getValue();
		        String restaurantName = restaurantEntry.values().iterator().next();
		        //Get the menu for this restaurant
		        String venueId = restaurantEntry.keySet().iterator().next();
		        Log.d(PHAConstants.PHA_DEBUG_TAG, "Retrieving menu for venue id " + venueId);
				String menuUrl = buildMenuUrl(venueId);
				
				RetrieveMenusTask menuTask = new RetrieveMenusTask();
				menuTask.setDelegate(this);
				menuTask.execute(new String[] {PHAConstants.FOOD_REC_PREFIX + (i+1), menuUrl, restaurantName, String.valueOf(getMaxCaloriesAllowed())});
			}
		}
      }    

	@Override
	public void processMenuResults(Map<String,List<RestaurantMenuItem>> menuResults) {
        Log.d(PHAConstants.PHA_DEBUG_TAG, "Finished retrieving menu information.");
    	
    	String key = menuResults.keySet().iterator().next();
    	String[] recNumberAndRestName = key.split(PHAConstants.REC_RESTAURANT_SEPARATOR);
    	
    	String recNumber = recNumberAndRestName[0]; 
        String restaurantName = recNumberAndRestName[1];
    	
    	List<RestaurantMenuItem> menuItems = menuResults.get(key);
        
		List<PhaListItem> recommendationsForRestaurant = new ArrayList<PhaListItem>();
		
		for (RestaurantMenuItem item : menuItems) {
			String menuItemName = item.getMenuItemName();
			String calories = item.getCalories() == null ? "" : item.getCalories() + " calories";
			if (PHAConstants.MENU_NOT_AVAILABLE.equals(menuItemName) && restaurantIterator.hasNext()) {
				//Try to fetch menu of another restaurant
				Map<String,String> restaurantVenueName = restaurantIterator.next().getValue();
				String venueId = restaurantVenueName.keySet().iterator().next();
				String newRestaurantName =  restaurantVenueName.values().iterator().next();
				String menuUrl = buildMenuUrl(venueId);
				RetrieveMenusTask menuTask = new RetrieveMenusTask();
				menuTask.setDelegate(this);
				Log.d(PHAConstants.PHA_DEBUG_TAG, "Max allowed calories = " + getMaxCaloriesAllowed());
				menuTask.execute(new String[] {recNumber, menuUrl, newRestaurantName, String.valueOf(getMaxCaloriesAllowed())});
			} else {
				recommendationsForRestaurant.add(new PhaListItem(menuItemName, calories));
			}
		}
		if (!recommendationsForRestaurant.isEmpty()) {
			Log.d(PHAConstants.PHA_DEBUG_TAG, "Adding section " + restaurantName);
			if (adapter.getCount() == 1) {
				adapter.clear();
			}
			adapter.addSection(restaurantName, new ListBaseAdapter(context, recommendationsForRestaurant ));
			recommendationsListView.setAdapter(adapter);
			recommendationsListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
					// TODO User told us he ate arg2 calories. Update the stats.
					arg2 = arg2 - 1;
					Toast.makeText(context, "" + arg2, 1000).show();
				}
			});
		}
	}

	@Override
	public PHAProperties getPhaProperties() {
		return phaProperties;
	}

	private Integer getMaxCaloriesAllowed() {
		
/*		
		Integer caloriesConsumedToday = 0;
		
		try {
			caloriesConsumedToday = Integer.parseInt(dailyStatusDao.getDailyStatus().get(DailyStatusDAO.CONSUMED));	
		} catch (NumberFormatException nfe) {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Could not parse calories consumed today "  + caloriesConsumedToday + ". Must be a number.");
		}
		
		int calorieDeficit = 2200 - caloriesConsumedToday;
		Log.i(PHAConstants.PHA_DEBUG_TAG, "Can still consume "  + calorieDeficit + " calories today.");
		return  (calorieDeficit > 0 ? calorieDeficit : 0);
*/	
		
		Integer caloriesBurnedToday = 0;
		
		try {
			caloriesBurnedToday = Integer.parseInt(dailyStatusDao.getDailyStatus().get(DailyStatusDAO.BURNED));	
		} catch (NumberFormatException nfe) {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Could not parse calories burned today "  + caloriesBurnedToday + ". Must be a number.");
		}
		
		Log.i(PHAConstants.PHA_DEBUG_TAG, "Can still consume "  + caloriesBurnedToday + " calories today.");
		return  caloriesBurnedToday;
	}

	
}
