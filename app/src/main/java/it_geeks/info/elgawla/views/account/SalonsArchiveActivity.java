package it_geeks.info.elgawla.views.account;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it_geeks.info.elgawla.Adapters.MySalonsArchiveAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.SalonArchiveModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_ARCHIVE;

public class SalonsArchiveActivity extends BaseActivity {

    private RecyclerView mySalonArchiveRecycler;
    private TextView mySalonArchiveEmptyView;

    private List<SalonArchiveModel> salonArchiveList = new ArrayList<>();

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salons_archive);

        initViews();

        getSalonsArchiveFromServer();

        handleEvents();
    }

    private void initViews() {
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
        mySalonArchiveRecycler = findViewById(R.id.salons_archive_recycler);
        mySalonArchiveEmptyView = findViewById(R.id.salons_archive_empty_view);
        snackBuilder = new SnackBuilder(findViewById(R.id.salons_archive_main_layout));
    }

    private void getSalonsArchiveFromServer() {
//        dialogBuilder.displayLoadingDialog();
//        RetrofitClient.getInstance(this).executeConnectionToServer(
//                this,
//                REQ_GET_SALONS_ARCHIVE, new RequestModel<>(REQ_GET_SALONS_ARCHIVE, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
//                        null, null, null, null, null),
//                new HandleResponses() {
//                    @Override
//                    public void handleTrueResponse(JsonObject mainObject) {
//                        salonArchiveList = ParseResponses.parseSalonsArchive(mainObject);
//                        Collections.reverse(salonArchiveList);
//                    }
//
//                    @Override
//                    public void handleAfterResponse() {
//                        dialogBuilder.hideLoadingDialog();
//                        initRecycler();
//                    }
//
//                    @Override
//                    public void handleConnectionErrors(String errorMessage) {
//                        dialogBuilder.hideLoadingDialog();
//                        snackBuilder.setSnackText(errorMessage).showSnack();
//                        initRecycler();
//                    }
//                });
    }

    private void initRecycler() {
        if (salonArchiveList.size() > 0)
        {
            mySalonArchiveRecycler.setHasFixedSize(true);
            mySalonArchiveRecycler.setAdapter(new MySalonsArchiveAdapter(this, salonArchiveList, snackBuilder));
            mySalonArchiveEmptyView.setVisibility(View.GONE);
        }
        else
        {
            mySalonArchiveEmptyView.setVisibility(View.VISIBLE);
            mySalonArchiveRecycler.setVisibility(View.GONE);
        }
    }

    private void handleEvents() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}