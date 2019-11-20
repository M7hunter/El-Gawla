package it_geeks.info.elgawla.views.main;

import android.content.Context;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import it_geeks.info.elgawla.Adapters.AdsAdapter;
import it_geeks.info.elgawla.Adapters.CategoryAdapter;
import it_geeks.info.elgawla.Adapters.SalonsAdapter;
import it_geeks.info.elgawla.repository.Models.Ad;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.Constants;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.TourManager;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.account.ProfileActivity;
import it_geeks.info.elgawla.views.salon.AllSalonsActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_FINISHED_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_SLIDERS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_SALONS;

public class MainFragment extends Fragment {

    // region fields
    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recentSalonsRecycler, finishedSalonsRecycler, rvCats;
    private ViewPager2 adsPager;
    private ProgressBar recentSalonsProgress, finishedSalonsProgress, pbAds;
    private LinearLayout emptyViewLayout, finishedEmptyViewLayout, adsEmptyView;
    private TextView btnSeeMoreSalons, btnSeeMoreFinishedSalons, noConnectionLayout;
    private ImageView imgNotification, ivUserImage;

    private SalonsAdapter recentSalonsPagedAdapter, finishedSalonsPagedAdapter;
    private LinearLayoutManager layoutManager, finishedLayoutManager;

    private List<Ad> adsList = new ArrayList<>();
    private List<Salon> salonList = new ArrayList<>(), finishedSalonList = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    private int page = 1, page_finished = 1, last_page = 1, last_page_finished = 1, userId, currentAd = 0;
    private String apiToken;

    private Timer timer;
    private Handler handler;
    private Runnable updateCurrentAd;

