package com.main.divvyapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helpeMethods.DataTransfer;
import helpeMethods.ServerAsyncParent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ChatAfterMatch extends Activity implements ServerAsyncParent {

	static final String get = "GET";
	static final String post = "POST";

	TextView chat;
	SharedPreferences pref;
	String claimedBy;
	String uid;
	private ListView mainList;
	Boolean visible;
	Button send;
	Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_after_match);

		pref = getSharedPreferences("com.main.divvyapp", MODE_PRIVATE);
		claimedBy = getIntent().getStringExtra("claimedBy");
		uid = pref.getString("uid", "error");
		mainList = (ListView) findViewById(R.id.mainChatList);

		final EditText chatWindow = (EditText) findViewById(R.id.chatWindow);
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = chatWindow.getText().toString();
				ChatServer(uid, claimedBy, message, post);
				ChatServer(uid, claimedBy, "", get);
				mainList.setSelection(mainList.getCount() - 1);
				chatWindow.setText("");
			}
		});
		
		// This thread refreshing chat view every 3 seconds
		t = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(3000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ChatServer(uid, claimedBy, "", get);
							}
						});
					}
				} 
				catch (InterruptedException e) {
				}
			}
		};
		t.start();
	}
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass
	    
	    // stops the threads that created
	    t.interrupt();
	}
	
	public void ChatServer(String uid, String claimedBy, String message, String operation) {
		String claimer = claimedBy.substring(0, claimedBy.indexOf("-"));
		String completer = uid.substring(0, uid.indexOf("-"));
		
		// Sending GET request to server
		if (operation == get) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("chatid", completer + claimer));
			new DataTransfer(this, params, DataTransfer.METHOD_GET).execute("http://192.168.43.171/php/milab_get_chat.php");
		} else {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("chatid", completer + claimer));
			params.add(new BasicNameValuePair("name", uid));
			params.add(new BasicNameValuePair("message", message));
			new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://192.168.43.171/php/milab_update_chat.php");
		}
	}

	public void setChatFromServer(JSONArray chat) {
		try {
			// create the grid item mapping
			String[] from = new String[] {"name", "spacing", "message"};
			int[] to = new int[] { R.id.name, R.id.spacing, R.id.message};

			// looping through All Users prepare the list of all records
			List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
			for(int i = 0; i <  chat.length(); i++){
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject row = chat.getJSONObject(i);
				map.put("name", row.getString("name"));
				map.put("spacing", row.getString("spacing"));
				map.put("message", row.getString("message"));
				fillMaps.add(map);
			}
			// fill in the grid_item layout
			SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.chat_list, from, to);
			mainList.setAdapter(adapter);
			
			// this will show the last item in the list
			mainList.setSelection(mainList.getCount() - 1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doOnPostExecute(JSONObject jObj) {
		try {
			// Retrieving JSON array from server
			setChatFromServer(jObj.getJSONArray("chat"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

