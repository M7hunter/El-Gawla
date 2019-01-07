package it_geeks.info.gawla_app.views.loginActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.R;

public class PaymentActivity extends AppCompatActivity {

    LinearLayout btnConfirmPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
    }

    private void initViews() {
        btnConfirmPayment = findViewById(R.id.payment_confirm_btn);
        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPrefManager.getInstance(PaymentActivity.this).getMembership();
                startActivity(new Intent(PaymentActivity.this,MainActivity.class));
            }
        });
    }
}
