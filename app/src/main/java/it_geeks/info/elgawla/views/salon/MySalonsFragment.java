package it_geeks.info.elgawla.views.salon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.elgawla.Adapters.SalonsMiniAdapter;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRounds;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_ARCHIVE;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_BY_USER_ID;

public class MySalonsFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private List<Salon> recentSalonsList = new ArrayList<>(), finishedSalonsList = new ArrayList<>();
    private RecyclerView mySalonsRecycler;
    private LinearLayout emptyViewLayout;
    private TextView tvMyRecentSalons, tvMyFinishedSalons;

    private ShimmerFrameLayout salonsShimmerLayout;

    private ImageView ivNotificationBell;

    private SnackBuilder snackBuilder;

    private int userId;
    private String apiToken;
    private boolean isRecent = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_salons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);

        initViews(fragmentView);

        handleEvents(fragmentView);

        loadSalonsUI();
        getRecentSalonsFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();

        SalonsMiniAdapter.clickable = true;
    }

    private void initViews(View fragmentView) {
        refreshLayout = fragmentView.findViewById(R.id.my_rounds_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        mySalonsRecycler = fragmentView.findViewById(R.id.my_rounds_recycler);
        emptyViewLayout = fragmentView.findViewById(R.id.my_rounds_empty_view);
        tvMyRecentSalons = fragmentView.findViewById(R.id.tv_my_recent_salons);
        tvMyFinishedSalons = fragmentView.findViewById(R.id.tv_my_finished_salons);
        salonsShimmerLayout = fragmentView.findViewById(R.id.sh_my_salons);

        snackBuilder = new SnackBuilder(fragmentView.findViewById(R.id.my_salons_main_layout));

        ivNotificationBell = fragmentView.findViewById(R.id.iv_notification_bell);
        View bellIndicator = fragmentView.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(getContext(), bellIndicator);

        // load user image
        ImageLoader.getInstance().loadUserImage(context, ((ImageView) fragmentView.findViewById(R.id.iv_user_image)));
    }

    private void handleEvents(final View fragmentView) {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRecent)
                {
                    getRecentSalonsFromServer();
                }
                else
                {
                    getFinishedSalonsFromServer();
                }
            }
        });

        // notification onClick
        ivNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        fragmentView.findViewById(R.id.iv_user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });


        View.OnClickListener clickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId())
                        {
                            case R.id.tv_my_recent_salons:
                                if (!isRecent)
                                {
                                    isRecent = true;
                                    selectRecent();
                                    loadSalonsUI();
                                    getRecentSalonsFromServer();
                                }
                                break;
                            case R.id.tv_my_finished_salons:
                                if (isRecent)
                                {
                                    isRecent = false;
                                    selectFinished();
                                    loadSalonsUI();
                                    getFinishedSalonsFromServer();
                                }
                                break;
                        }
                    }
                };

        tvMyRecentSalons.setOnClickListener(clickListener);
        tvMyFinishedSalons.setOnClickListener(clickListener);
    }

    private void selectRecent() {
        tvMyRecentSalons.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvMyFinishedSalons.setBackgroundColor(Color.WHITE);
        tvMyRecentSalons.setTextColor(Color.WHITE);
        tvMyFinishedSalons.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void selectFinished() {
        tvMyFinishedSalons.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvMyRecentSalons.setBackgroundColor(Color.WHITE);
        tvMyFinishedSalons.setTextColor(Color.WHITE);
        tvMyRecentSalons.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void getRecentSalonsFromServer() {
        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(context,
                REQ_GET_SALONS_BY_USER_ID, new RequestModel<>(REQ_GET_SALONS_BY_USER_ID, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        if (!isRecent) return;

                        recentSalonsList.clear();
                        recentSalonsList.addAll(parseRounds(mainObject));
                    }

                    @Override
                    public void handleAfterResponse() {
                        if (!isRecent) return;

                        initRecentSalonsRecycler();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initRecentSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getFinishedSalonsFromServer() {
        RetrofitClient.getInstance(context).executeConnectionToServer(
                context,
                REQ_GET_SALONS_ARCHIVE, new RequestModel<>(REQ_GET_SALONS_ARCHIVE, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        if (isRecent) return;

                        finishedSalonsList = ParseResponses.parseSalonsArchive(mainObject);
                        Collections.reverse(finishedSalonsList);
                    }

                    @Override
                    public void handleAfterResponse() {
                        if (isRecent) return;

                        initFinishedSalonsRecycler();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initFinishedSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void loadSalonsUI() {
        mySalonsRecycler.setVisibility(View.GONE);
        emptyViewLayout.setVisibility(View.GONE);
        startSalonsShimmer();
    }

    private void startSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() != View.VISIBLE)
            salonsShimmerLayout.setVisibility(View.VISIBLE);

        salonsShimmerLayout.startShimmerAnimation();
    }

    private void stopSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() == View.VISIBLE)
        {
            salonsShimmerLayout.stopShimmerAnimation();
            salonsShimmerLayout.setVisibility(View.GONE);
        }
    }

    private void initRecentSalonsRecycler() {
        stopSalonsShimmer();
        if (recentSalonsList.size() > 0)
        {
            emptyViewLayout.setVisibility(View.GONE);
            mySalonsRecycler.setVisibility(View.VISIBLE);
            updateSpanCount(recentSalonsList);
            mySalonsRecycler.setHasFixedSize(true);
            mySalonsRecycler.setAdapter(new SalonsMiniAdapter(context, recentSalonsList));
        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            mySalonsRecycler.setVisibility(View.GONE);
        }
    }

    private void initFinishedSalonsRecycler() {
        stopSalonsShimmer();
        if (finishedSalonsList.size() > 0)
        {
            emptyViewLayout.setVisibility(View.GONE);
            mySalonsRecycler.setVisibility(View.VISIBLE);
            updateSpanCount(finishedSalonsList);
            mySalonsRecycler.setHasFixedSize(true);
            mySalonsRecycler.setAdapter(new SalonsMiniAdapter(context, finishedSalonsList));
        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            mySalonsRecycler.setVisibility(View.GONE);
        }
    }

    private void updateSpanCount(List<Salon> list) {
        if (mySalonsRecycler.getLayoutManager() != null)
        {
            if (list.size() == 1)
            {
                ((GridLayoutManager) mySalonsRecycler.getLayoutManager()).setSpanCount(1);
            }
            else
            {
                ((GridLayoutManager) mySalonsRecycler.getLayoutManager()).setSpanCount(2);
            }
        }
    }
}