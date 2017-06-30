package com.davidgod93.easytrans;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.davidgod93.objects.Chat;
import com.davidgod93.objects.ChatAdapter;
import com.davidgod93.objects.Database;
import com.davidgod93.services.Push;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

	private RecyclerView chat;
	private EditText t;
	private List<Chat> chats = new ArrayList<>();
	private String token, myToken, name;
	private static ChatAdapter a;
	private static ChatActivity inst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		chat = (RecyclerView) findViewById(R.id.ac_chat_layout);
		t = (EditText) findViewById(R.id.ac_send_text);
		token = getIntent().getStringExtra("token");
		myToken = getIntent().getStringExtra("mytoken");
		name = getIntent().getStringExtra("name");
		inst = this;
		chat.setLayoutManager(new GridLayoutManager(this, 1));
		if(token != null) getChats();
		else {
			Toast.makeText(this, "Error obteniendo el token del usuario", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		inst = null;
		super.onDestroy();
	}

	private void showChats() {
		Collections.sort(chats);
		a = new ChatAdapter(chats);
		chat.setAdapter(a);
		chat.smoothScrollToPosition(a.getItemCount());
	}

	private void getChats() {  //DB Implementation
		Database db = new Database(this);
		chats.clear();
		chats.addAll(db.getMessages(token));
		showChats();
	}

	public static boolean isActive() {
		return inst != null;
	}

	public static void chatReceived(Context c, String token, String txt, long time) {
		final Chat ch = new Chat(txt, time, false);
		Database db = new Database(c);
		db.storeMessage(ch, token);
		if(inst != null) {
			inst.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					inst.addMessage(ch, false);  //Activity visible. Show message now
				}
			});
		}
	}

	private void addMessage(Chat c, boolean store) {
		a.add(c);
		if (store) storeChat(this, c);
		chat.smoothScrollToPosition(a.getItemCount());
	}

	public void sendMessage(View v) {
		String s = t != null ? t.getText().toString().trim() : null;
		if(s != null) {
			t.setText(null);
			send(s);
		}
	}

	private void send(String txt) {
		Chat c = new Chat(txt, new Date().getTime(), true);
		a.add(c);
		storeChat(this, c);
		try { sendPush(c); } catch (JSONException e) { e.printStackTrace(); }
		chat.smoothScrollToPosition(a.getItemCount());
	}

	private void sendPush(Chat c) throws JSONException {
		Push.sendPush(this, this.token, getJSONData(c));
	}

	private JSONObject getJSONData(Chat c) throws JSONException {
		JSONObject j = new JSONObject();
		j.put("message", c.text);
		j.put("time", c.timestamp);
		j.put("sender", this.myToken);
		j.put("not_title", "Nuevo mensaje");
		j.put("not_body", "Has recibido un nuevo mensaje en el chat de "+name);
		return j;
	}

	private void storeChat(Context c, Chat ch) {
		Database db = new Database(c);
		db.storeMessage(ch, token);
	}
}
