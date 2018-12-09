package it_geeks.info.gawla_app.Views;

import android.app.Activity;
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

import it_geeks.info.gawla_app.Views.NavigationFragments.AccountFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.CardsFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.HalesFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.MenuFragment;
import it_geeks.info.gawla_app.Views.NavigationFragments.MyRoundsFragment;
import it_geeks.info.gawla_app.R;

public class MainActivity extends AppCompatActivity {

    public static Activity mainActivityInstance = new Activity();

    private Fragment fragment = new HalesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityInstance = this;

        if (savedInstanceState == null) {
            changeStatusBarColor("#f4f7fa");// for startup
            displayFragment(fragment);
        }

        initNavigation();
    }

    private void initNavigation() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_hales:
                        fragment = new HalesFragment();
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
}
