package com.main.divvyapp;

import helpeMethods.DataTransfer;
import helpeMethods.ServerAsyncParent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.main.divvyapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginPage extends Activity implements OnClickListener , ServerAsyncParent {

	// project name = academic-actor-818
	// project number = 49315087842
	// API key = AIzaSyB-jPVlNNqpKP-G9FzHuwEIDvsAtVwLO1U

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "google_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	static final String TAG = "Divvy";
	
	String SENDER_ID = "49315087842";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	EditText userName ;
	EditText email;
	EditText phone;
	Context context;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	String uid;
	String googleId;
	TextView mDisplay;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	String msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pref = getSharedPreferences(LoginPage.class.getSimpleName(), Context.MODE_PRIVATE);
		editor = pref.edit();
		context = getApplicationContext();
		
	       // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            googleId = getRegistrationId(context);

            if (googleId.isEmpty()) {
                registerInBackground();
            } else {
            	doAfterPlayServices();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

		// Skips login if the user already signed in
	}
	
	private void doAfterPlayServices() {
		if (!checkIfAlreadyLoggedIn().equals("error")) {
			goToDealsPageAndFinish(null);
		} else {
			initLoginActivity();
		}
	}
	
	private void initLoginActivity() {
		setContentView(R.layout.activity_login_page);
		userName = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		phone = (EditText) findViewById(R.id.phone);
		uid = UUID.randomUUID().toString();
		
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
	}

	// checks if the user already signed in for the app
	private String checkIfAlreadyLoggedIn() {
		return pref.getString("uid", "error");
	}

	// Sends user data to the DB after first sign in
	public void sendData(View v) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", userName.getText().toString()));
		params.add(new BasicNameValuePair("email", email.getText().toString()));
		params.add(new BasicNameValuePair("phone", phone.getText().toString()));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("googleId", googleId));
		new DataTransfer(this, params, DataTransfer.METHOD_POST).execute("http://nir.milab.idc.ac.il/php/milab_send_details.php");
	}

	// saves the user id in the shared preferences in order to check the login next time
	@Override
	public void onClick(View v) {
		editor.putString("uid", uid);
		editor.commit();
		sendData(v);
	}
	
	  /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

	public void goToDealsPageAndFinish(View v) {
		Intent intent = new Intent(this, DealsPage.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void doOnPostExecute(JSONObject jObj) {
		goToDealsPageAndFinish(null);
	}


	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginPage.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    googleId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + googleId;

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, googleId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                doAfterPlayServices();
            }
        }.execute(null, null, null);
    }

}
