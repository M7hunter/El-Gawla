package it_geeks.info.elgawla.util.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

import androidx.core.app.NotificationCompat;

import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.services.FetchSalonDataService;
import it_geeks.info.elgawla.views.account.ProfileActivity;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.signing.SignInActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static it_geeks.info.elgawla.util.Constants.PATH;
import static it_geeks.info.elgawla.util.Constants.TO_STORE;

public class NotificationBuilder {

    // region channels
    private static final String REMOTE_NOTIFICATION_CHANNEL_ID = "REMOTE_CHANNEL_ID";
    private static final String REMOTE_NOTIFICATION_CHANNEL_NAME = "news";
    private static final String REMOTE_NOTIFICATION_CHANNEL_DESC = "news and updates";

    private static final String REMOTE_SALONS_NOTIFICATION_CHANNEL_ID = "SALONS_CHANNEL_ID";
    private static final String REMOTE_SALONS_NOTIFICATION_CHANNEL_NAME = "salons";
    private static final String REMOTE_SALONS_NOTIFICATION_CHANNEL_DESC = "new salon in your country & latest updates of subscribed salons";

    private static final String REMOTE_CARDS_NOTIFICATION_CHANNEL_ID = "CARDS_CHANNEL_ID";
    private static final String REMOTE_CARDS_NOTIFICATION_CHANNEL_NAME = "store";
    private static final String REMOTE_CARDS_NOTIFICATION_CHANNEL_DESC = "latest updates in the store";

    private static final String LOCALE_NOTIFICATION_CHANNEL_ID = "LOCALE_CHANNEL_ID";
    private static final String LOCALE_NOTIFICATION_CHANNEL_NAME = "global updates ";
    private static final String LOCALE_NOTIFICATION_CHANNEL_DESC = "e.g: uploading user account image";
    // endregion

    // region groups
    private static final String REMOTE_NOTIFICATION_GROUP_KEY = "remote_group";
    private static final int REMOTE_NOTIFICATION_GROUP_ID = 0;

    private static final String REMOTE_SALONS_NOTIFICATION_GROUP_KEY = "salons_group";
    private static final int REMOTE_SALONS_NOTIFICATION_GROUP_ID = 1;

    private static final String REMOTE_CARDS_NOTIFICATION_GROUP_KEY = "cards_group";
    private static final int REMOTE_CARDS_NOTIFICATION_GROUP_ID = 2;
    //endregion

    private static final int NOTIFICATION_ICON = R.drawable.g_round;
    public static final int UPLOAD_IMAGE_NOTIFICATION_ID = 1;

    private Context context;
    private static NotificationBuilder NB;
    private static NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private static NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    private NotificationBuilder(Context context) {
        this.context = context;
    }

    public static NotificationBuilder Instance(Context context) {
        if (NB == null)
        {
            NB = new NotificationBuilder(context);
        }
        return NB;
    }

