package it_geeks.info.gawla_app.views.menuOptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;

public class SettingsActivity extends AppCompatActivity {

    TextView tvLang, tvCountry, tvCurrency;
    ScrollView mainSettingsActivity;

    SwitchMaterial notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_settings);

        initViews();
    }

    private void initViews() {
        tvLang = findViewById(R.id.app_settings_language);
        tvCountry = findViewById(R.id.app_settings_country);
        tvCurrency = findViewById(R.id.app_settings_currency);
        notificationSwitch = findViewById(R.id.notification_switch);

        if (SharedPrefManager.getInstance(SettingsActivity.this).getNotificationState()) {
            notificationSwitch.setChecked(true);
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefManager.getInstance(SettingsActivity.this).setNotificationState(isChecked);
                if (isChecked){
                    startNotifications();
                }else {
                    stopNotifications();
                }
            }
        });

        tvLang.setText(displayLanguage());
        tvCountry.setText(SharedPrefManager.getInstance(SettingsActivity.this).getCountry().getCountry_title());

        // open languages page
        findViewById(R.id.settings_lang_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LanguageActivity.class));
            }
        });

        // back
        findViewById(R.id.app_settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Swipe Page Back
        mainSettingsActivity = findViewById(R.id.mainSettingsActivity);
        mainSettingsActivity.setOnTouchListener(new OnSwipeTouchListener(SettingsActivity.this){
            public void onSwipeRight() { finish(); }
        });
    }

    private void stopNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("country_"+String.valueOf(SharedPrefManager.getInstance(this).getCountry().getCountry_id()));
    }

    private void startNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic("country_"+String.valueOf(SharedPrefManager.getInstance(this).getCountry().getCountry_id()));
    }

    private String displayLanguage() {
        String s = SharedPrefManager.getInstance(SettingsActivity.this).getSavedLang();

        switch (s) {
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

    @Override
    protected void onResume() {
        super.onResume();

        tvLang.setText(displayLanguage());
    }
}
