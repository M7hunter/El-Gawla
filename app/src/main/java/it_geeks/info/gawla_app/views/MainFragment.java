package it_geeks.info.gawla_app.views;

import android.content.Intent;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import it_geeks.info.gawla_app.Adapters.AdsAdapter;
import it_geeks.info.gawla_app.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.repository.Models.Ad;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.Models.Data;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.repository.Models.WinnerNews;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.util.Constants;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.views.account.ProfileActivity;
import it_geeks.info.gawla_app.views.salon.AllSalonsActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_SLIDERS;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_SALONS;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_BLOGS;

public class MainFragment extends Fragment {

    // region fields
    private View fragmentView;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recentSalonsRecycler, winnersNewsRecycler;
    private ViewPager2 adsPager;
    private ProgressBar recentSalonsProgress, winnersNewsProgress, pbAds;
    private LinearLayout emptyViewLayout, winnersHeader, adsEmptyView, noConnectionLayout;
    private TextView btnRecentSalonsSeeAll;
    private ImageView imgNotification;

    private SalonsAdapter recentSalonsPagedAdapter;
    private LinearLayoutManager layoutManager;

    private List<Ad> adsList = new ArrayList<>();
    private List<Round> salonList = new ArrayList<>();
    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    private int page = 1, last_page = 1, userId, currentAd = 0;
    private String apiToken;

    private Timer timer;
    private Handler handler;
    private Runnable updateCurrentAd;

    private SnackBuilder snackBuilder;

    private Category catVehicles, catElectronics, catRealState, catJewellery;
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();

        initViews();

        handleEvents();

        getDataFromServer();

