package com.davidgod93.objects;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidgod93.easytrans.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.himanshusoni.chatmessageview.ChatMessageView;

/**
 * Created by david on 5/6/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

	private List<Chat> l = new ArrayList<>();

	public ChatAdapter(List<Chat> l) {
		if(l != null) this.l.addAll(l);
	}

	@Override
	public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message, parent, false);
		return new ChatViewHolder(rowView);
	}

	@Override
	public void onBindViewHolder(ChatViewHolder holder, int position) {
		final Chat c = l.get(position);
		holder.text.setText(c.text);
		//holder.time.setText(c.time);
		holder.balloon.setArrowPosition(c.isMine ? ChatMessageView.ArrowPosition.RIGHT : ChatMessageView.ArrowPosition.LEFT);
		holder.balloon.setGravity(c.isMine ? Gravity.END : Gravity.START);
		if (c.isMine) {
			//holder.balloon.setBackgroundColor();
		}
	}

	@Override
	public int getItemCount() {
		return l.size() ;
	}

	public void add(Chat c) {
		l.add(c);
		Collections.sort(l);
		this.notifyDataSetChanged();
	}

	static class ChatViewHolder extends RecyclerView.ViewHolder {

		private final ChatMessageView balloon;
		private final TextView text, time = null;

		ChatViewHolder(View itemView) {
			super(itemView);
			balloon = (ChatMessageView) itemView.findViewById(R.id.cm_layout);
			text = (TextView) itemView.findViewById(R.id.cm_text);
			//time = (TextView) itemView.findViewById(R.id.cm_time);
		}
	}
}
