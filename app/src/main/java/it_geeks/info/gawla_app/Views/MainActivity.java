package it_geeks.info.gawla_app.Views;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Views.NavigationFragments.AccountFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.CardsFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.MainFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.MenuFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.MyRoundsFragment;
import it_geeks.info.gawla_app.R;

public class MainActivity extends AppCompatActivity {

    public static Activity mainInstance;

    BottomNavigationView navigation;

    private Fragment fragment = new MainFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainInstance= this;

        if (savedInstanceState == null) {
            changeStatusBarColor("#f4f7fa");// for startup
            displayFragment(fragment);
        }

        initNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Common.Instance(this).setLang(SharedPrefManager.getInstance(this).getSavedLang());
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
                        // change status bar color to white
                        changeStatusBarColor("#f4f7fa");
                        break;
                    case R.id.navigation_my_rounds:
                        fragment = new MyRoundsFragment();
                        // change status bar color to white
                        changeStatusBarColor("#f4f7fa");
                        break;
                    case R.id.navigation_cards:
                        fragment = new CardsFragment();
                        // change status bar color to white
                        changeStatusBarColor("#f4f7fa");
                        break;
                    case R.id.navigation_account:
                        fragment = new AccountFragment();
                        // change status bar color to white
                        changeStatusBarColor("#FFFFFF");
                        break;
                    case R.id.navigation_menu:
                        fragment = new MenuFragment();
                        // change status bar color to white
                        changeStatusBarColor("#f4f7fa");
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

    // to change status bar color in fragments if wanted
    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    // handle back from !main page
    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() ==  R.id.navigation_hales){
            super.onBackPressed();

        } else {
            displayFragment(new MainFragment());
            navigation.setSelectedItemId(R.id.navigation_hales);
        }
    }
}
