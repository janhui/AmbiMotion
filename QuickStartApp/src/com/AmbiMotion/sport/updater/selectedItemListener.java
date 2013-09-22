package com.AmbiMotion.sport.updater;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class selectedItemListener implements OnItemSelectedListener {

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		
		AmbiMotion ambiMotion = (AmbiMotion)parent.getContext();
		Log.v("Adapter", parent.toString());
		Log.v("View", view.toString());

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
