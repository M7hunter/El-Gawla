package it_geeks.info.elgawla.views.salon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.elgawla.Adapters.SalonsAdapter;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Models.Round;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRounds;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_BY_USER_ID;

public class MySalonsFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private List<Round> salonsList = new ArrayList<>();
    private RecyclerView mySalonsRecycler;
    private LinearLayout emptyViewLayout;
    private ProgressBar mySalonsProgress;

    private ImageView ivNotificationBell;

    private TextView tvMySalonsHeader;
    private SnackBuilder snackBuilder;

    private int userId;
    private String apiToken;

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

        getUsrSalonsFromServer();
    }

    private void initViews(View fragmentView) {
        refreshLayout = fragmentView.findViewById(R.id.my_rounds_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        mySalonsProgress = fragmentView.findViewById(R.id.pb_my_rounds);
        mySalonsRecycler = fragmentView.findViewById(R.id.my_rounds_recycler);
        emptyViewLayout = fragmentView.findViewById(R.id.my_rounds_empty_view);

        snackBuilder = new SnackBuilder(fragmentView.findViewById(R.id.my_salons_main_layout));

        // translatable views
        tvMySalonsHeader = fragmentView.findViewById(R.id.tv_my_rounds_header);

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
                getUsrSalonsFromServer();
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
    }

    private void getUsrSalonsFromServer() {
        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(context,
                REQ_GET_SALONS_BY_USER_ID, new RequestModel<>(REQ_GET_SALONS_BY_USER_ID, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonsList.clear();
                        salonsList.addAll(parseRounds(mainObject));
                    }

                    @Override
                    public void handleAfterResponse() {
                        initMySalonsRecycler();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initMySalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void initMySalonsRecycler() {
        mySalonsProgress.setVisibility(View.GONE);

        if (salonsList.size() > 0)
        {
            tvMySalonsHeader.setText(context.getString(R.string.joined_salons));
            emptyViewLayout.setVisibility(View.GONE);
            mySalonsRecycler.setVisibility(View.VISIBLE);

            mySalonsRecycler.setHasFixedSize(true);
            mySalonsRecycler.setLayoutManager(new LinearLayoutManager(context));
            mySalonsRecycler.setAdapter(new SalonsAdapter(getActivity(), salonsList));

        }
        else
        {
            tvMySalonsHeader.setText(context.getString(R.string.no_joined_salons));
            emptyViewLayout.setVisibility(View.VISIBLE);
            mySalonsRecycler.setVisibility(View.GONE);
        }
    }
}