package it_geeks.info.gawla_app.general;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Notification;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;

public class NotificationStatus {

    public static void notificationStatus(final Context context, final ImageView notificationBell) {
        // notification status LiveData
        GawlaDataBse.getInstance(context).notificationDao().getStatusNotification(true).observe((LifecycleOwner) context, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                if (notifications.size() > 0){
                    notificationBell.setImageDrawable(context.getResources().getDrawable(R.drawable.bell_two));
                }else {
                    notificationBell.setImageDrawable(context.getResources().getDrawable(R.drawable.bell));
                }
            }
        });
    }

}
