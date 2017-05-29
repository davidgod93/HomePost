package com.davidgod93.objects;

import android.content.Context;
import android.content.res.Resources;

import com.davidgod93.homepost.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 21/5/17.
 */

public class Status {

	//TODO: Usar ints y mostrar todos los menores a ese.

	final int status;
	public static final String[] ASSOC_KEYS = new String[] {"image", "title"};
	public static final int[] ASSOC_RES = new int[] { R.id.elvli_icon, R.id.elvli_text };
	private static final int STATUS_PREPARING_R = R.drawable.status_warehouse,
						STATUS_IN_TRANSIT_R = R.drawable.status_in_transit,
						STATUS_WAREHOUSE_R = R.drawable.status_warehouse,
						STATUS_CAST_IN_R = R.drawable.status_cast_in,
						STATUS_DELIVERED_R = R.drawable.status_delivered,
						STATUS_PREPARING = 0,
						STATUS_IN_TRANSIT = 1,
						STATUS_WAREHOUSE = 2,
						STATUS_CAST_IN = 3,
						STATUS_DELIVERED = 4;

	public static final Status getDefault = new Status(STATUS_PREPARING);

	Status(int status) {
		this.status = status;
	}

	private int getImage(int status) {
		int s = STATUS_PREPARING_R;
		switch (status) {
			case STATUS_PREPARING:
				s = STATUS_PREPARING_R;
				break;
			case STATUS_IN_TRANSIT:
				s = STATUS_IN_TRANSIT_R;
				break;
			case STATUS_WAREHOUSE:
				s = STATUS_WAREHOUSE_R;
				break;
			case STATUS_CAST_IN:
				s = STATUS_CAST_IN_R;
				break;
			case STATUS_DELIVERED:
				s = STATUS_DELIVERED_R;
				break;
		}
		return s;
	}

	private static String getText(Context c, int status) {
		String t = "";
		Resources r = c.getResources();
		switch (status) {
			case STATUS_PREPARING:
				t = r.getString(R.string.status_0_txt);
				break;
			case STATUS_IN_TRANSIT:
				t = r.getString(R.string.status_1_txt);
				break;
			case STATUS_WAREHOUSE:
				t = r.getString(R.string.status_2_txt);
				break;
			case STATUS_CAST_IN:
				t = r.getString(R.string.status_3_txt);
				break;
			case STATUS_DELIVERED:
				t = r.getString(R.string.status_4_txt);
				break;
		}
		return t;
	}

	public List<Definition> getStatusList(Context c) {
		List<Definition> l = new ArrayList<>();
		for (int i=0; i<=status; i++) {
			l.add(new Definition(getText(c, i), getImage(i)));
		}
		return l;
	}

	public class Definition {

		public String text;
		public int img;

		private Definition(String text, int img) {
			this.text = text;
			this.img = img;
		}
	}
}
