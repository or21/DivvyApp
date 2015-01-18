package com.main.divvyapp;


import helpeMethods.ImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverComunication.ClietSideCommunicator;
import serverComunication.DataTransfer;
import serverComunication.ServerAsyncParent;

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
	String[] fillMapsArr;
	int dealid;
	private GridView dealList;
	Context context;
	SharedPreferences pref;
	ClietSideCommunicator cummunicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deals_page);
		
		pref = getSharedPreferences(LoginPage.class.getSimpleName(), MODE_PRIVATE);
		selectedStore =  getIntent().getExtras().getString("selectedFromList");
		fillMapsArr = getIntent().getExtras().getStringArray("fillMapsArr");
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
		cummunicator = new ClietSideCommunicator();
		cummunicator.connectDealsTable(this);
	}

	public void setDataFromServer(JSONArray deals) {
		try {
			final List<String> listOfDealsId = cummunicator.getFromTable(deals,"id","storeid",selectedStore);
			final List<String> listOfDealsClaimedBy = cummunicator.getFromTable(deals,"claimedBy","storeid",selectedStore);
			final List<String> listOfDealsDeadLine = cummunicator.getFromTable(deals,"deadLine","storeid",selectedStore);
			
			
			// Set deal logos on gridView
	        GridView gridview = dealList;

//	        GridView gridview = (GridView) findViewById(R.id.dealList);
	        gridview.setAdapter(new ImageAdapter(this,listOfDealsId));

//			dealList.setClickable(true);
			dealList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		

					if (!(listOfDealsClaimedBy.get(position).equals("")) && !(listOfDealsClaimedBy.get(position).equals("null"))) {
						Intent intent = new Intent(context, CompleteMatch.class);
						intent.putExtra("dealid", dealid);
						intent.putExtra("claimedBy", listOfDealsClaimedBy.get(position));
						intent.putExtra("uid", pref.getString("uid", "error"));
						intent.putExtra("deadLine", listOfDealsDeadLine.get(position));
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
