package it_geeks.info.gawla_app.general;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Notifications;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;

public class NotificationStatus {

    public void LiveStatus(final Context context, final ImageView imgNotification){
        // notification status LiveData
        GawlaDataBse.getGawlaDatabase(context).notificationDao().getStatusNotification(true).observe((LifecycleOwner) context, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                if (notifications.size()>0){
                    imgNotification.setImageDrawable(context.getResources().getDrawable(R.drawable.bell_two));
                }else {
                    imgNotification.setImageDrawable(context.getResources().getDrawable(R.drawable.bell));
                }
            }
        });
    }

}
