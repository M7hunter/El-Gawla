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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.elgawla.Adapters.SalonsMiniAdapter;
import it_geeks.info.elgawla.repository.Models.Data;
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

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseSalons;
import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseSalonsArchive;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_ARCHIVE;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_BY_USER_ID;

public class MySalonsFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private List<Salon> salonsList = new ArrayList<>();
    private RecyclerView rvMySalons;
    private SalonsMiniAdapter salonsAdapter;
    private GridLayoutManager layoutManager;
    private LinearLayout emptyViewLayout;
    private ProgressBar pbpSalons;
    private TextView tvMyRecentSalons, tvMyFinishedSalons;

    private ShimmerFrameLayout salonsShimmerLayout;

    private ImageView ivNotificationBell;

    private SnackBuilder snackBuilder;

    private int userId, page = 1, last_page = 1;
    private String apiToken;
    private boolean isRecent;

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
        isRecent = true;
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
        getSalonsFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();

        SalonsMiniAdapter.clickable = true;
    }

    private void initViews(View fragmentView) {
        refreshLayout = fragmentView.findViewById(R.id.my_rounds_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        rvMySalons = fragmentView.findViewById(R.id.my_rounds_recycler);
        emptyViewLayout = fragmentView.findViewById(R.id.my_rounds_empty_view);
        tvMyRecentSalons = fragmentView.findViewById(R.id.tv_my_recent_salons);
        tvMyFinishedSalons = fragmentView.findViewById(R.id.tv_my_finished_salons);
        salonsShimmerLayout = fragmentView.findViewById(R.id.sh_my_salons);
        pbpSalons = fragmentView.findViewById(R.id.pbp_my_salons);

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
                page = 1;
                getSalonsFromServer();
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
                        page = 1;
                        switch (v.getId())
                        {
                            case R.id.tv_my_recent_salons:
                                selectRecent();
                                break;
                            case R.id.tv_my_finished_salons:
                                selectFinished();
                                break;
                        }
                        loadSalonsUI();
                        getSalonsFromServer();
                    }
                };

        tvMyRecentSalons.setOnClickListener(clickListener);
        tvMyFinishedSalons.setOnClickListener(clickListener);
    }

    private void selectRecent() {
        isRecent = true;
        tvMyRecentSalons.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvMyFinishedSalons.setBackgroundColor(Color.WHITE);
        tvMyRecentSalons.setTextColor(Color.WHITE);
        tvMyFinishedSalons.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void selectFinished() {
        isRecent = false;
        tvMyFinishedSalons.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvMyRecentSalons.setBackgroundColor(Color.WHITE);
        tvMyFinishedSalons.setTextColor(Color.WHITE);
        tvMyRecentSalons.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void getSalonsFromServer() {
        salonsList.clear();
        RetrofitClient.getInstance(getActivity()).fetchDataPerPageFromServer(context,
                new Data(isRecent ? REQ_GET_SALONS_BY_USER_ID : REQ_GET_SALONS_ARCHIVE, 1), new RequestModel<>(isRecent ? REQ_GET_SALONS_BY_USER_ID : REQ_GET_SALONS_ARCHIVE, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonsList.addAll(isRecent ? parseSalons(mainObject) : parseSalonsArchive(mainObject));

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler(isRecent ? "my_recent" : "my_archive");
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler("");
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getNextSalonsFromServer() {
        onLoadMoreSalons();
        RetrofitClient.getInstance(getActivity()).fetchDataPerPageFromServer(context,
                new Data(isRecent ? REQ_GET_SALONS_BY_USER_ID : REQ_GET_SALONS_ARCHIVE, ++page), new RequestModel<>(isRecent ? REQ_GET_SALONS_BY_USER_ID : REQ_GET_SALONS_ARCHIVE, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = salonsList.size();
                        salonsList.addAll(isRecent ? parseSalons(mainObject) : parseSalonsArchive(mainObject));
                        for (int i = nextFirstPosition; i < salonsList.size(); i++)
                        {
                            salonsAdapter.notifyItemInserted(i);
                        }

                        rvMySalons.smoothScrollToPosition(nextFirstPosition);
                        addScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                        pbpSalons.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        pbpSalons.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void onLoadMoreSalons() {
        pbpSalons.setVisibility(View.VISIBLE);
        rvMySalons.scrollToPosition(salonsList.size() - 1);
    }

    private void loadSalonsUI() {
        rvMySalons.setVisibility(View.GONE);
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

    private void initSalonsRecycler(String from) {
        stopSalonsShimmer();
        if (!salonsList.isEmpty())
        {
            emptyViewLayout.setVisibility(View.GONE);
            rvMySalons.setVisibility(View.VISIBLE);
            rvMySalons.setHasFixedSize(true);
            layoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
            rvMySalons.setLayoutManager(layoutManager);
            updateSpanCount(salonsList);
            salonsAdapter = new SalonsMiniAdapter(context, salonsList, from);
            rvMySalons.setAdapter(salonsAdapter);

            addScrollListener();
        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            rvMySalons.setVisibility(View.GONE);
        }
    }

    private void updateSpanCount(List<Salon> list) {
        if (layoutManager != null)
        {
            if (list.size() == 1)
            {
                layoutManager.setSpanCount(1);
            }
            else
            {
                layoutManager.setSpanCount(2);
            }
        }
    }

    private void addScrollListener() {
        if (page < last_page)
        {
            rvMySalons.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (layoutManager.findLastCompletelyVisibleItemPosition() == salonsAdapter.getItemCount() - 1)
                    {
                        getNextSalonsFromServer();
                        Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
                        rvMySalons.removeOnScrollListener(this);
                    }
                }
            });
        }
    }
}