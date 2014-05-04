package com.mc.sensortag.personalhealthassistant;

import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mc.pha.util.PHAConstants;

public class BroadcastFakeData extends BroadcastReceiver {
	static double[] last = {1/1.73, 1/1.73, 1/1.73};
	static int seconds = 0;
	static int randSize = 3;

	@Override
	public void onReceive(Context context, Intent intent) {
//		Log.i(PHAConstants.PHA_DEBUG_TAG, "got here MAIN");
		Intent broadcast = new Intent(DeviceService.ACTION_UPDATE_DASHBOARD_WITH_POINT3D);
		double xyz[] = last;

		seconds++;
		if ((seconds/20) % 2 == 0) randSize = 2;
		else randSize = 3;
		int random = new Random().nextInt(randSize);
		xyz[2] = xyz[2] <= 1/1.73 ? xyz[2] + random: xyz[2] - random;
		last = xyz;
		
		broadcast.putExtra("X", xyz[0]);
		broadcast.putExtra("Y", xyz[1]);
		broadcast.putExtra("Z", xyz[2]);
		context.sendBroadcast(broadcast);
	}
}
