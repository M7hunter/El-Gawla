package it_geeks.info.gawla_app.views.accountOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.BuyingProcess;
import it_geeks.info.gawla_app.Adapters.BuyingProcessAdapter;

public class BuyingProcessesActivity extends AppCompatActivity {

    private RecyclerView buyingProcessRecycler;
    private List<BuyingProcess> buyingProcessList = new ArrayList<>();
    private TextView tvEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_buying_processes);

        getData();

        initViews();

        handleEvents();

        initRecycler();
    }

    private void getData() {

    }

    private void initViews() {
        buyingProcessRecycler = findViewById(R.id.buying_processes_recycler);
        tvEmptyView = findViewById(R.id.buying_processes_empty_view);
    }

    private void handleEvents() {
        // back
        findViewById(R.id.buying_processes_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initRecycler() {
        if (buyingProcessList.size() > 0) {
            buyingProcessRecycler.setHasFixedSize(true);
            buyingProcessRecycler.setLayoutManager(new LinearLayoutManager(BuyingProcessesActivity.this, RecyclerView.VERTICAL, false));
            buyingProcessRecycler.setAdapter(new BuyingProcessAdapter(BuyingProcessesActivity.this, buyingProcessList));

            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
    }
}