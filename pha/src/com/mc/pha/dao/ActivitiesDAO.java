package com.mc.pha.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mc.pha.util.PHAConstants;

public class ActivitiesDAO {
	private SQLiteDatabase db;
	private PhaSQLiteHelper phaHelper;
	
	public final static String ACTIVITIES = "ACTIVITIES";
	public final static String STATUS_DATE = "STATUS_DATE";
	public final static String ACTIVITYNAME = "ACTIVITYNAME";
	public final static String CALORIES = "CALORIES";

	public ActivitiesDAO(Context context) {
        phaHelper = new PhaSQLiteHelper(context);
    }
	
	public void open() throws SQLException{
		db = phaHelper.getWritableDatabase();
	}
	
	public void close(){
		phaHelper.close();
	}
	
	public long insertActivity(String date, String activityName, Integer caloriesConsumed){
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Inserting new activity: "+ activityName + " " + caloriesConsumed);
		ContentValues values = new ContentValues();

		values.put(STATUS_DATE, date);
		values.put(ACTIVITYNAME, activityName);
		values.put(CALORIES, caloriesConsumed);
	
		long newRowId;
		newRowId = db.insert(ACTIVITIES, null, values);
		
		return newRowId;
	}
	
	public List<Map<String,String>> getTodaysActivities()  {
		List<Map<String,String>> dailyStatus = new LinkedList<Map<String,String>>();
		String date;
		String activityName;
		Integer caloriesBurned;
		String today;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		
//		Log.i(PHAConstants.PHA_DEBUG_TAG,"Getting today's records of activity...");
		
		String[] allColumns = {STATUS_DATE, ACTIVITYNAME, CALORIES}; 
		Cursor cursor = db.query(ACTIVITIES, allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		// TODO better database query
		while (!cursor.isAfterLast()){
			Map<String, String> map = new HashMap<String, String>();
			date = cursor.getString(cursor.getColumnIndex(STATUS_DATE));
			if (date.equals(today)) {
				activityName = cursor.getString(cursor.getColumnIndex(ACTIVITYNAME));
				caloriesBurned = cursor.getInt(cursor.getColumnIndex(CALORIES));

				map.put(STATUS_DATE, date);
				map.put(ACTIVITYNAME, activityName);
				map.put(CALORIES, caloriesBurned.toString());

				dailyStatus.add(map);
			}
			cursor.moveToNext();
		}
		
		cursor.close();
		return dailyStatus;
	 }
}
