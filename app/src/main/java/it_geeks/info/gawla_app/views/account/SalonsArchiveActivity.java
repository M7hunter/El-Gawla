package it_geeks.info.gawla_app.views.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.MySalonsArchiveAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.SalonArchiveModel;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.DialogBuilder;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_SALONS_ARCHIVE;

public class SalonsArchiveActivity extends AppCompatActivity {

    private RecyclerView mySalonArchiveRecycler;
    private TextView mySalonArchiveEmptyView;

    private List<SalonArchiveModel> salonArchiveList = new ArrayList<>();

    public DialogBuilder dialogBuilder;

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
    }

    private void getSalonsArchiveFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(
                this,
                REQ_GET_SALONS_ARCHIVE, new Request<>(REQ_GET_SALONS_ARCHIVE, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonArchiveList = ParseResponses.parseSalonsArchive(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        initRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        initRecycler();
                    }
                });
    }

    private void initRecycler() {
        if (salonArchiveList.size() > 0)
        {
            mySalonArchiveRecycler.setHasFixedSize(true);
            mySalonArchiveRecycler.setAdapter(new MySalonsArchiveAdapter(this, salonArchiveList, findViewById(R.id.salons_archive_main_layout)));
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