package com.mc.sensortag.personalhealthassistant;

import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mc.pha.util.PHAConstants;

public class FakeDeviceService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		AlarmManager alarm = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		Intent inner = new Intent(getApplicationContext(), BroadcastFakeData.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, inner, PendingIntent.FLAG_CANCEL_CURRENT);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 1000, pendingIntent);
		
		return Service.START_NOT_STICKY;
	}
}
