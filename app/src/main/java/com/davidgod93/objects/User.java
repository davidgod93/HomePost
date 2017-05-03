package com.davidgod93.objects;

import com.davidgod93.homepost.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by David on 23/02/2017.
 */

public class User {

	public static final int DEFAULT_IMAGE = R.drawable.avatar_bald;
	public static final String INTENT_TAG = "USER-SERIALIZATION";
	public String uid, name, mail, type;
	public int image;

	User(String uid, String name, String mail, String type, int image) {
		this.uid = uid;
		this.name = name;
		this.mail = mail;
		this.type = type;
		this.image = image;
	}

	public boolean isNewUser() {
		return "Usuario anónimo".equals(name);
	}

	public boolean hasImage() {
		return this.image != DEFAULT_IMAGE;
	}

	public void updateInfo() {
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		DatabaseReference r = db.getReference("users").child(uid);
		r.child("mail").setValue(mail);
		r.child("nombre").setValue(name);
		r.child("imagen").setValue(image);
	}

	public String serialize() {
		JSONObject j = new JSONObject();
		try {
			j.put("uid", uid);
			j.put("name", name);
			j.put("mail", mail);
			j.put("type", type);
			j.put("image", image);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return j.toString();
	}

	public static User deserialize(String json) {
		User u = null;
		try {
			JSONObject j = new JSONObject(json);
			u = new User(j.getString("uid"), j.getString("name"), j.getString("mail"), j.getString("type"), j.getInt("image"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return u;
	}
}
