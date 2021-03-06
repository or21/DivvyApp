package com.main.divvyapp;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import serverComunication.DataTransfer;
import serverComunication.ServerAsyncParent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class FindMeMatch extends Activity implements OnClickListener, ServerAsyncParent {
	
	TimePicker deadLine;
	Context context;
	int dealId;
	SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_me_match);
		
		pref = getSharedPreferences(LoginPage.class.getSimpleName(), MODE_PRIVATE);
		dealId = getIntent().getExtras().getInt("dealid", -1);
		context = getApplicationContext();
		deadLine = (TimePicker) findViewById(R.id.deadLine);
		Button requestMatch = (Button) findViewById(R.id.claimDeal);
		requestMatch.setOnClickListener(this);
	}

	public void sendUpadte(View v) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("deadLine", (deadLine.getCurrentHour().toString() + ":" + deadLine.getCurrentMinute()).toString()));
		params.add(new BasicNameValuePair("dealid", "" + dealId));
		params.add(new BasicNameValuePair("uidNew", pref.getString("uid", "error")));
		params.add(new BasicNameValuePair("chatid", "request"));
		params.add(new BasicNameValuePair("uid", ""));
		new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://nir.milab.idc.ac.il/php/milab_send_deal_update.php");
	}
	
	@Override
	public void onClick(View v) {
		sendUpadte(v);
	}

	@Override
	public void doOnPostExecute(JSONObject jObj) {
		Intent intent = new Intent(this, StorePage.class);
		startActivity(intent);
		finish();
	}
}
