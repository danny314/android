package com.mc.sensortag.personalhealthassistant;

import com.mc.pha.util.PHAConstants;
import com.mc.sensortag.personalhealthassistant.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ScanActivity extends Activity {
	
	Context context;
	View view;
	
	private TextView mStatus;
	private MainActivity mActivity = null;
	
	public ScanActivity(Context context, View view){
		this.context = context;
		this.view = view;
		
		//mStatus = (TextView) view.findViewById(R.id.status);
	}
	
	void setStatus(String txt) {
		//.setText(txt);
        Log.i(PHAConstants.PHA_DEBUG_TAG, txt );
	}

	void setStatus(String txt, int duration) {
		setStatus(txt);
	}
	
	void updateGui(boolean scanning) {

	    if (scanning) {
	      Log.i(PHAConstants.PHA_DEBUG_TAG, "Scanning..." );
	      //mStatus.setText("Scanning...");
	      mActivity.updateGuiState();
	    }
	  }
}
