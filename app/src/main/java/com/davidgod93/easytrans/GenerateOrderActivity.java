package com.davidgod93.easytrans;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.davidgod93.objects.Order;
import com.davidgod93.objects.User;
import com.davidgod93.utils.Logger;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class GenerateOrderActivity extends AppCompatActivity {


	private final int MAP_REQUEST_CODE = 55546;
	private double dist = 0.0;
	private User u;
	private LatLng l;
	private JSONObject mappings;
	private Spinner receiver;
	private EditText destination,
					description;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_order);
		String j = getIntent().getStringExtra(User.INTENT_TAG);
		if(j != null) {
			u = User.deserialize(j);
		}
		try {
			mappings = new JSONObject(getIntent().getStringExtra(User.USER_ASSOC));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		destination = (EditText) findViewById(R.id.ago_destination);
		description = (EditText) findViewById(R.id.ago_description);
		receiver = (Spinner) findViewById(R.id.ago_receiver);
		fillUsersSpinner();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
			double lat = data.getDoubleExtra(MapSelectionActivity.LAT_KEY, 0.0);
			double lng = data.getDoubleExtra(MapSelectionActivity.LNG_KEY, 0.0);
			dist = data.getDoubleExtra(MapSelectionActivity.DISTANCE_KEY, 0.0);
			l = new LatLng(lat, lng);
			getLatitudeText(lat, lng);
			destination.setText(String.format(Locale.getDefault(), "lat(%f) (%f)", l.latitude, l.longitude));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.generate_order_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void fillUsersSpinner() {
		//Obteniendo usuarios
		try {
			final String[] names = new String[mappings.length()];
			final JSONArray keys = mappings.names();
			for (int i=0; i<mappings.length(); i++) {
				 names[i] = mappings.getString(keys.getString(i));
			}
			receiver.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getMapPosition() {
		startActivityForResult(new Intent(GenerateOrderActivity.this, MapSelectionActivity.class)
				.putExtra(MapSelectionActivity.LAT_KEY, u.lat)
				.putExtra(MapSelectionActivity.LNG_KEY, u.lng), MAP_REQUEST_CODE);
	}

	public void mapBtn(View v) {
		getMapPosition();
	}

	public void uploadBtn(MenuItem i) {
		if(l == null) {
			destination.setError(getString(R.string.minimum_required));
			destination.requestFocus();
		}
		else {
			new AlertDialog.Builder(this)
					.setTitle(R.string.publish_order)
					.setMessage(R.string.everything_ok)
					.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showPrice();
						}
					})
					.setNegativeButton(R.string.cancel, null)
					.show();
		}
	}

	private void showPrice() {
		String pp = new DecimalFormat("#.##").format(dist * 150);
		new AlertDialog.Builder(this)
				.setTitle("Precio del servicio")
				.setMessage("En función de la distancia relativa estimada ("+dist+") se ha calculado que el precio del servicio será de "+pp+"€.\n¿Confirmamos la publicación del pedido?")
				.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						upload();
						finish();
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.show();

	}

	private void upload() {
		String desc = description.getText().toString();
		String rcvr = receiver.getSelectedItem().toString();
		Order o = new Order(u.uid, desc, rcvr, l);
		o.upload();
	}

	private void getLatitudeText(double lt, double ln) {
		destination.setText(R.string.retreiving_address);
		getAddressNameInBackground(lt, ln);
	}

	private String getLatitudeTextAux(double lt, double ln) throws IOException {
		Geocoder g = new Geocoder(this, Locale.getDefault());
		List<Address> l = g.getFromLocation(lt, ln, 10);
		String r;
		if (l.size() > 0) {
			r = l.get(0).getAddressLine(0);
		}
		else {
			DecimalFormat newFormat = new DecimalFormat("#.##");
			r = "lat("+newFormat.format(lt)+"),lng("+newFormat.format(ln)+")";
		}
		return r;
	}

	private void getAddressNameInBackground(final double lt, final double ln) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final String n = getLatitudeTextAux(lt, ln);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							destination.setText(n);
						}
					});
				} catch (Exception e) { Logger.error("Error obteniendo la dirección del usuario. "+e.toString()); }

			}
		}).start();
	}
}
