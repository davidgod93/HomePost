package com.davidgod93.utils;

import android.app.Activity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by David on 13/02/2017.
 */

public class Kolibri {

	public static Kolibri.Init with(Activity c) {
		return new Init(c);
	}

	public static class Init {

		private Activity c;

		private Init(Activity ctx) {
			this.c = ctx;
		}

		public Request get(String url) {
			return new Request(c, url);
		}
	}

	public static class Request {

		private String url, params;  //Only GET implemented
		private Response.Listener<JSONObject> callbackJson;
		private Response.Listener<String> callbackString;
		private Response.ErrorListener error;
		private Activity c;
		private RequestQueue q;
		private boolean asJson = false;

		private Request(Activity ctx, String url) {
			this.url = url;
			this.c = ctx;
			q = Volley.newRequestQueue(c);
		}

		public Request withParams(Map<String, String> params) {
			String k, v;
			boolean first = true;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(first) { first = false; this.params = "?"; }
				else this.params += "&";
				k = entry.getKey();
				v = entry.getValue();
				this.params += k+"="+v;
			}
			return this;
		}

		public Request onResponseJson(Response.Listener<JSONObject> toRun) {
			this.callbackJson = toRun;
			return this;
		}

		public Request onResponseString(Response.Listener<String> toRun) {
			this.callbackString = toRun;
			return this;
		}

		public Request onError(Response.ErrorListener toRun) {
			this.error = toRun;
			return this;
		}

		public Request asJSON() {
			this.asJson = true;
			return this;
		}

		public void start() {
			com.android.volley.Request r;
			r = asJson ?
					new JsonObjectRequest(getUrl(), null, callbackJson, error) :
					new StringRequest(getUrl(), callbackString, error);
			q.add(r);
		}

		private String getUrl() {
			return params != null ? this.url + params : this.url;
		}
	}
}
