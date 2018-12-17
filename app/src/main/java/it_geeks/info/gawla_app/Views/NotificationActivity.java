package it_geeks.info.gawla_app.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;;

import it_geeks.info.gawla_app.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();
    }

    private void initViews() {
        // back
        findViewById(R.id.app_settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
