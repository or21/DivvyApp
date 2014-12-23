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
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CompleteMatch extends Activity implements ServerAsyncParent, OnClickListener {

	Context context;
	int dealId;
	SharedPreferences pref;
	TextView countdown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete_match);

		pref = getSharedPreferences(null, Context.MODE_PRIVATE);
		dealId = getIntent().getExtras().getInt("dealid", -1);
		context = getApplicationContext();

		CountDownTimer cT =  new CountDownTimer(100000, 1000) {

			public void onTick(long millisUntilFinished) {
				countdown = (TextView) findViewById(R.id.countdown);
				String v = String.format("%02d", millisUntilFinished/60000);
				int va = (int)( (millisUntilFinished%60000)/1000);
				countdown.setText("seconds remaining: " +v+":"+String.format("%02d",va));
			}

			public void onFinish() {
				countdown.setText("done!");
			}
		};
		cT.start();
		
		Button completeMatch = (Button) findViewById(R.id.completeDeal);
		completeMatch.setOnClickListener(this);
	}

	public void sendUpadte(View v) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("deadLine", "0"));
		params.add(new BasicNameValuePair("dealid", "" + dealId));
		params.add(new BasicNameValuePair("uid", "0"));
		new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://192.168.43.171/php/milab_send_deal_update.php");
	}

	@Override
	public void onClick(View v) {
		sendUpadte(v);
	}

	// This should change to class that will make the match by send phone number or anything like this
	@Override
	public void doOnPostExecute(JSONObject jObj) {
		Intent intent = new Intent(this, DealsPage.class);
		startActivity(intent);
		finish();
	}
}
