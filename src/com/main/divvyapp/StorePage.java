package com.main.divvyapp;

import helpeMethods.DealObj;
import helpeMethods.ImageAdapter;
import helpeMethods.ListDealsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverComunication.ClietSideCommunicator;
import serverComunication.ServerAsyncParent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class StorePage extends Activity implements ServerAsyncParent{

	String filter;
	// String[] fillMapsArr;
	int dealid;
	private ListView dealList;
	Context context;
	SharedPreferences pref;
	ClietSideCommunicator cummunicator;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_page);
		
		pref = getSharedPreferences(LoginPage.class.getSimpleName(),
				MODE_PRIVATE);
		filter = getIntent().getExtras().getString("filter");
		// fillMapsArr = getIntent().getExtras().getStringArray("fillMapsArr");
		context = getApplicationContext();

		// initialize the main list of deals
		dealList = (ListView) findViewById(R.id.dealList);

		// Gets data from previous activity - not necessary
		Intent intent = getIntent();
		int id = intent.getIntExtra("id", -1);
		getDataFromServer(id);

	}
	
	// Menu Bar
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_filter:
	        	Intent intent1 = new Intent(context, Filter.class);
				startActivity(intent1);
	        case R.id.previous_chats:
	        	Intent intent2 = new Intent(context, ChatHistory.class);
				startActivity(intent2);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void getDataFromServer(int id) {
		// Sending GET request to server
		cummunicator = new ClietSideCommunicator();
		cummunicator.connectDealsTable(this);
	}

	public void setDataFromServer(JSONArray deals) {
		try {

			final ArrayList<DealObj> fillMaps = new ArrayList<DealObj>();
			for (int i = 0; i < deals.length(); i++) {
				JSONObject row = deals.getJSONObject(i);

				if (filter.equals(row.getString("category"))
						|| filter.equals("all")) {
					DealObj deal = new DealObj(row.getString("id"),
							row.getString("storeid"),
							row.getString("category"),
							row.getString("claimedBy"),
							row.getString("picture"),
							row.getString("deadLine"),
							row.getString("dealName"));
					fillMaps.add(deal);
				}
			}

			// Get the ListView by Id and instantiate the adapter with
			// cars data and then set it the ListView
			dealList = (ListView) findViewById(R.id.dealList);
			ListDealsAdapter adapter = new ListDealsAdapter(this, fillMaps);
			dealList.setAdapter(adapter);

			// final List<String> listOfDealsId =
			// cummunicator.getFromTable(deals,"id","storeid",filter);
			// final List<String> listOfDealsClaimedBy =
			// cummunicator.getFromTable(deals,"claimedBy","storeid",filter);
			// final List<String> listOfDealsDeadLine =
			// cummunicator.getFromTable(deals,"deadLine","storeid",filter);

			dealList.setClickable(true);
			dealList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (fillMaps.get(position).getClaimedBy().length() < 15) {
						
						Intent intent = new Intent(context, FindMeMatch.class);
						intent.putExtra("dealid", Integer.parseInt(fillMaps
								.get(position).getId()));
						startActivity(intent);
					} else {
						
						Intent intent = new Intent(context, CompleteMatch.class);
						intent.putExtra("dealid", Integer.parseInt(fillMaps
								.get(position).getId()));
						intent.putExtra("claimedBy", fillMaps.get(position)
								.getClaimedBy());
						intent.putExtra("uid", pref.getString("uid", "error"));
						intent.putExtra("deadLine", fillMaps.get(position)
								.getDeadLine());
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
