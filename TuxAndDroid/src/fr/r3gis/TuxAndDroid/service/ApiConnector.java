package fr.r3gis.TuxAndDroid.service;

import java.util.HashMap;

import com.tuxisalive.api.TuxAPI;
import com.tuxisalive.api.TuxAPIConst;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApiConnector extends Service {

	//Give an external access for views 
	//TODO we should use IPC as far as I understood, but from now doc isn't really clear for me
	//So use static public objects
	public static TuxAPI tux;
	public static ApiConnector singleton=null; 
	
	//Constants for broadcasts messages
	public static final String BRODCAST_CONNECTED = "fr.r3gis.TuxAndDroid.service.connected";
	public static final String BRODCAST_STATE_CHANGED = "fr.r3gis.TuxAndDroid.service.state_changed";
	
	//Broadcast intents declared once to be reuse
	private Intent broadcast_connected = new Intent(BRODCAST_CONNECTED);
	private Intent broadcast_state_changed = new Intent(BRODCAST_STATE_CHANGED);
	
	//Connection thread to tuxdroid api
	private Thread connecting_thread;
	//Whether api well connected and started
	private Boolean is_started = false;
	
	//Synchronizable properties from tuxapi
	private String[] to_synchro = new String[]{
		TuxAPIConst.ST_NAME_EYES_POSITION,
		TuxAPIConst.ST_NAME_MOUTH_POSITION,
		TuxAPIConst.ST_NAME_FLIPPERS_POSITION
	};
	
	//Current status of the tux droid 
	//key : name of the tuxdroid part
	//value : status of this part
	//TODO: we may introduce delay but from now useless
	private HashMap<String, String> current_status = new HashMap<String, String>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		//Init hash map of current status with some defaults values
		current_status.put(TuxAPIConst.ST_NAME_RADIO_STATE, TuxAPIConst.SSV_OFF);
		current_status.put(TuxAPIConst.ST_NAME_EYES_POSITION, TuxAPIConst.SSV_OPEN);
		current_status.put(TuxAPIConst.ST_NAME_MOUTH_POSITION, TuxAPIConst.SSV_CLOSE);
		current_status.put(TuxAPIConst.ST_NAME_FLIPPERS_POSITION, TuxAPIConst.SSV_DOWN);
		singleton=this;
		// Ask a tux server connection
		connectToServer();
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//Destroy tuxapi connection if connected
		if(is_started){
    		tux.destroy();
    	}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		//Ignored since IPC not used from now
		return null;
	}
	
	/**
	 * Connect to the httptux server to provide api to control tux
	 */
	private void connectToServer(){
		
		if(connecting_thread != null){
    		connecting_thread.stop();
    	}
    	
		//Get tux server ip from preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	final String host = prefs.getString("pc_ip", "192.168.18.2");
    	
    	
    	//Start connection thread to connect the tuxdroid server
    	connecting_thread = new Thread(){
    		public void run(){
    			Log.i("Tux Thread", host);
    			tux = new TuxAPI(host, 270);
    			//Register to events (all at once)
    	    	tux.event.handler.register("all", new Object(){
    	    		@SuppressWarnings("unused")
					public void onAllEvent(String name, String value, Double delay) {
    	    	 		Log.i("TuxEventListener", String.format("onAllEvent : %s %s", name, value));
    	    	 		// Update current status
    	    	 		current_status.put(name, value);
    	    	 		// And send its done to all listeners via a broadcast message
    	    	 		sendBroadcast(broadcast_state_changed);
    	    	 		
    	    	 	}
    	    	}, "onAllEvent");
    	    	
    	    	//Connect
    	    	tux.server.autoConnect(TuxAPIConst.CLIENT_LEVEL_RESTRICTED, "Android", "andropass");
    	    	tux.server.waitConnected(10.0);
    	    	if (tux.server.getConnected()) {
    	    		is_started = true;
    	    		tux.dongle.waitConnected(10.0);
    	    		if (tux.dongle.getConnected()) {
    	    			tux.radio.waitConnected(10.0);
    	    			if (tux.radio.getConnected()) {
    	    				onRadioConnected();
    	    			}
    	    		}
    	    	}
    	    	//TODO : add elses
    		}
    	};
    	connecting_thread.start();
	}
	
	/**
	 * When radio connection is established with tux
	 * Update current tux status and say that status has changed (init changed)
	 * TODO: see if not better to not send one of connected/state_changed broadcast
	 */
	private void onRadioConnected(){
		current_status.put(TuxAPIConst.ST_NAME_RADIO_STATE, TuxAPIConst.SSV_ON);
		for(int i=0; i<to_synchro.length; i ++){
			current_status.put(to_synchro[i], tux.status.requestOne(to_synchro[i])[0].toString());
		}
		sendBroadcast(broadcast_state_changed);
		sendBroadcast(broadcast_connected);
	}
	
	public HashMap<String, String> getCurrentStatus(){
		return current_status;
	}
}
