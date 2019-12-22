package it_geeks.info.elgawla.views.salon;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import it_geeks.info.elgawla.Adapters.SalonsMiniAdapter;
import it_geeks.info.elgawla.Adapters.StoreCategoryAdapter;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.util.Constants;
import it_geeks.info.elgawla.util.DateUtil;
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
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.account.ProfileActivity;

import static it_geeks.info.elgawla.util.Constants.FILTER;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_CATEGORIES;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_FINISHED_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_ALL_SALONS;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALONS_BY_CAT_ID;

public class AllSalonsActivity extends BaseActivity {

    private RecyclerView rvSalons, rvFilterCats;
    private TextView tvAllSalonsTitle, tvCalenderTitle, etFilterDateFrom, etFilterDateTo;
    private LinearLayout emptyViewLayout;
    private BottomSheetDialog mBottomSheetDialogFilterBy, mBottomSheetCalender, mBottomSheetCategory;
    private FloatingActionButton fbtnFilter;
    private ProgressBar pbpSalons, pbFilterCats;
    private CalendarView cv;
    private Button btnContinue, btnClearTitle, btnClearDate, btnClearCat;
    private EditText etFilterTitle, etFilterCat;
    private View catSheetView;

    private ShimmerFrameLayout salonsShimmerLayout;

    private List<Salon> salonsList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    private int userId, catKey, page = 1, last_page = 1;
    private Integer catId;
    private boolean isFilter = false, isFinishedSalons = false, isDateFrom = true;
    private String apiToken, title, dateFrom = null, dateTo = null, selectedDateFrom, selectedDateTo;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_all_salons);

        userId = SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getApi_token());

        initViews();

        getExtraData();

        if (catKey == Constants.NULL_INT_VALUE)
        {
            initBottomSheetFilterBy();
            initBottomSheetCalender();
            initBottomSheetCategory();
            getFirstAllSalonsFromServer();
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
        rvSalons = findViewById(R.id.all_salons_recycler);
        emptyViewLayout = findViewById(R.id.all_salons_empty_view);
        tvAllSalonsTitle = findViewById(R.id.tv_all_salon_title);
        salonsShimmerLayout = findViewById(R.id.sh_all_salons);
        pbpSalons = findViewById(R.id.pbp_salons);

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
                if (!mBottomSheetDialogFilterBy.isShowing())
                {
                    mBottomSheetDialogFilterBy.show();
                    etFilterTitle.requestFocus();
                }
                else
                {
                    mBottomSheetDialogFilterBy.dismiss();
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
                    public void onTrueResponse(JsonObject mainObject) {
                        salonsList = ParseResponses.parseSalons(mainObject);
                    }

                    @Override
                    public void afterResponse() {
                        initSalonsRecycler();
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getFirstAllSalonsFromServer() {
        startSalonsShimmer();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, 1)
                , new RequestModel<>(FILTER, userId, apiToken, false,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        salonsList = ParseResponses.parseSalons(mainObject);

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void afterResponse() {
                        initSalonsRecycler();
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextAllSalonsFromServer() {
        onLoadMoreSalons();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, ++page)
                , new RequestModel<>(FILTER, userId, apiToken, false
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = salonsList.size();
                        salonsList.addAll(ParseResponses.parseSalons(mainObject));
                        for (int i = nextFirstPosition; i < salonsList.size(); i++)
                        {
                            rvSalons.getAdapter().notifyItemInserted(i);
                        }

                        rvSalons.smoothScrollToPosition(nextFirstPosition);
                        addSalonsScrollListener();
                    }

                    @Override
                    public void afterResponse() {
                        pbpSalons.setVisibility(View.GONE);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        pbpSalons.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void onLoadMoreSalons() {
        pbpSalons.setVisibility(View.VISIBLE);
        rvSalons.scrollToPosition(salonsList.size() - 1);
    }

    private void initBottomSheetFilterBy() {
        mBottomSheetDialogFilterBy = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_by, null);

        btnContinue = sheetView.findViewById(R.id.btn_filter_continue);
        btnClearTitle = sheetView.findViewById(R.id.btn_clear_filter_title);
        btnClearDate = sheetView.findViewById(R.id.btn_clear_filter_date);
        btnClearCat = sheetView.findViewById(R.id.btn_clear_filter_cat);
        etFilterTitle = sheetView.findViewById(R.id.et_filter_by_title);
        etFilterDateFrom = sheetView.findViewById(R.id.et_filter_by_date_from);
        etFilterDateTo = sheetView.findViewById(R.id.et_filter_by_date_to);
        etFilterCat = sheetView.findViewById(R.id.et_filter_by_category);

        //
        etFilterDateFrom.setInputType(InputType.TYPE_NULL);
        etFilterDateTo.setInputType(InputType.TYPE_NULL);
        etFilterCat.setInputType(InputType.TYPE_NULL);

        handleFilterEvents();

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

        mBottomSheetDialogFilterBy.setContentView(sheetView);
        Common.Instance().setBottomSheetHeight(sheetView);
        mBottomSheetDialogFilterBy.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void handleFilterEvents() {
        etFilterDateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    selectDateFrom();
                }
            }
        });

        etFilterDateTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    selectDateTo();
                }
            }
        });

        etFilterCat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    selectCat();
                }
            }
        });

        etFilterDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isDateFilter)
