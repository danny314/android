package com.mc.pha.dao;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.mc.pha.util.PHAConstants;

public class MenuDAO extends SQLiteOpenHelper {
	
	private static final String CR_TBL_RESTAURANTS =
			"CREATE TABLE RESTAURANTS (	"	+
			"		   ID INT PRIMARY KEY     NOT NULL,"	+
			"		   NAME           TEXT    NOT NULL,"	+
			"		   LATITUDE       REAL,"	+
			"		   LONGITUDE	  REAL"	+
		")";
	
	private static final String CR_TBL_NUTRITION =
			"CREATE TABLE NUTRITION ("	+
			"		  ID     INT PRIMARY KEY     NOT NULL, "	+
			"		  DISH   TEXT, "	+
			"		  CALORIES INTEGER,"	+
			"		  RESTAURANT_ID INTEGER,"	+
			"		  FOREIGN KEY(RESTAURANT_ID) REFERENCES RESTAURANTS(ID)"	+
			"		)";

	//Restaurants  
	private static final String INS_RESTAURANTS_CHIPOTLE = "insert into restaurants values (1,'Chipotle',30.0,-97.6311)";
	private static final String INS_RESTAURANTS_WHATTABURGER = "insert into restaurants values (2,'Whattaburger',30.432,-97.63)";
	private static final String INS_RESTAURANTS_CLAYPIT = "insert into restaurants values (3,'Clay Pit',30.27,-97.74)";

	
	//Dishes
	private static final String INS_DISH_CHIPOTLE_TACOS = "insert into NUTRITION values (1,'Tacos',900,1)";
	private static final String INS_DISH_CHIPOTLE_SALAD = "insert into NUTRITION values (2,'Salad',300,1)";
	
	private static final String INS_DISH_WHATTABURGER_CHEESEBURGER = "insert into NUTRITION values (3,'Cheese Burger',1200,2)";
	private static final String INS_DISH_WHATTABURGER_CHICKEN_SANDWICH = "insert into NUTRITION values (4,'Chicken Sandwich',700,2)";

	private static final String INS_DISH_CLAYPIT_RICE_PILAF = "insert into NUTRITION values (5,'Rice Pilaf with Vegetables',600,3)";
	private static final String INS_DISH_CLAYPIT_TANDOORI_CHICKEN = "insert into NUTRITION values (6,'Tandoori Chicken with Naan',900,3)";

	//Query dishes based on max calories
	private static final String SELECT_DISHES = 
			"select "
			+ "r.name, n.dish, n.calories "
			+ "from RESTAURANTS r join  nutrition n on r.id = n.RESTAURANT_ID "
			+ "where abs(r.LATITUDE - ?) < 0.03 and abs(r.LONGITUDE - ?) < 0.03 and n.calories < ?";

	public MenuDAO(Context context) {
        super(context, PHAConstants.DATABASE_NAME, null, PHAConstants.DATABASE_VERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Creating restaurant db");
		// Build menu db
		db.execSQL(CR_TBL_RESTAURANTS);		
		db.execSQL(INS_RESTAURANTS_CHIPOTLE);		
		db.execSQL(INS_RESTAURANTS_WHATTABURGER);		
		db.execSQL(INS_RESTAURANTS_CLAYPIT);		
		
		db.execSQL(CR_TBL_NUTRITION);
		
		db.execSQL(INS_DISH_CHIPOTLE_SALAD);		
		db.execSQL(INS_DISH_CHIPOTLE_TACOS);	
		
		db.execSQL(INS_DISH_WHATTABURGER_CHEESEBURGER);
		db.execSQL(INS_DISH_WHATTABURGER_CHICKEN_SANDWICH);

		db.execSQL(INS_DISH_CLAYPIT_RICE_PILAF);
		db.execSQL(INS_DISH_CLAYPIT_TANDOORI_CHICKEN);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public Map<String,String> getFoodRecommendation(Location location, Integer maxCalories)  {
		
		
		Map<String,String> foodRec = new HashMap<String,String>();
		
		if (location == null) {
			foodRec.put("ERROR", "No location passed");
			return foodRec;
		}
		
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Getting restaurant...");
		
		double latitude = location.getLatitude();
		String latStr = String.valueOf(latitude);
		
		double longitude = location.getLongitude();
		String longStr = String.valueOf(longitude);
		
		Log.i(PHAConstants.PHA_DEBUG_TAG,"lat/long = " + latStr + ";" + longStr);
		
		Cursor cursor = getWritableDatabase().rawQuery(SELECT_DISHES, new String[] { latStr , longStr, String.valueOf(maxCalories)  }); 
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Log.d(PHAConstants.PHA_DEBUG_TAG,"In while loop...");
			String restaurant = cursor.getString(cursor.getColumnIndex("NAME"));
			String dish = cursor.getString(cursor.getColumnIndex("DISH"));
			Integer calories = cursor.getInt(cursor.getColumnIndex("CALORIES"));
			
			String recommendation = dish + ";" + calories;
			Log.i(PHAConstants.PHA_DEBUG_TAG,"Retrieved restaurant " + restaurant + ";" + recommendation);
			
			if (foodRec.containsKey(restaurant)) {
				recommendation = foodRec.get(restaurant) + ";" + recommendation;  
			} 
			foodRec.put(restaurant, recommendation);
			cursor.moveToNext();
		}
		
		getWritableDatabase().close();
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Retrieved Food rec size " + foodRec);
		
		return foodRec;
	  }	

}
