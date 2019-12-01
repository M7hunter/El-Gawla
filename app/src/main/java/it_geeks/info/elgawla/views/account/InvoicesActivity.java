package it_geeks.info.elgawla.views.account;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.repository.Models.Invoice;
import it_geeks.info.elgawla.Adapters.InvoicesAdapter;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_MY_CARDS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_MY_INVOICES;

public class InvoicesActivity extends BaseActivity {

    private RecyclerView invoicesRecycler;
    private List<Invoice> invoiceList = new ArrayList<>();
    private TextView tvEmptyView;

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private int page = 1, last_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        initViews();

        getInvoicesFromServer();

        handleEvents();
    }

    private void initViews() {
        invoicesRecycler = findViewById(R.id.buying_processes_recycler);
        tvEmptyView = findViewById(R.id.buying_processes_empty_view);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.buying_processes_main_layout));
    }

    private void getInvoicesFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).fetchDataPerPageFromServer(
                this,
                new Data(REQ_GET_MY_INVOICES, 1), new RequestModel<>(REQ_GET_MY_INVOICES, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        invoiceList = ParseResponses.parseInvoices(mainObject);

                        last_page = mainObject.get("last_page").getAsInt();
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
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextInvoicesFromServer() {
        RetrofitClient.getInstance(this).fetchDataPerPageFromServer(this,
                new Data(REQ_GET_MY_INVOICES, ++page),
                new RequestModel<>(REQ_GET_MY_INVOICES, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = invoiceList.size();
                        invoiceList.addAll(ParseResponses.parseInvoices(mainObject));
                        for (int i = nextFirstPosition; i < invoiceList.size(); i++)
                        {
                            invoicesRecycler.getAdapter().notifyItemInserted(i);
                        }

                        invoicesRecycler.smoothScrollToPosition(nextFirstPosition);
                        addScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
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
        if (!invoiceList.isEmpty())
        {
            invoicesRecycler.setHasFixedSize(true);
            invoicesRecycler.setLayoutManager(new LinearLayoutManager(InvoicesActivity.this, RecyclerView.VERTICAL, false));
            invoicesRecycler.setAdapter(new InvoicesAdapter(this, invoiceList));

            tvEmptyView.setVisibility(View.GONE);

            addScrollListener();
        }
        else
        {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void addScrollListener() {
        if (page < last_page)
        {
            invoicesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (((LinearLayoutManager) invoicesRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition() == invoicesRecycler.getAdapter().getItemCount() - 1)
                    {
                        getNextInvoicesFromServer();
                        Toast.makeText(InvoicesActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                        invoicesRecycler.removeOnScrollListener(this);
                    }
                }
            });
        }
    }
}