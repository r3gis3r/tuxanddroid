package fr.r3gis.TuxAndDroid.views;

import fr.r3gis.TuxAndDroid.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstLaunch extends Activity {
	
	SharedPreferences sp;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.first_launch);
		
		TextView textContent = (TextView) findViewById(R.id.FirstLaunchText);
		textContent.setText(Html.fromHtml(getString(R.string.first_launch_text)));
		
		Button okbutton = (Button) findViewById(R.id.FirstScreenOk);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		okbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("TUXANDDROID", "Set current version pref "+TuxAndDroid.CURRENT_VERSION+ " while "+sp.getInt("last_version", 0));
				Editor editor = sp.edit();
				editor.putInt("last_version", TuxAndDroid.CURRENT_VERSION);
				editor.commit();
				finish();
			}
		});
	}
}
