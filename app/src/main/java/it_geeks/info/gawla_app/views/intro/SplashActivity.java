package it_geeks.info.gawla_app.views.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Adapters.CountryAdapter;
import it_geeks.info.gawla_app.R;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_COUNTRIES;

public class SplashActivity extends AppCompatActivity {

    private RecyclerView countryRecycler;
    private ProgressBar countriesProgress;
    private TextView tvCountriesHeader;
    private Button btnRetry;

    private List<Country> countries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initViews();

        getCountriesFromSever();
    }

    private void initViews() {
        countriesProgress = findViewById(R.id.countries_progress);
        countryRecycler = findViewById(R.id.country_recycler);
        tvCountriesHeader = findViewById(R.id.countries_header);
        btnRetry = findViewById(R.id.btn_retry);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRetry.setVisibility(View.INVISIBLE);
                countriesProgress.setVisibility(View.VISIBLE);
                tvCountriesHeader.setText(getString(R.string.choose_country));
                tvCountriesHeader.setTextColor(Color.WHITE);
                getCountriesFromSever();
            }
        });
    }

    private void getCountriesFromSever() {
        final String apiToken = "8QEqV21eAUneQcZYUmtw7yXhlzXsUuOvr6iH2qg9IBxwzYSOfiGDcd0W8vme";

        RetrofitClient.getInstance(SplashActivity.this).executeConnectionToServer(SplashActivity.this,
                REQ_GET_ALL_COUNTRIES, new Request<>(REQ_GET_ALL_COUNTRIES, apiToken
                        , null, null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        countries = ParseResponses.parseCountries(mainObject);

                        displayCountriesList();

                        GawlaDataBse.getInstance(SplashActivity.this).countryDao().insertCountryList(countries);
                    }

                    @Override
                    public void handleAfterResponse() {
                        if (countries.size() == 0)
                        {
                            displayRetry();
                        }

                        Common.Instance().hideProgress(countryRecycler, countriesProgress);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        displayRetry();
                        Common.Instance().hideProgress(countryRecycler, countriesProgress);
                        Toast.makeText(SplashActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void displayRetry() {
        btnRetry.setVisibility(View.VISIBLE);
        tvCountriesHeader.setText(getString(R.string.error_occurred));
        tvCountriesHeader.setTextColor(getResources().getColor(R.color.paleRed));
    }

    private void displayCountriesList() {
        countryRecycler.setLayoutManager(new LinearLayoutManager(SplashActivity.this, RecyclerView.VERTICAL, false));
        countryRecycler.setHasFixedSize(true);
        CountryAdapter countryAdapter = new CountryAdapter(SplashActivity.this, countries, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SharedPrefManager.getInstance(SplashActivity.this).setCountry(countries.get(position));

                startActivity(new Intent(SplashActivity.this, IntroActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        countryRecycler.setAdapter(countryAdapter);
    }
}