    private NotificationManager getNotificationManager(Context context) {
        if (notificationManager == null)
        {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public void cancelNotification(Context context, int notificationId) {
        getNotificationManager(context).cancel(notificationId);
    }

    public void CancelAll(Context context) {
        getNotificationManager(context).cancelAll();
    }

    public static void listenToNotificationStatus(Context context, final View indicator) {
        if (SharedPrefManager.getInstance(context).isNotificationEnabled())
            SharedPrefManager.newNotificationLive.observe(((LifecycleOwner) context), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean)
                    {
                        indicator.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        indicator.setVisibility(View.INVISIBLE);
                    }
                }
            });

    }

    private void createLocaleChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            for (NotificationChannel channel : getNotificationManager(context).getNotificationChannels())
            {
                if (channel.getId().equals(LOCALE_NOTIFICATION_CHANNEL_ID))
                {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(LOCALE_NOTIFICATION_CHANNEL_ID, LOCALE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(LOCALE_NOTIFICATION_CHANNEL_DESC);

            getNotificationManager(context).createNotificationChannel(notificationChannel);
        }
    }

    private void createRemoteChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            for (NotificationChannel channel : getNotificationManager(context).getNotificationChannels())
            {
                if (channel.getId().equals(REMOTE_NOTIFICATION_CHANNEL_ID))
                {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(REMOTE_NOTIFICATION_CHANNEL_ID, REMOTE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(REMOTE_NOTIFICATION_CHANNEL_DESC);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            getNotificationManager(context).createNotificationChannel(notificationChannel);
        }
    }

    private void createRemoteChannel(Context context, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            boolean isSalons = type.equals("salons");
            String cid = isSalons ? REMOTE_SALONS_NOTIFICATION_CHANNEL_ID : REMOTE_CARDS_NOTIFICATION_CHANNEL_ID;

            for (NotificationChannel channel : getNotificationManager(context).getNotificationChannels())
            {
                if (channel.getId().equals(cid))
                {
                    return;
                }
            }

            NotificationChannel notificationChannel = new NotificationChannel(isSalons ? REMOTE_SALONS_NOTIFICATION_CHANNEL_ID : REMOTE_CARDS_NOTIFICATION_CHANNEL_ID
                    , isSalons ? REMOTE_SALONS_NOTIFICATION_CHANNEL_NAME : REMOTE_CARDS_NOTIFICATION_CHANNEL_NAME
                    , NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(isSalons ? REMOTE_SALONS_NOTIFICATION_CHANNEL_DESC : REMOTE_CARDS_NOTIFICATION_CHANNEL_DESC);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            getNotificationManager(context).createNotificationChannel(notificationChannel);
        }
    }

    public void deleteChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            getNotificationManager(context).deleteNotificationChannel(channelId);
        }
    }

    public void displayUploadingImage() {
        PendingIntent cancelIntent = initUploadingImageIntent();

        notificationBuilder = new NotificationCompat.Builder(context, LOCALE_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(NOTIFICATION_ICON)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.updating_image))
                .setContentIntent(initUploadingImageIntent())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .addAction(new NotificationCompat.Action(0, context.getString(R.string.cancel), cancelIntent));

        createLocaleChannel();
        notificationBuilder.setChannelId(LOCALE_NOTIFICATION_CHANNEL_ID);
        getNotificationManager(context).notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void displayMessage(String message) {
        notificationBuilder = new NotificationCompat.Builder(context, LOCALE_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setContentText(message)
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentIntent(initUploadingImageIntent())
                .setAutoCancel(true);

        getNotificationManager(context).notify(UPLOAD_IMAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    void displayRemoteMessage(String title, String body, Context context) {
        PendingIntent pendingIntent = initRemoteMessageIntent(title, body, context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMOTE_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(NOTIFICATION_ICON)
                .setGroup(REMOTE_NOTIFICATION_GROUP_KEY)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body)
                        .setBigContentTitle(title));

        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, REMOTE_NOTIFICATION_CHANNEL_ID);
        summaryBuilder.setSmallIcon(NOTIFICATION_ICON)
                .setGroup(REMOTE_NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setShowWhen(true)
                .setSubText("not salon")
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent);

        createRemoteChannel(context);
        builder.setChannelId(REMOTE_NOTIFICATION_CHANNEL_ID);
        summaryBuilder.setChannelId(REMOTE_NOTIFICATION_CHANNEL_ID);

        getNotificationManager(context).notify(summaryBuilder.build().getGroup(), REMOTE_NOTIFICATION_GROUP_ID, summaryBuilder.build());
        getNotificationManager(context).notify(builder.build().getGroup(), new Random().nextInt(), builder.build());
    }

    void displayRemoteMessageWithData(String title, String body, String type, String id, Context context) {
        PendingIntent pendingIntent = initRemoteMessageDataIntent(title, body, type, id, context);

        inboxStyle.setBigContentTitle(context.getString(R.string.salons));
        inboxStyle.addLine(context.getString(R.string.salon_number) + id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMOTE_NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(NOTIFICATION_ICON)
                .setGroup(type.equals("salons") ? REMOTE_SALONS_NOTIFICATION_GROUP_KEY : REMOTE_CARDS_NOTIFICATION_GROUP_KEY)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setSubText(type)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent);

        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, REMOTE_NOTIFICATION_CHANNEL_ID);
        summaryBuilder.setSmallIcon(NOTIFICATION_ICON)
                .setGroup(type.equals("salons") ? REMOTE_SALONS_NOTIFICATION_GROUP_KEY : REMOTE_CARDS_NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true)
                .setShowWhen(true)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setSubText(type)
                .setStyle(inboxStyle);

        createRemoteChannel(context, type);
        builder.setChannelId(type.equals("salons") ? REMOTE_SALONS_NOTIFICATION_CHANNEL_ID : REMOTE_CARDS_NOTIFICATION_CHANNEL_ID);
        summaryBuilder.setChannelId(type.equals("salons") ? REMOTE_SALONS_NOTIFICATION_CHANNEL_ID : REMOTE_CARDS_NOTIFICATION_CHANNEL_ID);

        getNotificationManager(context).notify(summaryBuilder.build().getGroup(), type.equals("salons") ? REMOTE_SALONS_NOTIFICATION_GROUP_ID : REMOTE_CARDS_NOTIFICATION_GROUP_ID, summaryBuilder.build());
        getNotificationManager(context).notify(builder.build().getGroup(), new Random().nextInt(), builder.build());
    }

    private PendingIntent initUploadingImageIntent() {
        Intent i = new Intent(context, ProfileActivity.class);
        i.putExtra("notify_id", UPLOAD_IMAGE_NOTIFICATION_ID);
        return PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent initRemoteMessageIntent(String title, String body, Context context) {
        Bundle bundle = new Bundle();

        bundle.putString("title", title);
        bundle.putString("body", body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent();

        if (SharedPrefManager.getInstance(context).isLoggedIn())
        {
            if (context instanceof NotificationActivity)
            {
                ((NotificationActivity) context).recreate();
            }
            else
            {
                intent = new Intent(context, NotificationActivity.class);
                stackBuilder.addNextIntentWithParentStack(intent);
            }
        }
        else
        {
            if (!(context instanceof SignInActivity))
            {
                intent = new Intent(context, SignInActivity.class);
                stackBuilder.addNextIntentWithParentStack(intent);
            }
        }

        intent.putExtras(bundle);
        return stackBuilder.getPendingIntent(123456, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent initRemoteMessageDataIntent(String title, String body, String type, String id, Context context) {
        Bundle bundle = new Bundle();

        bundle.putString("title", title);
        bundle.putString("body", body);
        bundle.putInt("id", Integer.valueOf(id));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent();

        if (SharedPrefManager.getInstance(context).isLoggedIn())
        {
            if (type.equals("salons"))
            {
                intent = new Intent(context, FetchSalonDataService.class);
                intent.putExtras(bundle);
                return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            else if (type.equals("cards"))
            {
                intent = new Intent(context, MainActivity.class); // redirect to store fragment
                stackBuilder.addNextIntentWithParentStack(intent
                        .putExtra(PATH, TO_STORE));
            }
            else if (context instanceof NotificationActivity)
            {
                ((NotificationActivity) context).recreate();
            }
            else
            {
                intent = new Intent(context, NotificationActivity.class);
                stackBuilder.addNextIntentWithParentStack(intent);
            }
        }
        else
        {
            if (!(context instanceof SignInActivity))
            {
                intent = new Intent(context, SignInActivity.class);
                stackBuilder.addNextIntentWithParentStack(intent);
            }
        }

        intent.putExtras(bundle);
        return stackBuilder.getPendingIntent(123456, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
