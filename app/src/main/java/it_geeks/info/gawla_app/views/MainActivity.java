package it_geeks.info.gawla_app.views;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.ConnectionChangeReceiver;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.views.NavigationFragments.AccountFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.CardsFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MainFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MenuFragment;
import it_geeks.info.gawla_app.views.NavigationFragments.MyRoundsFragment;
import it_geeks.info.gawla_app.R;

public class MainActivity extends AppCompatActivity {

    public static Activity mainInstance;

    BottomNavigationView navigation;

    ConnectionChangeReceiver connectionChangeReceiver = new ConnectionChangeReceiver();
    View snackContainer;

    private Fragment fragment = new MainFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.Instance(this).setLang(SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#f4f7fa", this);
        setContentView(R.layout.activity_main);

        mainInstance = this;

        registerReceiver(connectionChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        if (savedInstanceState == null) {
            displayFragment(fragment);
        }

        initNavigation();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() == R.id.navigation_hales) {
            super.onBackPressed();

        } else {
            displayFragment(new MainFragment());
            navigation.setSelectedItemId(R.id.navigation_hales);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(connectionChangeReceiver);
        super.onDestroy();
    }

    public View getMainFrame() {
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
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                    case R.id.navigation_my_rounds:
                        fragment = new MyRoundsFragment();
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                    case R.id.navigation_cards:
                        fragment = new CardsFragment();
                        // change status bar color
                        Common.Instance(MainActivity.this).changeStatusBarColor("#f4f7fa", MainActivity.this);
                        break;
                    case R.id.navigation_account:
                        fragment = new AccountFragment();
                        // change status bar color to white
                        Common.Instance(MainActivity.this).changeStatusBarColor("#FFFFFF", MainActivity.this);
                        break;
                    case R.id.navigation_menu:
                        fragment = new MenuFragment();
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

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment).commit();
    }

    //back from !main page ?

}
