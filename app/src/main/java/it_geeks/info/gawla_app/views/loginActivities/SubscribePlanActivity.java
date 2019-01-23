package it_geeks.info.gawla_app.views.loginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.R;

public class SubscribePlanActivity extends AppCompatActivity {
    TextView txt_pay_later;
    CardView cardnurmal ,cardvip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff",this);
        setContentView(R.layout.activity_subscribe_plan);
        txt_pay_later = findViewById(R.id.txt_pay_later);
        cardnurmal = findViewById(R.id.cardnurmal);
        cardvip = findViewById(R.id.cardvip);

        txt_pay_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubscribePlanActivity.this,MainActivity.class));
                finish();
            }
        });

        cardnurmal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubscribePlanActivity.this,PaymentActivity.class));
            }
        });

        cardvip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubscribePlanActivity.this,PaymentActivity.class));
            }
        });

    }
}
