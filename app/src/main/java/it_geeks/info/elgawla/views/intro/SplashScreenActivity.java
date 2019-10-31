package it_geeks.info.elgawla.views.intro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Round;
import it_geeks.info.elgawla.repository.Models.WebPage;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;
import it_geeks.info.elgawla.views.signing.ResetPasswordActivity;
import it_geeks.info.elgawla.views.signing.SignInActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_COUNTRIES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_PAGES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;

public class SplashScreenActivity extends AppCompatActivity {

    public static Activity splashInstance;

    public List<WebPage> webPageList = new ArrayList<>();

    private ProgressBar pbSplash;
    private boolean webPagesReady = false, countriesReady = false;

    private SnackBuilder snackBuilder;
    private ClickInterface.SnackAction snackAction;

    private boolean isDeep = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        SharedPrefManager.getInstance(this).setLang(SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        init();

        initDynamicLinking();
    }

    private void init() {
        splashInstance = this;
        pbSplash = findViewById(R.id.pb_splash);
        snackBuilder = new SnackBuilder(findViewById(R.id.splash_screen_main_view));
        snackAction = new ClickInterface.SnackAction() {
            @Override
            public void onClick() {
                checkConnection();
            }
        };
    }

    private void initDynamicLinking() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        try
                        {
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null)
                            {
                                deepLink = pendingDynamicLinkData.getLink();
                                if (null != deepLink)
                                {

                                    if (deepLink.getLastPathSegment().equals("resetpassword"))
                                    {
                                        isDeep = true;
                                        startActivity(ResetPasswordActivity.class);
                                    }
                                    else if (deepLink.getLastPathSegment().equals("salons"))
                                    {
                                        isDeep = true;
                                        Log.d("dynamic-links", "getLastPathSegment: " + deepLink.getLastPathSegment());
                                        String salonId = deepLink.getQueryParameter("salon_id");

                                        if (SharedPrefManager.getInstance(SplashScreenActivity.this).isLoggedIn())
                                        {
                                            getSalonDataFromServer(salonId);
                                        }
                                        else
                                        {
                                            startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class)
                                                    .putExtra("salon_id", salonId)
                                                    .putExtra("salon_from_link", true)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            finish();
                                        }
                                    }
                                }
                            }
                            Log.d("dynamic-links", "getDynamicLink:onSuccess " + deepLink);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("dynamic-links", "getDynamicLink:onFailure", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<PendingDynamicLinkData>() {
                    @Override
                    public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {
                        if (!isDeep)
                            checkConnection();
                    }
                });
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

    private void getSalonDataFromServer(String salonId) {
        pbSplash.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(SplashScreenActivity.this).executeConnectionToServer(SplashScreenActivity.this,
                REQ_GET_SALON_BY_ID, new Request<>(REQ_GET_SALON_BY_ID, SharedPrefManager.getInstance(SplashScreenActivity.this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(SplashScreenActivity.this).getUser().getApi_token(), salonId
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        startActivity(new Intent(SplashScreenActivity.this, SalonActivity.class)
                                .putExtra("round", parseRoundByID(mainObject))
                                .putExtra("salon_from_link", true));
                        finish();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        pbSplash.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getCountriesFromSever() {
        final String countriesToken = "8QEqV21eAUneQcZYUmtw7yXhlzXsUuOvr6iH2qg9IBxwzYSOfiGDcd0W8vme";
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
        final String pagesToken = "T9hQoKYK7bGop5y6tuZq5S4RBH0dTNu0Lh6XuRzhyju8OVZ3Bz6TRDUJD4YH";

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
                startActivity(SignInActivity.class);
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
        finish();
    }

    private void retry(String message) {
        pbSplash.setVisibility(View.INVISIBLE);
        snackBuilder.setSnackText(message)
                .setSnackDuration(Snackbar.LENGTH_INDEFINITE)
                .setSnackAction(getString(R.string.retry), snackAction)
                .showSnack();
    }
}
