package it_geeks.info.gawla_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.ProductImageAdapter;
import it_geeks.info.gawla_app.Models.Round;

public class ProductDetailsActivity extends AppCompatActivity {

    FrameLayout frameLayout;

    RecyclerView imagesRecycler;

    List<String> imagesList = new ArrayList<>();

    Round round;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        setUI();

        getRoundData(savedInstanceState);

        initRoundViews_setData();

        initViews();

        initRecycler();
    }

    private void setUI() {
        frameLayout = findViewById(R.id.proDp);
        frameLayout.setElevation(100);
        getWindow().setStatusBarColor(getResources().getColor(R.color.ops));
    }

    private void getRoundData(Bundle savedInstanceState) {
        String product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                product_name = extras.getString("product_name");
                product_image = extras.getString("product_image");
                product_category = extras.getString("product_category");
                product_price = extras.getString("product_price");
                product_description = extras.getString("product_description");
                round_start_time = extras.getString("round_start_time");
                round_end_time = extras.getString("round_end_time");
                joined_members_number = extras.getString("joined_members_number");

                round = new Round(product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number);
            }

        } else {
            product_name = (String) savedInstanceState.getSerializable("product_name");
            product_image = (String) savedInstanceState.getSerializable("product_image");
            product_category = (String) savedInstanceState.getSerializable("product_category");
            product_price = (String) savedInstanceState.getSerializable("product_price");
            product_description = (String) savedInstanceState.getSerializable("product_description");
            round_start_time = (String) savedInstanceState.getSerializable("round_start_time");
            round_end_time = (String) savedInstanceState.getSerializable("round_end_time");
            joined_members_number = (String) savedInstanceState.getSerializable("joined_members_number");

            round = new Round(product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time, joined_members_number);
        }
    }

    private void initRoundViews_setData() {
        TextView tvProductName, tvProductPrice, tvProductDescription;
        ImageView imProductImage;

        // init views
        tvProductName = findViewById(R.id.product_details_name);
        tvProductPrice = findViewById(R.id.product_details_price);
        tvProductDescription = findViewById(R.id.product_details_description);
        imProductImage = findViewById(R.id.product_details_main_image);

        // set data
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_price());
        tvProductDescription.setText(round.getProduct_description());

        Picasso.with(ProductDetailsActivity.this).load(round.getProduct_image()).placeholder(R.drawable.gawla_logo_blue).into(imProductImage);
    }

    private void initViews() {
        // back
        findViewById(R.id.product_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                try { getWindow().setStatusBarColor(getResources().getColor(R.color.paleGrey)); } catch (Exception e) {  }
            }
        });

    }

    private void initRecycler() {
        imagesRecycler = findViewById(R.id.product_details_images_recycler);
        imagesRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        ProductImageAdapter productImageAdapter = new ProductImageAdapter(this, imagesList);
        imagesRecycler.setAdapter(productImageAdapter);
    }
}
