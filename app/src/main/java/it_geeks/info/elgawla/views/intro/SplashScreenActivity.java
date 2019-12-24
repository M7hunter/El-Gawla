package it_geeks.info.elgawla.views.intro;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.WebPage;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.CountryDao;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.ConnectivityReceiver;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.Interfaces.ConnectionInteface;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.util.services.FetchSalonDataService;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.signing.ResetPasswordActivity;
import it_geeks.info.elgawla.views.signing.SignInActivity;

import static it_geeks.info.elgawla.util.Constants.PATH;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_COUNTRIES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_PAGES;
import static it_geeks.info.elgawla.util.Constants.TO_SALON;
import static it_geeks.info.elgawla.util.Constants.TO_STORE;

public class SplashScreenActivity extends BaseActivity {

    public static Activity splashInstance;

    public List<WebPage> webPageList = new ArrayList<>();

    private ProgressBar pbSplash;
    private MutableLiveData<Integer> ready = new MutableLiveData<>();

    private SnackBuilder snackBuilder;
    private ClickInterface.SnackAction snackAction;

    private Class targetClass;
    private Bundle targetBundle;

    private ConnectivityReceiver connectivityReceiver;
    private int readyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.d("screen_size", "width : " + Resources.getSystem().getDisplayMetrics().widthPixels);
        Log.d("screen_size", "height : " + Resources.getSystem().getDisplayMetrics().heightPixels);

        init();

        connectivityReceiver = new ConnectivityReceiver(this, new ConnectionInteface() {
            @Override
            public void onConnected() {
                snackBuilder.hideSnack();
                getData();
            }

            @Override
            public void onDisconnected() {
                retry(getString(R.string.check_connection));
            }
        });
    }

    private void init() {
        splashInstance = this;
        targetClass = MainActivity.class;
        targetBundle = new Bundle();

        pbSplash = findViewById(R.id.pb_splash);
        snackBuilder = new SnackBuilder(findViewById(R.id.splash_screen_main_view));

        snackAction = new ClickInterface.SnackAction() {
            @Override
            public void onClick() {
                recreate();
            }
        };
    }

    private void getData() {
        readyCount = 0;
        ready.postValue(readyCount);

        getCountriesFromSever();
        getWebPagesFromServer();
        checkDynamicLinking();
        checkNotificationData();

        ready.observe(SplashScreenActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 4)
                {
                    checkLoginState();
                }

                Log.d("ready", "ready: " + ready.getValue());
                Log.d("ready", "readyCount: " + readyCount);
            }
        });
    }

    private void getCountriesFromSever() {
        pbSplash.setVisibility(View.VISIBLE);
        final String countriesToken = "8QEqV21eAUneQcZYUmtw7yXhlzXsUuOvr6iH2qg9IBxwzYSOfiGDcd0W8vme";
        RetrofitClient.getInstance(SplashScreenActivity.this).fetchDataFromServer(SplashScreenActivity.this,
                REQ_GET_ALL_COUNTRIES, new RequestModel<>(REQ_GET_ALL_COUNTRIES, countriesToken
                        , null, null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        CountryDao countryDao = GawlaDataBse.getInstance(SplashScreenActivity.this).countryDao();

                        countryDao.removeCountries(countryDao.getCountries());
                        countryDao.insertCountryList(ParseResponses.parseCountries(mainObject));
                    }

                    @Override
                    public void afterResponse() {
                        readyCount++;
                        ready.postValue(readyCount);
                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                        retry(errorMessage);
                    }
                });
    }

    private void getWebPagesFromServer() {
        int user_id = SharedPrefManager.getInstance(SplashScreenActivity.this).getUser().getUser_id();
        final String pagesToken = "T9hQoKYK7bGop5y6tuZq5S4RBH0dTNu0Lh6XuRzhyju8OVZ3Bz6TRDUJD4YH";

        pbSplash.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(SplashScreenActivity.this).fetchDataFromServer(SplashScreenActivity.this,
                REQ_GET_ALL_PAGES, new RequestModel<>(REQ_GET_ALL_PAGES, user_id, pagesToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        webPageList = ParseResponses.parseWebPages(mainObject);
                    }

                    @Override
                    public void afterResponse() {
                        readyCount++;
                        ready.postValue(readyCount);
                        Log.d("ready", "ready: " + ready.getValue());
                        Log.d("ready", "readyCount: " + readyCount);
//                        checkLoginState();
                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                        retry(errorMessage);
                    }
                });
    }

    private void checkDynamicLinking() {
        pbSplash.setVisibility(View.VISIBLE);
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // get deep link from result (may be null if no link is found)
                        try
                        {
                            if (pendingDynamicLinkData != null)
                            {
                                Uri deepLink = pendingDynamicLinkData.getLink();
                                if (null != deepLink)
                                {
                                    if ("salons".equals(deepLink.getLastPathSegment()))
                                    {
                                        String salonId = deepLink.getQueryParameter("salon_id");

                                        targetClass = FetchSalonDataService.class;
                                        targetBundle.putInt("id", Integer.valueOf(salonId));
                                        targetBundle.putString(PATH, TO_SALON);
                                    }
                                    else if ("resetpassword".equals(deepLink.getLastPathSegment()))
                                    {
                                        targetClass = ResetPasswordActivity.class;
                                    }
                                }
                            }
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
                        e.printStackTrace();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<PendingDynamicLinkData>() {
                    @Override
                    public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {
                        readyCount++;
                        ready.postValue(readyCount);
                    }
                });
    }

    private void checkNotificationData() {
        pbSplash.setVisibility(View.VISIBLE);
        try
        {
            if (getIntent().getExtras() != null)
            {
                String type, id;
                if (getIntent().getExtras().keySet().contains("type"))
                {
                    type = getIntent().getExtras().getString("type");
                    if ("salons".equals(type) && getIntent().getExtras().keySet().contains("id"))
                    {
                        id = getIntent().getExtras().getString("id");

                        targetClass = FetchSalonDataService.class;
                        targetBundle.putInt("id", Integer.valueOf(id));
                        targetBundle.putString(PATH, TO_SALON);
                    }
                    else if ("cards".equals(type))
                    {
                        targetClass = MainActivity.class;
                        targetBundle.putString(PATH, TO_STORE);
                    }
                }
            }

            readyCount++;
            ready.postValue(readyCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkLoginState() {
        if (SharedPrefManager.getInstance(this).getUser().getUser_id() == -111 // id !saved
                || SharedPrefManager.getInstance(this).getUser().getApi_token() == null // token !saved
                || !SharedPrefManager.getInstance(this).isLoggedIn())
        { // !logged in

            if (SharedPrefManager.getInstance(this).getCountry().getCountry_id() == -111)
            { // country !saved
                targetClass = SplashActivity.class;
                startPath();
            }
            else
            { // country saved
                targetClass = SignInActivity.class;
                startPath();
            }
        }
        else
        { // the user is logged in
            startPath();
        }
    }

    private void startPath() {
        if (!targetClass.equals(FetchSalonDataService.class))
        {
            startActivity(new Intent(this, targetClass)
                    .putExtras(targetBundle)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else
        {
            startService(new Intent(this, targetClass)
                    .putExtras(targetBundle)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void retry(String message) {
        pbSplash.setVisibility(View.INVISIBLE);
        snackBuilder.setSnackText(message)
                .setSnackDuration(Snackbar.LENGTH_INDEFINITE)
                .setSnackAction(getString(R.string.retry), snackAction)
                .showSnack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(connectivityReceiver);
    }
}
