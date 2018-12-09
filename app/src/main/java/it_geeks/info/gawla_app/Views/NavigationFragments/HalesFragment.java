package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.ViewModels.Adapters.RecentHalesPagedAdapter;
import it_geeks.info.gawla_app.ViewModels.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.WinnerNews;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.RoundViewModel;

public class HalesFragment extends Fragment {

    private RecyclerView recentHalesRecycler;
    private RecyclerView winnersNewsRecycler;
    private RecentHalesPagedAdapter recentHalesPagedAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;

    private ProgressBar recentHalesProgress;
    private ProgressBar winnersNewsProgress;
    LinearLayout winnersHeader;

    RoundViewModel roundViewModel;

    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hales, container, false);

        initViews(view);

        initHalesRecycler(view);

        initWinnersRecycler(view);

        return view;
    }

    private void initViews(View view) {
        recentHalesProgress = view.findViewById(R.id.recent_hales_progress);
        winnersNewsProgress = view.findViewById(R.id.winners_news_progress);
        TextView seeAllRecentHales = view.findViewById(R.id.recent_hales_see_all);
        winnersHeader = view.findViewById(R.id.winners_header);

        seeAllRecentHales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GawlaDataBse.getGawlaDatabase(getContext()).RoundDao().getRounds().size() > 0) {
                    for (Round round : GawlaDataBse.getGawlaDatabase(getContext()).RoundDao().getRounds()) {
                        GawlaDataBse.getGawlaDatabase(getContext()).RoundDao().removeRound(round);
                    }
                }
            }
        });
    }

    private void initHalesRecycler(View view) {
        recentHalesRecycler = view.findViewById(R.id.recent_hales_recycler);
        recentHalesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));
        recentHalesPagedAdapter = new RecentHalesPagedAdapter(getContext());

        roundViewModel = ViewModelProviders.of(this).get(RoundViewModel.class);
        roundViewModel.init();

        roundViewModel.getRoundsList().observe(this, new Observer<PagedList<Round>>() {
            @Override
            public void onChanged(@Nullable PagedList<Round> rounds) {
                if (rounds != null)
                    recentHalesPagedAdapter.submitList(rounds);
            }
        });

        recentHalesRecycler.setAdapter(recentHalesPagedAdapter);

        // to remove progress bar
        recentHalesRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recentHalesProgress.setVisibility(View.GONE);
                recentHalesRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initWinnersRecycler(View view) {
        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
        winnersNewsRecycler.setHasFixedSize(true);
        winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
        winnersNewsRecycler.setAdapter(winnersNewsAdapter);

        // no data ? hide header
        if (winnerNewsList == null || winnerNewsList.size() == 0) {
            winnersHeader.setVisibility(View.GONE);
        }

        // to remove progress bar
        winnersNewsRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                winnersNewsProgress.setVisibility(View.GONE);
                winnersNewsRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
