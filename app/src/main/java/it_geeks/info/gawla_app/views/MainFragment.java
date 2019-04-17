package it_geeks.info.gawla_app.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import it_geeks.info.gawla_app.Adapters.AdsAdapter;
import it_geeks.info.gawla_app.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.repository.Models.Ad;
import it_geeks.info.gawla_app.repository.Models.Data;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Models.WinnerNews;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.salon.AllSalonsActivity;

public class MainFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recentSalonsRecycler, winnersNewsRecycler;
    private ViewPager2 adsPager;
    private SalonsAdapter recentSalonsPagedAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;
    private LinearLayoutManager layoutManager;

    private List<Ad> adsList = new ArrayList<>();
    private List<Round> roundList = new ArrayList<>();
    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    private ProgressBar recentSalonsProgress, winnersNewsProgress;
    private LinearLayout winnersHeader, adsEmptyView, noConnectionLayout;

    private TextView btnRecentSalonsSeeAll, btnWinnersSeeAll; // <- trans & more
    private TextView recentSalonsLabel, winnersLabel, tvEmptyHint; // <- trans
    private ImageView imgNotification;

    private int page = 1, last_page = 1, userId, currentAd = 0;
    private String apiToken;

    private View view;
    private Timer timer;
    private Handler handler;
    private Runnable updateCurrentAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();

        initViews();

        setupTrans();

        handleEvents();

        getData();

        return view;
    }

    private void initViews() {
        refreshLayout = view.findViewById(R.id.main_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        recentSalonsRecycler = view.findViewById(R.id.recent_salons_recycler);
        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
        adsPager = view.findViewById(R.id.ads_viewpager);
        recentSalonsProgress = view.findViewById(R.id.recent_salons_progress);
        winnersNewsProgress = view.findViewById(R.id.winners_news_progress);
        winnersHeader = view.findViewById(R.id.winners_header);
        adsEmptyView = view.findViewById(R.id.ads_empty_view);
        noConnectionLayout = view.findViewById(R.id.no_connection);

        //Notification icon
        imgNotification = view.findViewById(R.id.notification_bell);

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
        winnersLabel.setText(transHolder.salons_winners);
        tvEmptyHint.setText(transHolder.salons_empty_hint);
    }

    private void handleEvents() {
        // refresh page
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        // open all salons page
        btnRecentSalonsSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllSalonsActivity.class));
            }
        });

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

    private void getData() {
        if (Common.Instance().isConnected(getContext())) {
            noConnectionLayout.setVisibility(View.GONE);

            getAdsFromServer();

            getFirstSalonsFromServer();

            getWinnersFromServer();

        } else {
            setOnNoConnection();
        }
    }

    private void setOnNoConnection() {
        noConnectionLayout.setVisibility(View.VISIBLE);
        recentSalonsRecycler.setVisibility(View.GONE);
        recentSalonsProgress.setVisibility(View.GONE);
        winnersNewsProgress.setVisibility(View.GONE);
        adsPager.setVisibility(View.GONE);
        adsEmptyView.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }

    private void getAdsFromServer() {
        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(), "getAllSliders", new Request(userId, apiToken), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                adsList.clear();
                adsList.addAll(ParseResponses.parseAds(mainObject));
            }

            @Override
            public void handleAfterResponse() {
                initAdsRecycler();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                initAdsRecycler();
            }
        });
    }

    private void initAdsRecycler() {
        if (adsList.size() > 0) {
            adsEmptyView.setVisibility(View.GONE);
            adsPager.setVisibility(View.VISIBLE);
            adsPager.setAdapter(new AdsAdapter(getContext(), adsList));

            autoSlideAds();

            adsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    currentAd = position;
                }
            });

        } else {
            adsPager.setVisibility(View.GONE);
            adsEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void autoSlideAds() {
        handler = new Handler();
        updateCurrentAd = new Runnable() {
            public void run() {
                if (currentAd == adsList.size()) {
                    currentAd = 0;
                }
                adsPager.setCurrentItem(currentAd++, true);
            }
        };

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(updateCurrentAd);
            }
        }, 0, 2500);
    }

    private void getFirstSalonsFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data("getAllSalons", 1), new Request(userId, apiToken, true), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        roundList.clear();
                        roundList.addAll(ParseResponses.parseRounds(mainObject));
                        initSalonsRecycler();

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsEmptyView(roundList);
                        recentSalonsProgress.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsEmptyView(roundList);
                        recentSalonsProgress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getNextSalonsFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data("getAllSalons", ++page), new Request(userId, apiToken, true), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = roundList.size();
                        roundList.addAll(ParseResponses.parseRounds(mainObject));
                        for (int i = nextFirstPosition; i < roundList.size(); i++) {
                            recentSalonsPagedAdapter.notifyItemInserted(i);
                        }

                        recentSalonsRecycler.smoothScrollToPosition(nextFirstPosition);

                        if (page < last_page)
                            addScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initSalonsRecycler() {
        if (recentSalonsRecycler.getVisibility() == View.GONE) {
            recentSalonsRecycler.setVisibility(View.VISIBLE);
        }
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
            recentSalonsRecycler.setLayoutManager(layoutManager);
        }

        recentSalonsRecycler.setHasFixedSize(true);
        recentSalonsPagedAdapter = new SalonsAdapter(getContext(), roundList);
        recentSalonsRecycler.setAdapter(recentSalonsPagedAdapter);

        Common.Instance().hideProgress(recentSalonsRecycler, recentSalonsProgress);

        if (page < last_page) {
            addScrollListener();
        }
    }

    private void addScrollListener() {
        recentSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (layoutManager.findLastCompletelyVisibleItemPosition() == recentSalonsPagedAdapter.getItemCount() - 1) {
                    getNextSalonsFromServer();
                    Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();

                    recentSalonsRecycler.removeOnScrollListener(this);
                }
            }
        });
    }

    private void initSalonsEmptyView(List<Round> roundList) {
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

    private void getWinnersFromServer() {
        winnersHeader.setVisibility(View.GONE);
        winnersNewsProgress.setVisibility(View.GONE);
        winnersNewsRecycler.setVisibility(View.GONE);
//        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(), "getAllBlogs", new Request(userId, apiToken), new HandleResponses() {
//            @Override
//            public void handleTrueResponse(JsonObject mainObject) {
//                winnerNewsList = ParseResponses.parseWinners(mainObject);
//            }
//
//            @Override
//            public void handleAfterResponse() {
//                initWinnersRecycler();
//            }
//
//            @Override
//            public void handleConnectionErrors(String errorMessage) {
//                initWinnersRecycler();
//            }
//        });
    }

    private void initWinnersRecycler() {
        if (winnerNewsList.size() > 0) {
            if (!winnersNewsRecycler.hasFixedSize())
                winnersNewsRecycler.setHasFixedSize(true);
            if (winnersNewsRecycler.getLayoutManager() == null)
                winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            if (winnersNewsRecycler.getAdapter() == null) {
                winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
                winnersNewsRecycler.setAdapter(winnersNewsAdapter);
            }

            // to remove progress bar
            if (winnersNewsProgress.getVisibility() == View.VISIBLE)
                Common.Instance().hideProgress(winnersNewsRecycler, winnersNewsProgress);
        } else {
            winnersHeader.setVisibility(View.GONE);
            winnersNewsProgress.setVisibility(View.GONE);
            winnersNewsRecycler.setVisibility(View.GONE);
        }
    }
}
