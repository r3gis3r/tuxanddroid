package fr.r3gis.TuxAndDroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TuxPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
