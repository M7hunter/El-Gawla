package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.General.WrapContentHeightViewPager;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Controllers.Adapters.RoundsPagerAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Views.MainActivity;
import it_geeks.info.gawla_app.Views.NotificationActivity;

import static it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses.parseRounds;

public class MyRoundsFragment extends Fragment {

    private WrapContentHeightViewPager roundsViewPager;
    private List<Round> roundsList = new ArrayList<>();

    private ProgressBar myRoundProgress;

    private ImageView arrowRight, arrowLeft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rounds, container, false);

        initViews(view);

        checkConnection(view);

        return view;
    }

    private void initViews(View view) {
        myRoundProgress = view.findViewById(R.id.my_rounds_progress);
        roundsViewPager = view.findViewById(R.id.rounds_pager);

        // arrows
        arrowRight = view.findViewById(R.id.my_rounds_right_arrow);
        arrowLeft = view.findViewById(R.id.my_rounds_left_arrow);

        // open Notification
        view.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
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

            getData(view);

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            myRoundProgress.setVisibility(View.GONE);
            arrowLeft.setVisibility(View.GONE);
            arrowRight.setVisibility(View.GONE);
        }
    }

    private void getData(final View view) {
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getActivity()).executeConnectionToServer("getSalonByUserID", new Request(userId, apiToken), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {

                roundsList.addAll(parseRounds(mainObject, GawlaDataBse.getGawlaDatabase(getActivity())));

                initPager();

                handleEvents(roundsList.size());
            }

            @Override
            public void handleEmptyResponse() {

                initEmptyView(view);
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {

                initEmptyView(view);

                Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPager() {
        // pager
        roundsViewPager.setAdapter(new RoundsPagerAdapter(getActivity(), roundsList));
    }

    private void handleEvents(int cardsCount) {
        // at the beginning
        arrowLeft.setImageResource(R.drawable.ic_arrow_left_grey);

        if (cardsCount > 1) {
            arrowRight.setImageResource(R.drawable.ic_arrow_right);
        }

        // clicks
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundsViewPager.setCurrentItem(roundsViewPager.getCurrentItem() - 1, true);
            }
        });

        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundsViewPager.setCurrentItem(roundsViewPager.getCurrentItem() + 1, true);
            }
        });

        // set arrows ui & remove pager progress
        roundsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                myRoundProgress.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int i) {
                if (i == roundsViewPager.getAdapter().getCount() - 1) {
                    arrowRight.setImageResource(R.drawable.ic_arrow_right_grey);
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left);
                } else if (i == 0) {
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left_grey);
                    arrowRight.setImageResource(R.drawable.ic_arrow_right);
                } else {
                    arrowRight.setImageResource(R.drawable.ic_arrow_right);
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initEmptyView(View view) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.my_rounds_empty_view);

        myRoundProgress.setVisibility(View.GONE);

        if (roundsList.size() > 0) {
            emptyViewLayout.setVisibility(View.INVISIBLE);
            roundsViewPager.setVisibility(View.VISIBLE);
            arrowLeft.setVisibility(View.VISIBLE);
            arrowRight.setVisibility(View.VISIBLE);
        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            roundsViewPager.setVisibility(View.INVISIBLE);
            arrowLeft.setVisibility(View.INVISIBLE);
            arrowRight.setVisibility(View.INVISIBLE);
        }
    }
}