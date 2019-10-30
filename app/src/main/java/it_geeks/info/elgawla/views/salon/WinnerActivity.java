package it_geeks.info.elgawla.views.salon;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.ImageLoader;

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
        ImageLoader.getInstance().load(SharedPrefManager.getInstance(this).getUser().getImage(), ivWinnerImage);
        Picasso.get()
                .load(R.drawable.winner_image)
                .resize(600, 1000)
                .onlyScaleDown()
                .centerInside()
                .into(((ImageView) findViewById(R.id.winner_image)));
    }

    private void handleEvents() {
        // goto winners page
        findViewById(R.id.btn_winner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
