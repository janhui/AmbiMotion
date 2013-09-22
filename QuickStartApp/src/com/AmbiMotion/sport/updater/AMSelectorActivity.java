package com.AmbiMotion.sport.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.AmbiMotion.sport.R;
import com.AmbiMotion.sport.connection.SfcConnector;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

public class AMSelectorActivity extends Activity {
	private PHHueSDK phHueSDK;
	private static final int MAX_HUE = 65535;
	public static final String TAG = "QuickStart";

	private Spinner leagueSpinner;
	private Spinner teamSpinner;
	private Button submit;
	public static RequestQueue QUEUE;
	private SfcConnector sfcConnector;
	private String team = "Hello";
	private HashMap<String , String> teamMap = new HashMap<String, String>();

	private List<String> teamList = new ArrayList<String>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		QUEUE = Volley.newRequestQueue(this);
		sfcConnector = new SfcConnector();
		setTitle(R.string.app_name);
		setContentView(R.layout.activity_selector);
		phHueSDK = PHHueSDK.create(getApplicationContext());

		// dynamically add teams onto the spinner
		addItemsOnteam();

		// Submit button to go to next screen
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				team = String.valueOf(teamSpinner.getSelectedItem());


				 Intent myIntent = new Intent(getApplicationContext(),
				 AMUpdaterActivity.class);
				 myIntent.putExtra("Team", teamMap.get(team));
				 startActivity(myIntent);
			}

		});

	}

	// dynamically adding the team names to the spinner
	private void addItemsOnteam() {
		teamSpinner = (Spinner) findViewById(R.id.teams);
		final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, teamList);

		/*
		 * JSONObject = sfcConnector.getTeams(competition); JSONArray json =
		 * sfcConnector.getTeams("premier-league"); for (int i = 0; i <
		 * json.length(); i++) { String team = "Hello"; try { team =
		 * json.getJSONObject(i).getString("name"); Log.v("Response", team);
		 * teamList.add(team); } catch (JSONException e) { } }
		 */

		String url = "https://api.statsfc.com/premier-league/teams.json?key=esuv6VTfsX4NzXPZjD9x0yIgxMZ741uCYZFERDD8&group=%22%22&year=2012/2013";

		// gets a jsonArray which is then iterated through to add to the list of
		// names
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
				new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						// TODO Auto-generated method stub
						teamList.clear();
						for (int i = 0; i < response.length(); i++) {
							String team = "Hello";
							try {
								team = response.getJSONObject(i).getString(
										"name");
								String value = response.getJSONObject(i).getString(
										"path");
								
								teamMap.put(team, value);
								
								teamList.add(team);
							} catch (JSONException e) {
							}
						}

						dataAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						teamSpinner.setAdapter(dataAdapter);

						Log.v("TeamList", teamList.toString());

					}
				}, null);

		QUEUE.add(jsonArrayRequest);

		// addes it to the spinner


	}

	// If you want to handle the response from the bridge, create a
	// PHLightListener object.
	PHLightListener listener = new PHLightListener() {

		@Override
		public void onSuccess() {
		}

		@Override
		public void onStateUpdate(Hashtable<String, String> arg0,
				ArrayList<PHHueError> arg1) {
			Log.w(TAG, "Light has updated");
		} 

		@Override
		public void onError(int arg0, String arg1) {
		}
	};
}
