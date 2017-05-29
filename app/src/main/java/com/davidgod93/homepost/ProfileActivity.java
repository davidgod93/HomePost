package com.davidgod93.homepost;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.davidgod93.objects.User;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {

	private static final int AVATAR_REQUEST_CODE = 55545;
	private static final int MAP_REQUEST_CODE = 22340;
	private User u;
	private EditText name, mail, address;
	private ImageView avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		u = User.deserialize(getIntent().getStringExtra(User.INTENT_TAG));
		name = (EditText) findViewById(R.id.ap_name);
		mail = (EditText) findViewById(R.id.ap_mail);
		address = (EditText) findViewById(R.id.ap_address);
		avatar = (ImageView) findViewById(R.id.ap_avatar);
		setUserValues();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if (AVATAR_REQUEST_CODE == requestCode) {
				u.image = data.getIntExtra("AVATAR", -1);
				Picasso.with(this).load(u.image).into(avatar);
			}
			else if (MAP_REQUEST_CODE == requestCode) {
				u.lat = data.getDoubleExtra(MapSelectionActivity.LAT_KEY, 0.0);
				u.lng = data.getDoubleExtra(MapSelectionActivity.LNG_KEY, 0.0);
				address.setText(getLatitudeText(u.lat, u.lng));
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profile_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.pa_menu_save) {
			new AlertDialog.Builder(this)
					.setTitle("Guardar cambios")
					.setMessage("¿Desea guardar los cambios realizados en su perfil?")
					.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							u.name = name.getText().toString();
							u.mail = mail.getText().toString();
							u.updateInfo(ProfileActivity.this);
						}
					})
					.setNegativeButton("No", null)
					.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Guardar cambios")
				.setMessage("¿Desea guardar los cambios realizados en su perfil y salir?")
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						u.name = name.getText().toString();
						u.mail = mail.getText().toString();
						u.updateInfo(ProfileActivity.this);
						ProfileActivity.super.onBackPressed();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ProfileActivity.super.onBackPressed();
					}
				})
				.setNeutralButton("Cancelar", null)
				.show();
	}

	public void changeImage(View v) {
		startActivityForResult(new Intent(this, AvatarSelectionActivity.class), AVATAR_REQUEST_CODE);
	}

	public void changeAddress(View v) {
		startActivityForResult(new Intent(this, MapSelectionActivity.class), MAP_REQUEST_CODE);
	}

	private void setUserValues() {
		name.setText(u.name);
		mail.setText(u.mail);
		address.setText(getLatitudeText(u.lat, u.lng));
		Picasso.with(this).load(u.image).error(User.DEFAULT_IMAGE).into(avatar);
	}

	private String getLatitudeText(double lt, double ln) {
		DecimalFormat newFormat = new DecimalFormat("#.##");
		return "lat("+Double.valueOf(newFormat.format(lt))+"),lng("+Double.valueOf(newFormat.format(ln))+")";
	}
}
