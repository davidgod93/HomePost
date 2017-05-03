package com.davidgod93.homepost;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidgod93.objects.User;
import com.davidgod93.objects.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Map;

@SuppressWarnings("ResourceType")
public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	DrawerLayout draw;
	Users u;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		Toolbar toolBar = (Toolbar) findViewById(R.id.activity_main_menu_toolbar);
		toolBar.setTitle(R.string.app_name);
		setSupportActionBar(toolBar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		draw = (DrawerLayout) findViewById(R.id.activity_main_menu_drawer);
		draw.setScrimColor(Color.TRANSPARENT);
		ActionBarDrawerToggle t = new ActionBarDrawerToggle(this, draw, toolBar, R.string.ai_register_user, R.string.ai_register_bussiness);
		t.setDrawerIndicatorEnabled(true);
		draw.addDrawerListener(t);
		t.syncState();
		((NavigationView)findViewById(R.id.activity_main_menu_sidebar)).setNavigationItemSelectedListener(this);

		FirebaseDatabase db = FirebaseDatabase.getInstance();
		db.getReference("users").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				JSONObject j = parseDBResponse(dataSnapshot);
				u = new Users(j);
				setUserData(u.getMyself());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {

		switch (item.getItemId()) {
			case R.id.ndm_preferences:
				Intent i = new Intent(MainMenuActivity.this, ProfileActivity.class);
				i.putExtra(User.INTENT_TAG, u.getMyself().serialize());
				startActivity(i);
				break;
			case R.id.ndm_logout:
				new AlertDialog.Builder(MainMenuActivity.this)
						.setTitle("¿Estas seguro?")
						.setMessage("Esta acción cerrará tu sesión actual")
						.setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
							@SuppressLint("CommitPrefEdits")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this).edit();
								e.remove("autologin");
								e.commit();
								FirebaseAuth.getInstance().signOut();
								finish();
								startActivity(new Intent(MainMenuActivity.this, IntroductionActivity.class));
							}
						})
						.setNegativeButton("Cancelar", null)
						.show();
				break;
		}

		// Close the drawer
		draw.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()) {
			case android.R.id.home:
				if(draw.isDrawerOpen(GravityCompat.START)) draw.closeDrawer(GravityCompat.START);
				else draw.openDrawer(GravityCompat.START);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setUserData(User u) {
		if(u.isNewUser())
			new AlertDialog.Builder(this)
					.setTitle("Lo primero de todo")
					.setMessage("El primer paso será configurar tu perfil")
					.setPositiveButton("Vamos", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class));
							dialog.dismiss();
						}
					})
					.setCancelable(false)
					.show();
		else {
			TextView n = (TextView) findViewById(R.id.username);
			TextView m = (TextView) findViewById(R.id.mail);
			ImageView i = (ImageView) findViewById(R.id.profileImage);
			n.setText(u.name);
			m.setText(u.mail);
			if(u.hasImage()) Picasso.with(this).load(u.image).error(R.drawable.avatar_bald).into(i);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject parseDBResponse(DataSnapshot dataSnapshot) {
		return new JSONObject((Map<String, String>) dataSnapshot.getValue());
	}
}
