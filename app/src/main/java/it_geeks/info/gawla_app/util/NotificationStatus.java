package it_geeks.info.gawla_app.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Notification;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class NotificationStatus {

    public static void notificationStatus(final Context context, final View indicator) {
        // notification status LiveData
        if (SharedPrefManager.getInstance(context).isNotificationEnabled())
            GawlaDataBse.getInstance(context).notificationDao().getStatusNotification(true).observe((LifecycleOwner) context, new Observer<List<Notification>>() {
                @Override
                public void onChanged(List<Notification> notifications) {
                    if (notifications.size() > 0)
                    {
                        indicator.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        indicator.setVisibility(View.GONE);
                    }
                }
            });
    }

}
