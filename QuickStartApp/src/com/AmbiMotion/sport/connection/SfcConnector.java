package com.AmbiMotion.sport.connection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.AmbiMotion.sport.updater.AMSelectorActivity;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

public class SfcConnector {
	private JSONObject response;
	private JSONArray array;
	public SfcConnector(){
		
	}

	public JSONArray getTeams(String competition) {
    	String url  = "http://api.statsfc.com/"+competition+"/teams.json?key=esuv6VTfsX4NzXPZjD9x0yIgxMZ741uCYZFERDD8&group=%22%22&year=2012/2013";

        Log.v("selector", "Selecto555");

        JsonArrayRequest jsonArrayObj = new JsonArrayRequest(url, new Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				resp(response);
			}
		}, null);


        AMSelectorActivity.QUEUE.add(jsonArrayObj);

        Log.v("selector", "Selecto534235");
		return array;
	}

	protected void resp(JSONArray response) {
		this.array = response;
		
	}
	
}
