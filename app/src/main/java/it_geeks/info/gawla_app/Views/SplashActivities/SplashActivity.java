package it_geeks.info.gawla_app.Views.SplashActivities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Controllers.Adapters.CountryAdapter;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    RecyclerView countryRecycler;
    CountryAdapter countryAdapter;
    ProgressBar countriesProgress;
    TextView tvCountriesHeader;
    Button btnRetry;

    List<Country> countries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppLang();
        super.onCreate(savedInstanceState);

        if (SharedPrefManager.getInstance(SplashActivity.this).getCountry().getCountry_id() != -1) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash);

            initViews();

            getCountriesFromSever();
        }
    }

    private void setAppLang() {
        Common.Instance(SplashActivity.this).setLang(SharedPrefManager.getInstance(SplashActivity.this).getSavedLang());
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

        RetrofitClient.getInstance(SplashActivity.this).executeConnectionToServer("getAllCountries", new Request(apiToken), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                    countries = ParseResponses.parseCountries(mainObject);

                    displayCountriesList();

                    GawlaDataBse.getGawlaDatabase(SplashActivity.this).countryDao().insertCountryList(countries);

                    Common.Instance(SplashActivity.this).hideProgress(countryRecycler, countriesProgress);
            }

            @Override
            public void handleEmptyResponse() {
                Common.Instance(SplashActivity.this).hideProgress(countryRecycler, countriesProgress);
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                retry();
                Common.Instance(SplashActivity.this).hideProgress(countryRecycler, countriesProgress);
                Toast.makeText(SplashActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void retry() {
        btnRetry.setVisibility(View.VISIBLE);
        tvCountriesHeader.setText(getString(R.string.error_occurred));
        tvCountriesHeader.setTextColor(getResources().getColor(R.color.paleRed));
    }

    private void displayCountriesList() {
        countryRecycler.setLayoutManager(new LinearLayoutManager(SplashActivity.this, 1, false));
        countryRecycler.setHasFixedSize(true);
        countryAdapter = new CountryAdapter(SplashActivity.this, countries);
        countryRecycler.setAdapter(countryAdapter);
    }
}
