package it_geeks.info.elgawla.views.salon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.Adapters.CategoryFilterAdapter;
import it_geeks.info.elgawla.Adapters.SalonsMiniAdapter;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.repository.Models.Date;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.util.Constants;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.Adapters.DateAdapter;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_CATEGORIES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_FINISHED_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_FILTER_DATES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_BY_CAT_ID;

public class AllSalonsActivity extends BaseActivity {
    private RecyclerView rvDates, rvCats, salonsRecycler;

    private TextView tvAllSalonsTitle;
    private LinearLayout emptyViewLayout;
    private BottomSheetDialog mBottomSheetDialogFilterBy;
    private FloatingActionButton fbtnFilter;

    private ShimmerFrameLayout salonsShimmerLayout;

    private List<Salon> salonsList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<Date> dateList = new ArrayList<>();

    private int userId, catKey, page = 1, page_date = 1, last_page = 1, last_page_date = 1, cat;
    private boolean isDateFilter = false, isCatFilter = false, isFinishedSalons = false;
    private String apiToken, date;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        userId = SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getApi_token());

        initViews();

        getExtraData();

        if (catKey == Constants.NULL_INT_VALUE)
        {
            initBottomSheetFilterBy();
            getAllSalonsFromServer();
        }
        else
        {
            getSalonsByCatFromServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SalonsMiniAdapter.clickable = true;
    }

    private void getExtraData() {
        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            isFinishedSalons = extra.getBoolean(Constants.FINISHED);
            catKey = extra.getInt(Constants.CATEGORY_KEY);
            String catName = extra.getString(Constants.CATEGORY_NAME);
            if (catName != null && !catName.isEmpty())
            {
                tvAllSalonsTitle.setText(extra.getString(Constants.CATEGORY_NAME));
                fbtnFilter.setVisibility(View.GONE);
            }
            if (isFinishedSalons)
            {
                tvAllSalonsTitle.setText(getString(R.string.previous_salons));
            }
        }
    }

    private void initViews() {
        fbtnFilter = findViewById(R.id.all_salon_filter_icon);
        salonsRecycler = findViewById(R.id.all_salons_recycler);
        emptyViewLayout = findViewById(R.id.all_salons_empty_view);
        tvAllSalonsTitle = findViewById(R.id.tv_all_salon_title);
        salonsShimmerLayout = findViewById(R.id.sh_all_salons);

        rvDates = findViewById(R.id.date_recycler);
        rvCats = findViewById(R.id.rv_cats);
        rvDates.setHasFixedSize(true);
        rvCats.setHasFixedSize(true);

        rvDates.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvCats.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        snackBuilder = new SnackBuilder(findViewById(R.id.all_salons_main_layout));

        // load user image
        ImageLoader.getInstance().loadUserImage(this, ((ImageView) findViewById(R.id.iv_user_image)));

        View bellIndicator = findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationBuilder.listenToNotificationStatus(this, bellIndicator);

        // notification onClick
        findViewById(R.id.iv_notification_bell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllSalonsActivity.this, NotificationActivity.class));
            }
        });

        // open filter sheet
        fbtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open sheet
                if (mBottomSheetDialogFilterBy.isShowing())
                {
                    mBottomSheetDialogFilterBy.dismiss();
                }
                else
                { // close sheet
                    mBottomSheetDialogFilterBy.show();
                }
            }
        });

        findViewById(R.id.iv_user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllSalonsActivity.this, ProfileActivity.class));
            }
        });
    }

    private void getSalonsByCatFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataFromServer(AllSalonsActivity.this,
                REQ_GET_SALONS_BY_CAT_ID, new RequestModel<>(REQ_GET_SALONS_BY_CAT_ID, userId, apiToken, catKey,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonsList = ParseResponses.parseSalons(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getAllSalonsFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, 1)
                , new RequestModel<>(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, userId, apiToken, false,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonsList = ParseResponses.parseSalons(mainObject);

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextAllSalonsFromServer() {
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, ++page)
                , new RequestModel<>(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, userId, apiToken, false
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = salonsList.size();
                        salonsList.addAll(ParseResponses.parseSalons(mainObject));
                        for (int i = nextFirstPosition; i < salonsList.size(); i++)
                        {
                            salonsRecycler.getAdapter().notifyItemInserted(i);
                        }

                        salonsRecycler.smoothScrollToPosition(nextFirstPosition);
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

    private void initBottomSheetFilterBy() {
        mBottomSheetDialogFilterBy = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_by, null);

        //init bottom sheet views
        sheetView.findViewById(R.id.btn_filter_by_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDateFilter)
                {
                    if (dateList.isEmpty())
                    {// query from server
                        getFirstDatesFromServer();
                    }
                    else
                    {// get locally
                        initDatesAdapter();
                        getFirstFilteredSalonsFromServer(date, null);
                    }
                    isDateFilter = true;
                    isCatFilter = false;
                }
                mBottomSheetDialogFilterBy.dismiss();
            }
        });

        sheetView.findViewById(R.id.btn_filter_by_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCatFilter)
                {
                    if (categoryList.isEmpty())
                    {// query from server
                        getCatsFromServer();
                    }
                    else
                    {// get locally
                        initCatsAdapter();
                        getFirstFilteredSalonsFromServer(null, cat);
                    }
                    isDateFilter = false;
                    isCatFilter = true;
                }
                mBottomSheetDialogFilterBy.dismiss();
            }
        });

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_filter_by).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogFilterBy.isShowing())
                {
                    mBottomSheetDialogFilterBy.dismiss();
                }
                else
                {
                    mBottomSheetDialogFilterBy.show();
                }
            }
        });

        //
        mBottomSheetDialogFilterBy.setContentView(sheetView);
        Common.Instance().setBottomSheetHeight(sheetView);
        mBottomSheetDialogFilterBy.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void getFirstDatesFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(REQ_GET_FILTER_DATES, 1), new RequestModel<>(REQ_GET_FILTER_DATES, userId, apiToken, !isFinishedSalons
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        last_page_date = mainObject.get("last_page").getAsInt();

                        dateList = ParseResponses.parseDates(mainObject);
                        initDatesAdapter();
                        date = dateList.get(0).getDate();
                        getFirstFilteredSalonsFromServer(date, null);
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextDatesFromServer() {
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(REQ_GET_FILTER_DATES, ++page_date), new RequestModel<>(REQ_GET_FILTER_DATES, userId, apiToken, !isFinishedSalons
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = dateList.size();
                        dateList.addAll(ParseResponses.parseDates(mainObject));
                        for (int i = nextFirstPosition; i < dateList.size(); i++)
                        {
                            rvDates.getAdapter().notifyItemInserted(i);
                        }

                        rvDates.smoothScrollToPosition(nextFirstPosition);
                        addDatesScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getCatsFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataFromServer(AllSalonsActivity.this,
                REQ_GET_ALL_CATEGORIES, new RequestModel<>(REQ_GET_ALL_CATEGORIES, userId, apiToken,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                        initCatsAdapter();
                        cat = categoryList.get(0).getCategoryId();
                        getFirstFilteredSalonsFromServer(null, cat);
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initDatesAdapter() {
        rvCats.setVisibility(View.GONE);
        rvDates.setVisibility(View.VISIBLE);
        rvDates.setAdapter(new DateAdapter(AllSalonsActivity.this, dateList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                date = dateList.get(position).getDate();
                getFirstFilteredSalonsFromServer(date, null);
                EventsManager.sendSearchEvent(AllSalonsActivity.this, String.valueOf(dateList.get(position).getDay_no()));
            }
        }));

        addDatesScrollListener();
    }

    private void initCatsAdapter() {
        rvDates.setVisibility(View.GONE);
        rvCats.setVisibility(View.VISIBLE);
        rvCats.setAdapter(new CategoryFilterAdapter(categoryList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                cat = categoryList.get(position).getCategoryId();
                getFirstFilteredSalonsFromServer(null, cat);
                EventsManager.sendSearchEvent(AllSalonsActivity.this, String.valueOf(cat));
            }
        }));
    }

    private void getFirstFilteredSalonsFromServer(String date, Integer cat) {
        startSalonsShimmer();
        page = 1;
        last_page = 1;
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, 1)
                , new RequestModel<>("filter", userId, apiToken, false, true, date, cat,
                        null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        salonsList.clear();
                        salonsList.addAll(ParseResponses.parseSalons(mainObject));

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextFilteredSalonsFromServer(String date, Integer cat) {
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, ++page)
                , new RequestModel<>("filter", userId, apiToken, false, true, date, cat,
                        null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = salonsList.size();
                        salonsList.addAll(ParseResponses.parseSalons(mainObject));
                        for (int i = nextFirstPosition; i < salonsList.size(); i++)
                        {
                            salonsRecycler.getAdapter().notifyItemInserted(i);
                        }

                        salonsRecycler.smoothScrollToPosition(nextFirstPosition);
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
        stopSalonsShimmer();
        if (!salonsList.isEmpty())
        { // !empty
            emptyViewLayout.setVisibility(View.GONE);
            salonsRecycler.setVisibility(View.VISIBLE);
            updateSpanCount(salonsList);
            salonsRecycler.setHasFixedSize(true);
            salonsRecycler.setAdapter(new SalonsMiniAdapter(AllSalonsActivity.this, salonsList, "all_salons"));

            addScrollListener();
        }
        else
        {  // empty
            emptyViewLayout.setVisibility(View.VISIBLE);
            salonsRecycler.setVisibility(View.GONE);
        }
        EventsManager.sendSearchResultsEvent(this, "");
    }

    private void updateSpanCount(List<Salon> list) {
        if (salonsRecycler.getLayoutManager() != null)
        {
            if (list.size() == 1)
            {
                ((GridLayoutManager) salonsRecycler.getLayoutManager()).setSpanCount(1);
            }
            else
            {
                ((GridLayoutManager) salonsRecycler.getLayoutManager()).setSpanCount(2);
            }
        }
    }

    private void addScrollListener() {
        if (page < last_page)
        {
            salonsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (page >= last_page)
                    {
                        salonsRecycler.removeOnScrollListener(this);
                        return;
                    }

                    if (((LinearLayoutManager) salonsRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition() == salonsRecycler.getAdapter().getItemCount() - 1)
                    {
                        if (isDateFilter || isCatFilter)
                        {
                            getNextFilteredSalonsFromServer(isDateFilter ? date : null, isCatFilter ? cat : null);
                        }
                        else
                        {
                            getNextAllSalonsFromServer();
                        }

                        Toast.makeText(AllSalonsActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                        salonsRecycler.removeOnScrollListener(this);
                    }
                }
            });
        }
    }

    private void addDatesScrollListener() {
        if (page_date < last_page_date)
        {
            rvDates.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (isDateFilter)
                    {
                        if (((LinearLayoutManager) rvDates.getLayoutManager()).findLastCompletelyVisibleItemPosition() == rvDates.getAdapter().getItemCount() - 1)
                        {
                            getNextDatesFromServer();

                            Toast.makeText(AllSalonsActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                            rvDates.removeOnScrollListener(this);
                        }
                    }
                    else
                    {
                        rvDates.removeOnScrollListener(this);
                    }
                }
            });
        }
    }

    private void startSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() != View.VISIBLE)
            salonsShimmerLayout.setVisibility(View.VISIBLE);

        salonsRecycler.setVisibility(View.GONE);
        salonsShimmerLayout.startShimmerAnimation();
    }

    private void stopSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() == View.VISIBLE)
        {
            salonsShimmerLayout.stopShimmerAnimation();
            salonsShimmerLayout.setVisibility(View.GONE);
        }
    }
}
