package it_geeks.info.elgawla.util.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.services.UploadImageService;
import it_geeks.info.elgawla.views.account.ProfileActivity;

public class NotificationInteractionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notifyId = intent.getIntExtra("notify_id", 0);

        switch (notifyId) {
            case 1: // cancel notification
                RetrofitClient.getInstance(context).cancelCall();
                context.stopService(new Intent(context, UploadImageService.class));
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notifyId);

                try {
                    ProfileActivity.accountDetailsInstance.setUIAfterUpdating();
                    ProfileActivity.accountDetailsInstance.btn_upload_image.setEnabled(true);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }
}
