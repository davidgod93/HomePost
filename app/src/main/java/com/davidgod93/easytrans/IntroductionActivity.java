package com.davidgod93.easytrans;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.davidgod93.utils.Logger;

public class IntroductionActivity extends AppCompatActivity {

	private final String TAG = "type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);
		boolean b = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autologin", false);
		logToken();
		if(b) {
			startActivity(new Intent(this, LoginActivity.class).putExtra(TAG, LoginActivity.LOGIN_ACT));
			finish();
		}
	}

	public void login(View v) {
		start(LoginActivity.LOGIN_ACT);
	}

	public void register(View v) {
		new AlertDialog.Builder(this)
				.setTitle("Selecciona el tipo de cuenta")
				.setItems(R.array.registration_modes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						start(which+1);
					}
				})
				.show();
	}

	public void start(int type) {
		startActivity(new Intent(this, LoginActivity.class).putExtra(TAG, type));
		finish();
	}

	private void logToken() {
		SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
		String t = s.getString("token", null);
		Logger.info("Token ("+t+")");
	}
}
