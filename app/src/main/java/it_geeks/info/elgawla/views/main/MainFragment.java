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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
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
import it_geeks.info.elgawla.Adapters.CategoryHomeAdapter;
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
    private LinearLayout recentEmptyViewLayout, finishedEmptyViewLayout, adsEmptyView;
    private TextView btnSeeMoreSalons, btnSeeMoreFinishedSalons, noConnectionLayout;
    private ImageView imgNotification, ivUserImage;

    private ShimmerFrameLayout salonsShimmerLayout, finishedSalonsShimmerLayout, catsShimmerLayout, sliderShimmerLayout;

    private List<Ad> adsList = new ArrayList<>();
    private List<Salon> recentSalonList = new ArrayList<>(), finishedSalonList = new ArrayList<>();
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
        recentEmptyViewLayout = fragmentView.findViewById(R.id.recent_salons_empty_view);
        finishedEmptyViewLayout = fragmentView.findViewById(R.id.finished_salons_empty_view);
        adsEmptyView = fragmentView.findViewById(R.id.ads_empty_view);
        noConnectionLayout = fragmentView.findViewById(R.id.no_connection);
        rvCats = fragmentView.findViewById(R.id.rv_home_cats);
        ivUserImage = fragmentView.findViewById(R.id.iv_user_image);
        salonsShimmerLayout = fragmentView.findViewById(R.id.sh_salons);
        finishedSalonsShimmerLayout = fragmentView.findViewById(R.id.sh_finished_salons);
        catsShimmerLayout = fragmentView.findViewById(R.id.sh_home_category);
        sliderShimmerLayout = fragmentView.findViewById(R.id.sh_slider);

        //Notification icon
        imgNotification = fragmentView.findViewById(R.id.iv_notification_bell);
        View bellIndicator = fragmentView.findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(context, bellIndicator);

        // load user image
        ImageLoader.getInstance().loadUserImage(context, ivUserImage);

        btnSeeMoreSalons = fragmentView.findViewById(R.id.btn_recent_salons_see_more);
        btnSeeMoreFinishedSalons = fragmentView.findViewById(R.id.btn_finished_salons_see_more);

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
        View.OnClickListener seeMoreClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AllSalonsActivity.class);
                i.putExtra(Constants.CATEGORY_KEY, Constants.NULL_INT_VALUE);
                i.putExtra(Constants.FINISHED, v.getId() == R.id.btn_finished_salons_see_more);
                startActivity(i);
            }
        };

        btnSeeMoreSalons.setOnClickListener(seeMoreClickListener);
        btnSeeMoreFinishedSalons.setOnClickListener(seeMoreClickListener);

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

            getFirstRecentSalonsFromServer();

            getFirstFinishedSalonsFromServer();
        }
        else
        {
            onNoConnection();
        }
    }

    private void onNoConnection() {
        noConnectionLayout.setVisibility(View.VISIBLE);
        recentSalonsRecycler.setVisibility(View.GONE);
        finishedSalonsRecycler.setVisibility(View.GONE);
        adsPager.setVisibility(View.GONE);
        adsEmptyView.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        stopCatsShimmer();
        stopSalonsShimmer();
        stopFinishedSalonsShimmer();
    }

    private void getAdsAndCatsFromServer() {
        startSliderShimmer();
        startCatsShimmer();
        RetrofitClient.getInstance(getContext()).fetchDataFromServer(getContext(),
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
                        initCatsRecycler();
                    }
                });
    }

    private void initAdsRecycler() {
        stopSliderShimmer();
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

    private void startSliderShimmer() {
        if (sliderShimmerLayout.getVisibility() == View.VISIBLE)
            sliderShimmerLayout.startShimmerAnimation();
    }

    private void stopSliderShimmer() {
        if (sliderShimmerLayout.getVisibility() == View.VISIBLE)
        {
            sliderShimmerLayout.stopShimmerAnimation();
            sliderShimmerLayout.setVisibility(View.GONE);
        }
    }

    private void initCatsRecycler() {
        stopCatsShimmer();
        if (!categories.isEmpty())
        {
            rvCats.setVisibility(View.VISIBLE);
            rvCats.setHasFixedSize(true);
            rvCats.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            rvCats.setAdapter(new CategoryHomeAdapter(categories, new ClickInterface.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Category cat = categories.get(position);
                    openFilterPageByCat(cat.getCategoryId(), cat.getCategoryName());
                }
            }));
        }
    }

    private void openFilterPageByCat(int catId, String categoryName) {
        Intent i = new Intent(context, AllSalonsActivity.class);
        i.putExtra(Constants.CATEGORY_KEY, catId);
        i.putExtra(Constants.CATEGORY_NAME, categoryName);
        startActivity(i);
    }

    private void startCatsShimmer() {
        if (catsShimmerLayout.getVisibility() == View.VISIBLE)
            catsShimmerLayout.startShimmerAnimation();
    }

    private void stopCatsShimmer() {
        if (catsShimmerLayout.getVisibility() == View.VISIBLE)
        {
            catsShimmerLayout.stopShimmerAnimation();
            catsShimmerLayout.setVisibility(View.GONE);
        }
    }

    private void getFirstRecentSalonsFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(getContext()).fetchDataPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_SALONS, 1), new RequestModel<>(REQ_GET_ALL_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        recentSalonList.clear();
                        recentSalonList.addAll(ParseResponses.parseSalons(mainObject));

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initRecentSalonsRecycler();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initRecentSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getNextRecentSalonsFromServer() {
        RetrofitClient.getInstance(getContext()).fetchDataPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_SALONS, ++page), new RequestModel<>(REQ_GET_ALL_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = recentSalonList.size();
                        recentSalonList.addAll(ParseResponses.parseSalons(mainObject));
                        for (int i = nextFirstPosition; i < recentSalonList.size(); i++)
                        {
                            recentSalonsRecycler.getAdapter().notifyItemInserted(i);
                        }

                        recentSalonsRecycler.smoothScrollToPosition(nextFirstPosition);
                        addRecentScrollListener();
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

    private void initRecentSalonsRecycler() {
        stopSalonsShimmer();
        if (!recentSalonList.isEmpty())
        {
            recentEmptyViewLayout.setVisibility(View.GONE);
            recentSalonsRecycler.setVisibility(View.VISIBLE);

            recentSalonsRecycler.setHasFixedSize(true);
            recentSalonsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
            recentSalonsRecycler.setAdapter(new SalonsAdapter(getContext(), recentSalonList));

            addRecentScrollListener();
        }
        else
        {
            recentEmptyViewLayout.setVisibility(View.VISIBLE);
            recentSalonsRecycler.setVisibility(View.GONE);
        }
    }

    private void addRecentScrollListener() {
        if (page < last_page)
        {
            recentSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (((LinearLayoutManager) recentSalonsRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition() == recentSalonsRecycler.getAdapter().getItemCount() - 1)
                    {
                        getNextRecentSalonsFromServer();
                        Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();

                        recentSalonsRecycler.removeOnScrollListener(this);
                    }
                }
            });
        }
    }

    private void startSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() == View.VISIBLE)
            salonsShimmerLayout.startShimmerAnimation();
    }

    private void stopSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() == View.VISIBLE)
        {
            salonsShimmerLayout.stopShimmerAnimation();
            salonsShimmerLayout.setVisibility(View.GONE);
        }
    }

    private void getFirstFinishedSalonsFromServer() {
        startFinishedSalonsShimmer();
        RetrofitClient.getInstance(getContext()).fetchDataPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_FINISHED_SALONS, 1), new RequestModel<>(REQ_GET_ALL_FINISHED_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        finishedSalonList.clear();
                        finishedSalonList.addAll(ParseResponses.parseSalons(mainObject));

                        last_page_finished = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initFinishedSalonsRecycler();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initFinishedSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    private void getNextFinishedSalonsFromServer() {
        RetrofitClient.getInstance(getContext()).fetchDataPerPageFromServer(getContext(),
                new Data(REQ_GET_ALL_FINISHED_SALONS, ++page_finished), new RequestModel<>(REQ_GET_ALL_FINISHED_SALONS, userId, apiToken, true
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = finishedSalonList.size();
                        finishedSalonList.addAll(ParseResponses.parseSalons(mainObject));
                        for (int i = nextFirstPosition; i < finishedSalonList.size(); i++)
                        {
                            finishedSalonsRecycler.getAdapter().notifyItemInserted(i);
                        }

                        finishedSalonsRecycler.smoothScrollToPosition(nextFirstPosition);
                        addFinishedScrollListener();
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
        stopFinishedSalonsShimmer();
        if (!finishedSalonList.isEmpty())
        {
            finishedSalonsRecycler.setVisibility(View.VISIBLE);
            finishedSalonsRecycler.setHasFixedSize(true);
            finishedSalonsRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            finishedSalonsRecycler.setAdapter(new SalonsAdapter(context, finishedSalonList));

            addFinishedScrollListener();
        }
        else
        {
            finishedEmptyViewLayout.setVisibility(View.VISIBLE);
            finishedSalonsRecycler.setVisibility(View.GONE);
        }
    }

    private void addFinishedScrollListener() {
        if (page_finished < last_page_finished)
        {
            finishedSalonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (((LinearLayoutManager) finishedSalonsRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition() == finishedSalonsRecycler.getAdapter().getItemCount() - 1)
                    {
                        getNextFinishedSalonsFromServer();
                        Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
                        finishedSalonsRecycler.removeOnScrollListener(this);
                    }
                }
            });
        }
    }

    private void startFinishedSalonsShimmer() {
        if (finishedSalonsShimmerLayout.getVisibility() == View.VISIBLE)
            finishedSalonsShimmerLayout.startShimmerAnimation();
    }

    private void stopFinishedSalonsShimmer() {
        finishedSalonsShimmerLayout.stopShimmerAnimation();
        finishedSalonsShimmerLayout.setVisibility(View.GONE);
    }
}
