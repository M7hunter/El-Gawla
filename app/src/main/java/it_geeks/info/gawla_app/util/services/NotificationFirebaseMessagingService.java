package it_geeks.info.gawla_app.util.services;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.NotificationBuilder;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled()) {
            super.onMessageReceived(remoteMessage);
            // auto
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);

            // notification when app open
            if (remoteMessage.getNotification() != null) {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();

                try {
                    if (!title.isEmpty() && !body.isEmpty()) {
                        new NotificationBuilder(this).displayRemoteMessage(title, body);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }

        // have a new notification
        SharedPrefManager.getInstance(getApplicationContext()).setNewNotification(true);
        GawlaDataBse.getInstance(this).notificationDao().updateStatusNotification(true);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d("on_fire", "onNew-firebaseToken:: " + token);
        SharedPrefManager.getInstance(this).setFirebaseToken(token);
        Common.Instance().updateFirebaseToken(this);
    }
}
