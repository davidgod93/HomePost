package com.davidgod93.easytrans;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidgod93.objects.User;
import com.davidgod93.objects.Users;
import com.davidgod93.utils.Chest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

@SuppressWarnings("ResourceType")
public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	FirebaseDatabase db;
	DrawerLayout draw;
	Users u;
	String myId;
	View p, c, w;
	boolean d = false;

	/**
	 * Posibilidad de añadir un usuario al hacer un envío. Así notificaría también al destinatario del progreso
	 * Layout de mapas para localizar las propuestas cercanas, y para asignar un punto a la recogida
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		db = FirebaseDatabase.getInstance();
		p = findViewById(R.id.amm_loading_view);
		c = findViewById(R.id.amm_client_view);
		w = findViewById(R.id.amm_worker_view);
		((NavigationView)findViewById(R.id.activity_main_menu_sidebar)).setNavigationItemSelectedListener(this);
		setToggleActionBar();

		if(savedInstanceState != null){
			myId = savedInstanceState.getString(User.USER_UID);
		}
		else {
			myId = getIntent().getStringExtra(User.USER_UID);
		}

		getUserInfo();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(User.USER_UID, myId);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {

		switch (item.getItemId()) {
			case R.id.ndm_preferences:
				startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class).putExtra(User.INTENT_TAG, u.getMyself().serialize()));
				break;
			case R.id.ndm_credits:
				startActivity(new Intent(MainMenuActivity.this, CreditsActivity.class));
				break;
			case R.id.ndm_logout:
				new AlertDialog.Builder(MainMenuActivity.this)
						.setTitle("¿Estas seguro?")
						.setMessage("Esta acción cerrará tu sesión actual")
						.setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
							@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
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

	private void getUserInfo() {
		db.getReference("users").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				JSONObject j = Chest.parseDBResponse(dataSnapshot);
				u = new Users(j, myId);
				setUserData(u.getMyself());
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	private void setToggleActionBar() {
		Toolbar toolBar = (Toolbar) findViewById(R.id.activity_main_menu_toolbar);
		//toolBar.setTitle(R.string.title_activity_mainmenu);
		setSupportActionBar(toolBar);
		if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		draw = (DrawerLayout) findViewById(R.id.activity_main_menu_drawer);
		draw.setScrimColor(Color.TRANSPARENT);
		ActionBarDrawerToggle t = new ActionBarDrawerToggle(this, draw, toolBar, R.string.ai_register_user, R.string.ai_register_bussiness);
		t.setDrawerIndicatorEnabled(true);
		draw.addDrawerListener(t);
		t.syncState();
	}

	private void setUserData(final User u) {
		if(u.isNewUser())
			new AlertDialog.Builder(this)
					.setTitle("Lo primero de todo")
					.setMessage("El primer paso será configurar tu perfil")
					.setPositiveButton("Vamos", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class).putExtra(User.INTENT_TAG, u.serialize()));
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
			if (d) {
				Toast.makeText(this, "Sesión iniciada como "+u.name, Toast.LENGTH_SHORT).show();
				d = false;
			}
			p.setVisibility(View.GONE);
			if(u.isWorker()) w.setVisibility(View.VISIBLE);
			else if(u.isClient()) c.setVisibility(View.VISIBLE);
		}
	}

	public void generateOrder(View v) {
		if (u.getMyself().isLocationSet()) {
			startActivity(new Intent(this, GenerateOrderActivity.class)
					.putExtra(User.INTENT_TAG, u.getMyself().serialize())
					.putExtra(User.USER_ASSOC, u.getNamesMap()));
		}
		else new AlertDialog.Builder(this)
				.setTitle("Configuración necesaria")
				.setMessage("Antes de entrar aquí es necesaria tener configurada tu dirección.")
				.setPositiveButton("Configurar ahora", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class).putExtra(User.INTENT_TAG, u.getMyself().serialize()));
					}
				})
				.setNegativeButton("Cancelar", null)
				.show();
	}

	public void nearOrders(View v) {
		startActivity(new Intent(this, NearOrdersActivity.class)
				.putExtra(User.INTENT_TAG, u.getMyself().serialize())
				.putExtra(User.USER_ASSOC, u.getNamesMap()));
	}

	public void showListOrders(View v) {
		startActivity(new Intent(this, ShowOrdersActivity.class)
				.putExtra(User.INTENT_TAG, u.getMyself().serialize())
				.putExtra(User.USER_ASSOC, u.getNamesMap()));
	}

	public void chatWithUser(View v) {
		final String t = PreferenceManager.getDefaultSharedPreferences(this).getString("token", null);
		chat(u.getNames(), u.getTokens(), t);
	}

	public void chat(final String[] names, final String dTokens[], final String mToken) {
		new AlertDialog.Builder(this)
				.setTitle("Selecciona destino")
				.setItems(names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(MainMenuActivity.this, ChatActivity.class)
								.putExtra("token", dTokens[which])
								.putExtra("mytoken", mToken)
								.putExtra("name", u.getMyself().name));
					}
				})
				.show();
	}
}
