package com.main.divvyapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helpeMethods.DataTransfer;
import helpeMethods.ServerAsyncParent;

import com.main.divvyapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DealsPage extends Activity implements ServerAsyncParent {
	
	String clickedStore;
	int dealid;
	private GridView mainList;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deals_page);
		
		context = getApplicationContext();
		
		// initialize the main list of deals
		mainList = (GridView) findViewById(R.id.mainList);

		// Gets data from previous activity - not necessary
		Intent intent = getIntent();
		int id = intent.getIntExtra("id", -1);
		getDataFromServer(id);
	}
	
	public void getDataFromServer(int id) {
		// Sending GET request to server
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(id)));
		new DataTransfer(this, params, DataTransfer.METHOD_GET).execute("http://10.0.0.19:8080/php/milab_get_deals.php");
	}
	
	public void setDataFromServer(JSONArray deals) {
		try {
	        // create the grid item mapping
	        String[] from = new String[] {"storeid"};
	        int[] to = new int[] {R.id.storeid};
	 
	        // gets all store names to represent each name once on the list
	        // not effective solution
	        List<String> storeidArr = new ArrayList<String>();
	        for (int i = 0; i < deals.length(); i++) {
	        	JSONObject row = deals.getJSONObject(i);
	        	String currentStoreid = row.getString("storeid");
	        	if (!(storeidArr.contains(currentStoreid))) {
		            storeidArr.add(row.getString("storeid"));

	        	}
			}
	        
	        // looping through All Users prepare the list of all records
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	        for(int i = 0; i <  deals.length(); i++){
	            JSONObject row = deals.getJSONObject(i);
	            String currentStoreid = row.getString("storeid");
	            
	        	if(storeidArr.contains(currentStoreid)){
	        		 HashMap<String, String> map = new HashMap<String, String>();
	 	            map.put("id", row.getString("id"));
	 	            map.put("claimedBy", row.getString("claimedBy"));
	 	            map.put("picture", row.getString("picture"));
	 	            map.put("storeid", row.getString("storeid"));
	 	            map.put("deadLine", row.getString("deadLine"));
	 	            fillMaps.add(map);
	 	            storeidArr.remove(storeidArr.indexOf(currentStoreid));
	        	}
	           
	        }
	        // fill in the grid_item layout
	        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.item_list, from, to);
	        mainList.setAdapter(adapter);
	        mainList.setClickable(true);
	        mainList.setOnItemClickListener(new OnItemClickListener() {
	        
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					HashMap<String, String> selected = (HashMap<String, String>) mainList.getItemAtPosition(position);
					String selectedStore = selected.get("storeid");
					Intent intent = new Intent(context, StorePage.class);
					intent.putExtra("selectedFromList", selectedStore);
					startActivity(intent);
					
					
				
//					HashMap<String, String> selected = (HashMap<String, String>) mainList.getItemAtPosition(position);
//					String claimedBy = selected.get("claimedBy");
//					String deadLine = selected.get("deadLine");
//					dealid = Integer.parseInt(selected.get("id"));
//					
//					if (!claimedBy.equals("")) {
//						Intent intent = new Intent(context, CompleteMatch.class);
//						intent.putExtra("dealid", dealid);
//						intent.putExtra("claimedBy", claimedBy);
//						intent.putExtra("deadLine", deadLine);
//						startActivity(intent);
//					}
//					else {
//						Intent intent = new Intent(context, FindMeMatch.class);
//						intent.putExtra("dealid", dealid);
//						startActivity(intent);
//					}
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
			setDataFromServer(jObj.getJSONArray("deals"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
