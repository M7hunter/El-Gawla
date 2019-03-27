package it_geeks.info.gawla_app.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import io.fabric.sdk.android.Fabric;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.WebPage;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.receivers.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.NavigationFragments.AccountFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.CardsFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MainFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MenuFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MyRoundsFragment;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import it_geeks.info.gawla_app.views.splashActivities.SplashActivity;

public class MainActivity extends AppCompatActivity {

    public static Activity mainInstance;

    private BottomNavigationView navigation;
    private Fragment fragment = new MainFragment();

    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    private View snackContainer;

    private TransHolder transHolder;

    public List<WebPage> webPageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        setLang();
        super.onCreate(savedInstanceState);

        if (!checkLoginState()) {
            return;
        }

        Common.Instance(this).changeStatusBarColor("#f4f7fa", this);
        setContentView(R.layout.activity_main);

        // Notification Update Status When App Open
        updateNotificationStatus();

        // Firebase Receive messaging notification
        FirebaseMessagingInitialize();

        mainInstance = this;

        transHolder = new TransHolder(MainActivity.this);
        transHolder.getMainActivityTranses(MainActivity.this);

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        if (savedInstanceState == null) {
            displayFragment(fragment);
        }

        initNavigation();

        handleEvents();

        setupTrans();

        getWebPagesFromServer();
    }

    private void setLang() {
        try {
            Common.Instance(this).setLang(SharedPrefManager.getInstance(this).getSavedLang());
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private boolean checkLoginState() {
        if (SharedPrefManager.getInstance(this).getUser().getUser_id() == -111 // id !saved
                || SharedPrefManager.getInstance(this).getUser().getApi_token() == null // token !saved
                || !SharedPrefManager.getInstance(this).isLoggedIn()) { // !logged in

            if (SharedPrefManager.getInstance(this).getCountry().getCountry_id() == -111) { // country saved ?
                startActivity(new Intent(this, SplashActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else { // country !saved
                startActivity(new Intent(this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

            return false;
        }

        return true;
    }

    private void updateNotificationStatus() {
        if (SharedPrefManager.getInstance(this).getNewNotification()) {
            GawlaDataBse.getInstance(this).notificationDao().updateStatusNotification(true);
        } else {
            GawlaDataBse.getInstance(this).notificationDao().updateStatusNotification(false);
        }
    }

    // Firebase initialize
    private void FirebaseMessagingInitialize() {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled()) startNotifications();
        else stopNotifications();
    }

    private void startNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic("country_" + String.valueOf(SharedPrefManager.getInstance(this).getCountry().getCountry_id()));
    }

    private void stopNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("country_" + String.valueOf(SharedPrefManager.getInstance(this).getCountry().getCountry_id()));
    }

    public View getSnackBarContainer() {
        if (snackContainer == null) {
            snackContainer = findViewById(R.id.snackbar_container);
        }
        return snackContainer;
    }

    private void initNavigation() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_hales:
                        fragment = new MainFragment();
                        menuItem.setTitle(transHolder.hales);
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                    case R.id.navigation_my_rounds:
                        fragment = new MyRoundsFragment();
                        menuItem.setTitle(transHolder.my_rounds);
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                    case R.id.navigation_cards:
                        fragment = new CardsFragment();
                        menuItem.setTitle(transHolder.cards);
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                    case R.id.navigation_account:
                        fragment = new AccountFragment();
                        menuItem.setTitle(transHolder.account);
                        // change status bar color to white
                        Common.Instance(MainActivity.this).changeStatusBarColor("#FFFFFF", MainActivity.this);
                        break;
                    case R.id.navigation_menu:
                        fragment = new MenuFragment();
                        menuItem.setTitle(transHolder.menu);
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                }

                if (fragment != null) {
                    displayFragment(fragment);
                    return true;
                }

                return false;
            }
        });
    }

    private void handleEvents() {
        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void setupTrans() {
        ((BottomNavigationItemView) findViewById(R.id.navigation_hales)).setTitle(transHolder.hales);
        ((BottomNavigationItemView) findViewById(R.id.navigation_my_rounds)).setTitle(transHolder.my_rounds);
        ((BottomNavigationItemView) findViewById(R.id.navigation_cards)).setTitle(transHolder.cards);
        ((BottomNavigationItemView) findViewById(R.id.navigation_account)).setTitle(transHolder.account);
        ((BottomNavigationItemView) findViewById(R.id.navigation_menu)).setTitle(transHolder.menu);
    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }

    private void getWebPagesFromServer() {
        int user_id = SharedPrefManager.getInstance(MainActivity.this).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(MainActivity.this).getUser().getApi_token();

        RetrofitClient.getInstance(MainActivity.this).executeConnectionToServer(MainActivity.this, "getAllPages", new Request(user_id, api_token), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                webPageList = ParseResponses.parseWebPages(mainObject);
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() == R.id.navigation_hales) { // back from main page
            super.onBackPressed();

        } else {
            displayFragment(new MainFragment());
            navigation.setSelectedItemId(R.id.navigation_hales);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(connectionChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        super.onDestroy();
    }
}
