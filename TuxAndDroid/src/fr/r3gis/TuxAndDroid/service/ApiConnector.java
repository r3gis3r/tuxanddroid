package fr.r3gis.TuxAndDroid.service;

import java.util.HashMap;
import java.util.List;
import com.tuxisalive.api.TuxAPI;
import com.tuxisalive.api.TuxAPIConst;
import com.tuxisalive.api.TuxAPILedBase;
import fr.r3gis.TuxAndDroid.R;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ApiConnector extends Service {

	// Give an external access for views
	// TODO we should use IPC as far as I understood, but from now doc isn't
	// really clear for me
	// So use static public objects
	private static TuxAPI tux;
	public static ApiConnector singleton = null;

	// Constants for broadcasts messages
	public static final String BRODCAST_CONNECTED = "fr.r3gis.TuxAndDroid.service.connected";
	public static final String BRODCAST_STATE_CHANGED = "fr.r3gis.TuxAndDroid.service.state_changed";
	public static final String BRODCAST_ERROR = "fr.r3gis.TuxAndDroid.service.connect_error";

	// Broadcast intents declared once to be reuse
	private Intent broadcast_connected = new Intent(BRODCAST_CONNECTED);
	private Intent broadcast_connect_error = new Intent(BRODCAST_ERROR);
	private Intent broadcast_state_changed = new Intent(BRODCAST_STATE_CHANGED);

	// Connection thread to tuxdroid api
	private Thread connecting_thread;
	// Whether api well connected and started
	private boolean is_started = false;
	// Whether api well connected and action could be performed
	private boolean is_connected = false;

	private boolean use_emulator = false;

	// Synchronizable properties from tuxapi
	private String[] to_synchro = new String[] {
			TuxAPIConst.ST_NAME_EYES_POSITION,
			TuxAPIConst.ST_NAME_MOUTH_POSITION,
			TuxAPIConst.ST_NAME_FLIPPERS_POSITION,
			TuxAPIConst.ST_NAME_RIGHT_LED,
			TuxAPIConst.ST_NAME_LEFT_LED
			};

	// Current status of the tux droid
	// key : name of the tuxdroid part
	// value : status of this part
	// TODO: we may introduce delay but from now useless
	private HashMap<String, String> current_status = new HashMap<String, String>();

	@Override
	public void onCreate() {
		super.onCreate();
		// Init hash map of current status with some defaults values
		current_status
				.put(TuxAPIConst.ST_NAME_RADIO_STATE, TuxAPIConst.SSV_OFF);
		current_status.put(TuxAPIConst.ST_NAME_EYES_POSITION,
				TuxAPIConst.SSV_OPEN);
		current_status.put(TuxAPIConst.ST_NAME_MOUTH_POSITION,
				TuxAPIConst.SSV_CLOSE);
		current_status.put(TuxAPIConst.ST_NAME_FLIPPERS_POSITION,
				TuxAPIConst.SSV_DOWN);
		current_status.put(TuxAPIConst.ST_NAME_RIGHT_LED,
				TuxAPIConst.SSV_OFF);
		current_status.put(TuxAPIConst.ST_NAME_LEFT_LED,
				TuxAPIConst.SSV_OFF);
		singleton = this;
		// Ask a tux server connection
		connectToServer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Destroy tuxapi connection if connected
		if (is_started) {
			tux.destroy();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Ignored since IPC not used from now
		return null;
	}

	/**
	 * Connect to the httptux server to provide api to control tux
	 */
	public void connectToServer() {

		if (connecting_thread != null) {
			connecting_thread.stop();
		}

		// Get tux server ip from preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		use_emulator = prefs.getBoolean("use_internal", false);

		if (!use_emulator) {
			final String host = prefs.getString("pc_ip", "");
			String pport = prefs.getString("pc_port", "0");
			final int port = Integer.parseInt(pport);

			// Start connection thread to connect the tuxdroid server
			connecting_thread = new Thread() {
				public void run() {
					Log.i("TUXAPI Thread", host + ":" + port);
					tux = new TuxAPI(host, port);
					// Register to events (all at once)
					tux.event.handler.register("all", new Object() {
						@SuppressWarnings("unused")
						public void onAllEvent(String name, String value,
								Double delay) {
							// Log.i("TuxEventListener",
							// String.format("onAllEvent : %s %s", name,
							// value));
							// Update current status
							current_status.put(name, value);
							// And send its done to all listeners via a
							// broadcast message
							// Skip battery state to improve perfs
							if (!name.equals("battery_state")) {
								sendBroadcast(broadcast_state_changed);
							}

						}
					}, "onAllEvent");

					// Connect
					tux.server.autoConnect(TuxAPIConst.CLIENT_LEVEL_RESTRICTED,
							"Android", "andropass");
					tux.server.waitConnected(10.0);
					if (tux.server.getConnected()) {
						is_started = true;
						tux.dongle.waitConnected(10.0);
						if (tux.dongle.getConnected()) {
							tux.radio.waitConnected(10.0);
							if (tux.radio.getConnected()) {
								onRadioConnected();
							} else {
								Log.i("TUXAPI", "No tux");
								onRadioFailed(getString(R.string.tux_not_found));
							}
						} else {
							Log.i("TUXAPI", "No dongle");
							onRadioFailed(getString(R.string.dongle_not_connected));
						}
					} else {
						Log.i("TUXAPI", "Server not raised");
						onRadioFailed(getString(R.string.cant_contact_server));

					}
				}
			};

			if (host.equals("") || port == -1) {
				Log.w("TUXAPI", "No configured server");
				CharSequence text = getString(R.string.please_configure);
				Toast toast = Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_LONG);
				toast.show();

			} else {
				Log.i("TUXAPI", "Start thread");
				connecting_thread.start();
			}
		} else {
			
			onRadioConnected();
		}
	}

	/**
	 * When radio connection is established with tux Update current tux status
	 * and say that status has changed (init changed) TODO: see if not better to
	 * not send one of connected/state_changed broadcast
	 */
	private void onRadioConnected() {
		current_status.put(TuxAPIConst.ST_NAME_RADIO_STATE, TuxAPIConst.SSV_ON);
		if(!use_emulator){
			for (int i = 0; i < to_synchro.length; i++) {
				try{
				current_status.put(to_synchro[i], tux.status
						.requestOne(to_synchro[i])[0].toString());
				}catch(NullPointerException e){
					Log.w("TUXAPI", "No value for "+to_synchro[i]);
				}
			}
			
			//Set locutor
			List<String> voices = tux.tts.getVoices();
			
			
			
			Log.d("TUXAPI", "Set voice to "+voices.get(0));
			tux.tts.setLocutor(voices.get(0));
		}
		
		is_connected = true;
		sendBroadcast(broadcast_state_changed);
		sendBroadcast(broadcast_connected);
	}

	private void onRadioFailed(String errorString) {
		current_status.put("ErrorInfo", errorString);
		sendBroadcast(broadcast_connect_error);

	}

	public HashMap<String, String> getCurrentStatus() {
		return current_status;
	}

	/**
	 * Start to play a given attitune
	 * @param url url of attitune to be played
	 */
	static public void tuxPlayAttitune(String url) {
		if(!singleton.is_connected){
			String errorString = singleton.getString(R.string.you_are_not_connected);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
			return; 
		}
		
		if(!singleton.use_emulator){
			tux.attitune.load(url);
			tux.attitune.play();
		}else{
			String errorString = singleton.getString(R.string.unimplemented_yet);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
		}
	}

	/**
	 * Stop currently playing attitune
	 */
	static public void tuxStopAttitune() {
		if(!singleton.is_connected){
			String errorString = singleton.getString(R.string.you_are_not_connected);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
			return; 
		}
		
		if(!singleton.use_emulator){
			tux.attitune.stop();
		}else{
			String errorString = singleton.getString(R.string.unimplemented_yet);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
		}
	}
	
	/**
	 * Make tuxdroid speak something
	 * @param tosay what has to be spoken
	 */
	static public void tuxTtsSpeak(String tosay) {
		if(!singleton.use_emulator){
			tux.tts.speakAsync(tosay);
		}else{
			String errorString = singleton.getString(R.string.unimplemented_yet);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
		}
	}
	
	/**
	 * Toogle tux eyes led state
	 * @param side -1 = right, 1 = left
	 */
	static public void tuxEyesLedToggle(int side) {
		
		if(!singleton.is_connected){
			String errorString = singleton.getString(R.string.you_are_not_connected);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
			return; 
		}
		
		String key;
		TuxAPILedBase myled;
		if(side <0){
			key = TuxAPIConst.ST_NAME_RIGHT_LED;
		}else{
			key = TuxAPIConst.ST_NAME_LEFT_LED;
		}
		
		
		if(!singleton.use_emulator){
			
			if(side <0){
				myled = tux.led.right;
			}else{
				myled = tux.led.left;
			}
			
			if (singleton.current_status.get(key).equals(TuxAPIConst.SSV_OFF)) {
				myled.on();
			} else {
				myled.off();
			}
		}else{
			if (singleton.current_status.get(key).equals(
					TuxAPIConst.SSV_OFF)) {
				singleton.current_status.put(key, TuxAPIConst.SSV_ON);
			} else {
				singleton.current_status.put(key, TuxAPIConst.SSV_OFF);
			}
			singleton.sendBroadcast(singleton.broadcast_state_changed);
		}
	}
	
	
	
	static public void tuxMouthMove(int sens) {
		if(!singleton.is_connected){
			String errorString = singleton.getString(R.string.you_are_not_connected);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
			return; 
		}
		
		if(!singleton.use_emulator){
			if (sens > 0) {
				tux.mouth.open();
			} else {
				tux.mouth.close();
			}
		}else{
			
			if (sens > 0) {
				singleton.current_status.put(TuxAPIConst.ST_NAME_MOUTH_POSITION, TuxAPIConst.SSV_OPEN);
			} else {
				singleton.current_status.put(TuxAPIConst.ST_NAME_MOUTH_POSITION, TuxAPIConst.SSV_CLOSE);
			}
			singleton.sendBroadcast(singleton.broadcast_state_changed);
		}
	}
	
	
	static public void tuxEyesMove(int sens) {
		if(!singleton.is_connected){
			String errorString = singleton.getString(R.string.you_are_not_connected);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
			return; 
		}
		
		if(!singleton.use_emulator){
			if (sens < 0) {
				tux.eyes.open();
			} else {
				tux.eyes.close();
			}
		}else{
			
			if (sens < 0) {
				singleton.current_status.put(TuxAPIConst.ST_NAME_EYES_POSITION, TuxAPIConst.SSV_OPEN);
			} else {
				singleton.current_status.put(TuxAPIConst.ST_NAME_EYES_POSITION, TuxAPIConst.SSV_CLOSE);
			}
			singleton.sendBroadcast(singleton.broadcast_state_changed);
		}
	}
	
	static public void tuxFlippersMove(int sens) {
		if(!singleton.is_connected){
			String errorString = singleton.getString(R.string.you_are_not_connected);
			singleton.current_status.put("ErrorInfo", errorString);
			singleton.sendBroadcast(singleton.broadcast_connect_error);
			return; 
		}
		
		if(!singleton.use_emulator){
			if (sens > 0) {
				tux.flippers.down();
			} else {
				tux.flippers.up();
			}
		}else{
			
			if (sens > 0) {
				singleton.current_status.put(TuxAPIConst.ST_NAME_FLIPPERS_POSITION, TuxAPIConst.SSV_DOWN);
			} else {
				singleton.current_status.put(TuxAPIConst.ST_NAME_FLIPPERS_POSITION, TuxAPIConst.SSV_UP);
			}
			singleton.sendBroadcast(singleton.broadcast_state_changed);
		}
	}
	
	static public String[] tuxGetVoices(){
		if(!singleton.use_emulator && singleton.is_connected && singleton.is_started){
			List<String> voices = tux.tts.getVoices();
			String[] mvoices = new String[voices.size()];
			int i=0;
			for(String voice : mvoices){
				mvoices[i] = voice;
				i++;
			}
			return mvoices;
		}
		return null;
	}
	

}
