package it_geeks.info.elgawla.views.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.JsonObject;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.elgawla.Adapters.PackageAdapter;
import it_geeks.info.elgawla.repository.Models.Package;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.R;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_PACKAGES;

public class MembershipActivity extends BaseActivity {

    private List<Package> packageList = new ArrayList<>();
    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        getPackagesFromServer();

        findViewById(R.id.btn_pay_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MembershipActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private void getPackagesFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(MembershipActivity.this).executeConnectionToServer(MembershipActivity.this,
                REQ_GET_ALL_PACKAGES, new RequestModel<>(REQ_GET_ALL_PACKAGES, SharedPrefManager.getInstance(MembershipActivity.this).getUser().getUser_id(), SharedPrefManager.getInstance(MembershipActivity.this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        packageList = ParseResponses.parsePackages(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initRecycler();
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        new SnackBuilder(findViewById(R.id.membership_main_layout)).setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initRecycler() {
        if (packageList.size() > 0)
        {
            RecyclerView packagesRecycler = findViewById(R.id.packages_recycler);
            packagesRecycler.setHasFixedSize(true);
            packagesRecycler.setAdapter(new PackageAdapter(this, packageList));
        }
    }
}
