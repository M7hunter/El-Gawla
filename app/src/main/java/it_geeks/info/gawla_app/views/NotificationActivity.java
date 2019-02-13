package it_geeks.info.gawla_app.views;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.NotificationAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Notification;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerNotificationList;
    TextView notificationLoading;
    ArrayList<Notification> NotificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();

        getData();

        initNotiRecycler();
    }

    private void getData() {
        notificationLoading.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(NotificationActivity.this).executeConnectionToServer(
                NotificationActivity.this,
                "getAllUserNotification",
                new Request(
                        SharedPrefManager.getInstance(this).getUser().getUser_id(),
                        SharedPrefManager.getInstance(this).getUser().getApi_token()
                ),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        NotificationList = ParseResponses.parseNotifications(mainObject);
                        initNotiRecycler();
                        notificationLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleFalseResponse(JsonObject errorObject) {
                        notificationLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleEmptyResponse() {
                        notificationLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        notificationLoading.setVisibility(View.GONE);
                        Toast.makeText(NotificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        notificationLoading = findViewById(R.id.notification_loading);
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
        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationActivity.this, NotificationList);
        recyclerNotificationList.setAdapter(notificationAdapter);

    }

}