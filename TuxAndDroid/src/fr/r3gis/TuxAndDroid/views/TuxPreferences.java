package fr.r3gis.TuxAndDroid.views;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.service.ApiConnector;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

public class TuxPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_dialog);
		
		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.FILL_PARENT;
		params.width = LayoutParams.FILL_PARENT;
		
		getWindow().setAttributes(params);
		
		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		Button bt = (Button) findViewById(R.id.confirm_prefs);
		
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ApiConnector.singleton.connectToServer();
				finish();
				
			}
			
		});
		
		/*
		ListPreference locpref = (ListPreference) getPreferenceScreen().findPreference("locutor");
		String[] voicelist = ApiConnector.tuxGetVoices();
		locpref.setEntries(voicelist);
		locpref.setEntryValues(voicelist);
		*/
		updatePrefsAvailability();
		updateDescriptions();
		
		Log.i("TuxPrefs", "Preferences started");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		Log.i("TuxPrefs", "Prefs stopped, reconnect to server");
		//ApiConnector.singleton.connectToServer();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if(key.equals("use_internal")){
			updatePrefsAvailability();
			ApiConnector.singleton.connectToServer();
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
		
		updateDescriptions();
		
	}
	
	
	private String getDefaultFieldSummary(String field_name){
		String val = "";
		try {
			String keyid = R.string.class.getField(field_name+"_desc").get(null).toString();
			val = getString( Integer.parseInt(keyid) );
		} catch (SecurityException e) {
			//Nothing to do : desc is null
		} catch (NoSuchFieldException e) {
			//Nothing to do : desc is null
		} catch (IllegalArgumentException e) {
			//Nothing to do : desc is null
		} catch (IllegalAccessException e) {
			//Nothing to do : desc is null
		}
		
		return val;
	}
	
	private void setStringFieldSummary(String field_name){
		PreferenceScreen pfs = getPreferenceScreen();
		SharedPreferences sp = pfs.getSharedPreferences();
		Preference pref = pfs.findPreference(field_name);
		
		String val = sp.getString(field_name, "");
		if(val.equals("")){
			val = getDefaultFieldSummary(field_name);
		}
		pref.setSummary(val);
		
	}
	
	private void updateDescriptions(){
		setStringFieldSummary("pc_ip");
		setStringFieldSummary("pc_port");
	}
	
	private void updatePrefsAvailability(){
		PreferenceScreen pfs = getPreferenceScreen();
		SharedPreferences sp = pfs.getSharedPreferences();
		boolean disabled = sp.getBoolean("use_internal", false);
		
		
		pfs.findPreference("pc_ip").setEnabled(!disabled);
		pfs.findPreference("pc_port").setEnabled(!disabled);
		
		
	}
}
