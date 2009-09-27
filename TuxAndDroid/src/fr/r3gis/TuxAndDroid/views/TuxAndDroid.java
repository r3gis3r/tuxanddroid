package fr.r3gis.TuxAndDroid.views;

import java.util.HashMap;
import java.util.Map.Entry;

import com.tuxisalive.api.TuxAPI;
import com.tuxisalive.api.TuxAPIConst;

import fr.r3gis.TuxAndDroid.R;
import fr.r3gis.TuxAndDroid.service.ApiConnector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TuxAndDroid extends Activity {

	private Intent serviceIntent = null;
	private boolean connected = false;

	public static final int PARAMS_MENU = Menu.FIRST + 1;
	public static final int ATTITUNES_MENU = Menu.FIRST + 2;

	private GestureDetector mGestureDetector;

	// Reciever for tux updates
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			onUpdateTuxStatus();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.main);

		// Listen for absolute layout events
		View tuxframe = findViewById(R.id.TuxMain);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		tuxframe.setOnTouchListener(new View.OnTouchListener() {

			@Override
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
		startService(serviceIntent);
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
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(
				ApiConnector.BRODCAST_STATE_CHANGED));
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterReceiver(receiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Should we destroy it?
		// For next steps surely not
		stopService(serviceIntent);
	}

	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, PARAMS_MENU, Menu.NONE, "Params").setIcon(
				android.R.drawable.ic_menu_preferences);

		menu.add(Menu.NONE, ATTITUNES_MENU, Menu.NONE, "Attitunes").setIcon(
				android.R.drawable.ic_menu_gallery);
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
				ApiConnector.tux.tts.speak(textfield.getText().toString());
			}
		});
	}

	/**
	 * Unique attach a button (according to its id to a click listener
	 * 
	 * @param layout_id
	 *            button identifier
	 * @param cl
	 *            OnClickListener instance to be fired on click
	 */
	/*
	private void attachActionToButton(int layout_id, View.OnClickListener cl) {
		ImageButton bt = (ImageButton) findViewById(layout_id);
		bt.setOnClickListener(cl);
	}
	*/

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
	 * Set a button enable/disabled according to his id
	 * 
	 * @param layout_id
	 *            button identifier
	 * @param active
	 *            whether the button should be enable/disabled
	 */
	/*
	private void setButtonEnable(int layout_id, boolean active) {
		ImageButton bt;
		bt = (ImageButton) findViewById(layout_id);
		bt.setFocusable(active);
		bt.setEnabled(active);
	}
	*/

	/**
	 * Callback for intent from service that say that tux state changed Change
	 * image of the shown tuxdroid
	 */
	private void onUpdateTuxStatus() {
		HashMap<String, String> current_status = ApiConnector.singleton
				.getCurrentStatus();

		String value;
		
		//Connection
		value = current_status.get(TuxAPIConst.ST_NAME_RADIO_STATE);
		if(value != null){
			Boolean active = value.equals(TuxAPIConst.SSV_ON);
	     	if(active){
	     		setImageSrc(R.id.RadioConnection, R.drawable.icon_radio_on);
	     	}else{
	     		setImageSrc(R.id.RadioConnection, R.drawable.icon_radio_off);
	     	}
	     	setButtonsEnabled(active);
	     	connected = active;
		}
		
		// Mouth
		value = current_status.get(TuxAPIConst.ST_NAME_MOUTH_POSITION);
		if (value != null && value.equals(TuxAPIConst.SSV_OPEN)) {
			setImageSrc(R.id.Mouth, R.drawable.mouth_opened);
		} else {
			setImageSrc(R.id.Mouth, R.drawable.mouth_closed);
		}
		// Flippers
		value = current_status.get(TuxAPIConst.ST_NAME_FLIPPERS_POSITION);
		if (value != null && value.equals(TuxAPIConst.SSV_UP)) {
			setImageSrc(R.id.Flippers, R.drawable.flippers_up);
		} else {
			setImageSrc(R.id.Flippers, R.drawable.flippers_down);
		}

		// And now treat eyes states
		value = current_status.get(TuxAPIConst.ST_NAME_EYES_POSITION);
		if (value!=null && value.equals(TuxAPIConst.SSV_CLOSE)) {
			setImageSrc(R.id.EyeLeft, R.drawable.left_eye_closed);
			setImageSrc(R.id.EyeRight, R.drawable.right_eye_closed);
		} else {
			String on_value;
			//left eye
			on_value = current_status.get(TuxAPIConst.ST_NAME_LEFT_LED);
			if (on_value != null && on_value.equals(TuxAPIConst.SSV_OFF)) {
				setImageSrc(R.id.EyeLeft, R.drawable.left_eye_off);
			} else {
				setImageSrc(R.id.EyeLeft, R.drawable.left_eye_on);
			}
			
			//Right eye
			on_value = current_status.get(TuxAPIConst.ST_NAME_RIGHT_LED);
			if (on_value != null && on_value.equals(TuxAPIConst.SSV_OFF)) {
				setImageSrc(R.id.EyeRight, R.drawable.right_eye_off);
			} else {
				setImageSrc(R.id.EyeRight, R.drawable.right_eye_on);
			}
		}

	}

	/**
	 * Set an imageView src param according to his id
	 * 
	 * @param layout_id
	 *            image identifier
	 * @param drawable_id
	 *            drawable resource id
	 */
	private void setImageSrc(int layout_id, int drawable_id) {
		ImageView im = (ImageView) findViewById(layout_id);
		im.setImageResource(drawable_id);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			// Probably eyes tap
			float x = ev.getX();
			float y = ev.getY();
			if (25.0 <= y && y <= 50.0 && 95 <= x && x <= 120.0) {
				// Right eye
				if (ApiConnector.tux.led.right.getState().equals(
						TuxAPIConst.SSV_OFF)) {
					ApiConnector.tux.led.right.on();
				} else {
					ApiConnector.tux.led.right.off();
				}

			}else if (25.0 <= y && y <= 50.0 && 125.0 <= x && x <= 145.0) {
				// Left eye
				if (ApiConnector.tux.led.left.getState().equals(
						TuxAPIConst.SSV_OFF)) {
					ApiConnector.tux.led.left.on();
				} else {
					ApiConnector.tux.led.left.off();
				}

			}
			Log.d("onSingleTapUp", ev.toString());
			return true;
		}
