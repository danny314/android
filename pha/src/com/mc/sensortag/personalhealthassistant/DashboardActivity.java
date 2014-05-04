package com.mc.sensortag.personalhealthassistant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ti.android.ble.common.BluetoothLeService;
import ti.android.util.Point3D;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mc.pha.dao.ActivitiesDAO;
import com.mc.pha.dao.DailyStatusDAO;
import com.mc.pha.dao.ProfileDAO;
import com.mc.pha.util.AccelerometerDataAnalysis;
import com.mc.pha.util.CalorieTracker;
import com.mc.pha.util.PHAConstants;
import com.mc.sensortag.personalhealthassistant.types.PhaBaseAdapter;
import com.mc.sensortag.personalhealthassistant.types.PhaListItem;

/**
 * Displays total calories burned by the user and weekly overview. 
 * It also displays all the activities in scrollable list view.
 * 
 * @author Sadaf, Jackson
 *
 */
public class DashboardActivity extends Activity{
	private ActivitiesDAO activitiesDao;
	private DailyStatusDAO dailyStatusDao;
	private ProfileDAO profileDao;
	private TextView name;
	private TextView todaysCount;
	private ListView activitiesListView;
	private List<PhaListItem> activitiesList;
	private static CalorieTracker calTracker = new CalorieTracker();
	private boolean mIsReceiving;
	private static int lastActivity;
	
	Messenger mService = null;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DeviceService.MSG_SEND_POINT3D_VALUE:
            	int[] xyz = msg.getData().getIntArray(DeviceService.POINT3D);
            	new Point3D(xyz[0], xyz[1], xyz[2]);
            	//TODO call tracker, update UI,
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);

            try {
                Message msg = Message.obtain(null, DeviceService.MSG_SEND_POINT3D_VALUE);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
        }
    };
	private BroadcastReceiver mDashboardReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.i(PHAConstants.PHA_DEBUG_TAG, "Got a new piece of data from Accelerometer");
			
			double x = intent.getDoubleExtra("X", 0);
			double y = intent.getDoubleExtra("Y", 0);
			double z = intent.getDoubleExtra("Z", 0);
//			Log.i(PHAConstants.PHA_DEBUG_TAG, "x = " + x);
			String action = intent.getAction();
			
			if (x != 0 || y != 0 || z != 0) {
				Point3D dataPoint = new Point3D(x, y, z);
				int activity = calTracker.updateCaloriesWithAccelerometerData(dataPoint);
				if (lastActivity == activity) {
					updateCaloriesBurnedDB(getCaloriesBurnedSoFarToday());
				} else {
					//input new activity into list
					String today = null;
					DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
					today = dateFormat.format(new Date());
					String activityString = "Standing";
					if (lastActivity == AccelerometerDataAnalysis.RUNNING) {
						activityString= "Running";
						if (calTracker.getCalFromLastActivity() > 0) {
							addToActivityList(activityString, String.valueOf(calTracker.getCalFromLastActivity()));
							activitiesDao.insertActivity(today, "Running", calTracker.getCalFromLastActivity());
							PhaBaseAdapter phaBaseAdapter = new PhaBaseAdapter(context, activitiesList);
							activitiesListView.setAdapter(phaBaseAdapter);
						}
					} else if (lastActivity == AccelerometerDataAnalysis.WALKING) {
						activityString = "Walking";					
						if (calTracker.getCalFromLastActivity() > 0) {
							addToActivityList(activityString, String.valueOf(calTracker.getCalFromLastActivity()));
							activitiesDao.insertActivity(today, "Walking", calTracker.getCalFromLastActivity());
							activitiesListView.setAdapter(new PhaBaseAdapter(context, activitiesList));
						}
					} else if (lastActivity == AccelerometerDataAnalysis.STANDING) {
						activityString = "Standing";
						//do not add this as an activity
					}
					lastActivity = activity;
					Toast.makeText(context, "Activity added: " + activityString, Toast.LENGTH_LONG).show();
					activitiesListView.refreshDrawableState();
				}
			} else {
				Log.i(PHAConstants.PHA_DEBUG_TAG, "x was " + x);
			}
			if ("".equals(action)) {
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_activity);
		
		initializeDailyStatusDB();
		updateCaloriesBurnedUI();
		initializeUserName();
		initializeActivitesList();
