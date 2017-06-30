package com.davidgod93.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.davidgod93.utils.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 6/6/17.
 */

public class Database extends SQLiteOpenHelper {

	private static final int v = 1;
	private static final String SQL_TABLE_KEY = "%dbn%",
			SQL_CREATE_CMD = "create table if not exists T"+ SQL_TABLE_KEY +" (id integer primary key, message text, creationtime integer, ismine integer);",
			SQL_DELETE_ENTRIES = "drop table if exists T"+ SQL_TABLE_KEY +";",
			SQL_SELECT_QUERY = "select * from T"+SQL_TABLE_KEY+" order by creationtime asc;";

	private static final String dbName = "messages";

	public Database(@NonNull Context context) {
		super(context, dbName, null, v);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES.replace(SQL_TABLE_KEY, hash(dbName)));
		onCreate(db);
	}

	List<Chat> getMessages(String tName) {
		List<Chat> l = new ArrayList<>();
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(SQL_CREATE_CMD.replace(SQL_TABLE_KEY, hash(tName)));
		Cursor c = db.rawQuery(SQL_SELECT_QUERY.replace(SQL_TABLE_KEY, hash(tName)), null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			Chat ch = new Chat(c.getString(1), c.getLong(2), getBoolValue(c.getInt(3)));
			l.add(ch);
		}
		c.close();
		return l;
	}

	long storeMessage(Chat c, String tName) {
		Logger.info("Guardado mensaje en BD");
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(SQL_CREATE_CMD.replace(SQL_TABLE_KEY, hash(tName)));
		long r = db.insert("T"+hash(tName), null, getInsertContentValues(c));
		db.close();
		return r;
	}

	private String hash(@NonNull String token) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(token.getBytes(), 0, token.length());
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		return String.valueOf("".hashCode());
	}

	private boolean getBoolValue(int n) {
		return n != 0;
	}

	private ContentValues getInsertContentValues(Chat ch) {
		ContentValues c = new ContentValues(3);
		c.put("message", ch.text);
		c.put("creationtime", ch.timestamp);
		c.put("ismine", ch.isMine);
		return c;
	}
}
