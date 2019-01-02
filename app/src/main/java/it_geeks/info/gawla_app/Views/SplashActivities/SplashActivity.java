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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
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

        RequestMainBody requestMainBody = new RequestMainBody(new Data("getAllCountries"), new Request(apiToken));
        RetrofitClient.getInstance(SplashActivity.this).getAPI().request(requestMainBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject mainObj = response.body().getAsJsonObject();
                    boolean status = mainObj.get("status").getAsBoolean();

                    if (status) { // no errors
                        countries = handleServerResponse(mainObj);

                        displayCountriesList();

                        GawlaDataBse.getGawlaDatabase(SplashActivity.this).countryDao().insertCountryList(countries);

                    } else { // errors from server
                        retry();
                        Toast.makeText(SplashActivity.this, handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) { // errors of response body
                    retry();
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Common.Instance(SplashActivity.this).hideProgress(countryRecycler, countriesProgress);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                retry();
                Common.Instance(SplashActivity.this).hideProgress(countryRecycler, countriesProgress);
                Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retry() {
        btnRetry.setVisibility(View.VISIBLE);
        tvCountriesHeader.setText(getString(R.string.error_occurred));
        tvCountriesHeader.setTextColor(getResources().getColor(R.color.paleRed));
    }

    private List<Country> handleServerResponse(JsonObject object) {
        List<Country> countries = new ArrayList<>();
        JsonArray roundsArray = object.get("data").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int country_id = roundObj.get("country_id").getAsInt();
            String country_title = roundObj.get("country_title").getAsString();
            String count_code = roundObj.get("count_code").getAsString();
            String country_timezone = roundObj.get("country_timezone").getAsString();
            String tel = roundObj.get("tel").getAsString();
            String image = roundObj.get("image").getAsString();

            countries.add(
                    new Country(country_id, country_title, count_code, country_timezone, tel, image));
        }

        return countries;
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

    private void displayCountriesList() {
        countryRecycler.setLayoutManager(new LinearLayoutManager(SplashActivity.this, 1, false));
        countryRecycler.setHasFixedSize(true);
        countryAdapter = new CountryAdapter(SplashActivity.this, countries);
        countryRecycler.setAdapter(countryAdapter);
    }
}
