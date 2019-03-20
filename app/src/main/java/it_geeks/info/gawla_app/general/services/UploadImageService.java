package it_geeks.info.gawla_app.general.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.NotificationBuilder;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.accountOptions.AccountDetailsActivity;

public class UploadImageService extends Service {

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
        final NotificationBuilder notificationBuilder = new NotificationBuilder(this);

        if (activity != null && activity.encodedImage != null) {

            if (SharedPrefManager.getInstance(this).isNotificationEnabled()) {
                notificationBuilder.displayUploadingImage();
            }

            RetrofitClient.getInstance(this).executeConnectionToServer(this,
                    "updateUserData", new Request(user_id, SharedPrefManager.getInstance(this).getCountry().getCountry_id(), api_token, activity.encodedImage), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                            // save updated user data
                            SharedPrefManager.getInstance(UploadImageService.this).saveUser(ParseResponses.parseUser(mainObject));

                            // notify user
                            Toast.makeText(UploadImageService.this, getString(R.string.updated), Toast.LENGTH_SHORT).show();

                            if (SharedPrefManager.getInstance(UploadImageService.this).isNotificationEnabled()) {
                                notificationBuilder.displayMessage(getString(R.string.image_updated));
                            }

                            if (activity != null) {
                                activity.setUpdatedStateOnUI();
                                activity.hideUploadImageButton();
                            }
                        }

                        @Override
                        public void handleFalseResponse(JsonObject mainObject) {

                        }

                        @Override
                        public void handleEmptyResponse() {
                            stopSelf();
                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                            if (SharedPrefManager.getInstance(UploadImageService.this).isNotificationEnabled()) {
                                notificationBuilder.displayMessage(errorMessage);
                            } else {
                                Toast.makeText(UploadImageService.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }

                            try {
                                activity.setUpdatedStateOnUI();
                                activity.btn_upload_image.setEnabled(true);
                            } catch (NullPointerException e) {
                                Crashlytics.logException(e);
                            }
                            stopSelf();
                        }
                    });
        }
    }
}