package com.mc.sensortag.personalhealthassistant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mc.pha.dao.DailyStatusDAO;
import com.mc.pha.dao.ProfileDAO;
import com.mc.pha.util.PHAConstants;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Prompts the user to manually enter the calories consumed.
 * 
 * @author Sadaf
 *
 */
public class CaloriesInputActivity extends Activity {
	
	private DailyStatusDAO dailyStatusDao;
	//private TextView tvCaloriesConsumed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calories_input);
		
		Intent sender = getIntent();
		String extraData = sender.getExtras().getString("ComingFrom");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calories_input, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onUpdateCalories(View view){
		TextView tvCaloriesConsumed = (TextView) findViewById(R.id.caloriesConsumed);
		EditText tvInputCalories = (EditText) findViewById(R.id.inputCalories);
		
		//get today's calories consumption from DB
		dailyStatusDao = new DailyStatusDAO(this);
		dailyStatusDao.open();
		
		int caloriesConsumed = 0;
		Map<String, String> dailyStatus = dailyStatusDao.getDailyStatus();
		if(dailyStatus != null){
			caloriesConsumed = Integer.parseInt(dailyStatus.get(dailyStatusDao.CONSUMED));
			Log.i(PHAConstants.PHA_DEBUG_TAG, "Calories Consumed : " + caloriesConsumed);
			//Log.i(PHAConstants.PHA_DEBUG_TAG, "User name: " + userProfile.get("NAME") + "; Calories Burned : " + userProfile.get("WEIGHT"));
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Today's record was not found.");
		}
		
		int inputCalories = Integer.parseInt(tvInputCalories.getText().toString());
		Log.i(PHAConstants.PHA_DEBUG_TAG, "Input Calories : " + inputCalories);
		
		int totalCaloriesConsumed =  caloriesConsumed + inputCalories;	
		Log.i(PHAConstants.PHA_DEBUG_TAG, "Total Calories Consumed : " + totalCaloriesConsumed);
		
		//update the calories consumed value in the db
		updateDailyStatusDB(totalCaloriesConsumed, inputCalories);
		
		//update the calories consumed UI
		//tvCaloriesConsumed.setText(totalCaloriesConsumed);
		Intent intent = new Intent();
		intent.putExtra("ComingFrom", totalCaloriesConsumed);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void updateDailyStatusDB(int caloriesConsumed, int consumedNow) {
		String today = null;
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		today = dateFormat.format(new Date());
		
		if(today != null){
			dailyStatusDao.updateCaloriesConsumed(today, caloriesConsumed, consumedNow);
		} else {
			Log.e(PHAConstants.PHA_DEBUG_TAG, "Cannot get today's date");
		}
	}
}
