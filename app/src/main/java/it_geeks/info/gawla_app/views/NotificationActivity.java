package it_geeks.info.gawla_app.views;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.NotificationAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Notifications;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerNotificationList;
    private List<Notifications> NotificationList = new ArrayList<>();

    public TextView notificationLoading;
    private CardView loadingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();

        getData();
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

                        GawlaDataBse.getGawlaDatabase(NotificationActivity.this).notificationDao().removeNotifications();
                        GawlaDataBse.getGawlaDatabase(NotificationActivity.this).notificationDao().insertNotification(NotificationList);
                        GawlaDataBse.getGawlaDatabase(NotificationActivity.this).notificationDao().updateStatusNotification(false);
                        initNotiRecycler();
                        notificationLoading.setVisibility(View.GONE);
                        if (NotificationList.size() < 1) {
                            notificationLoading.setVisibility(View.VISIBLE);
                            notificationLoading.setText("no notifications");
                        }
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
        loadingCard = findViewById(R.id.loading_card);
        notificationLoading = findViewById(R.id.notification_loading);

        // back
        findViewById(R.id.notification_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void initNotiRecycler() {
        SharedPrefManager.getInstance(this).setNewNotfication(false);
        GawlaDataBse.getGawlaDatabase(NotificationActivity.this).notificationDao().selectAllNotification().observe(NotificationActivity.this, new Observer<List<Notifications>>() {
            @Override
            public void onChanged(List<Notifications> notifications) {
                NotificationList = notifications;
                recyclerNotificationList = findViewById(R.id.notification_list);
                recyclerNotificationList.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                recyclerNotificationList.setAdapter(new NotificationAdapter(NotificationActivity.this, NotificationList));
            }
        });
    }
}