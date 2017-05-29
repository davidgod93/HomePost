package com.davidgod93.homepost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.davidgod93.utils.Chest;

import java.util.Arrays;

public class AvatarSelectionActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avatar_selection);
		AvatarAdapter a = new AvatarAdapter(this, Arrays.asList(Chest.avatars));
		RecyclerView r = (RecyclerView) findViewById(R.id.grid);
		r.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
		r.setAdapter(a);
	}

	public void selectAvatar(int id) {
		Intent i = new Intent();
		i.putExtra("AVATAR", id);
		setResult(RESULT_OK, i);
		finish();
	}
}
