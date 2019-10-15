package it_geeks.info.elgawla.views.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.elgawla.Adapters.CountrySpinnerAdapter;
import it_geeks.info.elgawla.repository.Models.Country;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;

public class SplashActivity extends AppCompatActivity {

    private List<Country> countries = new ArrayList<>();
    private boolean selected = false;

    private Button btnEnglish, btnArabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        countries = GawlaDataBse.getInstance(this).countryDao().getCountries();

        initCountriesSpinner();

        initLang();
    }

    private void initLang() {
        btnArabic = findViewById(R.id.btn_splash_arabic);
        btnEnglish = findViewById(R.id.btn_splash_english);

        if ("ar".equals(SharedPrefManager.getInstance(this).getSavedLang())) {
            selectArabic();
        } else {
            selectEnglish();
        }

        btnArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectArabic();
                Common.Instance().setLang(SplashActivity.this, "ar");
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEnglish();
                Common.Instance().setLang(SplashActivity.this, "en");
            }
        });
    }

    private void selectArabic() {
        btnArabic.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        btnEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void selectEnglish() {
        btnEnglish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        btnArabic.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void initCountriesSpinner() {
        Spinner spinner = findViewById(R.id.s_splash_country);
        spinner.setAdapter(new CountrySpinnerAdapter(countries, this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selected) {
                    if (position != 0)
                        saveAndProceed(position);
                } else {
                    selected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveAndProceed(int position) {
        // cache country
        SharedPrefManager.getInstance(SplashActivity.this).setCountry(countries.get(position));

        // move to intro screen
        startActivity(new Intent(SplashActivity.this, IntroActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
