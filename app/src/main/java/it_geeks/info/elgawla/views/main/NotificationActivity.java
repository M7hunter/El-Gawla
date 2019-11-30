package it_geeks.info.elgawla.views.main;

import android.os.Bundle;
import android.view.View;
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
    private NotificationAdapter notificationAdapter;
    private LinearLayoutManager layoutManager;

    private List<Notification> notificationList = new ArrayList<>();

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private int page = 1, last_page = 1;

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
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        shimmerLayout = findViewById(R.id.sh_notification);
        notificationRecycler = findViewById(R.id.notification_recycler);
        emptyView = findViewById(R.id.notification_empty_view);
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(refreshLayout);
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
        RetrofitClient.getInstance(NotificationActivity.this).fetchDataPerPageFromServer(
                NotificationActivity.this,
                new Data(REQ_GET_ALL_NOTIFICATION, 1), new RequestModel<>(REQ_GET_ALL_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        notificationList = ParseResponses.parseNotifications(mainObject);

                        last_page = mainObject.get("last_page").getAsInt();
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
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextNotificationListFromServer() {
        RetrofitClient.getInstance(NotificationActivity.this).fetchDataPerPageFromServer(
                NotificationActivity.this,
                new Data(REQ_GET_ALL_NOTIFICATION, ++page), new RequestModel<>(REQ_GET_ALL_NOTIFICATION, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = notificationList.size();
                        notificationList.addAll(ParseResponses.parseNotifications(mainObject));
                        for (int i = nextFirstPosition; i < notificationList.size(); i++)
                        {
                            notificationAdapter.notifyItemInserted(i);
                        }

                        notificationRecycler.smoothScrollToPosition(nextFirstPosition);
                        addFinishedScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
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
            layoutManager = new LinearLayoutManager(NotificationActivity.this);
            notificationRecycler.setLayoutManager(layoutManager);
            notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationList, findViewById(R.id.notification_main_layout));
            notificationRecycler.setAdapter(notificationAdapter);

            addFinishedScrollListener();
        }
        else
        {
            emptyView.setVisibility(View.VISIBLE);
            notificationRecycler.setVisibility(View.GONE);
        }
    }

    private void addFinishedScrollListener() {
        if (page < last_page)
        {
            notificationRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (layoutManager.findLastCompletelyVisibleItemPosition() == notificationAdapter.getItemCount() - 1)
                    {
                        getNextNotificationListFromServer();
                        Toast.makeText(NotificationActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                        notificationRecycler.removeOnScrollListener(this);
                    }
                }
            });
        }
    }
}