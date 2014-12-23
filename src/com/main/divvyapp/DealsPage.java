package com.main.divvyapp;

import com.main.divvyapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DealsPage extends Activity implements OnClickListener {
	
	int dealid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deals_page);
		
		dealid = 12;
		Button move = (Button) findViewById(R.id.moveToFindMatch);
		move.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, FindMeMatch.class);
		intent.putExtra("dealid", dealid);
		startActivity(intent);
		finish();
	}
}
