package it_geeks.info.gawla_app.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import java.util.Random;

import androidx.core.app.NotificationCompat;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.receivers.NotificationInteractionsReceiver;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class NotificationBuilder {

    private static final String REMOTE_NOTIFICATION_CHANNEL_ID = "GAWLA_CHANNEL_ID";
    private static final String REMOTE_NOTIFICATION_CHANNEL_NAME = "salons updates";
    private static final String REMOTE_NOTIFICATION_CHANNEL_DESC = "latest updates of subscribed salons & if there is a new salon in your country";
    private static final String REMOTE_NOTIFICATION_GROUP_ID = "remote_group";

    private static final String LOCALE_NOTIFICATION_CHANNEL_NAME = "updating user image";
    private static final String LOCALE_NOTIFICATION_CHANNEL_DESC = "uploading user account image states";
    private static final String LOCALE_NOTIFICATION_GROUP_ID = "locale_group";

    private static final String UPLOAD_IMAGE_CHANNEL_ID = "upload_image_channel";
    private static final int UPLOAD_IMAGE_NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_ICON = R.mipmap.ic_launcher;

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public NotificationBuilder(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void createRemoteChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (NotificationChannel channel : notificationManager.getNotificationChannels()) {
                if (channel.getId().equals(REMOTE_NOTIFICATION_CHANNEL_ID)) {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(REMOTE_NOTIFICATION_CHANNEL_ID, REMOTE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(REMOTE_NOTIFICATION_CHANNEL_DESC);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void createUploadImageChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (NotificationChannel channel : notificationManager.getNotificationChannels()) {
                if (channel.getId().equals(UPLOAD_IMAGE_CHANNEL_ID)) {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(UPLOAD_IMAGE_CHANNEL_ID, LOCALE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(LOCALE_NOTIFICATION_CHANNEL_DESC);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void deleteChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId);
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
        notificationManager.notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void displayMessage(String message) {
        notificationBuilder = new NotificationCompat.Builder(context, UPLOAD_IMAGE_CHANNEL_ID);
        notificationBuilder.setContentText(message)
                .setSmallIcon(NOTIFICATION_ICON)
                .setAutoCancel(true);

        notificationManager.notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void displayRemoteMessage(String title, String body) {
        PendingIntent pendingIntent = initRemoteMessageIntent(title, body);

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

        createRemoteChannel();
        builder.setChannelId(REMOTE_NOTIFICATION_CHANNEL_ID);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private PendingIntent initCancelUploadingImageIntent() {
        Intent i = new Intent(context, NotificationInteractionsReceiver.class);
        i.putExtra("notify_id", UPLOAD_IMAGE_NOTIFICATION_ID);
        return PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent initRemoteMessageIntent(String title, String body) {
        Bundle bundle = new Bundle();

        bundle.putString("title", title);
        bundle.putString("body", body);

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
