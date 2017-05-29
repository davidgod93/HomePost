package com.davidgod93.objects;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.davidgod93.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 23/02/2017.
 */

public class Users {

	private HashMap<String, User> map = new HashMap<>();
	private String myId;

	public Users(@NonNull JSONObject j, String myId) {
		this.myId = myId;
		String k;
		JSONArray a = j.names();
		JSONObject ju, addr;
		try {
			for (int i=0; i<a.length(); i++) {
				k = a.getString(i);
				ju = j.getJSONObject(k);
				addr = ju.getJSONObject("address");
				map.put(k, new User(k, ju.getString("nombre"), ju.getString("mail"), ju.getString("tipo"), ju.getInt("imagen"), addr.getDouble("lat"), addr.getDouble("lng")));
			}
		} catch (JSONException e) { e.printStackTrace(); }
	}

	public @Nullable User getUser(String uid) {
		return map.get(uid);
	}

	public User getMyself() {
		return map.get(myId);
	}

	public String getNamesMap() {
		JSONObject a = new JSONObject();
		try {
			for (HashMap.Entry<String, User> e : map.entrySet()) {
				a.put(e.getKey(), e.getValue().name);
			}
		} catch (Exception e) {
			Logger.error("Problema serializando los mappings de los nombres. "+e.toString());
		}
		return a.toString();
	}

	public static Map<String, String> deserializeMappings(String json) {
		Map<String, String> m = new HashMap<>();
		try {
			JSONObject j = new JSONObject(json);
			JSONArray n = j.names();
			String k;
			for (int i=0; i<n.length(); i++) {
				k = n.getString(i);
				m.put(k, j.getString(k));
			}
		} catch (Exception e) {
			Logger.error("Error deserializando mapping de JSON. "+e.toString());
		}
		return m;
	}
}
