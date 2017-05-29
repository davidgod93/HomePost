package com.davidgod93.objects;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.davidgod93.homepost.R;
import com.davidgod93.utils.Chest;
import com.davidgod93.utils.Logger;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 24/5/17.
 */

public class Order implements Comparable<Order> {

	public String id, sender, receiver, worker, description;
	public Date date;
	public Status status;
	public LatLng coords;

	public static List<Order> getOrdersFrom(JSONObject j, String uid) {
		List<Order> l = new ArrayList<>();
		JSONArray jj = j.names();
		JSONObject jo, d;
		Order o;
		String n;
		try {
			for (int i=0; i<jj.length(); i++) {
				n = jj.getString(i);
				jo = j.getJSONObject(n);
				d = jo.getJSONObject("destination");
				o = new Order(n,
						jo.getString("sender"),
						jo.has("description") ? jo.getString("description") : null,
						jo.has("worker") ? jo.getString("worker") : null,
						jo.has("receiver") ? jo.getString("receiver") : null,
						jo.getInt("status"),
						d.getDouble("lat"),
						d.getDouble("lng"),
						jo.getLong("date"));
				if(o.isInvolved(uid)) l.add(o);
			}
		} catch (Exception e) {
			Logger.error("Error parseando JSON: "+e.toString());
		}
		return l;
	}

	private Order(String id, String sender, String description, String worker, String receiver, int status, double lat, double lng, long date) {
		this.id = id;
		this.description = description;
		this.sender = sender;
		this.worker = worker;
		this.receiver = receiver;
		this.status = new Status(status);
		this.coords = new LatLng(lat, lng);
		this.date = new Date(date);
	}

	private boolean isInvolved(@NonNull String uid) {
		return uid.equals(this.receiver) || uid.equals(this.sender) || uid.equals(this.worker);
	}

	public String getSender(Resources r) {
		return sender;
	}

	public String getReceiver(Resources r) {
		return receiver != null ? receiver : r.getString(R.string.sin_receptor_especificado);
	}

	public String getWorker(Resources r) {
		return worker != null ? worker : r.getString(R.string.no_worker_yet);
	}

	public String getDescription(Resources r) {
		return description != null ? description : r.getString(R.string.without_description);
	}

	public void applyMappings(Map<String, String> map) {
		if (map.get(sender) != null) sender = map.get(sender);
		if (map.get(receiver) != null) receiver = map.get(receiver);
		if (map.get(worker) != null) worker = map.get(worker);
	}

	public void upload() {
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		DatabaseReference r = db.getReference("orders").child(this.id);
		r.child("sender").setValue(this.sender);
		r.child("receiver").setValue(this.receiver);
		r.child("worker").setValue(this.worker);
		r.child("description").setValue(this.description);
		r.child("status").setValue(this.status.status);
		r.child("destination").child("lng").setValue(this.coords.longitude);
		r.child("destination").child("lat").setValue(this.coords.latitude);
		r.child("date").setValue(new Date().getTime());
	}

	public static Order testOrder() {
		return new Order(Chest.getOrderReference(), "hTB9cSQS5NfHr6jD4hoY1mTxaPA2", null, null, null, 0, -33.865143, 151.209900, new Date().getTime());
	}

	@Override
	public int compareTo(@NonNull Order o) {
		return this.date.compareTo(o.date);
	}
}
