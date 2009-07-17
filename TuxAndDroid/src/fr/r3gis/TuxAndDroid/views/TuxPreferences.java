package fr.r3gis.TuxAndDroid.views;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.service.ApiConnector;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class TuxPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Log.i("TuxPrefs", "Preferences started");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		Log.i("TuxPrefs", "Prefs stopped, reconnect to server");
		ApiConnector.singleton.connectToServer();
	}
}
