package it_geeks.info.elgawla.views.account;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import it_geeks.info.elgawla.Adapters.PackageAdapter;
import it_geeks.info.elgawla.repository.Models.Package;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.R;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_PACKAGES;

public class MembershipActivity extends BaseActivity {

    public DialogBuilder dialogBuilder;
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        initViews();

        getPackagesFromServer();

        EventsManager.sendOpenMembershipEvent(this, "");
    }

    private void initViews() {
        contentLayout = findViewById(R.id.membership_content_layout);
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        findViewById(R.id.btn_subscribe_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getPackagesFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(MembershipActivity.this).fetchDataFromServer(MembershipActivity.this,
                REQ_GET_ALL_PACKAGES, new RequestModel<>(REQ_GET_ALL_PACKAGES, SharedPrefManager.getInstance(MembershipActivity.this).getUser().getUser_id(), SharedPrefManager.getInstance(MembershipActivity.this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        initRecyclers(ParseResponses.parsePackages(mainObject));
                    }

                    @Override
                    public void afterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        new SnackBuilder(findViewById(R.id.membership_main_layout)).setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initRecyclers(ArrayList<List<Package>> packagesLists) {
        for (final List<Package> packages : packagesLists)
        {
            if (!packages.isEmpty())
            {
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_packages_recycler, null);
                TextView packageHeader = itemView.findViewById(R.id.tv_package_header);
                RecyclerView packagesRecycler = itemView.findViewById(R.id.rv_packages_recycler);

                // header
                packageHeader.setText(packages.get(0).getPackage_category_name());
                packageHeader.setTextColor(Color.parseColor(packages.get(0).getPackage_color()));

                // recycler
                packagesRecycler.setHasFixedSize(true);
                final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);

                packagesRecycler.setLayoutManager(layoutManager);
                packagesRecycler.setAdapter(new PackageAdapter(this, packages));

                contentLayout.addView(itemView);
            }
        }
    }
}
