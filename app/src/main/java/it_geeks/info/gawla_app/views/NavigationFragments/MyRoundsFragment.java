package it_geeks.info.gawla_app.views.NavigationFragments;

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
import it_geeks.info.gawla_app.Controllers.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Models.Data;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRounds;

public class MyRoundsFragment extends Fragment {

    private List<Round> roundsList = new ArrayList<>();
    private RecyclerView myRoundsRecycler;
    private LinearLayout emptyViewLayout;
    private ProgressBar myRoundProgress;

    private ImageView imgNotification;

    private TextView tvMyRoundsHeader, tvMyRoundsEmptyHint; // <- trans
    private SalonsAdapter mySalonsAdapter;

    private int userId;
    private String apiToken;
    private boolean firstRequest = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rounds, container, false);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        initViews(view);

        setupTrans();

        handleEvents();

        checkConnection(view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!firstRequest)
            updateRoundsList();
    }

    private void updateRoundsList() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data("getSalonByUserID"), new Request(userId, apiToken), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        mySalonsAdapter.updateRoundsList(ParseResponses.parseRounds(mainObject));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                    }

                    @Override
                    public void handleEmptyResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews(View view) {
        myRoundProgress = view.findViewById(R.id.my_rounds_progress);
        myRoundsRecycler = view.findViewById(R.id.my_rounds_recycler);
        emptyViewLayout = view.findViewById(R.id.my_rounds_empty_view);

        //Notification icon
        imgNotification = view.findViewById(R.id.Notification);

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
        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), imgNotification);

        // notification onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void checkConnection(View view) {
        LinearLayout noConnectionLayout = view.findViewById(R.id.no_connection);

        if (Common.Instance(getActivity()).isConnected()) {
            noConnectionLayout.setVisibility(View.GONE);

            getUsrRoundsFromServer();

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            myRoundProgress.setVisibility(View.GONE);
        }
    }

    private void getUsrRoundsFromServer() {
        RetrofitClient.getInstance(getActivity()).executeConnectionToServer(MainActivity.mainInstance,
                "getSalonByUserID", new Request(userId, apiToken), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        roundsList.addAll(parseRounds(mainObject));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        initMyRoundsRecycler();
                        firstRequest = false;
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initMyRoundsRecycler();
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initMyRoundsRecycler() {
        myRoundProgress.setVisibility(View.GONE);

        if (roundsList.size() > 0) {
            emptyViewLayout.setVisibility(View.INVISIBLE);
            myRoundsRecycler.setVisibility(View.VISIBLE);

            myRoundsRecycler.setHasFixedSize(true);
            myRoundsRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.mainInstance, RecyclerView.VERTICAL, false));
            mySalonsAdapter = new SalonsAdapter(getActivity(), roundsList);
            myRoundsRecycler.setAdapter(mySalonsAdapter);

        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            myRoundsRecycler.setVisibility(View.INVISIBLE);
        }
    }
}