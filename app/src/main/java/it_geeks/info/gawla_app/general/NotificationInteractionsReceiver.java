package it_geeks.info.gawla_app.general;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.services.UploadImageService;
import it_geeks.info.gawla_app.views.accountOptions.AccountDetailsActivity;

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
                    AccountDetailsActivity.accountDetailsInstance.updatedStateUI();
                    AccountDetailsActivity.accountDetailsInstance.btn_upload_image.setEnabled(true);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }

                break;
            default:
                break;
        }
    }
}
