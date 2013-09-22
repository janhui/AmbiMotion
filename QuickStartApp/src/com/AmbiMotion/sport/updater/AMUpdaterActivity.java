package com.AmbiMotion.sport.updater;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.AmbiMotion.sport.R;
import com.AmbiMotion.sport.updater.AMSelectorActivity;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AMUpdaterActivity extends Activity {
	private PHHueSDK phHueSDK;
	private static final int MAX_HUE = 65535;
	PHBridge bridge;
	String team;
	String competition;
	Intent thisIntent;
	JSONArray incidentArray;

	JSONArray sample;
	Date currentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		phHueSDK = PHHueSDK.getInstance(getApplicationContext());
		bridge = phHueSDK.getSelectedBridge();
		super.onCreate(savedInstanceState);

		Log.v("team", "0");

		setTitle(R.string.app_name);
		setContentView(R.layout.activity_updater);

		currentTime = GetUTCdatetimeAsDate();
		Log.v("team", "1");
		thisIntent = getIntent();
		team = thisIntent.getStringExtra("Team");
		Log.v("team", team);
		competition = "premier-league";

		currentTime.setDate(29);
		currentTime.setMonth(11);
		currentTime.setYear(112);
		currentTime.setHours(17);
		currentTime.setMinutes(31);
		

		long count = 5400001; // for simulation
		long g = 0; // for simulatiom
		while (count - g > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				continue;
			}

			// currentTime.setHours(17);
			long millisec = 1356825600000L+5400000;
			currentTime.setTime(millisec + g);

			g += 600000;
			Log.v("timer",currentTime.toString());
			updateLights(competition, team);
		}
		changeLight("Default");

	} 

	private void updateLights(String competition, String team) {

		// url for live feeds

		/*
		 * team = "Arsenal"; String url = "http://api.statsfc.com/" +
		 * competition +
		 * "/live.json?key=esuv6VTfsX4NzXPZjD9x0yIgxMZ741uCYZFERDD8&team=" +
		 * team + "&timezone=Europe/London";// + //
		 * TimeZone.getDefault().getID();
		 */

		// String url =
		// "http://api.statsfc.com/premier-league/results.json?key=esuv6VTfsX4NzXPZjD9x0yIgxMZ741uCYZFERDD8"+
		// "&year=2012/2013&team="+team+"&from=2012-09-01&to=2012-09-02&timezone=Europe/London&limit=100";
		String url = "http://api.statsfc.com/premier-league/results.json?key=esuv6VTfsX4NzXPZjD9x0yIgxMZ741uCYZFERDD8"
				+ "&year=2012/2013&team="
				+ team
				+ "&from=2012-12-29&to=2012-12-30&timezone=Europe/London&limit=100";

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
				new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						int i = 0;

						// to see if the game starts

						String startTime = null;
						try {
							// startTime = response.getJSONObject(i).getString(
							// "gamestarted");
							startTime = response.getJSONObject(i).getString(
									"date");
						} catch (JSONException e2) {
						}
						Log.v("Starttime", startTime);
						Date kickoff = UTCtime(startTime);

						Log.v("Current", currentTime.toString());
						if (startTime == null) {
							changeLight("Default Light");
						} else {
							if (kickoff.compareTo(currentTime) == -1) {
								changeLight("Start Light");


								for (; i < response.length(); i++) {

									try {
										incidentArray = (JSONArray) response
												.getJSONObject(i).get(
														"incidents");
									} catch (JSONException e1) {
										continue;
									}
									// checks if the current time is before
									// the
									// start
									// time
									String minute;

									try {
										minute = incidentArray.getJSONObject(i)
												.getString("minute");
									} catch (JSONException e) {
										continue;
									}
									int mins = Integer.parseInt(minute) * 1000 * 60;
									long kickoffOffset = kickoff.getTime()
											+ mins;
									long currentTimeOffset = currentTime
											.getTime();
									int help = (int) (currentTimeOffset - kickoffOffset);
									Log.v("check", "2  "
											+ (help < (5 * 10000000)));
									Log.v("check", "4  " + currentTimeOffset);

									Log.v("check", "6  " + kickoffOffset);

									if (currentTimeOffset - kickoffOffset < 5 * 1000000) {

										Log.v("Current",
												"check inside incident loop");
										checkIncidents(incidentArray);

									}
									// go to default
									else {
										changeLight("Start Light");
									}

								}
							} else {
								changeLight("Default Light");
							}
						}
					}
				}, null);

		AMSelectorActivity.QUEUE.add(jsonArrayRequest);

	}

	protected void checkIncidents(JSONArray incidents) {
		try {
			for (int j = 0; j < incidents.length(); j++) {

				// Have different checks for goals etc!1!!
				if (incidents.getJSONObject(j).get("type").equals("Goal")) {
					if (incidents.getJSONObject(j).get("teampath").equals(team)) {

						// your team scored so green ligth
						changeLight("Green");
					} else {
						// other team scored so red light
						changeLight("Red");
					}
				}
			}

		} catch (JSONException e) {
		}

	}

	protected void changeLight(String color) {
		ArrayList<PHLight> allLights = bridge.getResourceCache().getAllLights();
		int r = 255;
		int g = 255;
		int b = 255;

		switch (color.charAt(0)) {
		case 'R':
			r = 255;
			g = 0;
			b = 0;

			break;
		case 'G':
			r = 0;
			g = 255;
			b = 0;
			break;
		case 'S':
			r = 0;
			g = 120;
			b = 255;
			break;

		default:
			r = 255;
			g = 255;
			b = 255;
			break;
		}

		float xy[] = PHUtilities.calculateXYFromRGB(r, g, b, "LCT001");
		PHLightState lightState = new PHLightState();
		lightState.setX(xy[0]);
		lightState.setY(xy[1]);

		bridge.setLightStateForDefaultGroup(lightState);   

	}

	protected Date UTCtime(String startTime) {

		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date incidentDate = null;
		try {
			incidentDate = sdf.parse(startTime);
		} catch (ParseException e) {
		}

		return incidentDate;
	}

	static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	private Date GetUTCdatetimeAsDate() {
		// note: doesn't check for null
		return StringDateToDate(GetUTCdatetimeAsString());
	}

	private String GetUTCdatetimeAsString() {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		final String utcTime = sdf.format(new Date());

		return utcTime;
	}

	private Date StringDateToDate(String StrDate) {
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dateToReturn;
	}

	private void sampleJson() {
		String url = "http://api.statsfc.com/premier-league/results.json?key=esuv6VTfsX4NzXPZjD9x0yIgxMZ741uCYZFERDD8"
				+ "&year=2012/2013&team=Stoke-City&from=2012-09-01&to=2012-09-02&timezone=Europe/London&limit=100";
		// sample;

	}

}
