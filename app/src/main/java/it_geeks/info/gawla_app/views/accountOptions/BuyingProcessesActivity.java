package it_geeks.info.gawla_app.views.accountOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.BuyingProcess;
import it_geeks.info.gawla_app.Controllers.Adapters.BuyingProcessAdapter;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;
import it_geeks.info.gawla_app.views.menuOptions.CallUsActivity;

public class BuyingProcessesActivity extends AppCompatActivity {

    List<BuyingProcess> buyingProcessList = new ArrayList<>();
    RelativeLayout mainBuyingProcessesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
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
        // Swipe Page Back
        mainBuyingProcessesActivity = findViewById(R.id.mainBuyingProcesses);
        mainBuyingProcessesActivity.setOnTouchListener(new OnSwipeTouchListener(BuyingProcessesActivity.this){
            public void onSwipeRight() { finish(); }
        });

    }

    private void initRecycler() {
        RecyclerView buyingProcessRecycler = findViewById(R.id.buying_processes_recycler);
        buyingProcessRecycler.setHasFixedSize(true);
        buyingProcessRecycler.setLayoutManager(new LinearLayoutManager(BuyingProcessesActivity.this, RecyclerView.VERTICAL, false));
        buyingProcessRecycler.setAdapter(new BuyingProcessAdapter(BuyingProcessesActivity.this, buyingProcessList));
    }
}