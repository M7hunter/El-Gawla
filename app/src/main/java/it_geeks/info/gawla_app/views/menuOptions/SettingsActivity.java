package it_geeks.info.gawla_app.views.menuOptions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;

public class SettingsActivity extends AppCompatActivity {

    TextView tvLang, tvCountry, tvCurrency;

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
