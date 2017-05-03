package com.davidgod93.objects;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.davidgod93.utils.Chest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by David on 23/02/2017.
 */

public class Users {

	private HashMap<String, User> map = new HashMap<>();

	public Users(@NonNull JSONObject j) {
		String k;
		JSONArray a = j.names();
		JSONObject ju;
		try {
			for (int i=0; i<a.length(); i++) {
				k = a.getString(i);
				ju = j.getJSONObject(k);
				map.put(k, new User(k, ju.getString("nombre"), ju.getString("mail"), ju.getString("tipo"), ju.getInt("imagen")));
			}
		} catch (JSONException e) { e.printStackTrace(); }
	}

	public @Nullable User getUser(String uid) {
		return map.get(uid);
	}

	public void saveUser(@NonNull User u) {
		map.put(u.uid, u);
		u.updateInfo();
	}

	public User getMyself() {
		return map.get(Chest.myId);
	}
}
