package it_geeks.info.gawla_app.views.salon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import it_geeks.info.gawla_app.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.account.AccountDetailsActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRounds;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_SALON_BY_USER_ID;

public class MySalonsFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private List<Round> salonsList = new ArrayList<>();
    private RecyclerView mySalonsRecycler;
    private LinearLayout emptyViewLayout, noConnectionLayout;
    private ProgressBar mySalonsProgress;

    private ImageView ivNotificationBell;

    private TextView tvMySalonsHeader, tvMySalonsEmptyHint; // <- trans

    private int userId;
    private String apiToken;

    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_my_salons, container, false);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        initViews();

        setupTrans();

        handleEvents();

        checkConnection();

        return fragmentView;
    }

    private void initViews() {
        refreshLayout = fragmentView.findViewById(R.id.my_rounds_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        mySalonsProgress = fragmentView.findViewById(R.id.pb_my_rounds);
        mySalonsRecycler = fragmentView.findViewById(R.id.my_rounds_recycler);
        emptyViewLayout = fragmentView.findViewById(R.id.my_rounds_empty_view);
        ivNotificationBell = fragmentView.findViewById(R.id.iv_notification_bell);
        noConnectionLayout = fragmentView.findViewById(R.id.no_connection);

        // translatable views
        tvMySalonsHeader = fragmentView.findViewById(R.id.tv_my_rounds_header);
        tvMySalonsEmptyHint = fragmentView.findViewById(R.id.tv_my_rounds_empty_hint);

        // load user image
        ImageLoader.getInstance().loadUserImage(MainActivity.mainInstance, ((ImageView) fragmentView.findViewById(R.id.iv_user_image)));
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getMyRoundsFragmentTranses(getContext());

        tvMySalonsHeader.setText(transHolder.joined_salons);
        tvMySalonsEmptyHint.setText(transHolder.rounds_empty_hint);
    }

    private void handleEvents() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkConnection();
            }
        });

        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), ivNotificationBell);

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
                startActivity(new Intent(MainActivity.mainInstance, AccountDetailsActivity.class));
            }
        });
    }

    private void checkConnection() {
        if (Common.Instance().isConnected(getActivity()))
        {
            noConnectionLayout.setVisibility(View.GONE);

            getUsrSalonsFromServer();

        }
        else
        {
            noConnectionLayout.setVisibility(View.VISIBLE);
            mySalonsProgress.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
        }
    }

    private void getUsrSalonsFromServer() {
        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(MainActivity.mainInstance,
                REQ_GET_SALON_BY_USER_ID, new Request<>(REQ_GET_SALON_BY_USER_ID, userId, apiToken
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
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void initMySalonsRecycler() {
        mySalonsProgress.setVisibility(View.GONE);

        if (salonsList.size() > 0)
        {
            tvMySalonsHeader.setText(MainActivity.mainInstance.getString(R.string.joined_salons));
            emptyViewLayout.setVisibility(View.GONE);
            mySalonsRecycler.setVisibility(View.VISIBLE);

            mySalonsRecycler.setHasFixedSize(true);
            mySalonsRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.mainInstance));
            mySalonsRecycler.setAdapter(new SalonsAdapter(getActivity(), salonsList));

        }
        else
        {
            tvMySalonsHeader.setText(MainActivity.mainInstance.getString(R.string.no_joined_salons));
            emptyViewLayout.setVisibility(View.VISIBLE);
            mySalonsRecycler.setVisibility(View.GONE);
        }
    }
}