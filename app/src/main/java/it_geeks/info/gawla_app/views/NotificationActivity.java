package it_geeks.info.gawla_app.views;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Adapters.NotificationAdapter;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.repository.Models.Notification;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.NotificationDao;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_USER_NOTIFICATION;

public class NotificationActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private List<Notification> notificationList = new ArrayList<>();

    private TextView notificationLoading;

    private NotificationDao notificationDao;

    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationDao = GawlaDataBse.getInstance(this).notificationDao();

        initViews();

        handleEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getNotificationListFromServer();
    }

    private void initViews() {
        refreshLayout = findViewById(R.id.notification_swipe_refresh);
        notificationLoading = findViewById(R.id.notification_loading);
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        // back
        findViewById(R.id.notification_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                REQ_GET_USER_NOTIFICATION,
                new Request<>(REQ_GET_USER_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        notificationDao.removeNotifications(notificationDao.getAllNotification());
                        notificationDao.insertNotification(ParseResponses.parseNotifications(mainObject));

                        notificationList = notificationDao.getAllNotification();
                        notificationDao.updateStatusNotification(false);
                    }

                    @Override
                    public void handleAfterResponse() {
                        if (notificationList.size() > 0) {
                            notificationLoading.setVisibility(View.GONE);
                            initNotifyRecycler();
                        } else {
                            notificationLoading.setVisibility(View.VISIBLE);
                            notificationLoading.setText(getString(R.string.no_notifications));
                        }
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        notificationLoading.setText(errorMessage);
                        Toast.makeText(NotificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initNotifyRecycler() {
        SharedPrefManager.getInstance(this).setNewNotification(false);

        RecyclerView notificationRecycler = findViewById(R.id.notification_recycler);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        notificationRecycler.setAdapter(new NotificationAdapter(NotificationActivity.this, notificationList));
    }
}