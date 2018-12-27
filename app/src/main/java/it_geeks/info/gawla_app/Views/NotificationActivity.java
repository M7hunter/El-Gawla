package it_geeks.info.gawla_app.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
;

import java.util.ArrayList;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.NotificationAdapter;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerNatificationList;

    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();

        getData();

        initNotiRecycler();
    }

    private void getData() {
        for (int i = 0; i < 6; i++) {
            arrayList.add(i+"");
        }
    }

    private void initViews() {
        View back = findViewById(R.id.app_settings_back);
        // back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        back.requestFocus();
    }

    private void initNotiRecycler() {
        recyclerNatificationList = findViewById(R.id.natification_list);
        recyclerNatificationList.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this,arrayList);
        recyclerNatificationList.setAdapter(notificationAdapter);

    }

}