    private SnackBuilder snackBuilder;
    // endregion

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        apiToken = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);

        initViews(fragmentView);

        handleEvents();

        getDataFromServer();

        TourManager.mainPageSequence(getActivity(), imgNotification, ivUserImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        SalonsAdapter.clickable = true;
    }

    private void initViews(View fragmentView) {
        refreshLayout = fragmentView.findViewById(R.id.main_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.paleRed, R.color.colorYellow, R.color.niceBlue, R.color.azure);
        recentSalonsRecycler = fragmentView.findViewById(R.id.recent_salons_recycler);
        finishedSalonsRecycler = fragmentView.findViewById(R.id.finished_salons_recycler);
        adsPager = fragmentView.findViewById(R.id.ads_viewpager);
        recentSalonsProgress = fragmentView.findViewById(R.id.recent_salons_progress);
        finishedSalonsProgress = fragmentView.findViewById(R.id.finished_salons_progress);
        pbAds = fragmentView.findViewById(R.id.pb_ads);
        emptyViewLayout = fragmentView.findViewById(R.id.recent_salons_empty_view);
        finishedEmptyViewLayout = fragmentView.findViewById(R.id.finished_salons_empty_view);
        adsEmptyView = fragmentView.findViewById(R.id.ads_empty_view);
        noConnectionLayout = fragmentView.findViewById(R.id.no_connection);
        rvCats = fragmentView.findViewById(R.id.rv_home_cats);
        ivUserImage = fragmentView.findViewById(R.id.iv_user_image);

        //Notification icon
        imgNotification = fragmentView.findViewById(R.id.iv_notification_bell);
        View bellIndicator = fragmentView.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(context, bellIndicator);

        // load user image
        ImageLoader.getInstance().loadUserImage(context, ivUserImage);

        btnSeeMoreSalons = fragmentView.findViewById(R.id.recent_salons_see_all_btn);
//        btnSeeMoreFinishedSalons = fragmentView.findViewById(R.id.finished_salons_see_all_btn);

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
        btnSeeMoreSalons.setOnClickListener(new View.OnClickListener() {
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

        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });
    }

    private void getDataFromServer() {
        if (Common.Instance().isConnected(context))
        {
            noConnectionLayout.setVisibility(View.GONE);

            getAdsAndCatsFromServer();

            getSalonsFirstPageFromServer();

            getFinishedSalonsFirstPageFromServer();
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
        finishedSalonsRecycler.setVisibility(View.GONE);
        finishedSalonsProgress.setVisibility(View.GONE);
        adsPager.setVisibility(View.GONE);
        adsEmptyView.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
    }

    private void getAdsAndCatsFromServer() {
        pbAds.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(getContext()).executeConnectionToServer(getContext(),
                REQ_GET_ALL_SLIDERS, new RequestModel<>(REQ_GET_ALL_SLIDERS, userId, apiToken
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        adsList.clear();
                        adsList.addAll(ParseResponses.parseAds(mainObject));

                        // parse cats
                        if (categories.isEmpty())
                            categories = ParseResponses.parseHomeCategories(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initAdsRecycler();
                        initCatsRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initAdsRecycler();
                    }
                });
    }

    private void initCatsRecycler() {
        if (categories.size() > 0)
        {
            rvCats.setHasFixedSize(true);

            if (rvCats.getAdapter() == null)
                rvCats.setAdapter(new CategoryAdapter(categories, context, new ClickInterface.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Category cat = categories.get(position);
                        openFilterByCat(cat.getCategoryId(), cat.getCategoryName());
                    }
                }));
        }
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

    private void openFilterByCat(int catId, String categoryName) {
        Intent i = new Intent(context, AllSalonsActivity.class);
        i.putExtra(Constants.CATEGORY_KEY, catId);
        i.putExtra(Constants.CATEGORY_NAME, categoryName);
        startActivity(i);
    }

    private void getSalonsFirstPageFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_SALONS, 1), new RequestModel<>(REQ_GET_ALL_SALONS, userId, apiToken, true
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
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getSalonsNextPageFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_SALONS, ++page), new RequestModel<>(REQ_GET_ALL_SALONS, userId, apiToken, true
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
                        snackBuilder.setSnackText(errorMessage).showSnack();
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

    private void initSalonsEmptyView(List<Salon> salonList) {
        recentSalonsProgress.setVisibility(View.GONE);

        if (salonList.size() > 0)
        {
            emptyViewLayout.setVisibility(View.GONE);
            recentSalonsRecycler.setVisibility(View.VISIBLE);

        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            recentSalonsRecycler.setVisibility(View.GONE);
        }
    }

    private void getFinishedSalonsFirstPageFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_FINISHED_SALONS, 1), new RequestModel<>(REQ_GET_ALL_FINISHED_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        finishedSalonList.clear();
                        finishedSalonList.addAll(ParseResponses.parseRounds(mainObject));
                        initFinishedSalonsRecycler();

                        last_page_finished = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initFinishedSalonsEmptyView(finishedSalonList);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initFinishedSalonsEmptyView(finishedSalonList);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getFinishedSalonsNextPageFromServer() {
        RetrofitClient.getInstance(getContext()).getSalonsPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_FINISHED_SALONS, ++page_finished), new RequestModel<>(REQ_GET_ALL_FINISHED_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = finishedSalonList.size();
                        finishedSalonList.addAll(ParseResponses.parseRounds(mainObject));
                        for (int i = nextFirstPosition; i < finishedSalonList.size(); i++)
                        {
                            finishedSalonsPagedAdapter.notifyItemInserted(i);
                        }

                        finishedSalonsRecycler.smoothScrollToPosition(nextFirstPosition);

                        if (page_finished < last_page_finished)
                            addScrollListenerToFinishedRecycler();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initFinishedSalonsRecycler() {
        if (finishedSalonsRecycler.getVisibility() == View.GONE)
        {
            finishedSalonsRecycler.setVisibility(View.VISIBLE);
        }
        if (finishedLayoutManager == null)
        {
            finishedLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            finishedSalonsRecycler.setLayoutManager(finishedLayoutManager);
        }

        finishedSalonsRecycler.setHasFixedSize(true);
        finishedSalonsPagedAdapter = new SalonsAdapter(context, finishedSalonList);
        finishedSalonsRecycler.setAdapter(finishedSalonsPagedAdapter);

        Common.Instance().hideProgress(finishedSalonsRecycler, finishedSalonsProgress);

        if (page_finished < last_page_finished)
        {
            addScrollListenerToFinishedRecycler();
        }
    }

    private void addScrollListenerToFinishedRecycler() {
        finishedSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (finishedLayoutManager.findLastCompletelyVisibleItemPosition() == finishedSalonsPagedAdapter.getItemCount() - 1)
                {
                    getFinishedSalonsNextPageFromServer();
                    Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();

                    finishedSalonsRecycler.removeOnScrollListener(this);
                }
            }
        });
    }

    private void initFinishedSalonsEmptyView(List<Salon> salonList) {
        finishedSalonsProgress.setVisibility(View.GONE);

        if (salonList.size() > 0)
        {
            finishedEmptyViewLayout.setVisibility(View.GONE);
            finishedSalonsRecycler.setVisibility(View.VISIBLE);
        }
        else
        {
            finishedEmptyViewLayout.setVisibility(View.VISIBLE);
            finishedSalonsRecycler.setVisibility(View.GONE);
        }
    }
}
