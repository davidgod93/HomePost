package com.davidgod93.easytrans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.davidgod93.objects.Order;
import com.davidgod93.objects.User;
import com.davidgod93.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class NearOrdersActivity extends AppCompatActivity {

	private final int REQUEST_CODE = 10111;
	User u;
	Order o;
	JSONObject m = new JSONObject();
	View w, ly;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_orders);
		u = User.deserialize(getIntent().getStringExtra(User.INTENT_TAG));
		try { m = new JSONObject(getIntent().getStringExtra(User.USER_ASSOC)); }
		catch (JSONException e) { e.printStackTrace(); }
		w = findViewById(R.id.ano_loading_view);
		ly = findViewById(R.id.ano_content_view);
		startActivityForResult(new Intent(this, MapSelectionActivity.class)
				.putExtra("searchable", false)
				.putExtra("from-near", true)
				.putExtra(User.USER_ASSOC, m.toString()), REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
			w.setVisibility(View.GONE);
			ly.setVisibility(View.VISIBLE);
			o = Order.fromJSONString(data.getStringExtra(Order.ORDER_INTENT));
			Logger.info(String.valueOf(o));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
