package com.davidgod93.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.davidgod93.homepost.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by David on 23/02/2017.
 */

public class User {

	public static final int DEFAULT_IMAGE = R.drawable.avatar_bald;
	public static final String INTENT_TAG = "USER-SERIALIZATION",
								USER_ASSOC = "USER-NAME-ASSOCIATION",
								USER_UID = "uid";
	private static final String TYPE_WORKER = "chofer",
								TYPE_CLIENT = "usuario";
	public String uid, name, mail, type;
	public int image;
	public double lat, lng;

	User(String uid, String name, String mail, String type, int image, double latit, double longit) {
		this.uid = uid;
		this.name = name;
		this.mail = mail;
		this.type = type;
		this.image = image;
		this.lat = latit;
		this.lng = longit;
	}

	public boolean isNewUser() {
		return "Usuario anónimo".equals(name);
	}

	public boolean hasImage() {
		return this.image != DEFAULT_IMAGE;
	}

	public void updateInfo(Context c) {
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		DatabaseReference r = db.getReference("users").child(uid);
		r.child("mail").setValue(mail);
		r.child("nombre").setValue(name);
		r.child("imagen").setValue(image);
		r.child("address").child("lat").setValue(lat);
		r.child("address").child("lng").setValue(lng);
		FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
		if(u != null && !mail.equals(u.getEmail())) {
			u.updateEmail(mail);
			boolean al = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("autologin", false);
			if(al) {
				SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
				e.putString("mail", mail);
				e.apply();
			}
		}
	}

	public String serialize() {
		JSONObject j = new JSONObject();
		try {
			j.put("uid", uid);
			j.put("name", name);
			j.put("mail", mail);
			j.put("type", type);
			j.put("image", image);
			j.put("latitude", lat);
			j.put("longitude", lng);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return j.toString();
	}

	public static User deserialize(String json) {
		User u = null;
		try {
			JSONObject j = new JSONObject(json);
			u = new User(j.getString("uid"), j.getString("name"), j.getString("mail"), j.getString("type"), j.getInt("image"), j.getDouble("latitude"), j.getDouble("longitude"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return u;
	}

	public boolean isClient() {
		return TYPE_CLIENT.equals(type);
	}

	public boolean isWorker() {
		return TYPE_WORKER.equals(type);
	}
}
