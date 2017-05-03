package com.davidgod93.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class TokenRegistration extends FirebaseInstanceIdService {

	@Override
	public void onTokenRefresh() {
		String t = FirebaseInstanceId.getInstance().getToken();
		SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
		e.putString("token", t);
		e.apply();
		super.onTokenRefresh();
	}
}
