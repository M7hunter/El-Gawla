package it_geeks.info.gawla_app.Views;

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

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.Adapters.ProductSubImagesAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;

public class ProductDetailsActivity extends AppCompatActivity {

    private FrameLayout MainLayout;

    private RecyclerView imagesRecycler;

    public ImageView imProductImage;

    private List<ProductSubImage> imagesList = new ArrayList<>();

    private Round round;

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
        MainLayout = findViewById(R.id.main_layout);
        MainLayout.setElevation(100);
        getWindow().setStatusBarColor(getResources().getColor(R.color.ops));
    }

    private void initViews() {
        // back
        findViewById(R.id.product_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                try {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.paleGrey));
                } catch (Exception e) {
                }
            }
        });
    }

    private void getRoundData(Bundle savedInstanceState) {
        String product_name, product_image, product_category, product_price, product_description, round_start_time, round_end_time;
        int product_id, salon_id;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) { // get data from previous page
                product_id = extras.getInt("product_id");
                salon_id = extras.getInt("salon_id");
                product_name = extras.getString("product_name");
                product_category = extras.getString("category_name");
                product_price = extras.getString("product_commercial_price");
                product_description = Common.Instance(ProductDetailsActivity.this).removeEmptyLines(extras.getString("product_product_description"));
                product_image = extras.getString("product_image");
                round_start_time = extras.getString("round_start_time");
                round_end_time = extras.getString("round_end_time");

                round = new Round(product_id,
                        salon_id,
                        product_name,
                        product_category,
                        extras.getString("country_name"),
                        product_price,
                        product_description,
                        product_image,
                        round_start_time,
                        round_end_time,
                        extras.getString("first_join_time"),
                        extras.getString("second_join_time"),
                        extras.getString("round_date"),
                        extras.getString("round_time"),
                        extras.getString("rest_time"));
            }

        } else { // get data from saved state
            product_id = savedInstanceState.getInt("product_id");
            salon_id = savedInstanceState.getInt("salon_id");
            product_name = (String) savedInstanceState.getSerializable("product_name");
            product_category = (String) savedInstanceState.getSerializable("category_name");
            product_price = (String) savedInstanceState.getSerializable("product_commercial_price");
            product_description = (String) savedInstanceState.getSerializable("product_product_description");
            product_image = (String) savedInstanceState.getSerializable("product_image");
            round_start_time = (String) savedInstanceState.getSerializable("round_start_time");
            round_end_time = (String) savedInstanceState.getSerializable("round_end_time");

            round = new Round(product_id,
                    salon_id,
                    product_name,
                    product_category,
                    (String) savedInstanceState.getSerializable("country_name"),
                    product_price,
                    product_description,
                    product_image,
                    round_start_time,
                    round_end_time,
                    (String) savedInstanceState.getSerializable("first_join_time"),
                    (String) savedInstanceState.getSerializable("second_join_time"),
                    (String) savedInstanceState.getSerializable("round_date"),
                    (String) savedInstanceState.getSerializable("round_time"),
                    (String) savedInstanceState.getSerializable("rest_time"));
        }

        imagesList.addAll(GawlaDataBse.getGawlaDatabase(ProductDetailsActivity.this).productImageDao().getSubImagesById(round.getProduct_id()));
    }

    private void initRoundViews_setData() {
        TextView tvProductName, tvProductPrice, tvProductDescription;

        // init views
        tvProductName = findViewById(R.id.product_details_name);
        tvProductPrice = findViewById(R.id.product_details_price);
        tvProductDescription = findViewById(R.id.product_details_descriptions);
        imProductImage = findViewById(R.id.product_details_main_image);

        // set data
        tvProductName.setText(round.getProduct_name());
        tvProductPrice.setText(round.getProduct_commercial_price());
        tvProductDescription.setText(round.getProduct_product_description());

        Picasso.with(ProductDetailsActivity.this).load(round.getProduct_image()).placeholder(R.drawable.gawla_logo_blue).into(imProductImage);
    }

    private void initRecycler() {
        imagesRecycler = findViewById(R.id.product_details_images_recycler);
        imagesRecycler.setHasFixedSize(true);
        imagesRecycler.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ProductSubImagesAdapter productSubImagesAdapter = new ProductSubImagesAdapter(this, imagesList);
        imagesRecycler.setAdapter(productSubImagesAdapter);
    }
}
