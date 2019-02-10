package it_geeks.info.gawla_app.views;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.NotificationAdapter;

public class NotificationActivity extends AppCompatActivity {

  RecyclerView recyclerNotificationList;

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
    View back = findViewById(R.id.notification_back);
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
    recyclerNotificationList = findViewById(R.id.notification_list);
    recyclerNotificationList.setLayoutManager(new LinearLayoutManager(this));
    NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this,arrayList);
    recyclerNotificationList.setAdapter(notificationAdapter);

  }

}