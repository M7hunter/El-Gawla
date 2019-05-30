package it_geeks.info.gawla_app.views.menu;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.R;

public class MoreAboutGawlaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance().changeStatusBarColor(this, "#ffffff");
        setContentView(R.layout.activity_more_about_gawla);

        findViewById(R.id.more_about_gawla_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
