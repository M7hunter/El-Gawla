package it_geeks.info.elgawla.views.main;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import it_geeks.info.elgawla.util.ConnectivityReceiver;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.Interfaces.ConnectionInteface;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.account.AccountFragment;
import it_geeks.info.elgawla.views.store.StoreFragment;
import it_geeks.info.elgawla.views.menu.MenuFragment;
import it_geeks.info.elgawla.views.salon.MySalonsFragment;
import it_geeks.info.elgawla.R;

import static it_geeks.info.elgawla.util.Constants.PATH;
import static it_geeks.info.elgawla.util.Constants.TO_STORE;

public class MainActivity extends BaseActivity {

    private BottomNavigationView navigation;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment mainFragment, mySalonsFragment, storeFragment, accountFragment, menuFragment;

    private View snackContainer;
    private CardView cvConnectivity;
    private TextView tvConnectivity;

    public DialogBuilder dialogBuilder;
    public SnackBuilder snackBuilder;
    private ConnectivityReceiver connectivityReceiver;
    private Animator.AnimatorListener connectivityAnimatorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        connectivityReceiver = new ConnectivityReceiver(this, new ConnectionInteface() {
            @Override
            public void onConnected() {
                hideConnectivityCard();
            }

            @Override
            public void onDisconnected() {
                displayConnectivityCard();
            }
        });

        if (savedInstanceState == null)
        {
            displayFragment(selectMainFragment());
        }

        initNavigation();

        getExtras();
    }

    private void hideConnectivityCard() {
        tvConnectivity.setText(getString(R.string.connected));
        cvConnectivity.setCardBackgroundColor(Color.GREEN);

        cvConnectivity.animate().translationX(-cvConnectivity.getWidth()).setDuration(500).setStartDelay(500);
    }

    private void displayConnectivityCard() {
        tvConnectivity.setText(getString(R.string.no_connection));
        cvConnectivity.setCardBackgroundColor(Color.RED);

        cvConnectivity.setVisibility(View.VISIBLE);
        cvConnectivity.animate().translationX(0).setDuration(500);
    }

    public void getExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            if (TO_STORE.equals(extras.getString(PATH)))
            {
                displayFragment(selectStoreFragment());
                navigation.setSelectedItemId(R.id.navigation_store);
            }
        }
    }

    private void init() {
        cvConnectivity = findViewById(R.id.cv_connectivity);
        tvConnectivity = findViewById(R.id.tv_connectivity);

        connectivityAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (tvConnectivity.getText().toString().equals(getString(R.string.connected)))
                    cvConnectivity.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        cvConnectivity.animate().setListener(connectivityAnimatorListener);

        NotificationBuilder.Instance(this).CancelAll(this);
        SharedPrefManager.getInstance(this).haveNewNotification();
        // Firebase Receive messaging notification
        FirebaseMessagingInitialize();

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(getSnackBarContainer());
    }

    // Firebase initialize
    private void FirebaseMessagingInitialize() {
        if (SharedPrefManager.getInstance(this).isNotificationEnabled())
            enableFirebaseNotifications();
        else disableFirebaseNotifications();
    }

    private void enableFirebaseNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FirebaseMessaging.getInstance().subscribeToTopic("country_" + SharedPrefManager.getInstance(this).getCountry().getCountry_id());
    }

    private void disableFirebaseNotifications() {
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

                Fragment selectedFragment = null;
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_main:
                        selectedFragment = selectMainFragment();
                        break;
                    case R.id.navigation_my_salons:
                        selectedFragment = selectMySalonsFragment();
                        break;
                    case R.id.navigation_store:
                        selectedFragment = selectStoreFragment();
                        break;
                    case R.id.navigation_account:
                        selectedFragment = selectAccountFragment();
                        break;
                    case R.id.navigation_menu:
                        selectedFragment = selectMenuFragment();
                        break;
                }

                if (selectedFragment != null)
                {
                    displayFragment(selectedFragment);
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
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }

    private Fragment selectMainFragment() {
//        if (mainFragment == null)
//        {
//            mainFragment = new MainFragment();
//        }
        return new MainFragment();
    }

    private Fragment selectMySalonsFragment() {
        if (mySalonsFragment == null)
        {
            mySalonsFragment = new MySalonsFragment();
        }
        return mySalonsFragment;
    }

    private Fragment selectStoreFragment() {
        if (storeFragment == null)
        {
            storeFragment = new StoreFragment();
        }
        return storeFragment;
    }

    private Fragment selectAccountFragment() {
        if (accountFragment == null)
        {
            accountFragment = new AccountFragment();
        }
        return accountFragment;
    }

    private Fragment selectMenuFragment() {
        if (menuFragment == null)
        {
            menuFragment = new MenuFragment();
        }
        return menuFragment;
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
        super.onDestroy();

        unregisterReceiver(connectivityReceiver);
    }
}
