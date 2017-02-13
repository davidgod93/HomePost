package com.davidgod93.kolibri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

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

public class Kolibri {

	/**
	 * Methods for this application
	 */
	public static KolibriResponse requestToFirebase(String scriptName) throws IOException {
		return requestToFirebase(scriptName, null);
	}

	public static KolibriResponse requestToFirebase(String scriptName, Map<String, String> params) throws IOException {
		return request(checkExtension(scriptName), params);
	}

	private static String checkExtension(String script) {
		return script.endsWith(".php") ? script : script+".php";
	}

	/**
	 * This method retrieves the response object from the server.
	 * @param url Server request link
	 * @return KolibriResponse
	 * @throws IOException
	 */
	public static KolibriResponse request(String url) throws IOException {
		return request(url, null);
	}
	
	/**
	 * This method retrieves the response object from the server using some parameters.
	 * @param url Server request link
	 * @param params POST (Key, Value) params with map structure
	 * @return KolibriResponse
	 * @throws IOException
	 */
	public static KolibriResponse request(String url, Map<String, String> params) throws IOException {
		HttpURLConnection c = requestBody(url, params);
		BufferedReader rd = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
		String l, b = "";
		while ((l = rd.readLine()) != null) { b += l; }
		rd.close();
		return new KolibriResponse(b, c.getResponseCode());
	}
	
	/**
	 * Base method to get information from a request.
	 * @param url Server request link
	 * @param params POST (Key, Value) params with map structure
	 * @return HttpURLConnection Java Connection Object to use its properties.
	 * @throws IOException
	 */
	private static HttpURLConnection requestBody(String url, Map<String, String> params) throws IOException {
		// Read POST parameters
		StringBuilder p = new StringBuilder();
		if(params != null) for (Map.Entry<String, String> value : params.entrySet()) {
			if (p.length() != 0) p.append('&');
			p.append(URLEncoder.encode(value.getKey(), "UTF-8"));
			p.append('=');
			p.append(URLEncoder.encode(String.valueOf(value.getValue()), "UTF-8"));
		}
		byte[] pb = p.toString().getBytes("UTF-8");
		// Establish connection and features
		HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
		c.setRequestMethod("POST");
		c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		c.setRequestProperty("Content-Length", String.valueOf(pb.length));
		c.setDoOutput(true);
		c.getOutputStream().write(pb);
		return c;
	}
}
