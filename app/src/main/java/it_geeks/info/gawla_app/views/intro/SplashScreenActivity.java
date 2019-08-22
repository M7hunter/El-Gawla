package it_geeks.info.gawla_app.views.intro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.WebPage;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.views.main.MainActivity;
import it_geeks.info.gawla_app.views.login.LoginActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_COUNTRIES;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_PAGES;

public class SplashScreenActivity extends AppCompatActivity {

    public static Activity splashInstance;

    public List<WebPage> webPageList = new ArrayList<>();

    private ProgressBar pbSplash;
    private boolean webPagesReady = false, countriesReady = false;

    private SnackBuilder snackBuilder;
    private ClickInterface.SnackAction snackAction;

    private final String countriesToken = "8QEqV21eAUneQcZYUmtw7yXhlzXsUuOvr6iH2qg9IBxwzYSOfiGDcd0W8vme";
    private final String pagesToken = "T9hQoKYK7bGop5y6tuZq5S4RBH0dTNu0Lh6XuRzhyju8OVZ3Bz6TRDUJD4YH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        Common.Instance().setLang(this, SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        init();

        checkConnection();
    }

    private void init() {
        splashInstance = this;
        pbSplash = findViewById(R.id.pb_splash);
        snackBuilder = new SnackBuilder(findViewById(R.id.splash_screen_main_view));
        snackAction = new ClickInterface.SnackAction() {
            @Override
            public void onClick() {
                snackBuilder.hideSnack();
                checkConnection();
            }
        };
    }

    private void checkConnection() {
        if (Common.Instance().isConnected(this))
        {
            pbSplash.setVisibility(View.VISIBLE);
            getCountriesFromSever();
        }
        else
        {
            retry(getString(R.string.check_connection));
        }
    }

    private void getCountriesFromSever() {
        RetrofitClient.getInstance(SplashScreenActivity.this).executeConnectionToServer(SplashScreenActivity.this,
                REQ_GET_ALL_COUNTRIES, new Request<>(REQ_GET_ALL_COUNTRIES, countriesToken
                        , null, null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        GawlaDataBse.getInstance(SplashScreenActivity.this).countryDao().insertCountryList(ParseResponses.parseCountries(mainObject));
                    }

                    @Override
                    public void handleAfterResponse() {
                        countriesReady = true;
                        getWebPagesFromServer();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        countriesReady = false;
                        retry(getString(R.string.check_connection));
                    }
                });
    }

    private void getWebPagesFromServer() {
        int user_id = SharedPrefManager.getInstance(SplashScreenActivity.this).getUser().getUser_id();

        RetrofitClient.getInstance(SplashScreenActivity.this).executeConnectionToServer(SplashScreenActivity.this,
                REQ_GET_ALL_PAGES, new Request<>(REQ_GET_ALL_PAGES, user_id, pagesToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        webPageList = ParseResponses.parseWebPages(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        webPagesReady = true;
                        checkLoginState();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        webPagesReady = false;
                        retry(getString(R.string.check_connection));
                    }
                });
    }

    private void checkLoginState() {
        if (SharedPrefManager.getInstance(this).getUser().getUser_id() == -111 // id !saved
                || SharedPrefManager.getInstance(this).getUser().getApi_token() == null // token !saved
                || !SharedPrefManager.getInstance(this).isLoggedIn())
        { // !logged in

            if (SharedPrefManager.getInstance(this).getCountry().getCountry_id() == -111)
            { // country !saved
                if (countriesReady)
                    startActivity(SplashActivity.class);
            }
            else
            { // country saved
                startActivity(LoginActivity.class);
            }
        }
        else
        { // the user is logged in
            if (webPagesReady)
                startActivity(MainActivity.class);
        }
    }

    private void startActivity(Class target) {
        startActivity(new Intent(this, target)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void retry(String message) {
        pbSplash.setVisibility(View.INVISIBLE);
        snackBuilder.setSnackText(message)
                .setSnackDuration(Snackbar.LENGTH_INDEFINITE)
                .setSnackAction(getString(R.string.retry), snackAction)
                .showSnack();
    }
}
