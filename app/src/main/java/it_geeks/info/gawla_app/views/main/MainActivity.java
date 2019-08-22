package it_geeks.info.gawla_app.views.main;

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
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.util.receivers.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.account.AccountFragment;
import it_geeks.info.gawla_app.views.store.StoreFragment;
import it_geeks.info.gawla_app.views.menu.MenuFragment;
import it_geeks.info.gawla_app.views.salon.MySalonsFragment;
import it_geeks.info.gawla_app.R;

import static it_geeks.info.gawla_app.util.Constants.MEMBERSHIP_MSG;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private Fragment fragment = new MainFragment();

    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    private View snackContainer;

    public DialogBuilder dialogBuilder;
    public SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.Instance().setLang(this, SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
            displayFragment(fragment);
        }

        init();

        getExtras();

        initNavigation();
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getString(MEMBERSHIP_MSG) != null)
        {
            if (!extras.getString(MEMBERSHIP_MSG).isEmpty())
                snackBuilder.setSnackText(extras.getString(MEMBERSHIP_MSG)).showSnack();
        }
    }

    private void init() {
        // Notification Update Status When App Open
        updateNotificationStatus();
        // Firebase Receive messaging notification
        FirebaseMessagingInitialize();

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(getSnackBarContainer());
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
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragment = null;
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_main:
                        fragment = new MainFragment();
                        break;
                    case R.id.navigation_my_salons:
                        fragment = new MySalonsFragment();
                        break;
                    case R.id.navigation_store:
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
        if (navigation.getSelectedItemId() != R.id.navigation_main)
        { // back to main page
            displayFragment(new MainFragment());
            navigation.setSelectedItemId(R.id.navigation_main);
        }
        else
        {
            super.onBackPressed();
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
        } finally
        {
            super.onDestroy();
        }
    }
}
