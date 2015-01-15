package com.main.divvyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverComunication.DataTransfer;
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
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DealsPage extends Activity implements ServerAsyncParent {

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String clickedStore;
	int dealid;
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

			cummunicator = new ClietSideCommunicator();
			context = getApplicationContext();

			// initialize the main list of deals
			mainList = (GridView) findViewById(R.id.mainList);
			
//		    mainList.setAdapter(new ImageAdapter(this));


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
		// Sending GET request to server
//		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("id", Integer.toString(id)));
//		new DataTransfer(this, params, DataTransfer.METHOD_GET).execute("http://nir.milab.idc.ac.il/php/milab_get_deals.php");
		cummunicator.connectToServer(this);
	}
	

	public void setDataFromServer(JSONArray deals) {
		try {
				
			final List<String> storeList = cummunicator.setDealsFromServer(deals);
			
	        GridView gridview = (GridView) findViewById(R.id.mainList);
	        gridview.setAdapter(new ImageAdapter(this,storeList));
			
			// create the grid item mapping
//			String[] from = new String[] {"storeid"};
//			int[] to = new int[] {R.id.storeid};
					    
			// fill in the grid_item layout
//			SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.item_list, from, to);
//			mainList.setAdapter(adapter);
//			mainList.setClickable(true);
	        
			mainList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					HashMap<String, String> selected = (HashMap<String, String>) mainList.getItemAtPosition(position);
					String selectedStore = storeList.get(position);
					
					Intent intent = new Intent(context, StorePage.class);
					intent.putExtra("selectedFromList", selectedStore);
					startActivity(intent);
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
	
	public JSONArray getJsonArr(){
		return this.jsonArr;
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
