package it_geeks.info.gawla_app.views.accountOptions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.R;

public class AccountDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_account_details);
        initViews();

    }

    private void initViews() {
        // back
        findViewById(R.id.account_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
