package it_geeks.info.gawla_app;


import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import it_geeks.info.gawla_app.Adapters.ProductImageAdapter;

public class ProductDetailsActivity extends AppCompatActivity {

    RecyclerView rProdList;
    ImageView imgOut;
    static FrameLayout frameLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        rProdList = findViewById(R.id.RecyProdList);
        imgOut = findViewById(R.id.outProductDetails);
        frameLayout = findViewById(R.id.proDp);

        frameLayout.setElevation(100);
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.ops));

        rProdList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        ProductImageAdapter productImageAdapter = new ProductImageAdapter(this);
        rProdList.setAdapter(productImageAdapter);

        imgOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailsActivity.this.onBackPressed();
                ProductDetailsActivity.this.getWindow().setStatusBarColor(getResources().getColor(R.color.paleGrey));
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
