package com.mc.pha.dao;

import com.mc.pha.util.PHAConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhaSQLiteHelper extends SQLiteOpenHelper{
	
	private static final String CR_TBL_PROFILE =
			"		CREATE TABLE PROFILE (	"	+
			"		   ID INT PRIMARY KEY     NOT NULL,"	+
			"		   NAME           TEXT    NOT NULL,"	+
			"		   WEIGHT       INTEGER"	+
		")";
	
	private static final String CR_TBL_DAILY_STATUS =
			"		CREATE TABLE DAILY_STATUS (	"	+
			"		   STATUS_DATE           TEXT    NOT NULL,"	+
			"		   BURNED     		     INTEGER,"	+
			"		   CONSUMED				 INTEGER" 	+
		")";
	
	private static final String CR_TBL_ACTIVITIES =
			"		CREATE TABLE ACTIVITIES (	"	+
			"		   STATUS_DATE           TEXT    NOT NULL,"	+
			"		   ACTIVITYNAME          TEXT    NOT NULL,"	+
			"		   CALORIES				 INTEGER" 	+
		")";


	public PhaSQLiteHelper(Context context) {
        super(context, PHAConstants.DATABASE_NAME, null, PHAConstants.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CR_TBL_PROFILE);
		db.execSQL(CR_TBL_DAILY_STATUS);
		db.execSQL(CR_TBL_ACTIVITIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
