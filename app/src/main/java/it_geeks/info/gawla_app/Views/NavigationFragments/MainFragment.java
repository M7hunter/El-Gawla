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

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.ViewModels.Adapters.RecentSalonsPagedAdapter;
import it_geeks.info.gawla_app.ViewModels.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.WinnerNews;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.SalonsViewModel;

public class MainFragment extends Fragment {

    private RecyclerView recentSalonsRecycler;
    private RecyclerView winnersNewsRecycler;
    private RecentSalonsPagedAdapter recentSalonsPagedAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;

    private ProgressBar recentSalonsProgress;
    private ProgressBar winnersNewsProgress;
    LinearLayout winnersHeader;

    SalonsViewModel salonsViewModel;

    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initViews(view);

        initSalonsRecycler(view);

        initWinnersRecycler(view);

        return view;
    }

    private void initViews(View view) {
        recentSalonsProgress = view.findViewById(R.id.recent_salons_progress);
        winnersNewsProgress = view.findViewById(R.id.winners_news_progress);
        winnersHeader = view.findViewById(R.id.winners_header);

        // TODO see all Hide Salons
        //  TextView seeAllRecentSalons = view.findViewById(R.id.recent_salons_see_all);
        //        seeAllRecentSalons.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                if (GawlaDataBse.getGawlaDatabase(getContext()).RoundDao().getRounds().size() > 0) {
        //                        GawlaDataBse.getGawlaDatabase(getContext()).RoundDao()
        //                                .removeRounds(GawlaDataBse.getGawlaDatabase(getContext()).RoundDao().getRounds());
        //                }
        //            }
        //        });

    }

    private void initSalonsRecycler(View view) {
        recentSalonsRecycler = view.findViewById(R.id.recent_hales_recycler);
        recentSalonsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));
        recentSalonsPagedAdapter = new RecentSalonsPagedAdapter(getContext());

        salonsViewModel = ViewModelProviders.of(this).get(SalonsViewModel.class);
        salonsViewModel.init();

        salonsViewModel.getRoundsList().observe(this, new Observer<PagedList<Round>>() {
            @Override
            public void onChanged(@Nullable PagedList<Round> rounds) {
                if (rounds != null) {
                    recentSalonsPagedAdapter.submitList(rounds);
                }
            }
        });

        recentSalonsRecycler.setAdapter(recentSalonsPagedAdapter);

        // to remove progress bar
        Common.Instance(getContext()).hideProgress(recentSalonsRecycler, recentSalonsProgress);
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
        Common.Instance(getContext()).hideProgress(winnersNewsRecycler, winnersNewsProgress);
    }
}
