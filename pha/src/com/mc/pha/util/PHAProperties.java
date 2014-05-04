package com.mc.pha.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * Helper class to read properties from assets/pha.conf file
 * pha.conf file contains app configuration parameters like api key, urls etc.
 * 
 * @author puneet
 */
public class PHAProperties {
	
	private Properties properties;
	
	private AssetManager assetManager;
	
	public PHAProperties(AssetManager asssetManager) {
		this.assetManager = asssetManager;
	}
	
	public String getPHAProperty(String key) {
		
		if (properties == null) {
			loadProperties();
		}
		return properties.getProperty(key);
	}
	
	private Properties loadProperties()  {
        this.properties = new Properties();
    	try {
        	InputStream inputStream = assetManager.open("pha.conf");
            properties.load(inputStream);
            Log.d(PHAConstants.PHA_DEBUG_TAG,"The properties are now loaded");
    	} catch (IOException ioe) {
    		Log.e(PHAConstants.PHA_DEBUG_TAG, "Missing properties file assets/pha.conf");
    	}
        return properties;
     }

}
