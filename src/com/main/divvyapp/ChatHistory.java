package com.main.divvyapp;

import helpeMethods.ChatAdapter;
import helpeMethods.ChatItem;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverComunication.ClietSideCommunicator;
import serverComunication.ServerAsyncParent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatHistory extends Activity implements ServerAsyncParent{
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private ListView mainList;
	private Context context;
	private ClietSideCommunicator cummunicator;
	private JSONArray jsonArr;
	private SharedPreferences pref;
	private String uid;
	private String myName;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		pref = getSharedPreferences(LoginPage.class.getSimpleName(), MODE_PRIVATE);
		uid = pref.getString("uid", "error");
		myName = pref.getString("myName", "error");
		
		// need to change to what happened when device is not supported
		if (!checkPlayServices()) {
			Toast.makeText(context, "No valid Google Play Services APK found.", Toast.LENGTH_LONG).show();
		}
		else {
			setContentView(R.layout.activity_chat_history);

			context = getApplicationContext();

			// initialize the main list of deals
			mainList = (ListView) findViewById(R.id.chats_list_view);

			/*
			 * Gets data from prev activity. We should use
			 * it to recognize the user after sign in process
			 */
			Intent intent = getIntent();
			int id = intent.getIntExtra("id", -1);
			getDataFromServer(id);
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.deals_page, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.previous_chats:
	            openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void openSettings() {
		Intent intent = new Intent(context, ChatHistory.class);
		startActivity(intent);	
	}

	public void getDataFromServer(int id) {
		cummunicator = new ClietSideCommunicator();
		cummunicator.connectChatsTable(this, uid);
	}

	public void setDataFromServer(JSONArray chats) {
		try {
	 
	        // looping through All Users prepare the list of all records
			ArrayList<ChatItem> fillMaps = new ArrayList<ChatItem>();
	        for(int i = 0; i <  chats.length(); i++){
	            JSONObject row = chats.getJSONObject(i);
	            if (myName.equals(row.getString("user1"))) {
	            	fillMaps.add(new ChatItem(row.getString("chatid"), row.getString("user1"), row.getString("user2"), row.getString("dealid"), row.getString("dealdescription")) );
	            }
	            else {
	            	fillMaps.add(new ChatItem(row.getString("chatid"), row.getString("user2"), row.getString("user1"), row.getString("dealid"), row.getString("dealdescription")) );
	            }
	            
	        }
	 
	        // fill in the grid_item layout
	        ChatAdapter adapter = new ChatAdapter(context, R.layout.chats_list , fillMaps);
	        mainList.setAdapter(adapter);
			/*
			 *  On click
			 */
			mainList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						ChatItem row = (ChatItem) mainList.getItemAtPosition(position);
						String chatid = row.getChatid();
						
						Intent intent = new Intent(context, ChatAfterMatch.class);
						
						Bundle chat = new Bundle();
						chat.putString("chatid", chatid);
						intent.putExtras(chat);
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
			jsonArr = jObj.getJSONArray("chats");
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
