package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Controllers.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Controllers.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.WinnerNews;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.AllSalonsActivity;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.menuOptions.MoreAboutGawlaActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class MainFragment extends Fragment {

    private RecyclerView recentSalonsRecycler;
    private RecyclerView winnersNewsRecycler;
    private SalonsAdapter recentSalonsPagedAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;

    private List<Round> roundList = new ArrayList<>();
    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    private GawlaDataBse gawlaDataBse;

    private ProgressBar recentSalonsProgress;
    private ProgressBar winnersNewsProgress;
    private LinearLayout winnersHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        gawlaDataBse = GawlaDataBse.getGawlaDatabase(getContext());

        initViews(view);

        checkConnection(view);

        return view;
    }

    private void initViews(View view) {
        recentSalonsRecycler = view.findViewById(R.id.recent_salons_recycler);
        recentSalonsProgress = view.findViewById(R.id.recent_salons_progress);
        winnersNewsProgress = view.findViewById(R.id.winners_news_progress);
        winnersHeader = view.findViewById(R.id.winners_header);

        // open all salons page
        view.findViewById(R.id.recent_salons_see_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllSalonsActivity.class));
            }
        });

        // more about gawla page
        view.findViewById(R.id.salons_top_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MoreAboutGawlaActivity.class));
            }
        });

        view.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),NotificationActivity.class));
            }
        });

        initWinnersEmptyView();
    }

    private void checkConnection(View view) {
        LinearLayout noConnectionLayout = view.findViewById(R.id.no_connection);

        if (Common.Instance(getActivity()).isConnected()) {
            noConnectionLayout.setVisibility(View.GONE);

            getSalonsFromServer(view);

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            recentSalonsProgress.setVisibility(View.GONE);
            winnersNewsProgress.setVisibility(View.GONE);
        }
    }

    private void getSalonsFromServer(final View view) {
        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(),
                "getAllSalons", new Request(SharedPrefManager.getInstance(getContext()).getUser().getUser_id(), SharedPrefManager.getInstance(getContext()).getUser().getApi_token()), new HandleResponses() {
                    @Override
                    public void handleResponseData(JsonObject mainObject) {

                        insertItemsIntoDatabase(mainObject);
                        roundList = ParseResponses.parseRounds(mainObject, gawlaDataBse);
                        initSalonsRecycler(view);
                    }

                    @Override
                    public void handleEmptyResponse() {
                        initSalonsEmptyView(view, roundList);
                        recentSalonsProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsEmptyView(view, roundList);
                        recentSalonsProgress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void insertItemsIntoDatabase(JsonObject mainObj) {
        gawlaDataBse.roundDao().removeRounds(gawlaDataBse.roundDao().getRounds());
        gawlaDataBse.roundDao().insertRoundList(ParseResponses.parseRounds(mainObj, gawlaDataBse));
    }

    private void initSalonsRecycler(final View view) {
        recentSalonsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        recentSalonsPagedAdapter = new SalonsAdapter(getContext(), roundList);

//        recentSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                recentSalonsProgress.setVisibility(View.GONE);
//            }
//        });



//        SalonsViewModel salonsViewModel = ViewModelProviders.of(this).get(SalonsViewModel.class);
//        salonsViewModel.init();
//
//        salonsViewModel.getRoundsList().observe(this, new Observer<PagedList<Round>>() {
//            @Override
//            public void onChanged(@Nullable PagedList<Round> rounds) {
//                recentSalonsProgress.setVisibility(View.GONE);
//                if (rounds != null) {
//                    recentSalonsPagedAdapter.submitList(rounds);
//
//                    initSalonsEmptyView(view, rounds);
//                }
//            }
//        });

        recentSalonsRecycler.setAdapter(recentSalonsPagedAdapter);
        Common.Instance(getContext()).hideProgress(recentSalonsRecycler, recentSalonsProgress);
    }

    private void initSalonsEmptyView(View view, List<Round> roundList) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.recent_salons_empty_view);

        recentSalonsProgress.setVisibility(View.GONE);

        if (roundList.size() > 0) {
            emptyViewLayout.setVisibility(View.GONE);
            recentSalonsRecycler.setVisibility(View.VISIBLE);

        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            recentSalonsRecycler.setVisibility(View.INVISIBLE);
        }
    }

    private void getWinnersFromServer(final View view) {

    }

    private void initWinnersRecycler(View view) {
        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
        winnersNewsRecycler.setHasFixedSize(true);
        winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VISIBLE, false));
        winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
        winnersNewsRecycler.setAdapter(winnersNewsAdapter);

        // to remove progress bar
        Common.Instance(getContext()).hideProgress(winnersNewsRecycler, winnersNewsProgress);
    }

    public void initWinnersEmptyView() {
        // no data ? hide header
        if (winnerNewsList == null || winnerNewsList.size() == 0) {
            winnersHeader.setVisibility(View.GONE);
            winnersNewsProgress.setVisibility(View.GONE);
            winnersNewsRecycler.setVisibility(View.GONE);
        }
    }
}
