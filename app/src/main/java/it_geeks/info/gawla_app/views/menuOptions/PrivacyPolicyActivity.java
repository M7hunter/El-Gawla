package it_geeks.info.gawla_app.views.menuOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;
import it_geeks.info.gawla_app.views.accountOptions.PrivacyDetailsActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ScrollView mainPrivacyPolicyActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_privacy_policy);

        // Swipe Page Back
        mainPrivacyPolicyActivity = findViewById(R.id.mainPrivacyPolicyActivity);
        mainPrivacyPolicyActivity.setOnTouchListener(new OnSwipeTouchListener(PrivacyPolicyActivity.this){
            public void onSwipeRight() { finish(); }
        });

        findViewById(R.id.privacy_policy_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
