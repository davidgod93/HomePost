package com.davidgod93.objects;

import android.support.annotation.NonNull;

/**
 * Created by david on 5/6/17.
 */

public class Chat implements Comparable<Chat> {

	public final String text;
	public final long timestamp;
	public final boolean isMine;

	public Chat(String text, long timestamp, boolean mine) {
		this.text = text;
		this.timestamp = timestamp;
		this.isMine = mine;
	}

	@Override
	public int compareTo(@NonNull Chat o) {
		return Long.compare(this.timestamp, o.timestamp);
	}
}