//                {
//                    if (dateList.isEmpty())
//                    {// query from server
//                        getFirstDatesFromServer();
//                    }
//                    else
//                    {// get locally
//                        initDatesAdapter();
//                        getFirstFilteredSalonsFromServer(dateFrom, null, null, null);
//                    }
//                    isDateFilter = true;
//                    isCatFilter = false;
//                }
//                mBottomSheetDialogFilterBy.dismiss();

                selectDateFrom();
            }
        });

        etFilterDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateTo();
            }
        });

        etFilterCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isCatFilter)
//                {
//                    if (categoryList.isEmpty())
//                    {// query from server
//                startSalonsShimmer();
//                        getCatsFromServer();
//                    }
//                    else
//                    {// get locally
//                        initCatsAdapter();
//                        getFirstFilteredSalonsFromServer(null, null, null, catId);
//                    }
//                    isDateFilter = false;
//                    isCatFilter = true;
//                }
//                mBottomSheetDialogFilterBy.dismiss();

                selectCat();
            }
        });

        // clears
        btnClearTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFilterTitle.setText(null);
                title = null;

                etFilterTitle.clearFocus();
            }
        });

        btnClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFilterDateFrom.setText(null);
                etFilterDateTo.setText(null);
                selectedDateFrom = DateUtil.getCurrentTimeAsString();
                selectedDateTo = selectedDateFrom;

                dateFrom = null;
                dateTo = null;

                etFilterDateFrom.clearFocus();
                etFilterDateTo.clearFocus();
            }
        });

        btnClearCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFilterCat.setText(null);
                catId = null;

                etFilterCat.clearFocus();
            }
        });

        etFilterTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                {
                    if (btnClearTitle.getVisibility() != View.GONE)
                        btnClearTitle.setVisibility(View.GONE);
                }
                else if (btnClearTitle.getVisibility() != View.VISIBLE)
                {
                    btnClearTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFilterDateFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                {
                    if (btnClearDate.getVisibility() != View.GONE)
                        btnClearDate.setVisibility(View.GONE);
                }
                else if (btnClearDate.getVisibility() != View.VISIBLE)
                {
                    btnClearDate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFilterDateTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                {
                    if (btnClearDate.getVisibility() != View.GONE)
                        btnClearDate.setVisibility(View.GONE);
                }
                else if (btnClearDate.getVisibility() != View.VISIBLE)
                {
                    btnClearDate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFilterCat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                {
                    if (btnClearCat.getVisibility() != View.GONE)
                        btnClearCat.setVisibility(View.GONE);
                }
                else if (btnClearCat.getVisibility() != View.VISIBLE)
                {
                    btnClearCat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // continue
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFilter = true;
                title = etFilterTitle.getText().toString();
                getFirstFilteredSalonsFromServer(dateFrom, dateTo, title, catId);
                mBottomSheetDialogFilterBy.dismiss();
            }
        });
    }

    private void selectDateFrom() {
        isDateFrom = true;
        cv.setDate(DateUtil.getDateAsMillisFromString(selectedDateFrom));
        tvCalenderTitle.setText(getString(R.string.from));
        mBottomSheetCalender.show();
    }

    private void selectDateTo() {
        isDateFrom = false;
        cv.setDate(DateUtil.getDateAsMillisFromString(selectedDateTo));
        tvCalenderTitle.setText(getString(R.string.to));
        mBottomSheetCalender.show();
    }

    private void selectCat() {
        if (categoryList.isEmpty())
        {
            pbFilterCats.setVisibility(View.VISIBLE);
            getCatsFromServer();
        }
        mBottomSheetCategory.show();
    }

    private void initBottomSheetCalender() {
        mBottomSheetCalender = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_calender, null);
        tvCalenderTitle = sheetView.findViewById(R.id.tv_calender_title);
        cv = sheetView.findViewById(R.id.cv);

        selectedDateFrom = DateUtil.getCurrentTimeAsString();
        selectedDateTo = selectedDateFrom;

        cv.setMinDate(isFinishedSalons ? DateUtil.getDateAsMillisFromString("2019-10-01") : DateUtil.getCurrentTimeAsMillis());
        cv.setMaxDate(isFinishedSalons ? DateUtil.getCurrentTimeAsMillis() : DateUtil.getDateAsMillisFromString("2021-01-01"));
        cv.setDate(DateUtil.getCurrentTimeAsMillis());

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String sDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                if (isDateFrom)
                {
                    dateFrom = sDate;
                    selectedDateFrom = sDate;
                    etFilterDateFrom.setText(sDate);
                    etFilterDateFrom.clearFocus();
                }
                else
                {
                    dateTo = sDate;
                    selectedDateTo = sDate;
                    etFilterDateTo.setText(sDate);
                    etFilterDateTo.clearFocus();
                }
                mBottomSheetCalender.dismiss();
            }
        });

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_calender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetCalender.isShowing())
                {
                    etFilterDateFrom.clearFocus();
                    etFilterDateTo.clearFocus();
                    mBottomSheetCalender.dismiss();
                }
                else
                {
                    mBottomSheetCalender.show();
                }
            }
        });

        mBottomSheetCalender.setContentView(sheetView);
        Common.Instance().setBottomSheetHeight(sheetView);
        mBottomSheetCalender.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void initBottomSheetCategory() {
        mBottomSheetCategory = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        catSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_category, null);
        pbFilterCats = catSheetView.findViewById(R.id.pb_filter_cats);
        rvFilterCats = catSheetView.findViewById(R.id.rv_filter_cats);
        rvFilterCats.setHasFixedSize(true);
        rvFilterCats.setLayoutManager(new LinearLayoutManager(this));

        //close bottom sheet
        catSheetView.findViewById(R.id.close_bottom_sheet_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetCategory.isShowing())
                {
                    etFilterCat.clearFocus();
                    mBottomSheetCategory.dismiss();
                }
                else
                {
                    mBottomSheetCategory.show();
                }
            }
        });

        mBottomSheetCategory.setContentView(catSheetView);
        Common.Instance().setBottomSheetHeight(catSheetView);
        mBottomSheetCategory.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    private void getCatsFromServer() {
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataFromServer(AllSalonsActivity.this,
                REQ_GET_ALL_CATEGORIES, new RequestModel<>(REQ_GET_ALL_CATEGORIES, userId, apiToken,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                        if (mBottomSheetCategory.isShowing())
                        {
                            initFilterCatsRecycler();
                        }
                    }

                    @Override
                    public void afterResponse() {
                        if (mBottomSheetCategory.isShowing()) pbFilterCats.setVisibility(View.GONE);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        if (mBottomSheetCategory.isShowing()) pbFilterCats.setVisibility(View.GONE);

                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initFilterCatsRecycler() {
        StoreCategoryAdapter filterCatsAdapter = new StoreCategoryAdapter(categoryList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoryList.get(position);
                catId = category.getCategoryId();
                etFilterCat.setText(category.getCategoryName());
                etFilterCat.clearFocus();
                mBottomSheetCategory.dismiss();
            }
        });
        rvFilterCats.setAdapter(filterCatsAdapter);
        // update height
        Common.Instance().setBottomSheetHeight(catSheetView);
    }

    private void getFirstFilteredSalonsFromServer(String dateFrom, String dateTo, String title, Integer catId) {
        startSalonsShimmer();
        page = 1;
        last_page = 1;
        salonsList.clear();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, 1)
                , new RequestModel<>(FILTER, userId, apiToken, dateFrom, dateTo, title, catId, null)
                , new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        salonsList.addAll(ParseResponses.parseSalons(mainObject));

                        last_page = mainObject.get("last_page").getAsInt();
                    }

                    @Override
                    public void afterResponse() {
                        initSalonsRecycler();
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void getNextFilteredSalonsFromServer(String dateFrom, String dateTo, String title, Integer catId) {
        onLoadMoreSalons();
        RetrofitClient.getInstance(AllSalonsActivity.this).fetchDataPerPageFromServer(AllSalonsActivity.this,
                new Data(isFinishedSalons ? REQ_GET_ALL_FINISHED_SALONS : REQ_GET_ALL_SALONS, ++page)
                , new RequestModel<>(FILTER, userId, apiToken, dateFrom, dateTo, title, catId, null)
                , new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        try
                        {
                            int nextFirstPosition = salonsList.size();
                            salonsList.addAll(ParseResponses.parseSalons(mainObject));
                            for (int i = nextFirstPosition; i < salonsList.size(); i++)
                            {
                                rvSalons.getAdapter().notifyItemInserted(i);
                            }

                            rvSalons.smoothScrollToPosition(nextFirstPosition);
                            addSalonsScrollListener();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterResponse() {
                        pbpSalons.setVisibility(View.GONE);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        pbpSalons.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initSalonsRecycler() {
        stopSalonsShimmer();
        if (!salonsList.isEmpty())
        { // !empty
            emptyViewLayout.setVisibility(View.GONE);
            rvSalons.setVisibility(View.VISIBLE);
            updateSpanCount(salonsList);
            rvSalons.setHasFixedSize(true);
            rvSalons.setAdapter(new SalonsMiniAdapter(AllSalonsActivity.this, salonsList, "all_salons"));

            addSalonsScrollListener();
        }
        else
        {  // empty
            emptyViewLayout.setVisibility(View.VISIBLE);
            rvSalons.setVisibility(View.GONE);
        }
        EventsManager.sendSearchResultsEvent(this, "");
    }

    private void updateSpanCount(List<Salon> list) {
        if (rvSalons.getLayoutManager() != null)
        {
            if (list.size() == 1)
            {
                ((GridLayoutManager) rvSalons.getLayoutManager()).setSpanCount(1);
            }
            else
            {
                ((GridLayoutManager) rvSalons.getLayoutManager()).setSpanCount(2);
            }
        }
    }

    private void addSalonsScrollListener() {
        if (page < last_page)
        {
            rvSalons.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (page >= last_page)
                    {
                        rvSalons.removeOnScrollListener(this);
                        return;
                    }

                    if (((GridLayoutManager) rvSalons.getLayoutManager()).findLastCompletelyVisibleItemPosition() == rvSalons.getAdapter().getItemCount() - 1)
                    {
                        if (isFilter)
                        {
                            getNextFilteredSalonsFromServer(dateFrom, dateTo, title, catId);
                        }
                        else
                        {
                            getNextAllSalonsFromServer();
                        }
                        rvSalons.removeOnScrollListener(this);
                    }
                }
            });
        }
    }

    private void startSalonsShimmer() {
        if (salonsShimmerLayout.getVisibility() != View.VISIBLE)
            salonsShimmerLayout.setVisibility(View.VISIBLE);

        rvSalons.setVisibility(View.GONE);
        emptyViewLayout.setVisibility(View.GONE);
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
