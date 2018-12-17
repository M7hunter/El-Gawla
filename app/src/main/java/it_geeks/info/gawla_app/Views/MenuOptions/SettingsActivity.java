package it_geeks.info.gawla_app.Views.MenuOptions;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;

public class SettingsActivity extends AppCompatActivity {

    TextView tvLang, tvCountry, tvCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_settings);

        initViews();
    }

    private void initViews() {
        tvLang = findViewById(R.id.app_settings_language);
        tvCountry = findViewById(R.id.app_settings_country);
        tvCurrency = findViewById(R.id.app_settings_currency);

        tvLang.setText(displayLanguage(SharedPrefManager.getInstance(SettingsActivity.this).getSavedLang()));
        tvCountry.setText(SharedPrefManager.getInstance(SettingsActivity.this).getCountry().getCountry_title());

        // back
        findViewById(R.id.app_settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String displayLanguage(String s) {
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

    // to change status bar color
    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
