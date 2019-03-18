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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.NotificationAdapter;
import it_geeks.info.gawla_app.repository.Models.Notification;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.NotificationDao;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class NotificationActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private List<Notification> notificationList = new ArrayList<>();

    private TextView notificationLoading;
    private CardView loadingCard;

    private NotificationDao notificationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationDao = GawlaDataBse.getGawlaDatabase(this).notificationDao();

        initViews();

        getNotificationListFromServer();

        handleEvent();
    }

    private void handleEvent() {
        // refresh page
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationListFromServer();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void getNotificationListFromServer() {
        notificationLoading.setText(getString(R.string.loading));
        notificationLoading.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(NotificationActivity.this).executeConnectionToServer(
                NotificationActivity.this,
                "getAllUserNotification",
                new Request(SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        notificationDao.insertNotification(ParseResponses.parseNotifications(mainObject));

                        notificationList = notificationDao.getAllNotification();
                        notificationDao.updateStatusNotification(false);

                        if (notificationList.size() > 0) {
                            notificationLoading.setVisibility(View.GONE);
                            initNotifyRecycler();
                        } else {
                            notificationLoading.setVisibility(View.VISIBLE);
                            notificationLoading.setText(getString(R.string.no_notifications));
                        }
                    }

                    @Override
                    public void handleFalseResponse(JsonObject errorObject) {
                        notificationLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleEmptyResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        notificationLoading.setText(errorMessage);
                        Toast.makeText(NotificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        loadingCard = findViewById(R.id.loading_card);
        notificationLoading = findViewById(R.id.notification_loading);

        //refresh
        refreshLayout = findViewById(R.id.notification_swipe_refresh);

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

    private void initNotifyRecycler() {
        SharedPrefManager.getInstance(this).setNewNotfication(false);

        RecyclerView notificationRecycler = findViewById(R.id.notification_recycler);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        notificationRecycler.setAdapter(new NotificationAdapter(NotificationActivity.this, notificationList));
    }
}