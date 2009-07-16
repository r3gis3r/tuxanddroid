package fr.r3gis.TuxAndDroid;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.tuxisalive.api.TuxAPI;
import com.tuxisalive.api.TuxAPIConst;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
//import android.view.ContextMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
//import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TuxAndDroid extends Activity {
	TuxAPI tux;
	Boolean is_started = false;
	Thread connecting_thread;
	
	public static final int PARAMS_MENU = Menu.FIRST+1;
	
	Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // process incoming messages here
        	HashMap<?, ?> obj;
        	obj = (HashMap<?, ?>) msg.obj;
        	
        	Set<?> keys = obj.keySet();
            Iterator<?> It = keys.iterator();
            while (It.hasNext()) {
                String name = (String)(It.next());
                String value = (String)(obj.get(name));
                
                //Radio state
                if(name.equals(TuxAPIConst.ST_NAME_RADIO_STATE)){
                	Boolean active = value.equals(TuxAPIConst.SSV_ON);
                	if(active){
                		setImageSrc(R.id.RadioConnection, R.drawable.icon_radio_on);
                	}else{
                		setImageSrc(R.id.RadioConnection, R.drawable.icon_radio_off);
                	}
                	setButtonsEnabled(active);
                // Eyes
                }else if(name.equals(TuxAPIConst.ST_NAME_EYES_POSITION)){
                	if(value.equals(TuxAPIConst.SSV_OPEN)){
                		setImageSrc(R.id.EyeLeft, R.drawable.left_eye_on);
                		setImageSrc(R.id.EyeRight, R.drawable.right_eye_on);
                	}else if(value.equals(TuxAPIConst.SSV_CLOSE)){
                		setImageSrc(R.id.EyeLeft, R.drawable.left_eye_off);
                		setImageSrc(R.id.EyeRight, R.drawable.right_eye_off);
                	}
                // Mouth
                }else if(name.equals(TuxAPIConst.ST_NAME_MOUTH_POSITION)){
                	if(value.equals(TuxAPIConst.SSV_OPEN)){
                		setImageSrc(R.id.Mouth, R.drawable.mouth_opened);
                	}else if(value.equals(TuxAPIConst.SSV_CLOSE)){
                		setImageSrc(R.id.Mouth, R.drawable.mouth_closed);
                	}
                // Flippers
                }else if(name.equals(TuxAPIConst.ST_NAME_FLIPPERS_POSITION)){
                	if(value.equals(TuxAPIConst.SSV_UP)){
                		setImageSrc(R.id.Flippers, R.drawable.flippers_up);
                	}else if(value.equals(TuxAPIConst.SSV_DOWN)){
                		setImageSrc(R.id.Flippers, R.drawable.flippers_down);
                	}
                }
            }
            super.handleMessage(msg);
        }
    };
 
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Start to disable buttons
        setButtonsEnabled(false);
        //Attach buttons to actions they should do
        attachActionsToButtons();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	populateMenu(menu);
    	super.onCreateContextMenu(menu, v, menuInfo);
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
		}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onResume(){
    	if(connecting_thread != null){
    		connecting_thread.stop();
    	}
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	final String host = prefs.getString("pc_ip", "192.168.18.2");
    	
    	//Start connection thread to connect the tuxdroid server
    	connecting_thread = new Thread(){
    		public void run(){
    			Log.i("Tux Thread", host);
    			tux = new TuxAPI(host, 270);
    	    	tux.event.handler.register("all", new TuxEventListener(TuxAndDroid.this), "onAllEvent");
    	    	tux.server.autoConnect(TuxAPIConst.CLIENT_LEVEL_RESTRICTED, "Android", "andropass");
    	    	tux.server.waitConnected(10.0);
    	    	if (tux.server.getConnected()) {
    	    		is_started = true;
    	    		tux.dongle.waitConnected(10.0);
    	    		if (tux.dongle.getConnected()) {
    	    			tux.radio.waitConnected(10.0);
    	    			if (tux.radio.getConnected()) {
    	    				Message lmsg;
    	    		 		HashMap<String, String> obj;
    	    				obj = new HashMap<String, String>();
    	    				obj.put(TuxAPIConst.ST_NAME_RADIO_STATE, TuxAPIConst.SSV_ON);
    	    				lmsg = new Message();
    	    		        lmsg.obj = obj;
    	    		        lmsg.what = 0;
    	    		        TuxAndDroid.this.handler.sendMessage(lmsg);
    	    			}
    	    		}
    	    	}
    		}
    	};
    	connecting_thread.start();
    	super.onResume();
    }
    
    @Override
    public void onStop(){
    	if(is_started){
    		tux.destroy();
    	}
    	super.onStop();
    }
    
    
    private void populateMenu(Menu menu){
    	menu.add(Menu.NONE, PARAMS_MENU, Menu.NONE, "Params")
    		.setIcon(android.R.drawable.ic_menu_preferences);
    }
    
    
    /**
     * Attach all actions to all buttons
     */
    private void attachActionsToButtons(){
    	
    	//Open eyes
    	attachActionToButton(R.id.OpenEyes, new View.OnClickListener() {
            public void onClick(View v) { tux.eyes.open(); }
        });
    	
        //Close eyes
        attachActionToButton(R.id.CloseEyes, new View.OnClickListener() {
            public void onClick(View v) { tux.eyes.close(); }
        });
        
        //Open mouth
        attachActionToButton(R.id.OpenMouth, new View.OnClickListener() {
            public void onClick(View v) { tux.mouth.open(); }
        });
        
        //Close mouth
        attachActionToButton(R.id.CloseMouth, new View.OnClickListener() {
            public void onClick(View v) { tux.mouth.close(); }
        });
        
        //Open flip
        attachActionToButton(R.id.OpenFlip, new View.OnClickListener() {
            public void onClick(View v) { tux.flippers.down(); }
        });
        
        //Close flip
        attachActionToButton(R.id.CloseFlip, new View.OnClickListener() {
            public void onClick(View v) { tux.flippers.up(); }
        });
        
        //TTS
        Button bt = (Button) findViewById(R.id.SayIt);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		EditText textfield = (EditText) findViewById(R.id.TextToSpeach);
        		tux.tts.speak(textfield.getText().toString());
            }
        });
    }
    
    /**
     * Unique attach a button (according to its id to a click listener
     * @param layout_id button identifier
     * @param cl OnClickListener instance to be fired on click
     */
    private void attachActionToButton(int layout_id, View.OnClickListener cl){
    	ImageButton bt = (ImageButton) findViewById(layout_id);
    	bt.setOnClickListener(cl);
    }
    
    private void setButtonsEnabled(boolean active){
    	setButtonEnable(R.id.OpenEyes, active);
    	setButtonEnable(R.id.CloseEyes, active);
    	setButtonEnable(R.id.OpenMouth, active);
    	setButtonEnable(R.id.CloseMouth, active);
    	setButtonEnable(R.id.OpenFlip, active);
    	setButtonEnable(R.id.CloseFlip, active);
    	
    	Button bt = (Button) findViewById(R.id.SayIt);
    	bt.setFocusable(active);
    	bt.setEnabled(active);
    }
    
    /**
     * Set a button enable/disabled according to his id
     * @param layout_id button identifier
     * @param active whether the button should be enable/disabled
     */
    private void setButtonEnable(int layout_id, boolean active){
    	ImageButton bt;
    	bt = (ImageButton) findViewById(layout_id);
    	bt.setFocusable(active);
    	bt.setEnabled(active);
    }
    
    /**
     * Set an imageView src param according to his id
     * @param layout_id image identifier
     * @param drawable_id drawable resource id
     */
    private void setImageSrc(int layout_id, int drawable_id){
    	ImageView im = (ImageView) findViewById(layout_id);
    	im.setImageResource(drawable_id);
    }
    
}