//		calTracker = new CalorieTracker();
		
		if (!mIsReceiving) {
			registerReceiver(mDashboardReceiver , new IntentFilter(DeviceService.ACTION_UPDATE_DASHBOARD_WITH_POINT3D));
			mIsReceiving = true;
		}
	}
	
	private void initializeDailyStatusDB() {
		todaysCount = (TextView) findViewById(R.id.caloriesBurned);
		dailyStatusDao = new DailyStatusDAO(this);
		dailyStatusDao.open();
		activitiesDao = new ActivitiesDAO(this);
		activitiesDao.open();
		
		//check if today's status record exists
		
		//if today's record does not exists then initialize and insert one
		Integer caloriesBurned = 0;
		Integer caloriesConsumed = 0;
		
		String today = null;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		
		if(today != null){
			dailyStatusDao.insertDailyStatus(today, caloriesBurned, caloriesConsumed);
		
			updateCaloriesBurnedUI();
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Cannot get today's date");
		}
	}

	private void updateCaloriesBurnedUI() {
		Map<String, String> dailyStatus = dailyStatusDao.getDailyStatus();
		if(dailyStatus != null){
			todaysCount.setText(dailyStatus.get(DailyStatusDAO.BURNED));
//			Log.i(PHAConstants.PHA_DEBUG_TAG, "**Dashboard**: Calories Burned in database = " + dailyStatus.get(dailyStatusDao.BURNED));
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Today's record was not found.");
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mDashboardReceiver);
		super.onDestroy();
	}
	
	private void initializeUserName() {
		name = (TextView) findViewById(R.id.dashboardUserName);
		
		profileDao = new ProfileDAO(this);
		profileDao.open();

		Map<String, String> userProfile = profileDao.getUserProfile();
		if(userProfile != null){
			name.setText(userProfile.get("NAME"));
			Log.i(PHAConstants.PHA_DEBUG_TAG, "User name: " + userProfile.get("NAME") + "; Calories Burned : " + userProfile.get("WEIGHT"));
		} else {
			Log.i(PHAConstants.PHA_DEBUG_TAG, "User profile was not found.");
		}
	}

	private void initializeActivitesList() {
		activitiesList = new LinkedList<PhaListItem>();
		//newest to oldest
		
		//demo data	
		activitiesList.add(new PhaListItem("Running", "415"));
		activitiesList.add(new PhaListItem("Walking", "55") );
		activitiesList.add(new PhaListItem("Standing", "28"));
		activitiesList.add(new PhaListItem("Running", "340"));
		activitiesList.add(new PhaListItem("Walking", "50") );
		activitiesList.add(new PhaListItem("Running", "205"));
		activitiesList.add(new PhaListItem("Walking", "80") );
		activitiesList.add(new PhaListItem("Standing", "20"));
		activitiesList.add(new PhaListItem("Running", "450"));
		activitiesList.add(new PhaListItem("Walking", "76") );
		activitiesList.add(new PhaListItem("Standing", "20"));
		activitiesList.add(0, new PhaListItem("Walking", "21"));
		
		addTodaysActivityToUI();
		
		int total = 0;
		for (PhaListItem listItem: activitiesList) {
			listItem.getTitle();
			total += Integer.parseInt(listItem.getValue());
		}
		
		activitiesListView = (ListView) findViewById(R.id.activitiesListView);
		TextView t = (TextView) findViewById(R.id.thisWeeksCaloriesBurned);
		t.setText(String.valueOf(total));
		t = (TextView) findViewById(R.id.caloriesBurned);
		//t.setText(String.valueOf(todayTotal + 21));
		
		updateCaloriesBurnedDB(getCaloriesBurnedSoFarToday());
		
		PhaBaseAdapter phaBaseAdapter = new PhaBaseAdapter(this, activitiesList);
		activitiesListView.setAdapter(phaBaseAdapter);
	}
	
	private void updateCaloriesBurnedDB(int caloriesBurned) {
		String today = null;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		
		if(today != null){
			dailyStatusDao.updateCaloriesBurned(today, caloriesBurned);
			updateCaloriesBurnedUI();
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Cannot get today's date");
		}
	}

	public void addToActivityList(String activity, String calories) {
		activitiesList.add(0, new PhaListItem(activity, calories));
	}
	
	private int getCaloriesBurnedSoFarToday() {
		int activityCalories = getTotalActivityCalories();
		Calendar calendar = Calendar.getInstance();
		int caloriesSoFarBase = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		// if 1440 calories are burned per day = 1 cal/min
//		Log.i(PHAConstants.PHA_DEBUG_TAG, "Total activity energy = " + caloriesSoFarBase + " "+ activityCalories + "-" + getConsumedCaloriesToday());
		return caloriesSoFarBase + activityCalories  - getConsumedCaloriesToday();
	}

	private int getConsumedCaloriesToday() {
		try {
			return Integer.parseInt(dailyStatusDao.getDailyStatus().get(dailyStatusDao.CONSUMED));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private int getTotalActivityCalories() {
		List<Map<String, String>> list = activitiesDao.getTodaysActivities();
		int total = 0;
		for (Map<String, String> map : list) {
			total += Integer.parseInt(map.get(ActivitiesDAO.CALORIES));
		}
//		Log.i(PHAConstants.PHA_DEBUG_TAG, "Total activity energy = " + total);
		return total;
	}
	private void addTodaysActivityToUI() {
		List<Map<String, String>> list = activitiesDao.getTodaysActivities();
		for (Map<String, String> map : list) {
			addToActivityList(map.get(ActivitiesDAO.ACTIVITYNAME), map.get(ActivitiesDAO.CALORIES));
		}
	}
}
