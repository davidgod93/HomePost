package com.davidgod93.homepost;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class IntroductionActivity extends AppCompatActivity {

	private final String TAG = "type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);
		boolean b = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autologin", false);
		if(b) {
			startActivity(new Intent(this, LoginActivity.class).putExtra(TAG, LoginActivity.LOGIN_ACT));
			finish();
		}
	}

	public void start(View v) {
		Intent i = new Intent(this, LoginActivity.class);
		switch (v.getId()) {
			case R.id.ai_login:
				i.putExtra(TAG, LoginActivity.LOGIN_ACT);
				break;
			case R.id.ai_register_bussiness:
				i.putExtra(TAG, LoginActivity.REG_BUSS_ACT);
				break;
			case R.id.ai_register_user:
				i.putExtra(TAG, LoginActivity.REG_USER_ACT);
				break;
		}
		startActivity(i);
		finish();
	}
}
