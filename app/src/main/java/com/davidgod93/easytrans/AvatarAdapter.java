package com.davidgod93.easytrans;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by david on 3/5/17.
 */

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

	private AvatarSelectionActivity c;
	private List<Integer> avatars;

	public AvatarAdapter(AvatarSelectionActivity ctx, List<Integer> avs) {
		this.c = ctx;
		this.avatars = avs;
	}

	@Override
	public AvatarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View row = c.getLayoutInflater().inflate(R.layout.avatar, parent, false);
		return new AvatarViewHolder(row);
	}

	@Override
	public void onBindViewHolder(AvatarViewHolder holder, int position) {
		final int id = avatars.get(position);
		holder.img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				c.selectAvatar(id);
			}
		});
		Picasso.with(c).load(id).into(holder.img);
	}

	@Override
	public int getItemCount() {
		return this.avatars.size();
	}

	class AvatarViewHolder extends RecyclerView.ViewHolder {

		private final ImageView img;

		AvatarViewHolder(View itemView) {
			super(itemView);
			img = (ImageView) itemView.findViewById(R.id.avatar_img);
		}
	}
}
