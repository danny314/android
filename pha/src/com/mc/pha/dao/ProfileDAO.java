package com.mc.pha.dao;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mc.pha.util.PHAConstants;

public class ProfileDAO{
	private SQLiteDatabase db;
	private PhaSQLiteHelper phaHelper;

	public ProfileDAO(Context context) {
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
	
	public long saveProfile(String name, Integer weight){
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Inserting user profile row");
		ContentValues values = new ContentValues();
		values.put("ID", "1");
		values.put("NAME", name);
		values.put("WEIGHT", weight);
	
		long newRowId;
		newRowId = db.insert("PROFILE", null, values);
		
		return newRowId;
	}
	
	public Map<String,String> getUserProfile()  {
		Map<String,String> userProfile = new HashMap<String,String>();
		String name;
		Integer weight;
		
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Getting profile...");
		
		String[] allColumns = {"NAME", "WEIGHT"}; 
		Cursor cursor = db.query("PROFILE", allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		
		if(!cursor.isAfterLast()){
			name = cursor.getString(cursor.getColumnIndex("NAME"));
			weight = cursor.getInt(cursor.getColumnIndex("WEIGHT"));
			
			userProfile.put("NAME", name);
			userProfile.put("WEIGHT", weight.toString());
			
			Log.i(PHAConstants.PHA_DEBUG_TAG,"Retrieved user, " + name + ", weighing " + weight + "lbs.") ;
		}
		
		cursor.close();
		return userProfile;
	 }	

	public boolean updateProfile(String name, Integer weight){
		ContentValues values = new ContentValues();
		//values.put("ID", "1");
		values.put("NAME", name);
		values.put("WEIGHT", weight);
		
		Log.i(PHAConstants.PHA_DEBUG_TAG,"Updating profile...");
		String whereClause = "NAME='" + name + "'";
		int updated = db.update("PROFILE", values, whereClause, null);
		
		if (updated > 0)
			return true;
		else
			return false;
	 }	
}
