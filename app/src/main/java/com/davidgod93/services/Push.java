package com.davidgod93.services;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.davidgod93.utils.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 11/6/17.
 */

public class Push {

	private static final String API_TOKEN = "AAAA4jOIAwU:APA91bEWwaXLbW58-RqJBvaqWofSXLlB1I_rA2OILqQ7ec_ioFVCo6LUwxfy6mE_Fp7abec_yzGwqqywkQBgViHzg90q-8I8NhrA8L_lCSefRPamL-l7odNVXkRAxbPMAwv1nJUXmCWr";
	private static final Response.Listener<JSONObject> defaultListener = new Response.Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject response) {
			Logger.info(response.toString());
		}
	};
	private static final Response.ErrorListener defaultErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Logger.error(error.toString());
		}
	};

	public static void sendPush(Context c, String token, JSONObject data) {
		sendPush(c, token, data, defaultListener, defaultErrorListener);
	}

	public static void sendPush(Context c, String token, JSONObject data, Response.Listener<JSONObject> responseListener) {
		sendPush(c, token, data, responseListener, defaultErrorListener);
	}

	public static void sendPush(Context c, String token, JSONObject data, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
		try {
			RequestQueue r = Volley.newRequestQueue(c);
			JSONObject req = new JSONObject();
			req.put("to", token);
			req.put("data", data);
			r.add(new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", req, responseListener, errorListener) {
				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String, String>  params = new HashMap<>();
					params.put("Content-Type", "application/json");
					params.put("Authorization", "key="+API_TOKEN);
					return params;
				}
			});
		} catch (Exception e) {
			Logger.error("Error enviado mensaje push. "+e.toString());
		}
	}
}
