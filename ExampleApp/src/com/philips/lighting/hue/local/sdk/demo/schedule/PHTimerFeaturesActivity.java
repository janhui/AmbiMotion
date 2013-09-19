package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHSchedule;

/**
 * Lists all Timer features found in SDK
 * 
 * @author Pallavi P. Ganorkar.
 */
public class PHTimerFeaturesActivity extends Activity implements
        OnItemClickListener {

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        ListView scheduleFeatureListView = (ListView) findViewById(R.id.list_items);
        scheduleFeatureListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        scheduleFeatureListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources()
                        .getStringArray(R.array.timer_features)));

        scheduleFeatureListView.setOnItemClickListener(this);

    }

    /**
     * Callback method to be invoked when an item in this AdapterView has been
     * clicked.
     * 
     * @param parent
     *            The AdapterView where the click happened.
     * @param view
     *            The view within the AdapterView that was clicked
     * @param position
     *            The position of the view in the adapter.
     * @param id
     *            The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        PHHueSDK phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        PHBridgeResourcesCache cache = phHueSDK.getSelectedBridge()
                .getResourceCache();
        ArrayList<PHSchedule> nonRecurringTimers = cache.getAllTimers(false);
        ArrayList<PHLight> lights = cache.getAllLights();

        Intent intent = null;
        switch (position) {
        case 0:
            if (lights.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_lights_found_for_timer);
                return;
            }
            intent = new Intent(this, PHCreateTimerActivity.class);
            break;
        case 1:
            if (nonRecurringTimers.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_timers);
                return;
            }
            intent = new Intent(this, PHUpdateTimerActivity.class);
            break;
        case 2:
            if (nonRecurringTimers.size() == 0) {
                PHWizardAlertDialog.showErrorDialog(this,
                        R.string.txt_no_timers);
                return;
            }
            intent = new Intent(this, PHRemoveTimerActivity.class);
            break;

        default:
            break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    /**
     * Called when option is selected.
     * 
     * @param item
     *            the MenuItem object.
     * @return boolean Return false to allow normal menu processing to proceed,
     *         true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        }
        return true;
    }

}
