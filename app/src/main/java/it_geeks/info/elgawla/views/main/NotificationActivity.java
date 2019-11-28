package it_geeks.info.elgawla.views.main;

import android.os.Bundle;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.Adapters.NotificationAdapter;
import it_geeks.info.elgawla.repository.Models.Notification;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_NOTIFICATION;

public class NotificationActivity extends BaseActivity {

    private SwipeRefreshLayout refreshLayout;
    private ShimmerFrameLayout shimmerLayout;
    private View emptyView;
    private RecyclerView notificationRecycler;

    private List<Notification> notificationList = new ArrayList<>();

    public DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Common.setLang(this, SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();

        handleEvent();

        startShimmer();

        getNotificationListFromServer();
    }

    private void initViews() {
        refreshLayout = findViewById(R.id.notification_swipe_refresh);
        shimmerLayout = findViewById(R.id.sh_notification);
        notificationRecycler = findViewById(R.id.notification_recycler);
        emptyView = findViewById(R.id.notification_empty_view);
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private void handleEvent() {
        // refresh page
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationListFromServer();
            }
        });

        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getNotificationListFromServer() {
        RetrofitClient.getInstance(NotificationActivity.this).executeConnectionToServer(
                NotificationActivity.this,
                REQ_GET_ALL_NOTIFICATION, new RequestModel<>(REQ_GET_ALL_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        notificationList = ParseResponses.parseNotifications(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initNotifyRecycler();
                        SharedPrefManager.getInstance(NotificationActivity.this).setHaveNewNotification(false);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initNotifyRecycler();
                        refreshLayout.setRefreshing(false);
                        new SnackBuilder(findViewById(R.id.notification_swipe_refresh)).setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void startShimmer() {
        if (shimmerLayout.getVisibility() != View.VISIBLE)
            shimmerLayout.setVisibility(View.VISIBLE);

        shimmerLayout.startShimmerAnimation();
    }

    private void stopShimmer() {
        if (shimmerLayout.getVisibility() == View.VISIBLE)
        {
            shimmerLayout.stopShimmerAnimation();
            shimmerLayout.setVisibility(View.GONE);
        }
    }

    private void initNotifyRecycler() {
        stopShimmer();
        if (!notificationList.isEmpty())
        {
            emptyView.setVisibility(View.GONE);
            notificationRecycler.setVisibility(View.VISIBLE);
            notificationRecycler.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
            notificationRecycler.setAdapter(new NotificationAdapter(NotificationActivity.this, notificationList, findViewById(R.id.notification_main_layout)));
        }
        else
        {
            emptyView.setVisibility(View.VISIBLE);
            notificationRecycler.setVisibility(View.GONE);
        }
    }
}