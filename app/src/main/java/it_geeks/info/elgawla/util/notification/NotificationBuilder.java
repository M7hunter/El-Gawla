package it_geeks.info.elgawla.util.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.Random;

import androidx.core.app.NotificationCompat;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Notification;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.receivers.NotificationInteractionsReceiver;
import it_geeks.info.elgawla.util.services.GetSalonDataService;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.signing.SignInActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;

public class NotificationBuilder {

    private static final String REMOTE_NOTIFICATION_CHANNEL_ID = "GAWLA_CHANNEL_ID";
    private static final String REMOTE_NOTIFICATION_CHANNEL_NAME = "salons updates";
    private static final String REMOTE_NOTIFICATION_CHANNEL_DESC = "latest updates of subscribed salons & if there is a new salon in your country";
    private static final String REMOTE_NOTIFICATION_GROUP_ID = "remote_group";

    private static final String LOCALE_NOTIFICATION_CHANNEL_NAME = "updating user image";
    private static final String LOCALE_NOTIFICATION_CHANNEL_DESC = "uploading user account image states";
    private static final String LOCALE_NOTIFICATION_GROUP_ID = "locale_group";

    private static final String UPLOAD_IMAGE_CHANNEL_ID = "upload_image_channel";
    public static final int UPLOAD_IMAGE_NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ICON = R.drawable.g_logo;

    private Context context;
    private static NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public NotificationBuilder(Context context) {
        this.context = context;
    }

    public static void listenToNotificationStatus(final Context context, final View indicator) {
        // notification status LiveData
        if (SharedPrefManager.getInstance(context).isNotificationEnabled())
            GawlaDataBse.getInstance(context).notificationDao().getStatusNotification(true).observe((LifecycleOwner) context, new Observer<List<Notification>>() {
                @Override
                public void onChanged(List<Notification> notifications) {
                    if (notifications.size() > 0)
                    {
                        indicator.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        indicator.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }

    public static void createRemoteChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            for (NotificationChannel channel : getNotificationManager(context).getNotificationChannels())
            {
                if (channel.getId().equals(REMOTE_NOTIFICATION_CHANNEL_ID))
                {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(REMOTE_NOTIFICATION_CHANNEL_ID, REMOTE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(REMOTE_NOTIFICATION_CHANNEL_DESC);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            getNotificationManager(context).createNotificationChannel(notificationChannel);
        }
    }

    public void createUploadImageChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            for (NotificationChannel channel : getNotificationManager(context).getNotificationChannels())
            {
                if (channel.getId().equals(UPLOAD_IMAGE_CHANNEL_ID))
                {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(UPLOAD_IMAGE_CHANNEL_ID, LOCALE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(LOCALE_NOTIFICATION_CHANNEL_DESC);

            getNotificationManager(context).createNotificationChannel(notificationChannel);
        }
    }

    public void deleteChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            getNotificationManager(context).deleteNotificationChannel(channelId);
        }
    }

    public void displayUploadingImage() {
        PendingIntent cancelIntent = initCancelUploadingImageIntent();

        notificationBuilder = new NotificationCompat.Builder(context, UPLOAD_IMAGE_CHANNEL_ID);
        notificationBuilder.setContentTitle(context.getString(R.string.updating_image))
                .setSmallIcon(NOTIFICATION_ICON)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(0, context.getString(R.string.cancel), cancelIntent))
                .setProgress(0, 0, true)
                .setGroup(LOCALE_NOTIFICATION_GROUP_ID);

        createUploadImageChannel();
        notificationBuilder.setChannelId(UPLOAD_IMAGE_CHANNEL_ID);
        getNotificationManager(context).notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void displayMessage(String message) {
        notificationBuilder = new NotificationCompat.Builder(context, UPLOAD_IMAGE_CHANNEL_ID);
        notificationBuilder.setContentText(message)
                .setSmallIcon(NOTIFICATION_ICON)
                .setAutoCancel(true);

        getNotificationManager(context).notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    static void displayRemoteMessage(String title, String body, String type, String id, Context context) {
        PendingIntent pendingIntent = initRemoteMessageIntent(title, body, type, id, context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMOTE_NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle(title)
                .setColor(context.getResources().getColor(R.color.greenBlue))
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title))
                .setContentInfo("Info")
                .setGroup(REMOTE_NOTIFICATION_GROUP_ID);

        createRemoteChannel(context);
        builder.setChannelId(REMOTE_NOTIFICATION_CHANNEL_ID);
        getNotificationManager(context).notify(new Random().nextInt(), builder.build());
    }

    private static NotificationManager getNotificationManager(Context context) {
        if (notificationManager == null)
        {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    private PendingIntent initCancelUploadingImageIntent() {
        Intent i = new Intent(context, NotificationInteractionsReceiver.class);
        i.putExtra("notify_id", UPLOAD_IMAGE_NOTIFICATION_ID);
        return PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent initRemoteMessageIntent(String title, String body, String type, String id, Context context) {
        Bundle bundle = new Bundle();

        bundle.putString("title", title);
        bundle.putString("body", body);
        bundle.putString("type", type);
        bundle.putInt("id", Integer.valueOf(id));

        Intent intent = new Intent();
        if (SharedPrefManager.getInstance(context).isLoggedIn())
        {

            if (type.equals("salons"))
            {
                intent = new Intent(context, GetSalonDataService.class);

                intent.putExtras(bundle);
                return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            else if (type.equals("cards"))
            {
                intent = new Intent(context, MainActivity.class); // redirect to store fragment
            }
            else if (context instanceof NotificationActivity)
            {
                ((NotificationActivity) context).recreate();
            }
            else
            {
                intent = new Intent(context, NotificationActivity.class);
            }
        }
        else
        {
            if (!(context instanceof SignInActivity))
            {
                intent = new Intent(context, SignInActivity.class);
            }
        }

        intent.putExtras(bundle);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
