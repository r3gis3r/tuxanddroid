package fr.r3gis.TuxAndDroid.views;

import java.util.HashMap;
import com.tuxisalive.api.TuxAPIConst;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.service.ApiConnector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class TuxAndDroid extends Activity {

	private Intent serviceIntent = null;
	private boolean connected = false;
	
	public static int CURRENT_VERSION = 1;

	public static final int PARAMS_MENU = Menu.FIRST + 1;
	public static final int ATTITUNES_MENU = Menu.FIRST + 2;
	public static final int HELP_MENU = Menu.FIRST + 3;
	public static final int QUIT_MENU = Menu.FIRST + 4;

	private GestureDetector mGestureDetector;

	private boolean is_big_screen = false;

	// Reciever for tux updates
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			onUpdateTuxStatus();
		}
	};

	private BroadcastReceiver error_receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			onErrorTuxStatus();
		}
	};
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Window transparency
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		//Detect display mode
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		int width = d.getWidth();
		//int height = d.getHeight();
		if (width == 800 && d.getOrientation() == 0) {
			// should be detect as 800x480 landscape
			// TODO: be sure it is x480
			is_big_screen = true;
		}
		
		
		setContentView(R.layout.main);
		
		// Listen for absolute layout events
		View tuxframe = findViewById(R.id.TuxMain);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		tuxframe.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (!connected) {
					return false;
				} else {
					if (mGestureDetector.onTouchEvent(event)) {
						return true;
					} else {
						return false;
					}

				}
			}
		});

		// Start to disable buttons
		setButtonsEnabled(false);
		// Attach buttons to actions they should do
		attachActionsToButtons();
		
		// Start service
		serviceIntent = new Intent(this, ApiConnector.class);
		
		//Manage application updates
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		final int last_version = sp.getInt("last_version", 0);
		if(last_version == 0){
			//First launch
			startActivity(new Intent(this, FirstLaunch.class));
		}else if(last_version != CURRENT_VERSION){
			//Changelog
		}
		
		Thread t = new Thread(){
			public void run() {
				registerReceiver(receiver, new IntentFilter(
						ApiConnector.BRODCAST_STATE_CHANGED));

				registerReceiver(error_receiver, new IntentFilter(
						ApiConnector.BRODCAST_ERROR));
				
				if(last_version != 0){
					serviceIntent.setAction(ApiConnector.AUTO_CONNECT);
				} else {
					serviceIntent.setAction(ApiConnector.JUST_START);
				}
				
				startService(serviceIntent);
				
			};
		};
		
		t.start();
		
				
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		unregisterReceiver(error_receiver);
		stopService(serviceIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PARAMS_MENU:
			startActivity(new Intent(this, TuxPreferences.class));
			return true;
		case ATTITUNES_MENU:
			startActivity(new Intent(this, AttitunesList.class));
			return true;
		case HELP_MENU:
			startActivity(new Intent(this, FirstLaunch.class));
			return true;
		case QUIT_MENU:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, PARAMS_MENU, Menu.NONE, getString(R.string.params)).setIcon(
				android.R.drawable.ic_menu_preferences);

		menu.add(Menu.NONE, ATTITUNES_MENU, Menu.NONE, "Attitunes").setIcon(
				android.R.drawable.ic_menu_gallery);
		
		menu.add(Menu.NONE, HELP_MENU, Menu.NONE, "Help").setIcon(
				android.R.drawable.ic_menu_help);
		
		menu.add(Menu.NONE, QUIT_MENU, Menu.NONE, "Quit").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
	}

	/**
	 * Attach all actions to all buttons
	 */
	private void attachActionsToButtons() {
		// TTS
		ImageButton bt = (ImageButton) findViewById(R.id.SayIt);
		bt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText textfield = (EditText) findViewById(R.id.TextToSpeach);
				ApiConnector.tuxTtsSpeak(textfield.getText().toString());
			}
		});
	}

	/**
	 * Set all buttons enable/disable mode
	 * 
	 * @param active
	 *            whether buttons are enabled
	 */
	private void setButtonsEnabled(boolean active) {
		ImageButton bt = (ImageButton) findViewById(R.id.SayIt);
		bt.setFocusable(active);
		bt.setEnabled(active);
	}


	/**
	 * Callback for intent from service that say that tux state changed Change
	 * image of the shown tuxdroid
	 */
	private void onUpdateTuxStatus() {
		HashMap<String, String> current_status = ApiConnector.singleton
				.getCurrentStatus();

		String value;

		// Connection
		value = current_status.get(TuxAPIConst.ST_NAME_RADIO_STATE);
		if (value != null) {
			Boolean active = value.equals(TuxAPIConst.SSV_ON);
			if (active) {
				setImageSrc(R.id.RadioConnection, "icon_radio_on", false);
			} else {
				setImageSrc(R.id.RadioConnection, "icon_radio_off", false);
			}
			setButtonsEnabled(active);
			connected = active;
			if(!active){
				return;
			}
		}

		// Mouth
		value = current_status.get(TuxAPIConst.ST_NAME_MOUTH_POSITION);
		if (value != null && value.equals(TuxAPIConst.SSV_OPEN)) {
			setImageSrc(R.id.Mouth, "mouth_opened");
		} else {
			setImageSrc(R.id.Mouth, "mouth_closed");
		}
		// Flippers
		value = current_status.get(TuxAPIConst.ST_NAME_FLIPPERS_POSITION);
		if (value != null && value.equals(TuxAPIConst.SSV_UP)) {
			setImageSrc(R.id.Flippers, "flippers_up");
		} else {
			setImageSrc(R.id.Flippers, "flippers_down");
		}
		
		// Spinning 
		String lvalue = current_status.get(TuxAPIConst.ST_NAME_SPIN_LEFT_MOTOR_ON);
		String rvalue = current_status.get(TuxAPIConst.ST_NAME_SPIN_RIGHT_MOTOR_ON);
		if(lvalue != null && lvalue.equals(TuxAPIConst.SSV_ON)){
			setImageSrc(R.id.Spin, "spin_left");
		}else if(rvalue != null && rvalue.equals(TuxAPIConst.SSV_ON)){
			setImageSrc(R.id.Spin, "spin_right");
		}else{
			setImageSrc(R.id.Spin, "spin_off");
		}
		
		
		// And now treat eyes states
		value = current_status.get(TuxAPIConst.ST_NAME_EYES_POSITION);
		if (value != null && value.equals(TuxAPIConst.SSV_CLOSE)) {
			setImageSrc(R.id.EyeLeft, "left_eye_closed");
			setImageSrc(R.id.EyeRight, "right_eye_closed");
		} else {
			String on_value;
			// left eye
			on_value = current_status.get(TuxAPIConst.ST_NAME_LEFT_LED);
			if (on_value != null && on_value.equals(TuxAPIConst.SSV_OFF)) {
				setImageSrc(R.id.EyeLeft, "left_eye_off");
			} else {
				setImageSrc(R.id.EyeLeft, "left_eye_on");
			}

			// Right eye
			on_value = current_status.get(TuxAPIConst.ST_NAME_RIGHT_LED);
			if (on_value != null && on_value.equals(TuxAPIConst.SSV_OFF)) {
				setImageSrc(R.id.EyeRight, "right_eye_off");
			} else {
				setImageSrc(R.id.EyeRight, "right_eye_on");
			}
		}

	}

	private void onErrorTuxStatus() {
		HashMap<String, String> current_status = ApiConnector.singleton
				.getCurrentStatus();

		String errorInfo = current_status.get("ErrorInfo");
		if (errorInfo != null) {
			Toast toast = Toast.makeText(getApplicationContext(), errorInfo,
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private void setImageSrc(int layout_id, String drawable_id) {
		setImageSrc(layout_id, drawable_id, true);
	}
	
	/**
	 * Set an imageView src param according to his id
	 * 
	 * @param layout_id
	 *            image identifier
	 * @param drawable_id
	 *            drawable resource id
	 */
	private void setImageSrc(int layout_id, String drawable_id, boolean use_suffix) {
		String suffix = "";
		if(is_big_screen && use_suffix){
			suffix = "_big";
		}
		String keyid;
		try {
			keyid = R.drawable.class.getField(drawable_id+suffix).get(null).toString();
			ImageView im = (ImageView) findViewById(layout_id);
			im.setImageResource(Integer.parseInt(keyid));
		} catch (IllegalArgumentException e) {
			Log.e("TuxAndDroid", "While getting image "+e.toString());
		} catch (SecurityException e) {
			Log.e("TuxAndDroid", "While getting image "+e.toString());
		} catch (IllegalAccessException e) {
			Log.e("TuxAndDroid", "While getting image "+e.toString());
		} catch (NoSuchFieldException e) {
			Log.e("TuxAndDroid", "While getting image "+e.toString());
		}
		
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private double scale = 1.0;
		public MyGestureListener() {
			if(!is_big_screen){
				scale = 1.745;
			}
		}
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			// Probably eyes tap
			double x = ev.getX()*scale;
			double y = ev.getY()*scale;
			
			if ( y <= 80.0 && 100.0 <= x && x <= 193.0) {
				// Right eye
				ApiConnector.tuxEyesLedToggle(-1);

			} else if (y <= 80.0 && 193.0 <= x && x <= 286.0) {
				// Left eye
				ApiConnector.tuxEyesLedToggle(1);

			}
			
			Log.d("onSingleTapUp", "--> "+x+","+y+" "+scale+" - "+ev.toString());
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// Log.d("onScroll", e1.toString());
			return true;
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			// Log.d("onDownd", ev.toString());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			double x1 = e1.getX()*scale;
			double y1 = e1.getY()*scale;
			double x2 = e2.getX()*scale;
			double y2 = e2.getY()*scale;

			double fysens = y2 - y1;
			double fxsens = x2 - x1;
			int ysens = (fysens > 0) ? 1 : -1;

			// Eyes
			if (100.0 <= x1 && x1 <= 294.0 && 
					( (y2 <= 80.0 && ysens < 0 ) || (y1 <= 80.0 && ysens > 0 )) ) {
				ApiConnector.tuxEyesMove(ysens);
				// Mouth
			} else if (100.0 <= x1 && x1 <= 286.0 && 
					( ( 80.0 <= y2 && y2 <= 142.0 && ysens < 0 ) || ( 80.0 <= y1 && y1 <= 142.0 && ysens > 0 )) ) {
				ApiConnector.tuxMouthMove(ysens);
			} else if (142.0 <= y1 && y1 <= 294.0) {
				ApiConnector.tuxFlippersMove(ysens);
			} else if(294.0 < y1){
				ApiConnector.tuxSpinDuring(fxsens / 200);
			}else{
				Log.d("NOT Cached ", "----");
				Log.d("d", e1.toString());
				Log.d("e2", e2.toString());
			}
			return true;
		}
	}
}
