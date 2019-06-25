package it_geeks.info.gawla_app.views;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.util.receivers.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.account.AccountFragment;
import it_geeks.info.gawla_app.views.card.StoreFragment;
import it_geeks.info.gawla_app.views.menu.MenuFragment;
import it_geeks.info.gawla_app.views.salon.MySalonsFragment;
import it_geeks.info.gawla_app.R;

public class MainActivity extends AppCompatActivity {

    public static Activity mainInstance;

    private BottomNavigationView navigation;
    private Fragment fragment = new MainFragment();

    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    private View snackContainer;

    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.Instance().setLang(this, SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainInstance = this;

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
                        break;
                    case R.id.navigation_my_rounds:
                        fragment = new MySalonsFragment();
                        break;
                    case R.id.navigation_cards:
                        fragment = new StoreFragment();
                        break;
                    case R.id.navigation_account:
                        fragment = new AccountFragment();
                        break;
                    case R.id.navigation_menu:
                        fragment = new MenuFragment();
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

        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // do nothing on reselection
            }
        });
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
