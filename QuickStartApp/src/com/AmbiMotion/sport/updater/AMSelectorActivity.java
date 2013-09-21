package com.AmbiMotion.sport.updater;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

import com.AmbiMotion.sport.R;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class AMSelectorActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    
    private Spinner leagueSpinner;
    private Spinner teamSpinner;
    private Button submit;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_selector);
        phHueSDK = PHHueSDK.create(getApplicationContext());
        
        
        addItemsOnteam();
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent myIntent = new Intent(getApplicationContext(), AMUpdaterActivity.class);
            	//myIntent.putExtra("key", value); //Optional parameters
            	startActivity(myIntent);
            }

        });

    }
    private void addItemsOnteam() {
    	teamSpinner = (Spinner) findViewById(R.id.teams);
    	
    	
		
	}
	public void updater(View view){
    	
    }

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        ArrayList<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();
        
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }
    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Hashtable<String, String> arg0, ArrayList<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {  
        }
    };
}
