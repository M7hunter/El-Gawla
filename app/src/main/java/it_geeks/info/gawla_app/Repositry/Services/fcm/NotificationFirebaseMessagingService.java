package it_geeks.info.gawla_app.Repositry.Services.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.SalonActivity;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "GAWLA_CHANNEL_ID";
    public static final String NOTIFICATION_CHANNEL_NAME = "GAWLA_CHANNEL_NAME";
    public static final String NOTIFICATION_CHANNEL_DESC = "GAWLA_CHANNEL_DESC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        String clickAction = remoteMessage.getNotification().getClickAction();
        new Intent(clickAction);

        try {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),clickAction);
        } catch (Exception e) {
        }

    }



    private void showNotification(String title, String body, String clickAction) {

        if (!title.isEmpty() && !body.isEmpty()) {

            PendingIntent pendingIntent = getNotificationData(title, body,clickAction);
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
                    .setColor(getResources().getColor(R.color.greenBlue))
                    .setContentIntent(pendingIntent)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body)
                            .setBigContentTitle(title))
                    .setContentInfo("Info");
            notificationManager.notify(new Random().nextInt(), builder.build());

        }
    }


    private PendingIntent getNotificationData(String title, String body , String click_action) {
        Bundle bundle = new Bundle();

        bundle.putString("title", title);
        bundle.putString("body", body);

        Intent intent = new Intent(click_action);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onNewToken(String token) {
        try {
            int user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
            String apiToken = SharedPrefManager.getInstance(this).getUser().getApi_token();

            if (!String.valueOf(user_id).isEmpty() && !apiToken.isEmpty()){
                new UpdateFirebaseToken(this);
            }
        }catch (Exception e){}
    }


}
