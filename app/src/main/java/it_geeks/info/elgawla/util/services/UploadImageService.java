package it_geeks.info.elgawla.util.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;

import androidx.annotation.Nullable;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_UPDATE_USER_DATA;
import static it_geeks.info.elgawla.util.notification.NotificationBuilder.UPLOAD_IMAGE_NOTIFICATION_ID;

public class UploadImageService extends Service {

    private boolean uploaded = false;

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
        final ProfileActivity activity = ProfileActivity.accountDetailsInstance;

        if (activity != null && activity.encodedImage != null)
        {
            activity.setUIOnUpdating();

            if (SharedPrefManager.getInstance(this).isNotificationEnabled())
            {
                NotificationBuilder.Instance(UploadImageService.this).displayUploadingImage();
            }

            RetrofitClient.getInstance(this).fetchDataFromServer(this,
                    REQ_UPDATE_USER_DATA, new RequestModel<>("updateUserImage", SharedPrefManager.getInstance(this).getUser().getUser_id(),
                            SharedPrefManager.getInstance(this).getUser().getApi_token(),
                            SharedPrefManager.getInstance(this).getCountry().getCountry_id(),
                            activity.encodedImage
                            , null, null, null), new HandleResponses() {
                        @Override
                        public void onTrueResponse(JsonObject mainObject) {
                            uploaded = true;
                            // save updated user data
                            SharedPrefManager.getInstance(UploadImageService.this).saveUser(ParseResponses.parseUser(mainObject));

                            if (SharedPrefManager.getInstance(UploadImageService.this).isNotificationEnabled())
                            {
                                NotificationBuilder.Instance(UploadImageService.this).displayMessage(getString(R.string.image_updated));
                            }
                        }

                        @Override
                        public void afterResponse() {
                            if (!uploaded) {
                                NotificationBuilder.Instance(UploadImageService.this).cancelNotification(UploadImageService.this,UPLOAD_IMAGE_NOTIFICATION_ID);
                            }

                            if (activity != null)
                            {
                                activity.setUIAfterUpdating();
                                activity.hideUploadImageButton();
                            }
                            stopSelf();
                        }

                        @Override
                        public void onConnectionErrors(String errorMessage) {
                            if (SharedPrefManager.getInstance(UploadImageService.this).isNotificationEnabled())
                            {
                                NotificationBuilder.Instance(UploadImageService.this).displayMessage(errorMessage);
                            }
                            else
                            {
                                Toast.makeText(UploadImageService.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }

                            try
                            {
                                activity.setUIAfterUpdating();
                                activity.btn_upload_image.setEnabled(true);
                            } catch (NullPointerException e)
                            {
                                Crashlytics.logException(e);
                            }
                            stopSelf();
                        }
                    });
        }
    }
}