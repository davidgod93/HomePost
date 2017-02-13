package com.davidgod93.kolibri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Kolibri library.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class KolibriResponse {
	private String s;
	public final int responseCode;
	
	KolibriResponse(String s, int r) {
		this.s = s;
		this.responseCode = r;
	}
	
	public JSONObject asJSONObject() throws JSONException {
		return new JSONObject(s);
	}
	
	public JSONArray asJSONArray() throws JSONException {
		return new JSONArray(s);
	}
	
	public String asString() {
		return s;
	}
	
	@Override
	public String toString() {
		return this.asString();
	}
}
