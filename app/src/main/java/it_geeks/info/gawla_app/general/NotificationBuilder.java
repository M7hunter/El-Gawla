package it_geeks.info.gawla_app.general;

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
import it_geeks.info.gawla_app.general.Receivers.NotificationInteractionsReceiver;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class NotificationBuilder {


    private static final String REMOTE_NOTIFICATION_CHANNEL_ID = "GAWLA_CHANNEL_ID";
    private static final String REMOTE_NOTIFICATION_CHANNEL_NAME = "GAWLA_CHANNEL_NAME";
    private static final String REMOTE_NOTIFICATION_CHANNEL_DESC = "GAWLA_CHANNEL_DESC";
    private static final String REMOTE_NOTIFICATION_GROUP_ID = "remote group";
    private static final String LOCALE_NOTIFICATION_GROUP_ID = "locale group";

    private static final String UPLOAD_IMAGE_CHANNEL_ID = "upload image";
    private static final int UPLOAD_IMAGE_NOTIFICATION_ID = 1;

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public NotificationBuilder(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void displayUploadingImage() {
        PendingIntent cancelIntent = initCancelIntent();

        notificationBuilder = new NotificationCompat.Builder(context, UPLOAD_IMAGE_CHANNEL_ID);
        notificationBuilder.setContentTitle(context.getString(R.string.updating_image))
                .setSmallIcon(R.mipmap.ic_launcher_gawla)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(true)
                .setGroup(LOCALE_NOTIFICATION_GROUP_ID)
                .addAction(new NotificationCompat.Action(0, context.getString(R.string.cancel), cancelIntent))
                .setProgress(0, 0, true);

        createChannel(notificationBuilder, UPLOAD_IMAGE_CHANNEL_ID, "Updating User Image", 4, "");
        notificationManager.notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private PendingIntent initCancelIntent() {
        Intent i = new Intent(context, NotificationInteractionsReceiver.class);
        i.putExtra("notify_id", UPLOAD_IMAGE_NOTIFICATION_ID);
        return PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void displayMessage(String message) {
        notificationBuilder = new NotificationCompat.Builder(context, UPLOAD_IMAGE_CHANNEL_ID);
        notificationBuilder.setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_gawla);

        notificationManager.notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void createChannel(NotificationCompat.Builder builder, String channelId, String channelName, int importance, String channelDesc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setDescription(channelDesc);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(channelId);
        }
    }

    public void displayRemoteMessage(String title, String body) {
        PendingIntent pendingIntent = getNotificationData(title, body);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMOTE_NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_gawla)
                .setContentTitle(title)
                .setColor(context.getResources().getColor(R.color.greenBlue))
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setGroup(REMOTE_NOTIFICATION_GROUP_ID)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title))
                .setContentInfo("Info");

        createChannel(builder, REMOTE_NOTIFICATION_CHANNEL_ID, REMOTE_NOTIFICATION_CHANNEL_NAME, 3, REMOTE_NOTIFICATION_CHANNEL_DESC);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private PendingIntent getNotificationData(String title, String body) {
        Bundle bundle = new Bundle();

        bundle.putString("title", title);
        bundle.putString("body", body);

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
