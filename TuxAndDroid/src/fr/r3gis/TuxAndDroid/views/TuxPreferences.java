package fr.r3gis.TuxAndDroid.views;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.service.ApiConnector;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

public class TuxPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		
		
		/*
		ListPreference locpref = (ListPreference) getPreferenceScreen().findPreference("locutor");
		String[] voicelist = ApiConnector.tuxGetVoices();
		locpref.setEntries(voicelist);
		locpref.setEntryValues(voicelist);
		*/
		updatePrefsAvailability();
		
		
		Log.i("TuxPrefs", "Preferences started");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		Log.i("TuxPrefs", "Prefs stopped, reconnect to server");
		ApiConnector.singleton.connectToServer();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if(key.equals("use_internal")){
			updatePrefsAvailability();
		}else if(key.equals("pc_ip") || key.equals("pc_port")){
			if(key.equals("pc_port")){
				String pref = sharedPreferences.getString("pc_port", "-1");
				SharedPreferences.Editor editor = sharedPreferences.edit();
				try{
					int port = Integer.parseInt(pref);
					if(port<0){
						editor.putInt("pc_port", 54321);
					}
				}catch(NumberFormatException e){
					editor.putInt("pc_port", 54321);
				}
			}
			ApiConnector.singleton.connectToServer();
		}
		
		
	}
	
	private void updatePrefsAvailability(){
		PreferenceScreen pfs = getPreferenceScreen();
		SharedPreferences sp = pfs.getSharedPreferences();
		boolean disabled = sp.getBoolean("use_internal", false);
		
		
		pfs.findPreference("pc_ip").setEnabled(!disabled);
		pfs.findPreference("pc_port").setEnabled(!disabled);
		
		
	}
}
