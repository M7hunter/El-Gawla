package it_geeks.info.gawla_app.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.NavigationFragments.AccountFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.CardsFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MainFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MenuFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MyRoundsFragment;
import it_geeks.info.gawla_app.R;

public class MainActivity extends AppCompatActivity {

    public static Activity mainInstance;

    private BottomNavigationView navigation;
    private Fragment fragment = new MainFragment();

    private ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();

    private View snackContainer;

    private TransHolder transHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.Instance(this).setLang(SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#f4f7fa", this);
        setContentView(R.layout.activity_main);

        mainInstance = this;

        transHolder = new TransHolder(MainActivity.this);
        transHolder.getMainActivityTranses(MainActivity.this);

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        if (savedInstanceState == null) {
            displayFragment(fragment);
        }

        initNavigation();

        setupTrans();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(connectionChangeReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // back from !main page ?
        if (navigation.getSelectedItemId() == R.id.navigation_hales) {
            super.onBackPressed();

        } else {
            displayFragment(new MainFragment());
            navigation.setSelectedItemId(R.id.navigation_hales);
        }
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
}
