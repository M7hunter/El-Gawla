package it_geeks.info.gawla_app.general;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import it_geeks.info.gawla_app.views.accountOptions.AccountDetailsActivity;

public class UploadImageNotificationService extends Service {

    private static final String CHANNEL_ID = "upload image notification";
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

        if (activity.encodedImage != null) {

            if (SharedPrefManager.getInstance(this).getNotificationState()) {
                displayNotification();
            }

            RetrofitClient.getInstance(this).executeConnectionToServer(this,
                    "updateUserData", new Request(user_id, api_token, activity.encodedImage), new HandleResponses() {
                        @Override
                        public void handleResponseData(JsonObject mainObject) {

                            // update uploading status
                            SharedPrefManager.getInstance(UploadImageNotificationService.this).setUploadStatus(States.UPLOADED);

                            // save updated user data
                            SharedPrefManager.getInstance(UploadImageNotificationService.this).saveUser(ParseResponses.parseUser(mainObject));

                            // notify user
                            Toast.makeText(UploadImageNotificationService.this, "updated", Toast.LENGTH_SHORT).show();

                            if (SharedPrefManager.getInstance(UploadImageNotificationService.this).getNotificationState()) {
                                messageNotification("Image updated successfully");
                            }

                            if (activity != null) {
                                activity.updatedStateUI();
                                activity.hideUploadImageButton();
                            }
                        }

                        @Override
                        public void handleEmptyResponse() {
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            if (SharedPrefManager.getInstance(UploadImageNotificationService.this).getNotificationState()) {
                                messageNotification(errorMessage);
                            }

                            Toast.makeText(UploadImageNotificationService.this, errorMessage, Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(UploadImageNotificationService.this).setUploadStatus(States.FAILED);

                            if (activity != null) {
                                activity.updatedStateUI();
                                activity.btn_upload_image.setEnabled(true);
                            }
                        }
                    });
        }
    }

    public void displayNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setContentTitle("Updating User Image")
                .setSmallIcon(R.mipmap.ic_launcher_gawla)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(0, 0, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Updating User Image", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(CHANNEL_ID);
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
}
