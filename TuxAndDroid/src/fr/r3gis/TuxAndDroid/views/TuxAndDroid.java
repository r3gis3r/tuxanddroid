package fr.r3gis.TuxAndDroid.views;

import java.util.HashMap;
import java.util.Map.Entry;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TuxAndDroid extends Activity {
	
	private Intent serviceIntent=null;
	
	public static final int PARAMS_MENU = Menu.FIRST+1;
	public static final int ATTITUNES_MENU = Menu.FIRST+2;
	
	//Reciever for tux updates
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        
        setContentView(R.layout.main);
        
        
        
        //Start to disable buttons
        setButtonsEnabled(false);
        //Attach buttons to actions they should do
        attachActionsToButtons();
        
        //Start service
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
		}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	registerReceiver(receiver, new IntentFilter(ApiConnector.BRODCAST_STATE_CHANGED));
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	unregisterReceiver(receiver);
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
    	//Should we destroy it?
    	//For next steps surely not
    	stopService(serviceIntent);
    }
    
    private void populateMenu(Menu menu){
    	menu.add(Menu.NONE, PARAMS_MENU, Menu.NONE, "Params")
    		.setIcon(android.R.drawable.ic_menu_preferences);
    	
    	menu.add(Menu.NONE, ATTITUNES_MENU, Menu.NONE, "Attitunes")
    		.setIcon(android.R.drawable.ic_menu_gallery);
    }
    
    
    /**
     * Attach all actions to all buttons
     */
    private void attachActionsToButtons(){
    	
    	//Open eyes
    	attachActionToButton(R.id.OpenEyes, new View.OnClickListener() {
            public void onClick(View v) { ApiConnector.tux.eyes.open(); }
        });
    	
        //Close eyes
        attachActionToButton(R.id.CloseEyes, new View.OnClickListener() {
            public void onClick(View v) { ApiConnector.tux.eyes.close(); }
        });
        
        //Open mouth
        attachActionToButton(R.id.OpenMouth, new View.OnClickListener() {
            public void onClick(View v) { ApiConnector.tux.mouth.open(); }
        });
        
        //Close mouth
        attachActionToButton(R.id.CloseMouth, new View.OnClickListener() {
            public void onClick(View v) { ApiConnector.tux.mouth.close(); }
        });
        
        //Open flip
        attachActionToButton(R.id.OpenFlip, new View.OnClickListener() {
            public void onClick(View v) { ApiConnector.tux.flippers.down(); }
        });
        
        //Close flip
        attachActionToButton(R.id.CloseFlip, new View.OnClickListener() {
            public void onClick(View v) { ApiConnector.tux.flippers.up(); }
        });
        
        //TTS
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
     * @param layout_id button identifier
     * @param cl OnClickListener instance to be fired on click
     */
    private void attachActionToButton(int layout_id, View.OnClickListener cl){
    	ImageButton bt = (ImageButton) findViewById(layout_id);
    	bt.setOnClickListener(cl);
    }
    
    /**
     * Set all buttons enable/disable mode
     * @param active whether buttons are enabled
     */
    private void setButtonsEnabled(boolean active){
    	setButtonEnable(R.id.OpenEyes, active);
    	setButtonEnable(R.id.CloseEyes, active);
    	setButtonEnable(R.id.OpenMouth, active);
    	setButtonEnable(R.id.CloseMouth, active);
    	setButtonEnable(R.id.OpenFlip, active);
    	setButtonEnable(R.id.CloseFlip, active);
    	
    	ImageButton bt = (ImageButton) findViewById(R.id.SayIt);
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
     * Callback for intent from service that say that tux state changed
     * Change image of the shown tuxdroid
     */
    private void onUpdateTuxStatus(){
    	HashMap<String, String> current_status = ApiConnector.singleton.getCurrentStatus();
    	for (Entry<String, String> currentEntry : current_status.entrySet()) {
    	    String name = currentEntry.getKey();
    	    String value = currentEntry.getValue();

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
