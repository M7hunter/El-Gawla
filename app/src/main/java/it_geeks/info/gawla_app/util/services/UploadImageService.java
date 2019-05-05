package it_geeks.info.gawla_app.util.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.NotificationBuilder;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.account.AccountDetailsActivity;

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
        final NotificationBuilder notificationBuilder = new NotificationBuilder(this);

        if (activity != null && activity.encodedImage != null) {
            activity.setUIOnUpdating();

            if (SharedPrefManager.getInstance(this).isNotificationEnabled()) {
                notificationBuilder.displayUploadingImage();
            }

            RetrofitClient.getInstance(this).executeConnectionToServer(this,
                    "updateUserData",
                    new Request(SharedPrefManager.getInstance(this).getUser().getUser_id(),
                            SharedPrefManager.getInstance(this).getCountry().getCountry_id(),
                            SharedPrefManager.getInstance(this).getUser().getApi_token(), activity.encodedImage), new HandleResponses() {
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
                                activity.setUIAfterUpdating();
                                activity.hideUploadImageButton();
                            }
                        }

                        @Override
                        public void handleAfterResponse() {
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
                                activity.setUIAfterUpdating();
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