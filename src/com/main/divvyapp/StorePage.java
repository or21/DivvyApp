package com.main.divvyapp;

import helpeMethods.DataTransfer;
import helpeMethods.ServerAsyncParent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class StorePage extends Activity implements ServerAsyncParent {

	String selectedStore;
	int dealid;
	private GridView dealList;
	Context context;
	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deals_page);
		
		pref = getSharedPreferences(LoginPage.class.getSimpleName(), MODE_PRIVATE);
		selectedStore =  getIntent().getExtras().getString("selectedFromList");

		context = getApplicationContext();

		// initialize the main list of deals
		dealList = (GridView) findViewById(R.id.mainList);

		// Gets data from previous activity - not necessary
		Intent intent = getIntent();
		int id = intent.getIntExtra("id", -1);
		getDataFromServer(id);
	}

	public void getDataFromServer(int id) {
		// Sending GET request to server
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(id)));
		new DataTransfer(this, params, DataTransfer.METHOD_GET).execute("http://nir.milab.idc.ac.il/php/milab_get_deals.php");
	}

	public void setDataFromServer(JSONArray deals) {
		try {
			// create the grid item mapping
			String[] from = new String[] {"id"};
			int[] to = new int[] {R.id.id};

			//counting how much deals the store have
			int dealsCounter = 0;
			for (int i = 0; i < deals.length(); i++) {
				JSONObject row = deals.getJSONObject(i);
				if (row.getString("storeid").equals(selectedStore)) {
					dealsCounter++;
				}
			}

			// looping through All deals in the store
			List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
			for(int i = 0; i <  dealsCounter; i++){
				JSONObject row = deals.getJSONObject(i);
				String currentStoreid = row.getString("storeid");

				if(selectedStore.equals(currentStoreid)){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", row.getString("id"));
					map.put("claimedBy", row.getString("claimedBy"));
					map.put("picture", row.getString("picture"));
					map.put("storeid", row.getString("storeid"));
					map.put("deadLine", row.getString("deadLine"));
					fillMaps.add(map);
				}

			}
			// fill in the grid_item layout
			SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.item_list, from, to);
			dealList.setAdapter(adapter);
			dealList.setClickable(true);
			dealList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					HashMap<String, String> selected = (HashMap<String, String>) dealList.getItemAtPosition(position);
					String claimedBy = selected.get("claimedBy");
					String deadLine = selected.get("deadLine");
					dealid = Integer.parseInt(selected.get("id"));

					if (!claimedBy.equals("")) {
						Intent intent = new Intent(context, CompleteMatch.class);
						intent.putExtra("dealid", dealid);
						intent.putExtra("claimedBy", claimedBy);
						intent.putExtra("uid", pref.getString("uid", "error"));
						intent.putExtra("deadLine", deadLine);
						startActivity(intent);
					}
					else {
						Intent intent = new Intent(context, FindMeMatch.class);
						intent.putExtra("dealid", dealid);
						startActivity(intent);
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOnPostExecute(JSONObject jObj) {
		try {
			// Retrieving JSON array from server
			setDataFromServer(jObj.getJSONArray("deals"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
