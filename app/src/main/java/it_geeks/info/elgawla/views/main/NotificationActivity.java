package it_geeks.info.elgawla.views.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.Adapters.NotificationAdapter;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.repository.Models.Notification;
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
    private RecyclerView rvNotification;
    private ProgressBar pbpNotify;

    private List<Notification> notificationList = new ArrayList<>();

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private int page = 1, last_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();

        handleEvent();

        startShimmer();

        getFirstNotificationsFromServer();
    }

    private void initViews() {
        refreshLayout = findViewById(R.id.notification_swipe_refresh);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        shimmerLayout = findViewById(R.id.sh_notification);
        rvNotification = findViewById(R.id.notification_recycler);
        emptyView = findViewById(R.id.notification_empty_view);
        pbpNotify = findViewById(R.id.pbp_notify);
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(refreshLayout);
    }

    private void handleEvent() {
        // refresh page
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFirstNotificationsFromServer();
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

    private void getFirstNotificationsFromServer() {
        RetrofitClient.getInstance(NotificationActivity.this).fetchDataPerPageFromServer(
                NotificationActivity.this,
                new Data(REQ_GET_ALL_NOTIFICATION, 1), new RequestModel<>(REQ_GET_ALL_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        notificationList = ParseResponses.parseNotifications(mainObject);

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void afterResponse() {
                        initNotifyRecycler();
                        SharedPrefManager.getInstance(NotificationActivity.this).setHaveNewNotification(false);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        initNotifyRecycler();
                        refreshLayout.setRefreshing(false);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextNotificationsFromServer() {
        onLoadMoreNotification();
        RetrofitClient.getInstance(NotificationActivity.this).fetchDataPerPageFromServer(
                NotificationActivity.this,
                new Data(REQ_GET_ALL_NOTIFICATION, ++page), new RequestModel<>(REQ_GET_ALL_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = notificationList.size();
                        notificationList.addAll(ParseResponses.parseNotifications(mainObject));
                        for (int i = nextFirstPosition; i < notificationList.size(); i++)
                        {
                            rvNotification.getAdapter().notifyItemInserted(i);
                        }

                        rvNotification.smoothScrollToPosition(nextFirstPosition);
                        addFinishedScrollListener();
                    }

                    @Override
                    public void afterResponse() {
                        pbpNotify.setVisibility(View.GONE);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        pbpNotify.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void onLoadMoreNotification() {
        pbpNotify.setVisibility(View.VISIBLE);
        rvNotification.scrollToPosition(notificationList.size() - 1);
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
            rvNotification.setVisibility(View.VISIBLE);
            rvNotification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
            rvNotification.setAdapter(new NotificationAdapter(NotificationActivity.this, notificationList, findViewById(R.id.notification_main_layout)));

            addFinishedScrollListener();
        }
        else
        {
            emptyView.setVisibility(View.VISIBLE);
            rvNotification.setVisibility(View.GONE);
        }
    }

    private void addFinishedScrollListener() {
        if (page < last_page)
        {
            rvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (((LinearLayoutManager) rvNotification.getLayoutManager()).findLastCompletelyVisibleItemPosition() == rvNotification.getAdapter().getItemCount() - 1)
                    {
                        getNextNotificationsFromServer();
                        rvNotification.removeOnScrollListener(this);
                    }
                }
            });
        }
    }
}