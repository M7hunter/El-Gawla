package it_geeks.info.gawla_app.views;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class WinnerActivity extends AppCompatActivity {

    private TextView tvWinnerName;
    private ImageView ivWinnerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        initViews();

        bindData();

        handleEvents();
    }

    private void initViews() {
        tvWinnerName = findViewById(R.id.tv_winner_name);
        ivWinnerImage = findViewById(R.id.iv_winner_image);
    }

    private void bindData() {
        tvWinnerName.setText(SharedPrefManager.getInstance(this).getUser().getName());
        Picasso.with(this).load(SharedPrefManager.getInstance(this).getUser().getImage()).into(ivWinnerImage);
    }

    private void handleEvents() {
        // back
        findViewById(R.id.winner_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // goto winners page
        findViewById(R.id.btn_winner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
