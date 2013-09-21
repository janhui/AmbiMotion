package com.AmbiMotion.sport.updater;

import java.util.ArrayList;
import java.util.Random;

import com.AmbiMotion.sport.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AMUpdaterActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        
		//Intent intent =  getIntent();
		randomlights();

		
	}

	private void randomlights() {
		// TODO Auto-generated method stub
		phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        PHBridge bridge = phHueSDK.getSelectedBridge();

        ArrayList<PHLight> allLights = bridge.getResourceCache().getAllLights();

        
        Random rand = new Random();
        
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(rand.nextInt(MAX_HUE));
            //bridge.updateLightState(light, lightState, listener);
            bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
	}

}
