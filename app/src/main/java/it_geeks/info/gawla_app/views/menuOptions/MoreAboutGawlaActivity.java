package it_geeks.info.gawla_app.views.menuOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;

public class MoreAboutGawlaActivity extends AppCompatActivity {
    ScrollView mainMoreAboutGawla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_more_about_gawla);

        // Swipe Page Back
        mainMoreAboutGawla = findViewById(R.id.main_more_about_gawla);
        mainMoreAboutGawla.setOnTouchListener(new OnSwipeTouchListener(MoreAboutGawlaActivity.this){
            public void onSwipeRight() { finish(); }
        });

        findViewById(R.id.more_about_gawla_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
