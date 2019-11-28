package it_geeks.info.elgawla.views.salon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.Adapters.CategoryFilterAdapter;
import it_geeks.info.elgawla.Adapters.SalonsMiniAdapter;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.util.Constants;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.repository.Storage.CardDao;
import it_geeks.info.elgawla.repository.Storage.ProductImageDao;
import it_geeks.info.elgawla.repository.Storage.RoundDao;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.notification.NotificationBuilder;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.Models.SalonDate;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.Adapters.DateAdapter;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_CATEGORIES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_FINISHED_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_BY_CAT_ID;

public class AllSalonsActivity extends BaseActivity {

    private RecyclerView filterRecycler, rvCats, salonsRecycler;
    private TextView tvAllSalonsTitle;
    private LinearLayout emptyViewLayout;
    private BottomSheetDialog mBottomSheetDialogFilterBy;
    private FloatingActionButton fbtnFilter;

    private ShimmerFrameLayout salonsShimmerLayout;

    private List<Salon> roundsList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<SalonDate> dateList = new ArrayList<>();

    private int userId, catKey;
    private boolean isDateFilter = true, isFinishedSalons = false;
    private String apiToken;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        userId = SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getApi_token());

        initViews();

        initBottomSheetFilterBy();

        getExtraData();

        if (catKey == Constants.NULL_INT_VALUE)
        {
            getDatesAndRoundsFromServer();
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
        }
    }

    private void initViews() {
        fbtnFilter = findViewById(R.id.all_salon_filter_icon);
        salonsRecycler = findViewById(R.id.all_salons_recycler);
        emptyViewLayout = findViewById(R.id.all_salons_empty_view);
        tvAllSalonsTitle = findViewById(R.id.tv_all_salon_title);
        salonsShimmerLayout = findViewById(R.id.sh_all_salons);

        filterRecycler = findViewById(R.id.filter_recycler);
        rvCats = findViewById(R.id.rv_cats);
        filterRecycler.setHasFixedSize(true);
        rvCats.setHasFixedSize(true);

        filterRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
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
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(AllSalonsActivity.this,
                REQ_GET_SALONS_BY_CAT_ID, new RequestModel<>(REQ_GET_SALONS_BY_CAT_ID, userId, apiToken, catKey,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        roundsList = ParseResponses.parseRounds(mainObject);
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

    private void getDatesAndRoundsFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(AllSalonsActivity.this,
                isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, new RequestModel<>(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, userId, apiToken, false,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        updateDatabaseList(ParseResponses.parseRounds(mainObject));

                        transAndSortDates();

                        initDatesAdapter();

                        try
                        {
                            roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByDate(String.valueOf(dateList.get(0).getsDate()));
                        }
                        catch (IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
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

    private void updateDatabaseList(List<Salon> salons) {
        RoundDao roundDao = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao();
        roundDao.removeRounds(roundDao.getRounds());
        roundDao.insertRoundList(salons);

        ProductImageDao productImageDao = GawlaDataBse.getInstance(AllSalonsActivity.this).productImageDao();
        productImageDao.removeSubImages(productImageDao.getSubImages());
        CardDao cardDao = GawlaDataBse.getInstance(AllSalonsActivity.this).cardDao();
        cardDao.removeCards(cardDao.getCards());

        for (int i = 0; i < salons.size(); i++)
        {
            productImageDao.insertSubImages(salons.get(i).getProduct_images());
            cardDao.insertCards(salons.get(i).getSalon_cards());
        }
    }

    private void transAndSortDates() {
        List<String> dates = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsDates();

        dateList.clear();

        for (String date : dates)
        {
            dateList.add(transformDateToNames(date));
        }

        Common.Instance().sortList(dateList);
    }

    public SalonDate transformDateToNames(String sDate) {
        String[] dateParts = sDate.split("-"); // separate date
        String day = dateParts[2];
        String month = dateParts[1];
        String year = dateParts[0];

        String monthName = new DateFormatSymbols(new Locale(SharedPrefManager.getInstance(AllSalonsActivity.this).getSavedLang())).getMonths()[Integer.parseInt(month) - 1]; // month from num to nam

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK); // day in month to day in week
        String dayOfWeek = new DateFormatSymbols(new Locale(SharedPrefManager.getInstance(AllSalonsActivity.this).getSavedLang())).getWeekdays()[dayWeek]; // day in week from num to nam

        Date date = null;
        try
        {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(sDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return new SalonDate(date, sDate, day, monthName, dayOfWeek, GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getDatesCount(sDate) + getResources().getString(R.string.salons));
    }

    private void initDatesAdapter() {
        rvCats.setVisibility(View.GONE);
        filterRecycler.setVisibility(View.VISIBLE);
        filterRecycler.setAdapter(new DateAdapter(AllSalonsActivity.this, dateList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SalonDate salonDate = dateList.get(position);
                roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByDate(String.valueOf(salonDate.getsDate()));

                initSalonsRecycler();
                EventsManager.sendSearchEvent(AllSalonsActivity.this, String.valueOf(salonDate.getsDate()));
            }
        }));
    }

    private void getCategoriesAndRoundsFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(AllSalonsActivity.this,
                REQ_GET_ALL_CATEGORIES, new RequestModel<>(REQ_GET_ALL_CATEGORIES, userId, apiToken,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);

                        try
                        {
                            roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByCategory(categoryList.get(0).getCategoryName());
                        }
                        catch (IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        initCategoriesAdapter();
                        initSalonsRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initCategoriesAdapter() {
        filterRecycler.setVisibility(View.GONE);
        rvCats.setVisibility(View.VISIBLE);
        rvCats.setAdapter(new CategoryFilterAdapter(categoryList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoryList.get(position);
                roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByCategory(category.getCategoryName());

                initSalonsRecycler();
                EventsManager.sendSearchEvent(AllSalonsActivity.this, String.valueOf(category.getCategoryName()));
            }
        }));
    }

    private void initSalonsRecycler() {
        EventsManager.sendSearchResultsEvent(this, "");
        stopSalonsShimmer();
        if (!roundsList.isEmpty())
        { // !empty ?
            emptyViewLayout.setVisibility(View.GONE);
            salonsRecycler.setVisibility(View.VISIBLE);
            updateSpanCount(roundsList);
            salonsRecycler.setHasFixedSize(true);
            salonsRecycler.setAdapter(new SalonsMiniAdapter(AllSalonsActivity.this, roundsList, "all_salons"));
        }
        else
        {  // empty ?
            emptyViewLayout.setVisibility(View.VISIBLE);
            salonsRecycler.setVisibility(View.GONE);
        }
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
                        getDatesAndRoundsFromServer();
                    }
                    else
                    {// get locally
                        initDatesAdapter();
                        roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByDate(String.valueOf(dateList.get(0).getsDate()));
                        initSalonsRecycler();
                    }
                    isDateFilter = true;
                }
                mBottomSheetDialogFilterBy.dismiss();
            }
        });

        sheetView.findViewById(R.id.btn_filter_by_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDateFilter)
                {
                    if (categoryList.isEmpty())
                    {// query from server
                        getCategoriesAndRoundsFromServer();
                    }
                    else
                    {// get locally
                        initCategoriesAdapter();
                        roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByCategory(categoryList.get(0).getCategoryName());
                        initSalonsRecycler();
                    }
                    isDateFilter = false;
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
}
