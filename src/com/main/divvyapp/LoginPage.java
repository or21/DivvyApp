package com.main.divvyapp;

import helpeMethods.DataTransfer;
import helpeMethods.ServerAsyncParent;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.main.divvyapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * test test
 * @author nleinov
 *
 */
public class LoginPage extends Activity implements OnClickListener , ServerAsyncParent {

	EditText userName ;
	EditText email;
	EditText phone;
	Context context;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pref = getSharedPreferences(null, Context.MODE_PRIVATE);
		context = getApplicationContext();
		editor = pref.edit();

		Log.i("Login", pref.getString("uid", "error"));
		if (!checkIfAlreadyLoggedIn().equals("error")) {
			View v = new View(context);
			goToDealsPageAndFinish(v);
		}

		else {
			setContentView(R.layout.activity_login_page);

			//get input text objects
			userName = (EditText) findViewById(R.id.username);
			email = (EditText) findViewById(R.id.email);
			phone = (EditText) findViewById(R.id.phone);
			uid = UUID.randomUUID().toString();

			Button login = (Button) findViewById(R.id.login);
			login.setOnClickListener(this);
		}
	}

	private String checkIfAlreadyLoggedIn() {
		return pref.getString(uid, "error");
	}

	public void sendData(View v) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", userName.getText().toString()));
		params.add(new BasicNameValuePair("email", email.getText().toString()));
		params.add(new BasicNameValuePair("phone", phone.getText().toString()));
		params.add(new BasicNameValuePair("uid", uid));
		new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://10.10.7.35/php/milab_send_details.php");
	}

	@Override
	public void onClick(View v) {
		editor.putString("uid", uid);
		editor.commit();
		sendData(v);
		goToDealsPageAndFinish(v);
	}

	public void goToDealsPageAndFinish(View v) {
		Intent intent = new Intent(this, DealsPage.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void doOnPostExecute(JSONObject jObj) {
			View v = new View(context);
			goToDealsPageAndFinish(v);
		}
}
