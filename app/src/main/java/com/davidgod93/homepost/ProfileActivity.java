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

public class ProfileActivity extends AppCompatActivity {

	private static final int AVATAR_REQUEST_CODE = 55545;
	private User u;
	private EditText name, mail;
	private ImageView avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		u = User.deserialize(getIntent().getStringExtra(User.INTENT_TAG));
		name = (EditText) findViewById(R.id.ap_name);
		mail = (EditText) findViewById(R.id.ap_mail);
		avatar = (ImageView) findViewById(R.id.ap_avatar);
		setUserValues();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(AVATAR_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
			u.image = data.getIntExtra("AVATAR", -1);
			Picasso.with(this).load(u.image).into(avatar);
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
							u.updateInfo();
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
						u.updateInfo();
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

	private void setUserValues() {
		name.setText(u.name);
		mail.setText(u.mail);
		Picasso.with(this).load(u.image).into(avatar);
	}
}
