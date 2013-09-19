package com.philips.lighting.hue.local.sdk.demo.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.local.sdk.demo.PHHomeActivity;
import com.philips.lighting.hue.local.sdk.demo.PHWizardAlertDialog;
import com.philips.lighting.hue.local.sdk.demo.R;
import com.philips.lighting.hue.local.sdk.demo.light.PHUpdateLightStateActivity;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.wizard.helper.PHHelper;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;

/**
 * Contains demo for create non-recurring schedule API.
 * 
 * @author Pallavi P. Ganorkar
 */
public class PHCreateNonRecurringScheduleActivity extends Activity {
    private static final String TAG = "PHCreateNonRecurringScheduleActivity";
    private PHHueSDK phHueSDK;
    private PHBridge bridge;

    private EditText editTvScheduleName;
    private Button btnScheduleTime;
    private RadioButton rbLightForSchedule;
    private RadioButton rbGroupForSchedule;
    private Spinner lightSpinner;
    private Spinner groupSpinner;
    private Button btnScheduleLightState;
    private EditText editTvSceduleDescriptor;
    private EditText editTvTimerRandomTime;

    private int mHour;
    private int mMinute;
    private static Date timeToSend;
    private PHLightState stateToSend;

    private ArrayList<PHLight> lights;
    private ArrayList<PHGroup> groups;

    /**
     * Called when the activity will start interacting with the user.
     * 
     * @param savedInstanceState
     *            the bundle object.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createschedule);
        initComponents();
        String lightArray[];
        String groupArray[];
        
        phHueSDK = PHHueSDK.getInstance(getApplicationContext());
        bridge = phHueSDK.getSelectedBridge();

        // lights to create schedule.
        lights = bridge.getResourceCache().getAllLights();
        lightArray=phHueSDK.getLightNames(lights);

        if (lightArray.length == 0) {
            rbLightForSchedule.setEnabled(false);
        }

        // groups to create schedule.
        groups = bridge.getResourceCache().getAllGroups();
        groupArray = phHueSDK.getGroupNames(groups);

        if (groupArray.length == 0) {
            rbGroupForSchedule.setEnabled(false);
        }

        // set adapter to light spinner
        ArrayAdapter<String> lightSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, lightArray);
        lightSpinner.setAdapter(lightSpinnerAdapter);
        lightSpinner.setEnabled(false);

        // set adapter to group spinner
        ArrayAdapter<String> groupSpinnerAdapter = new ArrayAdapter<String>(
                this, R.layout.inner_spinner_item, groupArray);
        groupSpinner.setAdapter(groupSpinnerAdapter);
        groupSpinner.setEnabled(false);

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // set listener for button click to set time.
        btnScheduleTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog(
                        PHCreateNonRecurringScheduleActivity.this,
                        mTimeSetListener, mHour, mMinute, true);

                timePicker.show();
            }
        });

        // set listener for radio button click for light.
        rbLightForSchedule.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForSchedule.setChecked(true);
                rbGroupForSchedule.setChecked(false);
                lightSpinner.setEnabled(true);
                groupSpinner.setEnabled(false);
            }
        });

        // set listener for radio button click for group.
        rbGroupForSchedule.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                rbLightForSchedule.setChecked(false);
                rbGroupForSchedule.setChecked(true);
                lightSpinner.setEnabled(false);
                groupSpinner.setEnabled(true);
            }
        });

        // set listener for button click to set light state.
        btnScheduleLightState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        PHCreateNonRecurringScheduleActivity.this,
                        PHUpdateLightStateActivity.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Creates option menu.
     * 
     * @param menu
     *            the Menu Object.
     * @return true for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_timer, menu);
        return true;
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
        case R.id.create_timer:
            createNonRecurringSchedule();
            break;
        case android.R.id.home:
            Intent intent = new Intent(this, PHHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        stateToSend = phHueSDK.getCurrentLightState();
    }

    /**
     * Initialize the UI components.
     */
    void initComponents() {

        editTvScheduleName = (EditText) findViewById(R.id.editTvTimerName);
        rbLightForSchedule = (RadioButton) findViewById(R.id.rbLightForTimer);
        rbGroupForSchedule = (RadioButton) findViewById(R.id.rbGroupForTimer);
        lightSpinner = (Spinner) findViewById(R.id.lightSpinnerForTimer);
        groupSpinner = (Spinner) findViewById(R.id.groupSpinnerForTimer);
        btnScheduleTime = (Button) findViewById(R.id.btnTimerTime);
        TextView tvScheduleTime = (TextView) findViewById(R.id.tvTimerTime);
        tvScheduleTime.setText(R.string.txt_schedule_time);
        btnScheduleLightState = (Button) findViewById(R.id.btnTimerLightState);
        editTvSceduleDescriptor = (EditText) findViewById(R.id.editTvTimerDescriptor);
        editTvTimerRandomTime = (EditText) findViewById(R.id.editTvTimerRandomTime);
    }

