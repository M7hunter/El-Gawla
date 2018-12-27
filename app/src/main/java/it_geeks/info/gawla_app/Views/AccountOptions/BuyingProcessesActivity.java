package it_geeks.info.gawla_app.Views.AccountOptions;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.BuyingProcess;
import it_geeks.info.gawla_app.Controllers.Adapters.BuyingProcessAdapter;

public class BuyingProcessesActivity extends AppCompatActivity {

    List<BuyingProcess> buyingProcessList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_buying_processes);

        getData();

        initViews();

        initRecycler();
    }

    private void getData() {
        for (int i = 0; i < 6; i++) {
            BuyingProcess buyingProcess = new BuyingProcess("process " + i,
                    "card " + i,
                    "date " + i,
                    "cost " + i);

            buyingProcessList.add(buyingProcess);
        }
    }

    private void initViews() {
        // back
        findViewById(R.id.buying_processes_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initRecycler() {
        RecyclerView buyingProcessRecycler = findViewById(R.id.buying_processes_recycler);
        buyingProcessRecycler.setHasFixedSize(true);
        buyingProcessRecycler.setLayoutManager(new LinearLayoutManager(BuyingProcessesActivity.this, 1, false));
        BuyingProcessAdapter buyingProcessAdapter = new BuyingProcessAdapter(BuyingProcessesActivity.this, buyingProcessList);
        buyingProcessRecycler.setAdapter(buyingProcessAdapter);
    }

    // to change status bar color
    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
