package com.davidgod93.objects;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.davidgod93.easytrans.R;
import com.davidgod93.utils.Chest;
import com.davidgod93.utils.Logger;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 24/5/17.
 */

public class Order implements Comparable<Order> {

	public static String ORDER_INTENT = "order-serialization";
	public String id, sender, receiver, worker, description;
	public Date date;
	public Status status;
	public LatLng coords;

	public static List<Order> getOrdersFrom(JSONObject j, String uid, boolean isWorker) {
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
				if(o.isInvolved(uid, isWorker)) l.add(o);
			}
		} catch (Exception e) {
			Logger.error("Error parseando JSON: "+e.toString());
		}
		return l;
	}

	public static Order fromJSONString(String jsonString) {
		try {
			JSONObject j = new JSONObject(jsonString);
			return new Order(j);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Order(JSONObject j) throws JSONException {
		this.id = j.getString("id");
		this.sender = j.getString("sender");
		this.description = j.has("description") ? j.getString("description") : null;
		this.receiver = j.has("receiver") ? j.getString("receiver") : null;
		this.worker = j.has("worker") ? j.getString("worker") : null;
		JSONObject jj = j.getJSONObject("destination");
		this.coords = new LatLng(jj.getDouble("lat"), jj.getDouble("lng"));
		this.status = new Status(j.getInt("status"));
		this.date = new Date(j.getLong("date"));
	}

	public Order(String sender, String description, String receiver, LatLng coords) {
		this.id = Chest.getOrderReference();
		this.sender = sender;
		this.description = "".equals(description) ? null : description;
		this.receiver = "".equals(receiver) ? null : receiver;
		this.coords = new LatLng(coords.latitude, coords.longitude);
		this.status = new Status(0);
		this.date = new Date();
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

	private boolean isInvolved(@NonNull String uid, boolean isWorker) {
		return isWorker ? uid.equals(this.worker) : (uid.equals(this.receiver) || uid.equals(this.sender));
	}

	public boolean hasWorker() {
		return worker != null;
	}

	public String getSender() {
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

	public String serialize() {
		JSONObject j = new JSONObject();
		try {
			j.put("id", this.id);
			j.put("sender", this.sender);
			j.put("description", this.description);
			j.put("receiver", this.receiver);
			j.put("worker", this.worker);
			JSONObject jj = new JSONObject();
			jj.put("lat", this.coords.latitude);
			jj.put("lng", this.coords.longitude);
			j.put("destination", jj);
			j.put("status", this.status.status);
			j.put("date", this.date.getTime());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return j.toString();
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

	public void updateStatus() {
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		DatabaseReference r = db.getReference("orders").child(this.id);
		r.child("status").setValue(this.status.status);
		Logger.info("Estado del encargo actualizado");
	}

	public void updateWorker() {
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		DatabaseReference r = db.getReference("orders").child(this.id);
		r.child("worker").setValue(this.worker);
		Logger.info("Trabajador asignado actualizado");
	}

	@Override
	public int compareTo(@NonNull Order o) {
		return o.date.compareTo(this.date);
	}
}
