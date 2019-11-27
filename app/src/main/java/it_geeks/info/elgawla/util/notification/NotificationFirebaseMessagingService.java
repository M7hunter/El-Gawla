package it_geeks.info.elgawla.util.notification;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Notification";

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled())
        {
            super.onMessageReceived(remoteMessage);
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);

            Log.d(TAG, "data:: " + remoteMessage.toString());
            Log.d(TAG, "salon_id:: " + SharedPrefManager.getInstance(getApplicationContext()).getSubscribedSalonId());

            try
            {
                if (remoteMessage.getData().isEmpty())
                {
                    if (remoteMessage.getNotification() != null)
                    {
                        String title = remoteMessage.getNotification().getTitle();
                        String body = remoteMessage.getNotification().getBody();


                        if (!title.isEmpty() && !body.isEmpty())
                        {
                            NotificationBuilder.displayRemoteMessage(title, body, getApplicationContext());
                        }
                    }
                }
                else
                {
                    Map<String, String> data = remoteMessage.getData();

                    Log.d(TAG, "data:: " + data.toString());

                    String title = data.get("title");
                    String body = data.get("body");
                    String type = data.get("type");
                    String id = data.get("id");

                    NotificationBuilder.displayRemoteMessageData(title, body, type, id, getApplicationContext());
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
