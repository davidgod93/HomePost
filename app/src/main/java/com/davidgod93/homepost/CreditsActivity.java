package com.davidgod93.homepost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);
		TextView t = (TextView) findViewById(R.id.ac_text);
		setTitle("# " + getResources().getString(R.string.app_name) + " #");
		String txt = getResources().getString(R.string.credits_txt);
		t.setText(txt);
	}

	/**
	 * Iconos avatares atribución
	 * Author: Madebyoliver - License: Flaticon Basic License
	 * http://www.flaticon.com/packs/hairstyles
	 *
	 * https://romannurik.github.io/AndroidAssetStudio/icons-actionbar.html#source.type=clipart&source.clipart=chevron_left&source.space.trim=0&source.space.pad=0&name=ic_action_chevron_left&theme=light&color=rgba(33%2C%20150%2C%20243%2C%200.6)
	 *
	 * Iconos notificación atribución
	 * <a href="http://www.freepik.com/free-vector/logistic-delivery-icons-set_718426.htm">Designed by Freepik</a>
	 * */
}
