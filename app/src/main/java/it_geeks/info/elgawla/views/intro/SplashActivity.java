package it_geeks.info.elgawla.views.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView ivSAFlag, ivUSFlag, ivSACheck, ivUSCheck;

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
        ivSAFlag = findViewById(R.id.sa_flag);
        ivUSFlag = findViewById(R.id.us_flag);
        ivSACheck = findViewById(R.id.sa_check);
        ivUSCheck = findViewById(R.id.us_check);

        if ("ar".equals(SharedPrefManager.getInstance(this).getSavedLang()))
        {
            selectArabic();
        } else
        {
            selectEnglish();
        }

        btnArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectArabic();
                SharedPrefManager.getInstance(SplashActivity.this).setLang("ar");
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEnglish();
                SharedPrefManager.getInstance(SplashActivity.this).setLang("en");
            }
        });

        ivSAFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnArabic.performClick();
            }
        });

        ivUSFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEnglish.performClick();
            }
        });
    }

    private void selectArabic() {
        ivSACheck.setVisibility(View.VISIBLE);
        ivUSCheck.setVisibility(View.GONE);
        btnArabic.setTextColor(getResources().getColor(R.color.colorSecondary));
        btnEnglish.setTextColor(Color.WHITE);
    }

    private void selectEnglish() {
        ivSACheck.setVisibility(View.GONE);
        ivUSCheck.setVisibility(View.VISIBLE);
        btnArabic.setTextColor(Color.WHITE);
        btnEnglish.setTextColor(getResources().getColor(R.color.colorSecondary));
    }

    private void initCountriesSpinner() {
        Spinner spinner = findViewById(R.id.s_splash_country);
        spinner.setAdapter(new CountrySpinnerAdapter(countries, this));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selected)
                {
                    if (position != 0)
                        saveAndProceed(position);
                } else
                {
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
