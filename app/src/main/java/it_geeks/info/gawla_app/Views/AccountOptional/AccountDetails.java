package it_geeks.info.gawla_app.Views.AccountOptional;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import it_geeks.info.gawla_app.R;

public class AccountDetails extends AppCompatActivity {
    ImageView arrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        init();

        arrowBack.setOnClickListener(click);
    }

    private void init() {
        arrowBack = findViewById(R.id.details_arrow_out);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                // back
                case R.id.details_arrow_out:
                    AccountDetails.this.onBackPressed();
                    break;

            }
        }
    };

}
