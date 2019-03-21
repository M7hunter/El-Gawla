package it_geeks.info.gawla_app.general.services;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.NotificationBuilder;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // auto
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        // notification when app open
        if (remoteMessage.getNotification() != null)
            displayNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

        // have a new notification
        SharedPrefManager.getInstance(getApplicationContext()).setNewNotification(true);
        GawlaDataBse.getInstance(this).notificationDao().updateStatusNotification(true);
    }

    private void displayNotification(String title, String body) {
        try {
            if (!title.isEmpty() && !body.isEmpty()) {
               new NotificationBuilder(this).displayRemoteMessage(title, body);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onNewToken(String token) {
        try {
            Common.Instance(this).updateFirebaseToken();

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
