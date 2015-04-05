package com.main.divvyapp;


import java.util.ArrayList;
import java.util.Calendar;

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
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CompleteMatch extends Activity implements ServerAsyncParent {

	Context context;
	int dealId;
	SharedPreferences pref;
	TextView countdown;
	String time;
	String claimedBy;
	String uid;
	final static String msg = "You have a match for a deal - click to start chat";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_complete_match);

		pref = getSharedPreferences(LoginPage.class.getSimpleName(), MODE_PRIVATE);
		dealId = getIntent().getExtras().getInt("dealid");
		context = getApplicationContext();
		time = getIntent().getExtras().getString("deadLine");
		uid = pref.getString("uid", "error");
		claimedBy = getIntent().getExtras().getString("claimedBy");

		int miliDeadLine = calcMili(time);

		// if the deal is finishing right now giving the option to return to DealsPage
		if (miliDeadLine <= 60000) {
			countdown = (TextView) findViewById(R.id.countdown);
			countdown.setText("Expired!");
			Button completeMatch = (Button) findViewById(R.id.completeDeal);
			completeMatch.setText("Back to Deals");
			completeMatch.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, StorePage.class);
					startActivity(intent);
					finish();
				}
			});

		// otherwise - start the countDown
		} else {
			// launching the countDown
			CountDownTimer cT =  new CountDownTimer(miliDeadLine, 1000) {

				public void onTick(long millisUntilFinished) {
					countdown = (TextView) findViewById(R.id.countdown);
					int vh = (int)( (millisUntilFinished / (1000*60*60)) % 24);
					int vm = (int)( (millisUntilFinished / 60000) % 60);
					int vs = (int)( (millisUntilFinished / 1000) % 60);
					countdown.setText(String.format("%02d",vh)+":"+String.format("%02d",vm)+":"+String.format("%02d",vs));
				}

				public void onFinish() {
					countdown.setText("Expired!");
				}
			};
			cT.start();

			// performing match - sets the claimedBy and deadLine fields in DB to 0
			Button completeMatch = (Button) findViewById(R.id.completeDeal);
			completeMatch.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					sendUpadte(v);
				}
			});
		}
	}

	public void sendUpadte(View v) {
		String claimer = claimedBy.substring(0, claimedBy.indexOf("-"));
		String completer = uid.substring(0, uid.indexOf("-"));
		String newMsg = msg + "chatid:" + uid;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("deadLine", null));
		params.add(new BasicNameValuePair("dealid", "" + dealId));
		params.add(new BasicNameValuePair("uidNew", null));
		params.add(new BasicNameValuePair("chatid", completer + claimer));
		params.add(new BasicNameValuePair("uid", completer));
		params.add(new BasicNameValuePair("msg", newMsg));
		params.add(new BasicNameValuePair("target", claimedBy));
		new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://nir.milab.idc.ac.il/php/milab_send_deal_update.php");
	}

	// This should change to class that will make the match by send phone number or anything like this
	@Override
	public void doOnPostExecute(JSONObject jObj) {
		Intent intent = new Intent(this, ChatAfterMatch.class);
		
		Bundle extras = new Bundle();
		extras.putString("claimedBy", claimedBy);
		extras.putString("uid", uid);
		
		intent.putExtras(extras);
		startActivity(intent);
		finish();
	}
	
	// returns the difference between current time given string (format HH:MM) in milliseconds 
	private int calcMili(String timeToCalc) {
		
		// divides the time to hours and minutes
		final int hour = Integer.parseInt((String) time.subSequence(0, time.indexOf(':')));
		final int minutes = Integer.parseInt((String) time.subSequence(time.indexOf(':') + 1, time.length()));

		// Gets the time from the device and calculates the difference
		Calendar c = Calendar.getInstance(); 

		int diffhour = (int) (hour - c.get(Calendar.HOUR_OF_DAY)) ;
		int diffminute = (int) (minutes - c.get(Calendar.MINUTE));

		// taking care of time calculations
		if (diffhour < 0) {
			diffhour = diffhour + 24;
		}

		if (diffminute < 0) {
			diffhour--;
			diffminute = diffminute + 60;
		}

		// sums the difference from dead line and convert to milliseconds
		return (diffhour * 3600000) + (diffminute * 60000);
	}
}
