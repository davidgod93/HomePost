package com.davidgod93.easytrans;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.davidgod93.objects.Order;
import com.davidgod93.objects.User;
import com.davidgod93.utils.Chest;
import com.davidgod93.utils.Logger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("MissingPermission")
public class MapSelectionActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

	public static final String LAT_KEY = "latitude";
	public static final String LNG_KEY = "longitude";
	public static final String DISTANCE_KEY = "distance";
	private final String[] perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
	private final int REQUEST_CODE_LOC = 10100;
	private boolean ended = false, fromNear, ratio;
	private SupportMapFragment mapFragment;
	private EditText searchText;
	private View v;
	private GoogleMap map;
	private LocationManager lm;
	private Marker mk;
	private LatLng c;
	private LatLng p;
	private ValueEventListener vel;
	private Map<String, Order> o = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_select);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ams_map);
		v = findViewById(R.id.ams_loading);
		v.setVisibility(View.INVISIBLE);
		TextView t = (TextView) findViewById(R.id.upl_progress_text);
		searchText = (EditText) findViewById(R.id.ams_search);
		boolean search = getIntent().getBooleanExtra("searchable", true);
		fromNear = getIntent().getBooleanExtra("from-near", false);
		double lat = getIntent().getDoubleExtra(LAT_KEY, Double.MIN_VALUE), lon = getIntent().getDoubleExtra(LNG_KEY, Double.MIN_VALUE);
		ratio = lat != Double.MIN_VALUE || lon != Double.MIN_VALUE;
		if (ratio) p = new LatLng(lat, lon);
		if (!search) findViewById(R.id.ams_search_layout).setVisibility(View.GONE);
		t.setText(R.string.obtaining_location);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		requestPermissions();
		ended = false;
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
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		googleMap.setMyLocationEnabled(true);
	}

	@Override
	public void onLocationChanged(Location location) {
		c = new LatLng(location.getLatitude(), location.getLongitude());
		float z = fromNear ? 7.65f : 15f;
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(c, z);
		map.animateCamera(cameraUpdate);
		if (fromNear) {
			FirebaseDatabase db = FirebaseDatabase.getInstance();
			vel = db.getReference("orders").addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					try {
						List<Order> l = new ArrayList<>();
						JSONObject c,  j = Chest.parseDBResponse(dataSnapshot);
						JSONArray n;
						String k;
						for (int i=0; i<j.length(); i++) {
							n = j.names();
							k = n.getString(i);
							c = j.getJSONObject(k);
							l.add(new Order(c.put("id", k)));
						}
						showNearOrders(l);
					} catch (Exception e) {
						onCancelled(DatabaseError.fromException(e));
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Logger.error(databaseError.toString());
					Toast.makeText(MapSelectionActivity.this, R.string.error_near_orders, Toast.LENGTH_SHORT).show();
					finish();
				}
			});
		}
		else {
			v.setVisibility(View.GONE);
			MarkerOptions m = new MarkerOptions().title("Dirección de entrega").position(c);
			mk = map.addMarker(m);
			map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				@Override
				public void onMapClick(LatLng latLng) {
					mk.setPosition(latLng);
				}
			});
			ended = true;
		}
		lm.removeUpdates(this);
	}

	@Override
	protected void onDestroy() {
		if (vel != null) FirebaseDatabase.getInstance().getReference("orders").removeEventListener(vel);
		super.onDestroy();
	}

	private void showNearOrders(List<Order> list) {
		v.setVisibility(View.GONE);
		View vv = findViewById(R.id.floatingActionButton);
		if (vv != null) vv.setVisibility(View.GONE);
		map.setOnInfoWindowClickListener(this);
		final double MAX_RELATIVE_DISTANCE = 1.5;
		double dist;
		JSONObject mp = new JSONObject();
		try {
			mp = new JSONObject(getIntent().getStringExtra(User.USER_ASSOC));
		}
		catch (JSONException e) { e.printStackTrace(); }
		for (Order o: list) {
			dist = Math.sqrt(Math.pow(c.latitude-o.coords.latitude, 2) + Math.pow(c.longitude-o.coords.longitude, 2));
			if (dist <= MAX_RELATIVE_DISTANCE) {  //Filtrado de encargos lejanos
				this.o.put(o.id, o);
				MarkerOptions m = new MarkerOptions().title(getSenderMarker(mp, o.sender)).snippet(o.getDescription(getResources())).position(o.coords);
				if (o.hasWorker()) m = m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				Marker mk = map.addMarker(m);
				mk.setTag(o.id);
			}
		}
		ended = true;
	}



	public String getSenderMarker(JSONObject mappings, String s) {
		String sender;
		try {
			sender = mappings.getString(s);
		} catch (JSONException e) {
			e.printStackTrace();
			sender = s;
		}
		return getResources().getString(R.string.order_from) + " " + sender;
	}

	public void searchLocation(View v) {
		try {
			if (map != null && ended) {
				String t = searchText.getText().toString();
				Geocoder g = new Geocoder(this);
				List<Address> list = g.getFromLocationName(t, 1);
				if (list.size() > 0) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					Address a = list.get(0);
					LatLng l = new LatLng(a.getLatitude(), a.getLongitude());
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(l, 15);
					map.animateCamera(cameraUpdate);
					MarkerOptions m = new MarkerOptions().title("Dirección de entrega").position(l);
					mk = map.addMarker(m);
				}
				else new AlertDialog.Builder(this)
						.setTitle("Sin resultados")
						.setMessage("No se ha encontrado ningún resultado")
						.setPositiveButton("OK", null)
						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		LatLng d = mk.getPosition();
		i.putExtra(LAT_KEY, d.latitude);
		i.putExtra(LNG_KEY, d.longitude);
		if (ratio) i.putExtra(DISTANCE_KEY, Math.sqrt(Math.pow(p.latitude - d.latitude, 2) + Math.pow(p.longitude - d.longitude, 2)));
		setResult(RESULT_OK, i);
		finish();
	}

	private void selectOrder(Marker m) {
		Order o = this.o.get(String.valueOf(m.getTag()));
		Intent i = new Intent();
		i.putExtra(Order.ORDER_INTENT, o.serialize());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onInfoWindowClick(final Marker marker) {
		if (!this.o.get(String.valueOf(marker.getTag())).hasWorker())
			new AlertDialog.Builder(this)
					.setTitle("Marcador seleccionado")
					.setMessage("Estás seguro que quieres aceptar este encargo")
					.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							selectOrder(marker);
						}
					})
					.setNegativeButton("Cancelar", null)
					.show();
		else Toast.makeText(this, "Este trabajo ya se encuentra asignado", Toast.LENGTH_SHORT).show();
	}
}
