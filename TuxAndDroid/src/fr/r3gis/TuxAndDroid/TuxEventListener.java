package fr.r3gis.TuxAndDroid;

import java.util.HashMap;


import android.os.Message;
import android.util.Log;


public class TuxEventListener {
	TuxAndDroid act;
	
	public TuxEventListener(TuxAndDroid activity){
		act = activity;
	}
	
	public void onAllEvent(String name, String value, Double delay) {
 		Log.i("TuxEventListener", String.format("onAllEvent : %s %s", name, value));
 		Message lmsg;
 		HashMap<String, String> obj;
		obj = new HashMap<String, String>();
		obj.put(name, value);
		lmsg = new Message();
        lmsg.obj = obj;
        lmsg.what = 0;
		act.handler.sendMessage(lmsg);
 			
 		
 	}
}
