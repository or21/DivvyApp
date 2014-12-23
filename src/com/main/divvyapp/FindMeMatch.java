package com.main.divvyapp;

import helpeMethods.DataTransfer;
import helpeMethods.ServerAsyncParent;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

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
		
		pref = getSharedPreferences(null, Context.MODE_PRIVATE);
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
		params.add(new BasicNameValuePair("uid", pref.getString("uid", "error")));
		new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://192.168.43.171/php/milab_send_deal_update.php");
	}
	
	@Override
	public void onClick(View v) {
		sendUpadte(v);
	}

	@Override
	public void doOnPostExecute(JSONObject jObj) {
		Intent intent = new Intent(this, DealsPage.class);
		startActivity(intent);
		finish();
	}
}
