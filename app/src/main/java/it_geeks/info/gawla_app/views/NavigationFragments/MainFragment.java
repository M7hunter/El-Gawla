package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Controllers.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Data;
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
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.AllSalonsActivity;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.menuOptions.MoreAboutGawlaActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class MainFragment extends Fragment {

    private RecyclerView recentSalonsRecycler;
    private RecyclerView winnersNewsRecycler;
    private SalonsAdapter recentSalonsPagedAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;
    private LinearLayoutManager layoutManager;

    private List<Round> roundList = new ArrayList<>();
    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    private GawlaDataBse gawlaDataBse;

    private ProgressBar recentSalonsProgress;
    private ProgressBar winnersNewsProgress;
    private LinearLayout winnersHeader;

    private TextView btnRecentSalonsSeeAll,btnWinnersSeeAll; // <- trans & more
    private TextView recentSalonsLabel, winnersLabel, tvEmptyHint; // <- trans

    private int page = 1;
    private int last_page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        gawlaDataBse = GawlaDataBse.getGawlaDatabase(getContext());

        initViews(view);

        setupTrans();

        handleEvents(view);

        checkConnection(view);

        return view;
    }

    private void initViews(View view) {
        recentSalonsRecycler = view.findViewById(R.id.recent_salons_recycler);
        recentSalonsProgress = view.findViewById(R.id.recent_salons_progress);
        winnersNewsProgress = view.findViewById(R.id.winners_news_progress);
        winnersHeader = view.findViewById(R.id.winners_header);

        // translatable views
        btnRecentSalonsSeeAll = view.findViewById(R.id.recent_salons_see_all_btn);
        recentSalonsLabel = view.findViewById(R.id.recent_salons_header_label);
        btnWinnersSeeAll = view.findViewById(R.id.winners_news_see_all_btn);
        winnersLabel = view.findViewById(R.id.winners_news_header_label);
        tvEmptyHint = view.findViewById(R.id.recent_salons_empty_hint);

    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getMainFragmentTranses(getContext());

        btnWinnersSeeAll.setText(transHolder.see_all);
        btnRecentSalonsSeeAll.setText(transHolder.see_all);
        recentSalonsLabel.setText(transHolder.recent_salons);
        winnersLabel.setText(transHolder.winners_of_hales_news);
        tvEmptyHint.setText(transHolder.salons_empty_hint);
    }

    private void handleEvents(View view) {
        // open all salons page
        btnRecentSalonsSeeAll.setOnClickListener(new View.OnClickListener() {
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
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        initWinnersEmptyView();
    }

    private void checkConnection(View view) {
        LinearLayout noConnectionLayout = view.findViewById(R.id.no_connection);

        if (Common.Instance(getActivity()).isConnected()) {
            noConnectionLayout.setVisibility(View.GONE);

            getFirstSalonsFromServer(view);

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            recentSalonsProgress.setVisibility(View.GONE);
            winnersNewsProgress.setVisibility(View.GONE);
        }
    }

    private void getFirstSalonsFromServer(final View view) {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data("getAllSalons", page), new Request(SharedPrefManager.getInstance(getContext()).getUser().getUser_id(), SharedPrefManager.getInstance(getContext()).getUser().getApi_token()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        roundList.addAll(ParseResponses.parseRounds(mainObject));
                        initSalonsRecycler();

                        last_page = mainObject.get("last_page").getAsInt();

                        page = page + 1;
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

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

    private void getNextSalonsFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data("getAllSalons", page), new Request(SharedPrefManager.getInstance(getContext()).getUser().getUser_id(), SharedPrefManager.getInstance(getContext()).getUser().getApi_token()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = roundList.size();
                        roundList.addAll(ParseResponses.parseRounds(mainObject));
                        for (int i = nextFirstPosition; i < roundList.size(); i++) {
                            recentSalonsPagedAdapter.notifyItemInserted(i);
                        }

                        recentSalonsRecycler.smoothScrollToPosition(nextFirstPosition);

                        addScrollListener();
                        page = page + 1;
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

    private void initSalonsRecycler() {
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recentSalonsRecycler.setLayoutManager(layoutManager);
        recentSalonsPagedAdapter = new SalonsAdapter(getContext(), roundList);
        recentSalonsRecycler.setAdapter(recentSalonsPagedAdapter);

        Common.Instance(getContext()).hideProgress(recentSalonsRecycler, recentSalonsProgress);
        recentSalonsRecycler.scrollToPosition(recentSalonsPagedAdapter.getItemCount() - 11);

        addScrollListener();

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
    }

    private void addScrollListener() {
        recentSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (page <= last_page) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == recentSalonsPagedAdapter.getItemCount() - 1) {
                        getNextSalonsFromServer();
                        Toast.makeText(getContext(), "loading...", Toast.LENGTH_SHORT).show();

                        recentSalonsRecycler.removeOnScrollListener(this);
                    }
                } else {
                    recentSalonsRecycler.removeOnScrollListener(this);
                }
            }
        });
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

//    private void getWinnersFromServer(final View view) {
//
//    }
//
//    private void initWinnersRecycler(View view) {
//        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
//        winnersNewsRecycler.setHasFixedSize(true);
//        winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VISIBLE, false));
//        winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
//        winnersNewsRecycler.setAdapter(winnersNewsAdapter);
//
//        // to remove progress bar
//        Common.Instance(getContext()).hideProgress(winnersNewsRecycler, winnersNewsProgress);
//    }

    public void initWinnersEmptyView() {
        // no data ? hide header
        if (winnerNewsList == null || winnerNewsList.size() == 0) {
            winnersHeader.setVisibility(View.GONE);
            winnersNewsProgress.setVisibility(View.GONE);
//            winnersNewsRecycler.setVisibility(View.GONE);
        }
    }
}
