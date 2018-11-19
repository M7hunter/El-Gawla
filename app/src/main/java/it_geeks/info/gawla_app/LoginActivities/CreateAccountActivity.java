package it_geeks.info.gawla_app.LoginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it_geeks.info.gawla_app.R;

public class CreateAccountActivity extends AppCompatActivity {
    TextView txtHaveAccount;
    Button btnCreateAndLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        btnCreateAndLogin = findViewById(R.id.txtCreateAndLogin);
        txtHaveAccount = findViewById(R.id.txt_haveAccount);
        txtHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });
        btnCreateAndLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this,SubscribePlanActivity.class));
                finish();
            }
        });
    }

}
