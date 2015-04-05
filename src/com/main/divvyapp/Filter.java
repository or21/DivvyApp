package com.main.divvyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Filter extends Activity {

	Context context;
	final String[] categories = {"all", "apparel children", "apparel men", "apparel woman",
			"women's shoes", "men's shoes", "health and beauty",
			"home furnishing decor", "jewellery & accessories", "technology",
			"baby"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		
		context = getApplicationContext();
		
		ArrayAdapter adapter = new ArrayAdapter(this,R.layout.filter_list_item , categories);
		ListView listview = (ListView) findViewById(R.id.listViewId);
		listview.setAdapter(adapter);
		
		listview.setClickable(true);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(context, StorePage.class);
				intent.putExtra("filter", categories[position]);
				startActivity(intent);
			}
		});

	}
}
