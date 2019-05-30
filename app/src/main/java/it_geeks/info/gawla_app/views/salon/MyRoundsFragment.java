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
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRounds;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_SALON_BY_USER_ID;

public class MyRoundsFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private List<Round> roundsList = new ArrayList<>();
    private RecyclerView myRoundsRecycler;
    private LinearLayout emptyViewLayout, noConnectionLayout;
    private ProgressBar myRoundProgress;

    private ImageView ivNotificationBell;

    private TextView tvMyRoundsHeader, tvMyRoundsEmptyHint; // <- trans

    private int userId;
    private String apiToken;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_rounds, container, false);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        initViews();

        setupTrans();

        handleEvents();

        checkConnection();

        return view;
    }

    private void initViews() {
        refreshLayout = view.findViewById(R.id.my_rounds_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        myRoundProgress = view.findViewById(R.id.pb_my_rounds);
        myRoundsRecycler = view.findViewById(R.id.my_rounds_recycler);
        emptyViewLayout = view.findViewById(R.id.my_rounds_empty_view);
        ivNotificationBell = view.findViewById(R.id.iv_notification_bell);
        noConnectionLayout = view.findViewById(R.id.no_connection);

        // translatable views
        tvMyRoundsHeader = view.findViewById(R.id.tv_my_rounds_header);
        tvMyRoundsEmptyHint = view.findViewById(R.id.tv_my_rounds_empty_hint);
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getMyRoundsFragmentTranses(getContext());

        tvMyRoundsHeader.setText(transHolder.joined_salons);
        tvMyRoundsEmptyHint.setText(transHolder.rounds_empty_hint);
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
    }

    private void checkConnection() {
        if (Common.Instance().isConnected(getActivity())) {
            noConnectionLayout.setVisibility(View.GONE);

            getUsrRoundsFromServer();

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            myRoundProgress.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
        }
    }

    private void getUsrRoundsFromServer() {
        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(MainActivity.mainInstance,
                REQ_GET_SALON_BY_USER_ID, new Request<>(REQ_GET_SALON_BY_USER_ID, userId, apiToken
                ,null,null,null,null,null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        roundsList.clear();
                        roundsList.addAll(parseRounds(mainObject));
                    }

                    @Override
                    public void handleAfterResponse() {
                        initMyRoundsRecycler();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initMyRoundsRecycler();
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void initMyRoundsRecycler() {
        myRoundProgress.setVisibility(View.GONE);

        if (roundsList.size() > 0) {
            tvMyRoundsHeader.setText(MainActivity.mainInstance.getString(R.string.joined_salons));
            emptyViewLayout.setVisibility(View.GONE);
            myRoundsRecycler.setVisibility(View.VISIBLE);

            myRoundsRecycler.setHasFixedSize(true);
            myRoundsRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.mainInstance, RecyclerView.VERTICAL, false));
            myRoundsRecycler.setAdapter(new SalonsAdapter(getActivity(), roundsList));

        } else {
            tvMyRoundsHeader.setText(MainActivity.mainInstance.getString(R.string.no_joined_salons));
            emptyViewLayout.setVisibility(View.VISIBLE);
            myRoundsRecycler.setVisibility(View.GONE);
        }
    }
}