package it_geeks.info.elgawla.views.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.Adapters.NotificationAdapter;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.Models.Notification;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.repository.Storage.NotificationDao;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_NOTIFICATION;

public class NotificationActivity extends BaseActivity {

    private SwipeRefreshLayout refreshLayout;
    private List<Notification> notificationList = new ArrayList<>();

    private TextView notificationLoading;
    private ShimmerFrameLayout salonsShimmerLayout;

    private NotificationDao notificationDao;

    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.setLang(this, SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationDao = GawlaDataBse.getInstance(this).notificationDao();

        initViews();

        handleEvent();

        startShimmer();

        getNotificationListFromServer();
    }

    private void initViews() {
        refreshLayout = findViewById(R.id.notification_swipe_refresh);
        notificationLoading = findViewById(R.id.notification_loading);
        salonsShimmerLayout = findViewById(R.id.sh_notification);
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
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
            }
        });
    }

    private void getNotificationListFromServer() {
        refreshLayout.setRefreshing(false);
        notificationLoading.setText(getString(R.string.loading));
        notificationLoading.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(NotificationActivity.this).executeConnectionToServer(
                NotificationActivity.this,
                REQ_GET_ALL_NOTIFICATION,
                new RequestModel<>(REQ_GET_ALL_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
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
                        initNotifyRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        notificationLoading.setText(errorMessage);
                        stopShimmer();
                        new SnackBuilder(findViewById(R.id.notification_swipe_refresh)).setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void startShimmer() {
        if (salonsShimmerLayout.getVisibility() != View.VISIBLE)
            salonsShimmerLayout.setVisibility(View.VISIBLE);

        salonsShimmerLayout.startShimmerAnimation();
    }

    private void stopShimmer() {
        if (salonsShimmerLayout.getVisibility() == View.VISIBLE)
        {
            salonsShimmerLayout.stopShimmerAnimation();
            salonsShimmerLayout.setVisibility(View.GONE);
        }
    }

    private void initNotifyRecycler() {
        stopShimmer();
        if (!notificationList.isEmpty())
        {
            notificationLoading.setVisibility(View.GONE);
            SharedPrefManager.getInstance(this).setNewNotification(false);

            RecyclerView notificationRecycler = findViewById(R.id.notification_recycler);
            notificationRecycler.setVisibility(View.VISIBLE);
            notificationRecycler.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
            notificationRecycler.setAdapter(new NotificationAdapter(NotificationActivity.this, notificationList, findViewById(R.id.notification_main_layout)));
        }
        else
        {
            notificationLoading.setVisibility(View.VISIBLE);
            notificationLoading.setText(getString(R.string.no_notifications));
        }
    }
}