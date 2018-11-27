package it_geeks.info.gawla_app.SplashActivities;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;


public class SplashActivity extends AppCompatActivity {
    private LinearLayout btn_kuwait,btn_egypt;
    SharedPrefManager sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPreferences = new SharedPrefManager(SplashActivity.this);
        if (sharedPreferences.getCountry_Save() != null) {
        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        finish();
        } else {

            btn_egypt = findViewById(R.id.btn_egypt);
            btn_kuwait = findViewById(R.id.btn_kuwait);
            btn_kuwait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SharedPrefManager(SplashActivity.this).Country_Save("kuwait");
                    startActivity(new Intent(SplashActivity.this,IntroActivity.class));
                    finish();
                }
            });
            btn_egypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SharedPrefManager(SplashActivity.this).Country_Save("egypt");
                    startActivity(new Intent(SplashActivity.this,IntroActivity.class));
                    finish();
                }
            });

        } // sharedPref Country IF
    }
}
