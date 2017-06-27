package com.davidgod93.easytrans;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidgod93.objects.Order;
import com.davidgod93.objects.Status;
import com.davidgod93.objects.User;
import com.davidgod93.objects.Users;
import com.davidgod93.utils.Chest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ShowOrdersActivity extends AppCompatActivity implements OnMapReadyCallback {

	private FirebaseDatabase db;
	private User u;
	private Map<String, String> m;
	private View w, v;
	private RelativeLayout ly;
	private MenuItem mi, miW;
	private List<Order> l;
	private String[] titles;
	private LatLng latLng;
	private int idxOrder = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_orders);
		ActionBar b = getSupportActionBar();
		if (b != null) {
			b.setDisplayHomeAsUpEnabled(true);
			b.setHomeButtonEnabled(true);
			b.setDisplayShowHomeEnabled(true);
			b.setHomeAsUpIndicator(R.drawable.ic_action_chevron_left);
		}
		db = FirebaseDatabase.getInstance();
		u = User.deserialize(getIntent().getStringExtra(User.INTENT_TAG));
		m = Users.deserializeMappings(getIntent().getStringExtra(User.USER_ASSOC));
		w = findViewById(R.id.aso_loading_view);
		ly = (RelativeLayout) findViewById(R.id.aso_content_view);

		getData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.order_list_menu, menu);
		mi = menu.getItem(0);
		miW = menu.getItem(1);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.olm_change:
				if (titles.length > 1)
					new AlertDialog.Builder(this)
							.setTitle("Selecciona el envío a consultar")
							.setItems(titles, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									fillData(which, titles[which]);
								}
							})
							.show();
				else Toast.makeText(this, R.string.no_items_to_change, Toast.LENGTH_SHORT).show();
				break;
			case R.id.olm_change_status:
				new AlertDialog.Builder(this)
						.setTitle("Selecciona el nuevo estado")
						.setItems(R.array.status_modes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Order o = l.get(idxOrder);
								o.status = new Status(which);
								o.updateStatus();
								String n = "Envío #" + idxOrder + "#" + o.id;
								fillData(idxOrder, n);
							}
						})
						.show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getData() {
		db.getReference("orders").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				JSONObject j = Chest.parseDBResponse(dataSnapshot);
				if (j.length() > 0) {
					l = Order.getOrdersFrom(j, u.uid, u.isWorker());
					Collections.sort(l);
					updateNames();
				} else new AlertDialog.Builder(ShowOrdersActivity.this)
						.setTitle(R.string.show_orders_no_content_title)
						.setMessage(R.string.show_orders_no_data)
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {}
		});
	}

	private void updateNames() {
		for (Order o : l) o.applyMappings(m);
		setData();
	}

	private void setData() {
		if (l.isEmpty()) new AlertDialog.Builder(this)
				.setTitle("No hay envíos")
				.setMessage("No existe ningún envío relacionado contigo")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
		w.setVisibility(View.GONE);
		ly.setVisibility(View.VISIBLE);
		setSpinnerTitles();
	}

	private void setSpinnerTitles() {
		int n = l.size();
		titles = new String[n];
		for (Order o : l) titles[l.indexOf(o)] = "Envío #" + n-- + "#" + o.id;
		if(titles.length > 1 && mi != null) {
			mi.setVisible(true);
			if(u.isWorker()) miW.setVisible(true);
		}
		fillData(0, titles[0]);
	}

	private boolean fillData(int pos, String title) {
		if (v == null) v = getLayoutInflater().inflate(R.layout.aso_order_content, null);
		final Order o = l.get(pos);
		setTitle(title);
		idxOrder = pos;
		TextView sender = (TextView) v.findViewById(R.id.elvc_sender);
		sender.setText(o.getSender());
		TextView receiver = (TextView) v.findViewById(R.id.elvc_receiver);
		receiver.setText(o.getReceiver(this.getResources()));
		TextView worker = (TextView) v.findViewById(R.id.elvc_worker);
		worker.setText(o.getWorker(this.getResources()));
		SupportMapFragment m = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.aso_map_fragment);
		latLng = o.coords;
		TextView description = (TextView) v.findViewById(R.id.elvc_description);
		description.setText(o.getDescription(this.getResources()));
		TextView date = (TextView) v.findViewById(R.id.elvc_date);
		date.setText(o.date.toString());
		LinearLayout list = (LinearLayout) v.findViewById(R.id.elvc_list);
		List<Status.Definition> l = o.status.getStatusList(this);
		View ln;
		list.removeAllViews();
		for (Status.Definition d : l) {
			ln = getLayoutInflater().inflate(R.layout.aso_order_list_item, null);
			TextView t = (TextView) ln.findViewById(R.id.elvli_text);
			ImageView i = (ImageView) ln.findViewById(R.id.elvli_icon);
			t.setText(d.text);
			i.setImageResource(d.img);
			list.addView(ln);
		}
		ly.removeAllViews();
		ly.addView(v);
		m.getMapAsync(this);
		return true;
	}

	private void showMap() {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getUriFromCoords(latLng))));
	}

	private String getUriFromCoords(LatLng c) {
		return "geo:" + c.latitude + "," + c.longitude + "?q=" + c.latitude + "," + c.longitude + "(Destino del paquete)";
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		MarkerOptions m = new MarkerOptions()
				.title("Destino del paquete")
				.position(latLng)
				.draggable(false)
				.visible(true);
		googleMap.addMarker(m);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
		googleMap.setLatLngBoundsForCameraTarget(new LatLngBounds(latLng, latLng));
		googleMap.getUiSettings().setAllGesturesEnabled(false);
		googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				showMap();
			}
		});
	}


}
