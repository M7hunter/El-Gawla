package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Controllers.Adapters.RecentSalonsPagedAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.WinnerNews;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.ViewModels.SalonsViewModel;
import it_geeks.info.gawla_app.Views.AllSalonsActivity;
import it_geeks.info.gawla_app.Views.MenuOptions.MoreAboutGawlaActivity;
import it_geeks.info.gawla_app.Views.NotificationActivity;

public class MainFragment extends Fragment {

    private RecyclerView recentSalonsRecycler;
    private RecyclerView winnersNewsRecycler;
    private RecentSalonsPagedAdapter recentSalonsPagedAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;

    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    private ProgressBar recentSalonsProgress;
    private ProgressBar winnersNewsProgress;
    private LinearLayout winnersHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initViews(view);

        checkConnection(view);

        return view;
    }

    private void initViews(View view) {
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

            initSalonsRecycler(view);

            initWinnersRecycler(view);

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            recentSalonsProgress.setVisibility(View.GONE);
            winnersNewsProgress.setVisibility(View.GONE);
        }
    }

    private void initSalonsRecycler(final View view) {
        recentSalonsRecycler = view.findViewById(R.id.recent_salons_recycler);
        recentSalonsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));
        recentSalonsPagedAdapter = new RecentSalonsPagedAdapter(getContext());

        recentSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recentSalonsProgress.setVisibility(View.GONE);
            }
        });

        SalonsViewModel salonsViewModel = ViewModelProviders.of(this).get(SalonsViewModel.class);
        salonsViewModel.init();

        salonsViewModel.getRoundsList().observe(this, new Observer<PagedList<Round>>() {
            @Override
            public void onChanged(@Nullable PagedList<Round> rounds) {
                recentSalonsProgress.setVisibility(View.GONE);
                if (rounds != null) {
                    recentSalonsPagedAdapter.submitList(rounds);

                    initSalonsEmptyView(view, rounds);
                }
            }
        });

        recentSalonsRecycler.setAdapter(recentSalonsPagedAdapter);
    }

    private void initWinnersRecycler(View view) {
        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
        winnersNewsRecycler.setHasFixedSize(true);
        winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
        winnersNewsRecycler.setAdapter(winnersNewsAdapter);

        // to remove progress bar
        Common.Instance(getContext()).hideProgress(winnersNewsRecycler, winnersNewsProgress);
    }

    private void initSalonsEmptyView(View view, List<Round> roundList) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.recent_salons_empty_view);

        if (roundList.size() > 0) {
            emptyViewLayout.setVisibility(View.GONE);
            recentSalonsRecycler.setVisibility(View.VISIBLE);

        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            recentSalonsRecycler.setVisibility(View.INVISIBLE);
        }
    }

    public void initWinnersEmptyView() {
        // no data ? hide header
        if (winnerNewsList == null || winnerNewsList.size() == 0) {
            winnersHeader.setVisibility(View.GONE);
        }
    }
}
