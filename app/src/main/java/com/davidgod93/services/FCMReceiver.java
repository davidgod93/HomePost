package com.davidgod93.services;

import com.davidgod93.utils.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMReceiver extends FirebaseMessagingService {

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {

		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			Logger.info("Message data payload: " + remoteMessage.getData());
		}

		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Logger.info("Notificacion payload: " + remoteMessage.getNotification().getBody());
		}
	}
}