    /**
     * Listener for TimerPicker dialog to indicate the user is done filling in
     * the time.
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateDisplay();

            timeToSend = Calendar.getInstance().getTime();
            timeToSend.setHours(mHour);
            timeToSend.setMinutes(mMinute);
        }
    };

    /**
     * update the displayed time on button.
     */
    private void updateDisplay() {
        btnScheduleTime.setText(new StringBuilder().append(PHHelper.pad(mHour))
                .append(":").append(PHHelper.pad(mMinute)));
    }

    /**
     * Creates non recurring schedule.
     */
    private void createNonRecurringSchedule() {

        PHSchedule schedule = null;
        // name
        String scheduleName = editTvScheduleName.getText().toString().trim();
        if (scheduleName.length() == 0) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateNonRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_timer_name),
                    R.string.btn_ok);
            return;
        }
        schedule = new PHSchedule(scheduleName);

        // time
        if (timeToSend == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateNonRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_time),
                    R.string.btn_ok);
            return;
        } else {
            schedule.setDate(timeToSend);
        }

        // light or group for schedule.
        String lightIdentifier = null;
        String groupIdentifier = null;
        if (rbLightForSchedule.isChecked()) {
            int lightPos = lightSpinner.getSelectedItemPosition();
            PHLight light = lights.get(lightPos);
            lightIdentifier = light.getIdentifier();
            schedule.setLightIdentifier(lightIdentifier);

        } else if (rbGroupForSchedule.isChecked()) {
            int groupPos = groupSpinner.getSelectedItemPosition();
            PHGroup group = groups.get(groupPos);
            groupIdentifier = group.getIdentifier();
            schedule.setGroupIdentifier(groupIdentifier);
        } else {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateNonRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_light_group),
                    R.string.btn_ok);
            return;
        }

        if (lightIdentifier == null && groupIdentifier == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateNonRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_light_group),
                    R.string.btn_ok);
            return;
        }

        // light state
        if (stateToSend == null) {
            PHWizardAlertDialog.showErrorDialog(
                    PHCreateNonRecurringScheduleActivity.this, getResources()
                            .getString(R.string.txt_empty_light_state),
                    R.string.btn_ok);
            return;
        } else {
            schedule.setLightState(stateToSend);
        }

        // description
        String timerDescription = editTvSceduleDescriptor.getText().toString()
                .trim();

        if (timerDescription.length() != 0) {
            schedule.setDescription(timerDescription);
        }

        // random time
        String scheduleRandomTime = editTvTimerRandomTime.getText().toString()
                .trim();
        if (scheduleRandomTime.length() != 0) {
            schedule.setRandomTime(Integer.parseInt(scheduleRandomTime));
        }

        final PHWizardAlertDialog dialogManager = PHWizardAlertDialog
                .getInstance();
        dialogManager.showProgressDialog(R.string.sending_progress,
                PHCreateNonRecurringScheduleActivity.this);

        // api call
        bridge.createSchedule(schedule, new PHScheduleListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onCreated(PHSchedule schedule) {
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showResultDialog(
                        PHCreateNonRecurringScheduleActivity.this,
                        getString(R.string.txt_timer_created), R.string.btn_ok,
                        R.string.txt_result);
                return;
            }

            @Override
            public void onStateUpdate(
                    Hashtable<String, String> successAttribute,
                    ArrayList<PHHueError> errorAttribute) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int code, String msg) {
                Log.v(TAG, "onError : " + code + " : " + msg);
                dialogManager.closeProgressDialog();
                PHWizardAlertDialog.showErrorDialog(
                        PHCreateNonRecurringScheduleActivity.this, msg,
                        R.string.btn_ok);

            }
        });
    }

}
