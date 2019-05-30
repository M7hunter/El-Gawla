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
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.WebPage;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.receivers.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.views.account.AccountFragment;
import it_geeks.info.gawla_app.views.card.CardsFragment;
import it_geeks.info.gawla_app.views.menu.MenuFragment;
import it_geeks.info.gawla_app.views.salon.MyRoundsFragment;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.login.LoginActivity;
import it_geeks.info.gawla_app.views.intro.SplashActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_PAGES;

public class MainActivity extends AppCompatActivity {

    public static Activity mainInstance;

    private BottomNavigationView navigation;
    private Fragment fragment = new MainFragment();

    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    private View snackContainer;

    private TransHolder transHolder;
    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainInstance = this;

        transHolder = new TransHolder(MainActivity.this);
        transHolder.getMainActivityTranses(MainActivity.this);

        if (savedInstanceState == null)
        {
            displayFragment(fragment);
        }

        // Notification Update Status When App Open
        updateNotificationStatus();
        // Firebase Receive messaging notification
        FirebaseMessagingInitialize();

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        initNavigation();

        handleEvents();

        setupTrans();
    }

    private void updateNotificationStatus() {
        if (SharedPrefManager.getInstance(this).getNewNotification())
        {
            GawlaDataBse.getInstance(this).notificationDao().updateStatusNotification(true);
        }
        else
        {
            GawlaDataBse.getInstance(this).notificationDao().updateStatusNotification(false);
        }
    }

    // Firebase initialize
    private void FirebaseMessagingInitialize() {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled())
            startNotifications();
        else stopNotifications();
    }

    private void startNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic("country_" + SharedPrefManager.getInstance(this).getCountry().getCountry_id());
    }

    private void stopNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("country_" + SharedPrefManager.getInstance(this).getCountry().getCountry_id());
    }

    public View getSnackBarContainer() {
        if (snackContainer == null)
        {
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
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_salons:
                        fragment = new MainFragment();
                        menuItem.setTitle(transHolder.hales);
                        // change status bar color
                        Common.Instance().changeStatusBarColor(MainActivity.this, "#f4f7fa");
                        break;
                    case R.id.navigation_my_rounds:
                        fragment = new MyRoundsFragment();
                        menuItem.setTitle(transHolder.my_rounds);
                        // change status bar color
                        Common.Instance().changeStatusBarColor(MainActivity.this, "#f4f7fa");
                        break;
                    case R.id.navigation_cards:
                        fragment = new CardsFragment();
                        menuItem.setTitle(transHolder.cards);
                        // change status bar color
                        Common.Instance().changeStatusBarColor(MainActivity.this, "#f4f7fa");
                        break;
                    case R.id.navigation_account:
                        fragment = new AccountFragment();
                        menuItem.setTitle(transHolder.account);
                        // change status bar color to white
                        Common.Instance().changeStatusBarColor(MainActivity.this, "#FFFFFF");
                        break;
                    case R.id.navigation_menu:
                        fragment = new MenuFragment();
                        menuItem.setTitle(transHolder.menu);
                        // change status bar color
                        Common.Instance().changeStatusBarColor(MainActivity.this, "#f4f7fa");
                        break;
                }

                if (fragment != null)
                {
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
        ((BottomNavigationItemView) findViewById(R.id.navigation_salons)).setTitle(transHolder.hales);
        ((BottomNavigationItemView) findViewById(R.id.navigation_my_rounds)).setTitle(transHolder.my_rounds);
        ((BottomNavigationItemView) findViewById(R.id.navigation_cards)).setTitle(transHolder.cards);
        ((BottomNavigationItemView) findViewById(R.id.navigation_account)).setTitle(transHolder.account);
        ((BottomNavigationItemView) findViewById(R.id.navigation_menu)).setTitle(transHolder.menu);
    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() == R.id.navigation_salons)
        { // back from main page
            super.onBackPressed();

        }
        else
        {
            displayFragment(new MainFragment());
            navigation.setSelectedItemId(R.id.navigation_salons);
        }
    }

    @Override
    protected void onDestroy() {
        try
        {
            unregisterReceiver(connectionChangeReceiver);
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        super.onDestroy();
    }
}
