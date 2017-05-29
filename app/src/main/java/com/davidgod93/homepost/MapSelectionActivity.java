package com.davidgod93.homepost;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressWarnings("MissingPermission")
public class MapSelectionActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

	public static final String LAT_KEY = "latitude";
	public static final String LNG_KEY = "longitude";
	private final String[] perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
	private final int REQUEST_CODE_LOC = 10100;
	private SupportMapFragment mapFragment;
	private View v;
	private GoogleMap map;
	private LocationManager lm;
	private Marker mk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_select);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ams_map);
		v = findViewById(R.id.ams_loading);
		v.setVisibility(View.INVISIBLE);
		TextView t = (TextView) findViewById(R.id.upl_progress_text);
		t.setText(R.string.obtaining_location);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		requestPermissions();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_CODE_LOC && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
				grantResults[1] == PackageManager.PERMISSION_GRANTED))
			getMap();
		else new AlertDialog.Builder(this)
				.setTitle("Se requiere el permiso")
				.setMessage("No es posible acceder a esta funcionalidad sin el servicio de ubicación. Es un requisito")
				.setCancelable(false)
				.setPositiveButton("Volver a pedir", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestPermissions();
					}
				})
				.setNegativeButton("No conceder", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.show();
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		googleMap.setMyLocationEnabled(true);
	}

	@Override
	public void onLocationChanged(Location location) {
		v.setVisibility(View.GONE);
		LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(l, 15);
		map.animateCamera(cameraUpdate);
		MarkerOptions m = new MarkerOptions().title("Dirección de entrega").position(l);
		mk = map.addMarker(m);
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				mk.setPosition(latLng);
			}
		});
		lm.removeUpdates(this);
	}

	private void requestPermissions() {
		if (ActivityCompat.checkSelfPermission(this, perms[0]) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(this, perms[1]) != PackageManager.PERMISSION_GRANTED)
			new AlertDialog.Builder(this)
					.setTitle("Permiso de localización")
					.setMessage("Esta aplicación usa el servicio de localización para poder mejorar la experiencia de usuario. Ahora va a proceder a pedir dichos permisos")
					.setCancelable(false)
					.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions(MapSelectionActivity.this, perms, REQUEST_CODE_LOC);
						}
					})
					.show();
		else getMap();
	}

	private void getMap() {
		v.setVisibility(View.VISIBLE);
		mapFragment.getMapAsync(this);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, this);
	}

	public void setMarker(View v) {
		Intent i = new Intent();
		LatLng dst = mk.getPosition();
		i.putExtra(LAT_KEY, dst.latitude);
		i.putExtra(LNG_KEY, dst.longitude);
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}
}
