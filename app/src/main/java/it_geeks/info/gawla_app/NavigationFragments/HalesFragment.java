package it_geeks.info.gawla_app.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.RecentHalesAdapter;
import it_geeks.info.gawla_app.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.Models.WinnerNews;
import it_geeks.info.gawla_app.R;

public class HalesFragment extends Fragment {

    private RecyclerView recentHalesRecycler;
    private RecyclerView winnersNewsRecycler;
    private RecentHalesAdapter recentHalesAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;

    private List<Round> roundsList = new ArrayList<>();
    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hales, container, false);

        getData();

        initHalesRecycler(view);

        initWinnersRecycler(view);

        return view;
    }

    private void getData() {
        // fake data
        for (int i = 0; i < 5; i++) {
            Round round  = new Round("samsung A" + i + " 201" + i
                    , "image" + i
                    , "Electronics"
                    , i + "0000 L.E"
                    , "this should be description"
                    , "14 hr 40 min"
                    , "8 hr 40 min"
                    , "2" + i + " member joined");

            roundsList.add(round);

            WinnerNews winnerNews = new WinnerNews("title " + i
                    , "body " + i
                    , "not yet");

            winnerNewsList.add(winnerNews);
        }
    }

    private void initHalesRecycler(View view) {
        recentHalesRecycler = view.findViewById(R.id.recent_hales_recycler);
        recentHalesRecycler.setHasFixedSize(true);
        recentHalesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 0,false));
        recentHalesAdapter = new RecentHalesAdapter(getActivity(), roundsList);
        recentHalesRecycler.setAdapter(recentHalesAdapter);
    }

    private void initWinnersRecycler(View view) {
        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
        winnersNewsRecycler.setHasFixedSize(true);
        winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1,false));
        winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
        winnersNewsRecycler.setAdapter(winnersNewsAdapter);
    }
}
