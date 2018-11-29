package it_geeks.info.gawla_app.SplashActivities;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;


public class SplashActivity extends AppCompatActivity {
    private LinearLayout btn_kuwait, btn_egypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppLang();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if (SharedPrefManager.getInstance(SplashActivity.this).getCountry_Save() != null) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        } else {
            btn_egypt = findViewById(R.id.btn_egypt);
            btn_kuwait = findViewById(R.id.btn_kuwait);

            btn_egypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefManager.getInstance(SplashActivity.this).Country_Save("egypt");
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });

            btn_kuwait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefManager.getInstance(SplashActivity.this).Country_Save("kuwait");
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });

        } // sharedPref Country IF
    }

    private void setAppLang() {
        Common.Instance(SplashActivity.this).setLang(SharedPrefManager.getInstance(SplashActivity.this).getSavedLang());
    }
}
