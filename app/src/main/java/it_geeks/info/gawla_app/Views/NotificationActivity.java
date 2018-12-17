package it_geeks.info.gawla_app.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;;

import java.util.ArrayList;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.ViewModels.Adapters.NotificationAdapter;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerNatificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initViews();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            arrayList.add(i+"");
        }


        recyclerNatificationList.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this,arrayList);
        recyclerNatificationList.setAdapter(notificationAdapter);
    }

    private void initViews() {
        recyclerNatificationList = findViewById(R.id.natification_list);
        // back
        findViewById(R.id.app_settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
