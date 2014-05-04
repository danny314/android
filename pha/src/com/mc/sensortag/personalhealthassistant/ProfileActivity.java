package com.mc.sensortag.personalhealthassistant;

import java.util.Map;

import com.mc.pha.dao.ProfileDAO;
import com.mc.pha.util.PHAConstants;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Displays user profile to store user name and weight
 * 
 * @author Sadaf
 *
 */
 public class ProfileActivity extends Activity {
	
	private ProfileDAO profileDao;

	private EditText name;
	private EditText weight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		name = (EditText) findViewById(R.id.userName);
		weight = (EditText) findViewById(R.id.weight);
		
		profileDao = new ProfileDAO(this);
		profileDao.open();
		
		updateProfileUI();
	}
	
	public void onSaveProfile(View view){
		profileDao.saveProfile(name.getText().toString(), Integer.parseInt(weight.getText().toString()));
		
		updateProfileUI();
	}

	private void updateProfileUI() {
		Map<String, String> userProfile = profileDao.getUserProfile();
		if(userProfile != null){
			name.setText(userProfile.get("NAME"));
			weight.setText(userProfile.get("WEIGHT"));
			Log.i(PHAConstants.PHA_DEBUG_TAG, "User name: " + userProfile.get("NAME") + "; Calories Burned : " + userProfile.get("WEIGHT"));
		} else {
			Log.i(PHAConstants.PHA_DEBUG_TAG, "User profile not was not found.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.profile, menu);
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
}
