package it_geeks.info.gawla_app.views.menuOptions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.R;

public class MoreAboutGawlaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_more_about_gawla);

        findViewById(R.id.more_about_gawla_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
