package com.davidgod93.utils;

import android.support.annotation.DrawableRes;

import com.davidgod93.easytrans.R;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * Created by David on 13/02/2017.
 */
public class Chest {
	public static @DrawableRes Integer[] avatars = new Integer[] { R.drawable.avatar_bald, R.drawable.avatar_man_hair_00, R.drawable.avatar_man_hair_01, R.drawable.avatar_man_hair_02, R.drawable.avatar_man_hair_03, R.drawable.avatar_man_hair_04, R.drawable.avatar_man_hair_05, R.drawable.avatar_man_hair_06, R.drawable.avatar_man_hair_07, R.drawable.avatar_man_hair_08, R.drawable.avatar_man_hair_09, R.drawable.avatar_man_hair_10, R.drawable.avatar_man_hair_11, R.drawable.avatar_man_hair_12, R.drawable.avatar_man_hair_13, R.drawable.avatar_man_hair_14, R.drawable.avatar_man_hair_15, R.drawable.avatar_man_hair_16, R.drawable.avatar_man_hair_17, R.drawable.avatar_man_hair_18, R.drawable.avatar_man_hair_19, R.drawable.avatar_man_hair_20, R.drawable.avatar_man_hair_21, R.drawable.avatar_man_hair_22, R.drawable.avatar_man_hair_23, R.drawable.avatar_man_hair_24, R.drawable.avatar_man_hair_25, R.drawable.avatar_man_hair_26, R.drawable.avatar_man_hair_27, R.drawable.avatar_man_hair_28, R.drawable.avatar_man_hair_29, R.drawable.avatar_man_hair_30, R.drawable.avatar_man_hair_31, R.drawable.avatar_man_hair_32, R.drawable.avatar_man_hair_33, R.drawable.avatar_man_hair_34, R.drawable.avatar_man_hair_35, R.drawable.avatar_man_hair_36, R.drawable.avatar_man_hair_37, R.drawable.avatar_man_hair_38, R.drawable.avatar_man_hair_39, R.drawable.avatar_man_hair_40, R.drawable.avatar_man_hair_41, R.drawable.avatar_man_hair_42, R.drawable.avatar_man_hair_43, R.drawable.avatar_man_hair_44, R.drawable.avatar_man_hair_45, R.drawable.avatar_man_hair_46, R.drawable.avatar_man_hair_47, R.drawable.avatar_man_hair_48, R.drawable.avatar_man_hair_49, R.drawable.avatar_woman_hair_00, R.drawable.avatar_woman_hair_01, R.drawable.avatar_woman_hair_02, R.drawable.avatar_woman_hair_03, R.drawable.avatar_woman_hair_04, R.drawable.avatar_woman_hair_05, R.drawable.avatar_woman_hair_06, R.drawable.avatar_woman_hair_07, R.drawable.avatar_woman_hair_08, R.drawable.avatar_woman_hair_09, R.drawable.avatar_woman_hair_10, R.drawable.avatar_woman_hair_11, R.drawable.avatar_woman_hair_12, R.drawable.avatar_woman_hair_13, R.drawable.avatar_woman_hair_14, R.drawable.avatar_woman_hair_15, R.drawable.avatar_woman_hair_16, R.drawable.avatar_woman_hair_17, R.drawable.avatar_woman_hair_18, R.drawable.avatar_woman_hair_19, R.drawable.avatar_woman_hair_20, R.drawable.avatar_woman_hair_21, R.drawable.avatar_woman_hair_22, R.drawable.avatar_woman_hair_23, R.drawable.avatar_woman_hair_24, R.drawable.avatar_woman_hair_25, R.drawable.avatar_woman_hair_26, R.drawable.avatar_woman_hair_27, R.drawable.avatar_woman_hair_28, R.drawable.avatar_woman_hair_29, R.drawable.avatar_woman_hair_30, R.drawable.avatar_woman_hair_31, R.drawable.avatar_woman_hair_32, R.drawable.avatar_woman_hair_33, R.drawable.avatar_woman_hair_34, R.drawable.avatar_woman_hair_35, R.drawable.avatar_woman_hair_36, R.drawable.avatar_woman_hair_37, R.drawable.avatar_woman_hair_38, R.drawable.avatar_woman_hair_39, R.drawable.avatar_woman_hair_40, R.drawable.avatar_woman_hair_41, R.drawable.avatar_woman_hair_42, R.drawable.avatar_woman_hair_43, R.drawable.avatar_woman_hair_44, R.drawable.avatar_woman_hair_45, R.drawable.avatar_woman_hair_46, R.drawable.avatar_woman_hair_47, R.drawable.avatar_woman_hair_48 };

	@SuppressWarnings("unchecked")
	public static JSONObject parseDBResponse(DataSnapshot dataSnapshot) {
		Map m = (Map) dataSnapshot.getValue();
		return m != null ? new JSONObject(m) : new JSONObject();
	}

	/**
	 * Generates a random string chain by default length
	 * @return Random string chain
	 */
	public static String getOrderReference() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
