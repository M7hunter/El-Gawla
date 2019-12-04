package it_geeks.info.elgawla.views.main;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.views.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WinnersActivity extends BaseActivity {

    private ImageView ivNewsImage;
    private TextView tvNewsTitle, tvNewsBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winners);

        initViews();

        getData();
    }

    private void initViews() {
        ivNewsImage = findViewById(R.id.iv_news_image);
        tvNewsTitle = findViewById(R.id.tv_news_title);
        tvNewsBody = findViewById(R.id.tv_news_body);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getData() {
        ImageLoader.getInstance().load(getIntent().getStringExtra("news_image"), ivNewsImage);
        tvNewsTitle.setText(getIntent().getStringExtra("news_title"));
        tvNewsBody.setText(getIntent().getStringExtra("news_body"));
    }
}

