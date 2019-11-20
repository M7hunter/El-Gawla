package it_geeks.info.elgawla.views.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it_geeks.info.elgawla.Adapters.CountrySpinnerAdapter;
import it_geeks.info.elgawla.repository.Models.Country;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.views.BaseActivity;

public class SplashActivity extends BaseActivity {

    private List<Country> countries = new ArrayList<>();
    private Country selectedCountry;
    private String selectedLang;

    private Button btnContinue;
    private View llAR, llEN;
    private ImageView ivARCheck, ivENCheck, ivSpinnerArrow;
    private TextView tvAR, tvEN;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        countries = GawlaDataBse.getInstance(this).countryDao().getCountries();

        initViews();

        initLang();

        initCountriesSpinner();
    }

    private void initViews() {
        btnContinue = findViewById(R.id.btn_splash_continue);
        ivARCheck = findViewById(R.id.iv_ar_check);
        ivENCheck = findViewById(R.id.iv_en_check);
        ivSpinnerArrow = findViewById(R.id.iv_country_spinner_arrow);
        llAR = findViewById(R.id.ar_container);
        llEN = findViewById(R.id.en_container);
        tvAR = findViewById(R.id.tv_splash_arabic);
        tvEN = findViewById(R.id.tv_splash_english);
        spinner = findViewById(R.id.s_splash_country);
        spinner.setAdapter(new CountrySpinnerAdapter(countries, this));

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndProceed();
            }
        });
    }

    private void initLang() {
        selectedLang = SharedPrefManager.getInstance(this).getSavedLang();
        if ("ar".equals(selectedLang))
        {
            selectArabic();
        }
        else
        {
            selectEnglish();
        }

        View.OnClickListener arClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectArabic();
            }
        };
        View.OnClickListener enClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEnglish();
            }
        };

        llAR.setOnClickListener(arClickListener);
        tvAR.setOnClickListener(arClickListener);
        llEN.setOnClickListener(enClickListener);
        tvEN.setOnClickListener(enClickListener);
    }

    private void selectArabic() {
        ivARCheck.setVisibility(View.VISIBLE);
        ivENCheck.setVisibility(View.GONE);
        llAR.setBackground(getResources().getDrawable(R.drawable.bg_rounded_c_white_bordered_c_primary));
        llEN.setBackground(getResources().getDrawable(R.drawable.bg_rounded_c_primary_bordered_c_white));
        tvAR.setTextColor(getResources().getColor(R.color.colorPrimary));
        tvEN.setTextColor(Color.WHITE);

        selectedLang = "ar";
    }

    private void selectEnglish() {
        ivARCheck.setVisibility(View.GONE);
        ivENCheck.setVisibility(View.VISIBLE);
        llAR.setBackground(getResources().getDrawable(R.drawable.bg_rounded_c_primary_bordered_c_white));
        llEN.setBackground(getResources().getDrawable(R.drawable.bg_rounded_c_white_bordered_c_primary));
        tvAR.setTextColor(Color.WHITE);
        tvEN.setTextColor(getResources().getColor(R.color.colorPrimary));

        selectedLang = "en";
    }

    private void initCountriesSpinner() {
        String localeCountryCode = getResources().getConfiguration().locale.getCountry();

        for (Country country : countries)
        {
            if (localeCountryCode.equals(country.getCount_code()))
            {
//                countries.sort(new Comparator<Country>() {
//                    @Override
//                    public int compare(Country o1, Country o2) {
//                        return o1.getCountry_id();
//                    }
//                });
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = countries.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ivSpinnerArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });
    }

    private void saveAndProceed() {
        // save lang & country
        SharedPrefManager.getInstance(SplashActivity.this).setLang(selectedLang);
        SharedPrefManager.getInstance(SplashActivity.this).setCountry(selectedCountry);
        // move to intro screen
        startActivity(new Intent(SplashActivity.this, IntroActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
