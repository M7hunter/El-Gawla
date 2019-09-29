package it_geeks.info.elgawla.views.menu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.NotificationBuilder;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvLang, tvNotificationOptions;
    private SwitchMaterial notificationSwitch, soundSwitch;
    private RelativeLayout rlAppNotification;

    private NotificationBuilder notificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvLang.setText(displayLanguage());
    }

    private void initViews() {
        tvLang = findViewById(R.id.app_settings_language);
        tvNotificationOptions = findViewById(R.id.tv_notification_options);
        notificationSwitch = findViewById(R.id.notification_switch);
        soundSwitch = findViewById(R.id.sound_switch);
        rlAppNotification = findViewById(R.id.rl_app_notification);

        if (SharedPrefManager.getInstance(SettingsActivity.this).isNotificationEnabled())
        {
            notificationSwitch.setChecked(true);
        }
        else
        {
            notificationSwitch.setChecked(false);
        }

        if (SharedPrefManager.getInstance(SettingsActivity.this).isSoundEnabled())
        {
            soundSwitch.setChecked(true);
        }
        else
        {
            soundSwitch.setChecked(false);
        }

        if (Build.VERSION.SDK_INT > 26)
        {
            notificationBuilder = new NotificationBuilder(this);
            notificationBuilder.createUploadImageChannel();
            notificationBuilder.createRemoteChannel();

            rlAppNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                }
            });
        }
        else
        {
            tvNotificationOptions.setVisibility(View.GONE);
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("onCheckedChanged", "isChecked:: " + isChecked);
                SharedPrefManager.getInstance(SettingsActivity.this).setNotificationEnabled(isChecked);
                if (isChecked)
                {
                    startNotifications();
                }
                else
                {
                    stopNotifications();
                }
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("onCheckedChanged", "isChecked:: " + isChecked);
                SharedPrefManager.getInstance(SettingsActivity.this).setSoundEnabled(isChecked);
            }
        });

        tvLang.setText(displayLanguage());

        // open languages page
        findViewById(R.id.settings_lang_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LanguageActivity.class));
            }
        });

        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void startNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic("country_" + String.valueOf(SharedPrefManager.getInstance(this).getCountry().getCountry_id()));
        FirebaseMessaging.getInstance().subscribeToTopic("salon_" + SharedPrefManager.getInstance(this).getSubscribedSalonId());
    }

    private void stopNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("country_" + SharedPrefManager.getInstance(this).getCountry().getCountry_id());
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + SharedPrefManager.getInstance(this).getSubscribedSalonId());
    }

    private String displayLanguage() {
        String s = SharedPrefManager.getInstance(SettingsActivity.this).getSavedLang();

        switch (s)
        {
            case "en":
                s = "English";
                break;
            case "ar":
                s = "العربية";
                break;
            default:
                break;
        }

        return s;
    }
}
