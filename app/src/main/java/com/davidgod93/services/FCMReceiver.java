package com.davidgod93.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.davidgod93.easytrans.ChatActivity;
import com.davidgod93.easytrans.IntroductionActivity;
import com.davidgod93.easytrans.R;
import com.davidgod93.utils.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FCMReceiver extends FirebaseMessagingService {

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {

		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			try {
				JSONObject j = new JSONObject(remoteMessage.getData());
				String token = j.getString("sender");
				String text = j.getString("message");
				long time = j.getLong("time");
				if(!ChatActivity.isActive()) {
					String notTitle = j.getString("not_title");
					String notBody = j.getString("not_body");
					Intent i = new Intent(this, IntroductionActivity.class);
					PendingIntent pi = PendingIntent.getActivity(this, 1101, i, PendingIntent.FLAG_UPDATE_CURRENT);
					NotificationCompat.Builder b = new NotificationCompat.Builder(this)
							.setContentTitle(notTitle)
							.setSmallIcon(R.drawable.logo_notification)
							.setContentIntent(pi)
							.setContentText(notBody);
					NotificationManagerCompat.from(this).notify(b.mNumber, new NotificationCompat.BigTextStyle(b).bigText(notBody).build());
				}
				ChatActivity.chatReceived(this, token, text, time);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Logger.info("Notificacion payload: " + remoteMessage.getNotification().getBody());
		}
	}
}
