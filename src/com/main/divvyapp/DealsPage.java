package com.main.divvyapp;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverComunication.ServerAsyncParent;
import serverComunication.ClietSideCommunicator;

import helpeMethods.ImageAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.main.divvyapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class DealsPage extends Activity implements ServerAsyncParent {

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String clickedStore;
	private GridView mainList;
	Context context;
	ClietSideCommunicator cummunicator;
	JSONArray jsonArr;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// need to change to what happened when device is not supported
		if (!checkPlayServices()) {
			Toast.makeText(context, "No valid Google Play Services APK found.", Toast.LENGTH_LONG).show();
		}
		else {
			setContentView(R.layout.activity_deals_page);

			context = getApplicationContext();

			// initialize the main list of deals
			mainList = (GridView) findViewById(R.id.mainList);
			

		    /*
		     * Gets data from prev activity. We should use
		     * it to recognize the user after sign in process
		     */
			Intent intent = getIntent();
			int id = intent.getIntExtra("id", -1);
			getDataFromServer(id);
		}
	}
	

	public void getDataFromServer(int id) {
		cummunicator = new ClietSideCommunicator();
		cummunicator.connectDealsTable(this);
	}
	

	public void setDataFromServer(JSONArray deals) {
		try {
				
			// Get array with all available stores
			final List<String> storeList = cummunicator.getFromTable(deals,"storeid");
			
			// Set store's logos on gridView
	        GridView gridview = mainList;
	        gridview.setAdapter(new ImageAdapter(this,storeList));
			
	        /*
	         *  On click
	         */
			mainList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
					if(position == storeList.size()){
						Intent intent = new Intent(context, AddNewDeal.class);
						startActivity(intent);	
					}else{
					
					String selectedStore = storeList.get(position);
					Intent intent = new Intent(context, StorePage.class);
					intent.putExtra("selectedFromList", selectedStore);
					startActivity(intent);
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void doOnPostExecute(JSONObject jObj) {
		try {
			// Retrieving JSON array from server
			jsonArr = jObj.getJSONArray("deals");
			setDataFromServer(jsonArr);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	

	
	// Added for GCM
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("@string/app_name", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
}
