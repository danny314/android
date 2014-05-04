package com.mc.pha.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mc.pha.util.PHAConstants;

/**
 * DAO class to handle inserts, updates, and retrieval from the App databases.
 * This class also processes reduction of calories burned based on the calories consumption.
 * 
 * @author Sadaf
 *
 */
public class DailyStatusDAO {
	private SQLiteDatabase db;
	private PhaSQLiteHelper phaHelper;
	
	public final static String DAILY_STATUS = "DAILY_STATUS";
	public final static String STATUS_DATE = "STATUS_DATE";
	public final static String BURNED = "BURNED";
	public final static String CONSUMED = "CONSUMED";

	public DailyStatusDAO(Context context) {
        phaHelper = new PhaSQLiteHelper(context);
    }
	
	public void open() throws SQLException{
		db = phaHelper.getWritableDatabase();
	}
	
	public void close(){
		phaHelper.close();
	}
	
	/*@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Creating restaurant db");
		this.db = db;
		// Build menu db
		db.execSQL(CR_TBL_PROFILE);					
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}*/
	
	public long insertDailyStatus(String date, Integer caloriesBurned, Integer caloriesConsumed){
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Inserting user profile row");
		ContentValues values = new ContentValues();
		//values.put("ID", "1");
		values.put(STATUS_DATE, date.toString());
		values.put(BURNED, caloriesBurned);
		values.put(CONSUMED, caloriesConsumed);
	
		long newRowId;
		newRowId = db.insert(DAILY_STATUS, null, values);
		
		return newRowId;
	}
	
	public Map<String,String> getDailyStatus()  {
		Map<String,String> dailyStatus = new HashMap<String,String>();
		String date;
		Integer caloriesBurned;
		Integer caloriesConsumed;
		
//		Log.i(PHAConstants.PHA_DEBUG_TAG,"Getting today's record...");
		
		String[] allColumns = {STATUS_DATE, BURNED, CONSUMED}; 
		Cursor cursor = db.query(DAILY_STATUS, allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		
		if(!cursor.isAfterLast()){
			date = cursor.getString(cursor.getColumnIndex(STATUS_DATE));
			caloriesBurned = cursor.getInt(cursor.getColumnIndex(BURNED));
			caloriesConsumed = cursor.getInt(cursor.getColumnIndex(CONSUMED));
			
			dailyStatus.put(STATUS_DATE, date);
			dailyStatus.put(BURNED, caloriesBurned.toString());
			dailyStatus.put(CONSUMED, caloriesConsumed.toString());
			
//			Log.i(PHAConstants.PHA_DEBUG_TAG,"Retrieved today's, " + date + ", record; Calories Burned: " + caloriesBurned + "; Calroies Consumed: " + caloriesConsumed) ;
		}
		
		cursor.close();
		return dailyStatus;
	 }

	public boolean updateCaloriesBurned(String date, Integer caloriesBurned){
		ContentValues values = new ContentValues();
		values.put(BURNED, caloriesBurned);
		
//		Log.i(PHAConstants.PHA_DEBUG_TAG,"Updating today's, " + date + " calories burned record...");
		
		String whereClause = "STATUS_DATE='" + date + "'";
		int updated = db.update(DAILY_STATUS, values, whereClause, null);
		
		if (updated > 0) {
//			Log.i(PHAConstants.PHA_DEBUG_TAG, "Today's calories expendation updated.") ;
			return true;
		}
		else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Today's record not found.");
			return false;
		}
	 }
	
	public boolean updateCaloriesConsumed(String date, Integer caloriesConsumed, int consumedNow){
		ContentValues values = new ContentValues();
		values.put(CONSUMED, caloriesConsumed);
		
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Updating today's, " + date + " calories consumed record...");
		
		String whereClause = "STATUS_DATE='" + date + "'";
		int updated = db.update(DAILY_STATUS, values, whereClause, null);
		
		if (updated > 0) {
			Log.i(PHAConstants.PHA_DEBUG_TAG, "Today's calories consumption is updated.") ;
			
			//update the calories burned by excluding the calories consumed
			reduceCaloriesBurned(caloriesConsumed, consumedNow);
			
			return true;
		}
		else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Today's record not found.");
			return false;
		}
	 }

	private void reduceCaloriesBurned(int caloriesConsumed, int consumedNow) {
		Map<String, String> dailyStatus = getDailyStatus();
		
		Integer reducedCalories = Integer.parseInt(dailyStatus.get(BURNED)) - consumedNow;
		
		String today = null;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		
		updateCaloriesBurned(today, reducedCalories);
		
		Map<String, String> updatedStatus = getDailyStatus();
		Log.i(PHAConstants.PHA_DEBUG_TAG, "Calories Burned reduces to " + updatedStatus.get(BURNED));
		
	}
}
