package it_geeks.info.gawla_app.LoginActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import it_geeks.info.gawla_app.MainActivity;
import it_geeks.info.gawla_app.R;

public class PaymentActivity extends AppCompatActivity {

    RelativeLayout rBtnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        rBtnPay = findViewById(R.id.rBtnPay);
        rBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this,MainActivity.class));
            }
        });
    }
}
