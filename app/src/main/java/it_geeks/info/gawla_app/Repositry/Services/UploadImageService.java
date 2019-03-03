package it_geeks.info.gawla_app.Repositry.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.NotificationInteractionsReceiver;
import it_geeks.info.gawla_app.views.accountOptions.AccountDetailsActivity;

public class UploadImageService extends Service {

    private static final String CHANNEL_ID = "upload image service";
    private static final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uploadImage();
        return super.onStartCommand(intent, flags, startId);
    }

    public void uploadImage() {
        final AccountDetailsActivity activity = AccountDetailsActivity.accountDetailsInstance;
        int user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(this).getUser().getApi_token();

        if (activity != null)
            if (activity.encodedImage != null) {

                if (SharedPrefManager.getInstance(this).getNotificationState()) {
                    displayNotification();
                }

                RetrofitClient.getInstance(this).executeConnectionToServer(this,
                        "updateUserData", new Request(user_id, api_token, activity.encodedImage), new HandleResponses() {
                            @Override
                            public void handleTrueResponse(JsonObject mainObject) {
                                // save updated user data
                                SharedPrefManager.getInstance(UploadImageService.this).saveUser(ParseResponses.parseUser(mainObject));

                                // notify user
                                Toast.makeText(UploadImageService.this, getString(R.string.updated), Toast.LENGTH_SHORT).show();

                                if (SharedPrefManager.getInstance(UploadImageService.this).getNotificationState()) {
                                    messageNotification(getString(R.string.image_updared));
                                }

                                if (activity != null) {
                                    activity.updatedStateUI();
                                    activity.hideUploadImageButton();
                                }
                            }

                            @Override
                            public void handleFalseResponse(JsonObject mainObject) {

                            }

                            @Override
                            public void handleEmptyResponse() {
                            }

                            @Override
                            public void handleConnectionErrors(String errorMessage) {
                                if (SharedPrefManager.getInstance(UploadImageService.this).getNotificationState()) {
                                    messageNotification(errorMessage);
                                } else {
                                    Toast.makeText(UploadImageService.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }

                                try {
                                    activity.updatedStateUI();
                                    activity.btn_upload_image.setEnabled(true);
                                } catch (NullPointerException e) {
                                }
                            }
                        });
            }
    }

    public void displayNotification() {
        PendingIntent cancelIntent = cancelIntent();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setContentTitle(getString(R.string.updating_image))
                .setSmallIcon(R.mipmap.ic_launcher_gawla)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(0, getString(R.string.cancel), cancelIntent))
                .setProgress(0, 0, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Updating User Image", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(CHANNEL_ID);
//            notificationManager.deleteNotificationChannel(CHANNEL_ID);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void messageNotification(String message) {
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_gawla);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        stopSelf();
    }

    private PendingIntent cancelIntent() {
        Intent i = new Intent(this, NotificationInteractionsReceiver.class);
        i.putExtra("notify_id", NOTIFICATION_ID);
        return PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
