package it_geeks.info.gawla_app.views.menuOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;

public class TermsAndConditionsActivity extends AppCompatActivity {
    ScrollView mainTermsAndConditionsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_terms_and_conditions);

        // Swipe Page Back
        mainTermsAndConditionsActivity = findViewById(R.id.mainTermsAndConditionsActivity);
        mainTermsAndConditionsActivity.setOnTouchListener(new OnSwipeTouchListener(TermsAndConditionsActivity.this){
            public void onSwipeRight() { finish(); }
        });

        findViewById(R.id.terms_conditions_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
