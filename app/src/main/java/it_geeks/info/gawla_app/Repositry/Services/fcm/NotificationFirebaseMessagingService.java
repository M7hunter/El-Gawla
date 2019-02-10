package it_geeks.info.gawla_app.Repositry.Services.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.SalonActivity;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "GAWLA_CHANNEL_ID";
    public static final String NOTIFICATION_CHANNEL_NAME = "GAWLA_CHANNEL_NAME";
    public static final String NOTIFICATION_CHANNEL_DESC = "GAWLA_CHANNEL_DESC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        if (!remoteMessage.getData().isEmpty()) {
            showNotification(remoteMessage.getData());
        } else {
            //	showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }

    }

    private void showNotification(Map<String, String> data) {

        PendingIntent pendingIntent = getNotificationData(data);

        String title = "Gawla";
        String body = "";
        try {
            title = data.get("title");
            body = data.get("body");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESC);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableLights(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_message)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.example_picture))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title))
                .addAction(
                        R.drawable.ic_action_stat_share,
                        getResources().getString(R.string.action_share),
                        PendingIntent.getActivity(
                                getApplication(),
                                new Random().nextInt(),
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, "http://www.google.com"), "Share Gawla Link"),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentInfo("Info");
        notificationManager.notify(new Random().nextInt(), builder.build());

    }

    private PendingIntent getNotificationData(Map<String, String> data) {
        Bundle bundle = new Bundle();

        bundle.putString("title", data.get("title"));
        bundle.putString("body", data.get("body"));
        bundle.putString("salon", data.get("salon"));
        bundle.putString("new_salon", data.get("new_salon"));
        bundle.putString("start_salon", data.get("start_salon"));
        bundle.putString("end_salon", data.get("end_salon"));

        Intent intent = new Intent(this, MainActivity.class) ;
        intent.putExtras(bundle);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

/*	private void showNotification(String title, String body) {

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
			notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESC);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.BLUE);
			notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
			notificationChannel.enableLights(true);

			notificationManager.createNotificationChannel(notificationChannel);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
		builder.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL)
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
				.setContentTitle(title)
				.setContentText(body)
				.setContentInfo("Info");
		notificationManager.notify(new Random().nextInt(),builder.build());
	}
	*/

    @Override
    public void onNewToken(String token) {
        Log.e("MO7", "Refreshed token: " + token);
    }

}
