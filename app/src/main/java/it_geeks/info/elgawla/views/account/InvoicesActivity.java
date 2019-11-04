package it_geeks.info.elgawla.views.account;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Invoice;
import it_geeks.info.elgawla.Adapters.InvoicesAdapter;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_MY_INVOICES;

public class InvoicesActivity extends AppCompatActivity {

    private RecyclerView buyingProcessRecycler;
    private List<Invoice> invoiceList = new ArrayList<>();
    private TextView tvEmptyView;

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        initViews();

        getData();

        handleEvents();
    }

    private void initViews() {
        buyingProcessRecycler = findViewById(R.id.buying_processes_recycler);
        tvEmptyView = findViewById(R.id.buying_processes_empty_view);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.buying_processes_main_layout));
    }

    private void getData() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(
                this,
                REQ_GET_MY_INVOICES, new RequestModel<>(REQ_GET_MY_INVOICES, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        invoiceList = ParseResponses.parseInvoices(mainObject);
                        Collections.reverse(invoiceList);
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        initRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        initRecycler();
                    }
                });
    }

    private void handleEvents() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initRecycler() {
        if (invoiceList.size() > 0) {
            buyingProcessRecycler.setHasFixedSize(true);
            buyingProcessRecycler.setLayoutManager(new LinearLayoutManager(InvoicesActivity.this, RecyclerView.VERTICAL, false));
            buyingProcessRecycler.setAdapter(new InvoicesAdapter(this, invoiceList));

            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
    }
}