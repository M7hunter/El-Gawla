package it_geeks.info.gawla_app.views.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.gawla_app.Adapters.CountrySpinnerAdapter;
import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.R;

public class SplashActivity extends AppCompatActivity {

    private List<Country> countries = new ArrayList<>();
    private boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        countries = GawlaDataBse.getInstance(this).countryDao().getCountries();

        initCountriesSpinner();
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
                        saveAndProceed();
                }
                else
                {
                    selected = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveAndProceed() {
        // cache country
//                SharedPrefManager.getInstance(SplashActivity.this).setCountry(countries.get(position));

        // move to intro screen
        startActivity(new Intent(SplashActivity.this, IntroActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
