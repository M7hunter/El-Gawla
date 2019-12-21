package it_geeks.info.elgawla.util.notification;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class FCM extends FirebaseMessagingService {

    private static final String TAG = "Notification";

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled())
        {
            super.onMessageReceived(remoteMessage);
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);

            Log.d(TAG, "remoteMessage:: " + remoteMessage.toString());

            try
            {
                String title = "", body = "", type = "", id = "";
                if (remoteMessage.getNotification() != null)
                {
                    title = remoteMessage.getNotification().getTitle();
                    body = remoteMessage.getNotification().getBody();
                }

                if (!remoteMessage.getData().isEmpty())
                {
                    type = remoteMessage.getData().get("type");
                    id = remoteMessage.getData().get("id");
                }

                Log.d(TAG, "onMessageReceived: type: " + type);
                if (type != null && (type.equals("salons") || type.equals("cards")))
                {
                    NotificationBuilder.Instance(getApplicationContext()).displayRemoteMessageWithData(title, body, type, id, getApplicationContext());
                }
                else
                {
                    NotificationBuilder.Instance(getApplicationContext()).displayRemoteMessage(title, body, getApplicationContext());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }

        // have a new notification
        SharedPrefManager.getInstance(getApplicationContext()).setHaveNewNotification(true);
    }

    @Override
    public void onNewToken(@NotNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "onNew-firebaseToken:: " + token);
        SharedPrefManager.getInstance(this).setFirebaseToken(token);
        Common.Instance().updateFirebaseToken(this);
    }
}
