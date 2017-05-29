package com.davidgod93.homepost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.davidgod93.objects.Order;
import com.google.android.gms.maps.model.LatLng;

public class GenerateOrderActivity extends AppCompatActivity {


	private final int MAP_REQUEST_CODE = 55546;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_order);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
			double lat = data.getDoubleExtra(MapSelectionActivity.LAT_KEY, 0.0);
			double lng = data.getDoubleExtra(MapSelectionActivity.LNG_KEY, 0.0);
			LatLng l = new LatLng(lat, lng);
			Toast.makeText(this, "LatLng recibido: "+l.toString(), Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getMapPosition() {
		startActivityForResult(new Intent(GenerateOrderActivity.this, MapSelectionActivity.class), MAP_REQUEST_CODE);
	}

	public void mapBtn(View v) {
		getMapPosition();
	}

	public void uploadBtn(View v) {
		Order.testOrder().upload();
	}
}