/*
		@Override
		public void onShowPress(MotionEvent ev) {
			Log.d("onShowPress", ev.toString());
		}

		@Override
		public void onLongPress(MotionEvent ev) {
			Log.d("onLongPress", ev.toString());
		}
*/
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			//Log.d("onScroll", e1.toString());
			return true;
		}

		@Override
		public boolean onDown(MotionEvent ev) {
	//		Log.d("onDownd", ev.toString());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x1 = e1.getX();
			float y1 = e1.getY();
			float x2 = e2.getX();
			float y2 = e2.getY();
			
			//Eyes -> top-down
			if( 80 <= x1 && x1 <= 160 &&
				0.0 <= y1 && y1 <= 35.0 &&
				80 <= x2 && x2 <= 160 &&
				25.0 <= y2 && y2 <= 80.0 ){
				ApiConnector.tux.eyes.close();
			//Eyes -> down-top
			}else if(80 <= x1 && x1 <= 160 &&
					25.0 <= y1 && y1 <= 80.0 &&
					80 <= x2 && x2 <= 160 &&
					0.0 <= y2 && y2 <= 35.0){
				ApiConnector.tux.eyes.open();
			//Mouth -> top-down
			}else if(100.0 <= x1 && x1 <= 150.0 &&
					100.0 <=x2 && x2 <= 150.0 &&
					40.0 <= y1 && y1 <= 60.0 &&
					70.0 <= y2 && y2 <= 90.0){
				ApiConnector.tux.mouth.open();
			}else if(100.0 <= x1 && x1 <= 150.0 &&
					100.0 <=x2 && x2 <= 150.0 &&
					70.0 <= y1 && y1 <= 90.0 &&
					40.0 <= y2 && y2 <= 60.0
					){
				ApiConnector.tux.mouth.close();
			}else if(90.0 <= y1 && y1 <= 140.0 &&
					140.0 <= y2 && y2 <= 220.0
					){
				ApiConnector.tux.flippers.down();
			}else if(90.0 <= y2 && y2 <= 140.0 &&
					140.0 <= y1 && y1 <= 220.0
					){
				ApiConnector.tux.flippers.up();
			}else{
				Log.d("NOT Cached ", "----"); 
				Log.d("d", e1.toString());
				Log.d("e2", e2.toString());
			}
			return true;
		}
	}
}
