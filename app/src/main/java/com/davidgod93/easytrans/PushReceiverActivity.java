package com.davidgod93.easytrans;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PushReceiverActivity extends AppCompatActivity {

	/*
	 *
	 * https://developers.google.com/android/reference/com/google/android/gms/gcm/GoogleCloudMessaging
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_receiver);
	}

}
