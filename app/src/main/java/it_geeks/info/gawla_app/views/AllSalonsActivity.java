package it_geeks.info.gawla_app.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Controllers.Adapters.CategoryAdapter;
import it_geeks.info.gawla_app.repository.Storage.CardDao;
import it_geeks.info.gawla_app.repository.Storage.ProductImageDao;
import it_geeks.info.gawla_app.repository.Storage.RoundDao;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.Interfaces.ConnectionInterface;
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.general.Interfaces.OnItemClickListener;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Models.SalonDate;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Controllers.Adapters.DateAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.SalonsAdapter;

public class AllSalonsActivity extends AppCompatActivity {

    public static Activity allSalonsActivityInstance;

    private RecyclerView filterRecycler;

    private List<Round> roundsList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<SalonDate> dateList = new ArrayList<>();

    private BottomSheetDialog mBottomSheetDialogFilterBy;

    ImageView imgNotification;

    private CardView loadingCard;

    private int userId;
    private String apiToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        allSalonsActivityInstance = this;

        userId = SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getUser_id();
        apiToken = Common.Instance(AllSalonsActivity.this).removeQuotes(SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getApi_token());

        initViews();

        initBottomSheetFilterBy();

        Common.Instance(AllSalonsActivity.this).ApplyOnConnection(AllSalonsActivity.this, new ConnectionInterface() {
            @Override
            public void onConnected() {
                getDatesAndRoundsFromServer();
            }

            @Override
            public void onFailed() {
                hideLoading();
            }
        });
    }

    private void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void initViews() {
        loadingCard = findViewById(R.id.loading_card);
        filterRecycler = findViewById(R.id.filter_recycler);
        filterRecycler.setHasFixedSize(true);
        filterRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, RecyclerView.HORIZONTAL, false));

        // back
        findViewById(R.id.all_salons_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Notification icon
        imgNotification = findViewById(R.id.all_salon_notification_icon);

        // notification status LiveData
        NotificationStatus.notificationStatus(this,imgNotification);

        // notofocation onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllSalonsActivity.this, NotificationActivity.class));
            }
        });

        // open filter sheet
        findViewById(R.id.all_salon_filter_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open sheet
                if (mBottomSheetDialogFilterBy.isShowing()) {
                    mBottomSheetDialogFilterBy.dismiss();
                } else { // close sheet
                    mBottomSheetDialogFilterBy.show();
                }
            }
        });
    }

    private void getDatesAndRoundsFromServer() {
        displayLoading();
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(MainActivity.mainInstance,
                "getAllSalons", new Request(userId, apiToken), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        updateDatabaseList(ParseResponses.parseRounds(mainObject));

                        transAndSortDates();

                        initDatesAdapter();

                        try {
                            roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(dateList.get(0).getDate());
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }

                        initSalonsRecycler();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        initSalonsEmptyView();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsEmptyView();
                        Toast.makeText(AllSalonsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateDatabaseList(List<Round> rounds) {
        RoundDao roundDao = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao();
        roundDao.removeRounds(roundDao.getRounds());
        roundDao.insertRoundList(rounds);

        ProductImageDao productImageDao = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).productImageDao();
        productImageDao.removeSubImages(productImageDao.getSubImages());
        CardDao cardDao = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).cardDao();
        cardDao.removeCards(cardDao.getCards());

        for (int i = 0; i < rounds.size(); i++) {
            productImageDao.insertSubImages(rounds.get(i).getProduct_images());
            cardDao.insertCards(rounds.get(i).getSalon_cards());
        }
    }

    private void transAndSortDates() {
        List<String> dates = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsDates();

        dateList.clear();

        for (String date : dates) {
            dateList.add(transformDateToNames(date));
        }

        Common.Instance(AllSalonsActivity.this).sortList(dateList);
    }

    public SalonDate transformDateToNames(String date) {
        String[] dateParts = date.split("-"); // separate date
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        String monthName = new DateFormatSymbols(new Locale(SharedPrefManager.getInstance(AllSalonsActivity.this).getSavedLang())).getMonths()[Integer.parseInt(month) - 1]; // month from num to nam

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK); // day in month to day in week
        String dayOfWeek = new DateFormatSymbols(new Locale(SharedPrefManager.getInstance(AllSalonsActivity.this).getSavedLang())).getWeekdays()[dayWeek]; // day in week from num to nam

        return new SalonDate(date, day, monthName, dayOfWeek, GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getDatesCount(date) + getResources().getString(R.string.salons));
    }

    private void initDatesAdapter() {
        filterRecycler.setAdapter(new DateAdapter(AllSalonsActivity.this, dateList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SalonDate salonDate = dateList.get(position);
                roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(salonDate.getDate());

                initSalonsRecycler();

                initSalonsEmptyView();
            }
        }));
    }

    private void getCategoriesAndRoundsFromServer() {
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(MainActivity.mainInstance,
                "getAllCategories", new Request(userId, apiToken), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {

                        categoryList = ParseResponses.parseCategories(mainObject);

                        initCategoriesAdapter();

                        try {
                            roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByCategory(categoryList.get(0).getCategoryName());
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }

                        initSalonsRecycler();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                    }

                    @Override
                    public void handleEmptyResponse() {
                        initSalonsEmptyView();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsEmptyView();
                        Toast.makeText(AllSalonsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initCategoriesAdapter() {
        filterRecycler.setAdapter(new CategoryAdapter(AllSalonsActivity.this, categoryList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoryList.get(position);
                roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByCategory(category.getCategoryName());

                initSalonsRecycler();

                initSalonsEmptyView();
            }
        }));
    }

    private void initSalonsRecycler() {
            RecyclerView dateSalonsRecycler = findViewById(R.id.date_salons_recycler);
            dateSalonsRecycler.setHasFixedSize(true);
            dateSalonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, RecyclerView.HORIZONTAL, false));
            dateSalonsRecycler.setAdapter(new SalonsAdapter(AllSalonsActivity.this, roundsList));
    }

    private void initSalonsEmptyView() {
        LinearLayout emptyViewLayout = findViewById(R.id.all_salons_empty_view);

        hideLoading();

        if (roundsList.size() > 0) { // !empty ?
            emptyViewLayout.setVisibility(View.GONE);

        } else {  // empty ?
            emptyViewLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initBottomSheetFilterBy() {
        mBottomSheetDialogFilterBy = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter_by, null);

        //init bottom sheet views
        sheetView.findViewById(R.id.btn_filter_by_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatesAndRoundsFromServer();
                displayLoading();
                mBottomSheetDialogFilterBy.dismiss();
            }
        });

        sheetView.findViewById(R.id.btn_filter_by_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCategoriesAndRoundsFromServer();
                displayLoading();
                mBottomSheetDialogFilterBy.dismiss();
            }
        });

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_filter_by).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogFilterBy.isShowing()) {
                    mBottomSheetDialogFilterBy.dismiss();

                } else {
                    mBottomSheetDialogFilterBy.show();
                }
            }
        });

        //
        mBottomSheetDialogFilterBy.setContentView(sheetView);
        Common.Instance(AllSalonsActivity.this).setBottomSheetHeight(sheetView);
        mBottomSheetDialogFilterBy.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }
}