        return fragmentView;
    }

    private void initViews() {
        refreshLayout = fragmentView.findViewById(R.id.main_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        recentSalonsRecycler = fragmentView.findViewById(R.id.recent_salons_recycler);
        winnersNewsRecycler = fragmentView.findViewById(R.id.winners_news_recycler);
        adsPager = fragmentView.findViewById(R.id.ads_viewpager);
        recentSalonsProgress = fragmentView.findViewById(R.id.recent_salons_progress);
        winnersNewsProgress = fragmentView.findViewById(R.id.winners_news_progress);
        pbAds = fragmentView.findViewById(R.id.pb_ads);
        winnersHeader = fragmentView.findViewById(R.id.winners_header);
        emptyViewLayout = fragmentView.findViewById(R.id.recent_salons_empty_view);
        adsEmptyView = fragmentView.findViewById(R.id.ads_empty_view);
        noConnectionLayout = fragmentView.findViewById(R.id.no_connection);

        //Notification icon
        imgNotification = fragmentView.findViewById(R.id.iv_notification_bell);
        View bellIndicator = fragmentView.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationStatus.notificationStatus(getContext(), bellIndicator);

        // load user image
        ImageLoader.getInstance().loadUserImage(MainActivity.mainInstance, ((ImageView) fragmentView.findViewById(R.id.iv_user_image)));

        btnRecentSalonsSeeAll = fragmentView.findViewById(R.id.recent_salons_see_all_btn);

        snackBuilder = new SnackBuilder(fragmentView.findViewById(R.id.main_snack_view));
    }

    private void handleEvents() {
        // refresh page
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });

        // open all salons page
        btnRecentSalonsSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AllSalonsActivity.class);
                i.putExtra(Constants.CATEGORY_KEY, Constants.NULL_INT_VALUE);
                startActivity(i);
            }
        });

        // notification onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        fragmentView.findViewById(R.id.iv_user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.mainInstance, ProfileActivity.class));
            }
        });
    }

    private void getDataFromServer() {
        if (Common.Instance().isConnected(getContext()))
        {
            noConnectionLayout.setVisibility(View.GONE);

            getAdsAndCatsFromServer();

            getSalonsFirstPageFromServer();

            getWinnersFromServer();
        }
        else
        {
            onNoConnection();
        }
    }

    private void onNoConnection() {
        noConnectionLayout.setVisibility(View.VISIBLE);
        recentSalonsRecycler.setVisibility(View.GONE);
        recentSalonsProgress.setVisibility(View.GONE);
        winnersNewsProgress.setVisibility(View.GONE);
        adsPager.setVisibility(View.GONE);
        adsEmptyView.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }

    private void getAdsAndCatsFromServer() {
        pbAds.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(),
                REQ_GET_ALL_SLIDERS, new Request<>(REQ_GET_ALL_SLIDERS, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        adsList.clear();
                        adsList.addAll(ParseResponses.parseAds(mainObject));

                        // parse cats
                        catVehicles = ParseResponses.parseHomeCategories(mainObject, Constants.CAT_ENGINES);
                        catElectronics = ParseResponses.parseHomeCategories(mainObject, Constants.CAT_MATERIALS);
                        catRealState = ParseResponses.parseHomeCategories(mainObject, Constants.CAT_REAL_STATE);
                        catJewellery = ParseResponses.parseHomeCategories(mainObject, Constants.CAT_JEWELLERY);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initAdsRecycler();
                        initCategories();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initAdsRecycler();
                    }
                });
    }

    private void initAdsRecycler() {
        pbAds.setVisibility(View.GONE);
        if (adsList.size() > 0)
        {
            adsEmptyView.setVisibility(View.GONE);
            adsPager.setVisibility(View.VISIBLE);
            adsPager.setAdapter(new AdsAdapter(getContext(), adsList, snackBuilder));

            autoSlideAds();

            adsPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    currentAd = position;
                }
            });

        }
        else
        {
            adsPager.setVisibility(View.GONE);
            adsEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void autoSlideAds() {
        handler = new Handler();
        updateCurrentAd = new Runnable() {
            public void run() {
                if (currentAd == adsList.size())
                {
                    currentAd = 0;
                }
                adsPager.setCurrentItem(currentAd++, true);
            }
        };

        if (timer != null)
        {
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

    private void initCategories() {
        fragmentView.findViewById(R.id.cats_layout).setVisibility(View.VISIBLE);
        // init views
        CardView cvCatVehicles = fragmentView.findViewById(R.id.cv_cat_vehicles),
                cvCatElectronics = fragmentView.findViewById(R.id.cv_cat_electronics),
                cvCatRealState = fragmentView.findViewById(R.id.cv_cat_real),
                cvCatJewellery = fragmentView.findViewById(R.id.cv_cat_jewellery);

        View layoutCatVehicles = fragmentView.findViewById(R.id.layout_vehicles),
                layoutCatElectronics = fragmentView.findViewById(R.id.layout_electronics),
                layoutCatRealState = fragmentView.findViewById(R.id.layout_real),
                layoutCatJewellery = fragmentView.findViewById(R.id.layout_jewellery);

        ImageView ivCatVehicles = fragmentView.findViewById(R.id.iv_cat_icon_vehicles),
                ivCatElectronics = fragmentView.findViewById(R.id.iv_cat_icon_electronics),
                ivCatRealState = fragmentView.findViewById(R.id.iv_cat_icon_real),
                ivCatJewellery = fragmentView.findViewById(R.id.iv_cat_icon_jewellery);

        TextView tvCatVehicles = fragmentView.findViewById(R.id.tv_cat_label_vehicles),
                tvCatElectronics = fragmentView.findViewById(R.id.tv_cat_label_electronics),
                tvCatRealState = fragmentView.findViewById(R.id.tv_cat_label_real),
                tvCatJewellery = fragmentView.findViewById(R.id.tv_cat_label_jewellery);

        // bind cats data
        if (catVehicles != null)
        {
            // background color
            layoutCatVehicles.setBackgroundColor(Color.parseColor(catVehicles.getCategoryColor()));
            // icon
            ImageLoader.getInstance().loadIcon(catVehicles.getCategoryImage(), ivCatVehicles);
            // title
            tvCatVehicles.setText(catVehicles.getCategoryName());
            // clicks
            cvCatVehicles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFilterByCat(catVehicles.getCategoryId(), catVehicles.getCategoryName());
                }
            });
        }
        else
        {
            cvCatVehicles.setVisibility(View.GONE);
        }

        if (catElectronics != null)
        {
            // background color
            layoutCatElectronics.setBackgroundColor(Color.parseColor(catElectronics.getCategoryColor()));
            // icon
            ImageLoader.getInstance().loadIcon(catElectronics.getCategoryImage(), ivCatElectronics);
            // title
            tvCatElectronics.setText(catElectronics.getCategoryName());
            // clicks
            cvCatElectronics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFilterByCat(catElectronics.getCategoryId(), catElectronics.getCategoryName());
                }
            });
        }
        else
        {
            cvCatElectronics.setVisibility(View.GONE);
        }

        if (catRealState != null)
        {
            // background color
            layoutCatRealState.setBackgroundColor(Color.parseColor(catRealState.getCategoryColor()));
            // icon
            ImageLoader.getInstance().loadIcon(catRealState.getCategoryImage(), ivCatRealState);
            // title
            tvCatRealState.setText(catRealState.getCategoryName());
            // clicks
            cvCatRealState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFilterByCat(catRealState.getCategoryId(), catRealState.getCategoryName());
                }
            });
        }
        else
        {
            cvCatRealState.setVisibility(View.GONE);
        }

        if (catJewellery != null)
        {
            // background color
            layoutCatJewellery.setBackgroundColor(Color.parseColor(catJewellery.getCategoryColor()));
            // icon
            ImageLoader.getInstance().loadIcon(catJewellery.getCategoryImage(), ivCatJewellery);
            // title
            tvCatJewellery.setText(catJewellery.getCategoryName());
            // clicks
            cvCatJewellery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFilterByCat(catJewellery.getCategoryId(), catJewellery.getCategoryName());
                }
            });
        }
        else
        {
            cvCatJewellery.setVisibility(View.GONE);
        }
    }

    private void openFilterByCat(int catId, String categoryName) {
        Intent i = new Intent(MainActivity.mainInstance, AllSalonsActivity.class);
        i.putExtra(Constants.CATEGORY_KEY, catId);
        i.putExtra(Constants.CATEGORY_NAME, categoryName);
        startActivity(i);
    }

    private void getSalonsFirstPageFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_SALONS, 1), new Request<>(REQ_GET_ALL_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonList.clear();
                        salonList.addAll(ParseResponses.parseRounds(mainObject));
                        initSalonsRecycler();

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsEmptyView(salonList);
                        recentSalonsProgress.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsEmptyView(salonList);
                        recentSalonsProgress.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getSalonsNextPageFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_SALONS, ++page), new Request<>(REQ_GET_ALL_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = salonList.size();
                        salonList.addAll(ParseResponses.parseRounds(mainObject));
                        for (int i = nextFirstPosition; i < salonList.size(); i++)
                        {
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
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
                    }
                });
    }

    private void initSalonsRecycler() {
        if (recentSalonsRecycler.getVisibility() == View.GONE)
        {
            recentSalonsRecycler.setVisibility(View.VISIBLE);
        }
        if (layoutManager == null)
        {
            layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
            recentSalonsRecycler.setLayoutManager(layoutManager);
        }

        recentSalonsRecycler.setHasFixedSize(true);
        recentSalonsPagedAdapter = new SalonsAdapter(getContext(), salonList);
        recentSalonsRecycler.setAdapter(recentSalonsPagedAdapter);

        Common.Instance().hideProgress(recentSalonsRecycler, recentSalonsProgress);

        if (page < last_page)
        {
            addScrollListener();
        }
    }

    private void addScrollListener() {
        recentSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (layoutManager.findLastCompletelyVisibleItemPosition() == recentSalonsPagedAdapter.getItemCount() - 1)
                {
                    getSalonsNextPageFromServer();
                    Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();

                    recentSalonsRecycler.removeOnScrollListener(this);
                }
            }
        });
    }

    private void initSalonsEmptyView(List<Round> roundList) {
        recentSalonsProgress.setVisibility(View.GONE);

        if (roundList.size() > 0)
        {
            emptyViewLayout.setVisibility(View.GONE);
            recentSalonsRecycler.setVisibility(View.VISIBLE);

        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            recentSalonsRecycler.setVisibility(View.INVISIBLE);
        }
    }

    private void getWinnersFromServer() {
        winnersHeader.setVisibility(View.GONE);
        winnersNewsProgress.setVisibility(View.GONE);
        winnersNewsRecycler.setVisibility(View.GONE);
        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(),
                REQ_GET_ALL_BLOGS, new Request<>(REQ_GET_ALL_BLOGS, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        winnerNewsList = ParseResponses.parseWinners(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initWinnersRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initWinnersRecycler();
                    }
                });
    }

    private void initWinnersRecycler() {
        if (winnerNewsList.size() > 0)
        {
            winnersNewsRecycler.setVisibility(View.VISIBLE);
            winnersNewsRecycler.setHasFixedSize(true);
            if (winnersNewsRecycler.getAdapter() == null)
            {
                winnersNewsRecycler.setAdapter(new WinnersNewsAdapter(winnerNewsList));
            }

            // to remove progress bar
            if (winnersNewsProgress.getVisibility() == View.VISIBLE)
                Common.Instance().hideProgress(winnersNewsRecycler, winnersNewsProgress);
        }
    }
}
