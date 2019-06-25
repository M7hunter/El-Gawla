package it_geeks.info.gawla_app.views.salon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.Adapters.CategoryAdapter;
import it_geeks.info.gawla_app.util.Constants;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Storage.CardDao;
import it_geeks.info.gawla_app.repository.Storage.ProductImageDao;
import it_geeks.info.gawla_app.repository.Storage.RoundDao;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.Interfaces.ConnectionInterface;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Models.SalonDate;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Adapters.DateAdapter;
import it_geeks.info.gawla_app.Adapters.SalonsAdapter;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.account.AccountDetailsActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_CATEGORIES;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_SALONS;

public class AllSalonsActivity extends AppCompatActivity {

    public static Activity allSalonsActivityInstance;

    private RecyclerView filterRecycler, salonsRecycler;

    private List<Round> roundsList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<SalonDate> dateList = new ArrayList<>();

    private BottomSheetDialog mBottomSheetDialogFilterBy;

    private ImageView ivNotificationBell;

    private LinearLayout emptyViewLayout;

    private int userId, catKey;
    private String apiToken;
    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        allSalonsActivityInstance = this;

        userId = SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getUser_id();
        apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(AllSalonsActivity.this).getUser().getApi_token());

        initViews();

        initBottomSheetFilterBy();

        Common.Instance().ApplyOnConnection(AllSalonsActivity.this, new ConnectionInterface() {
            @Override
            public void onConnected() {
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
            public void onFailed() {
                dialogBuilder.hideLoadingDialog();
            }
        });
    }

    private void getExtraData() {
        Bundle extra = getIntent().getExtras();
        if (extra != null)
        {
            catKey = extra.getInt(Constants.CATEGORY_KEY);
        }
    }

    private void initViews() {
        salonsRecycler = findViewById(R.id.all_salons_recycler);
        emptyViewLayout = findViewById(R.id.all_salons_empty_view);
        filterRecycler = findViewById(R.id.filter_recycler);
        filterRecycler.setHasFixedSize(true);
        filterRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, RecyclerView.HORIZONTAL, false));

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        // load user image
        ImageLoader.getInstance().loadUserImage(this, ((ImageView) findViewById(R.id.iv_user_image)));

        // Notification icon
        ivNotificationBell = findViewById(R.id.iv_notification_bell);
        View bellIndicator = findViewById(R.id.bell_indicator);

        // notification status LiveData
        NotificationStatus.notificationStatus(this, bellIndicator);

        // notification onClick
        ivNotificationBell.setOnClickListener(new View.OnClickListener() {
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
                startActivity(new Intent(AllSalonsActivity.this, AccountDetailsActivity.class));
            }
        });
    }

    private void getDatesAndRoundsFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(MainActivity.mainInstance,
                REQ_GET_ALL_SALONS, new Request<>(REQ_GET_ALL_SALONS, userId, apiToken, false,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        updateDatabaseList(ParseResponses.parseRounds(mainObject));

                        transAndSortDates();

                        initDatesAdapter();

                        try
                        {
                            roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByDate(String.valueOf(dateList.get(0).getsDate()));
                        } catch (IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler();
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        dialogBuilder.hideLoadingDialog();
                        Toast.makeText(AllSalonsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getSalonsByCatFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(MainActivity.mainInstance,
                "getSalonsByCategoryID", new Request<>("getSalonsByCategoryID", userId, apiToken, catKey,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        roundsList = ParseResponses.parseRounds(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler();
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        dialogBuilder.hideLoadingDialog();
                        Toast.makeText(AllSalonsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateDatabaseList(List<Round> rounds) {
        RoundDao roundDao = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao();
        roundDao.removeRounds(roundDao.getRounds());
        roundDao.insertRoundList(rounds);

        ProductImageDao productImageDao = GawlaDataBse.getInstance(AllSalonsActivity.this).productImageDao();
        productImageDao.removeSubImages(productImageDao.getSubImages());
        CardDao cardDao = GawlaDataBse.getInstance(AllSalonsActivity.this).cardDao();
        cardDao.removeCards(cardDao.getCards());

        for (int i = 0; i < rounds.size(); i++)
        {
            productImageDao.insertSubImages(rounds.get(i).getProduct_images());
            cardDao.insertCards(rounds.get(i).getSalon_cards());
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
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        String monthName = new DateFormatSymbols(new Locale(SharedPrefManager.getInstance(AllSalonsActivity.this).getSavedLang())).getMonths()[Integer.parseInt(month) - 1]; // month from num to nam

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK); // day in month to day in week
        String dayOfWeek = new DateFormatSymbols(new Locale(SharedPrefManager.getInstance(AllSalonsActivity.this).getSavedLang())).getWeekdays()[dayWeek]; // day in week from num to nam

        Date date = null;
        try
        {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(sDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return new SalonDate(date, sDate, day, monthName, dayOfWeek, GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getDatesCount(sDate) + getResources().getString(R.string.salons));
    }

    private void initDatesAdapter() {
        filterRecycler.setAdapter(new DateAdapter(AllSalonsActivity.this, dateList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SalonDate salonDate = dateList.get(position);
                roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByDate(String.valueOf(salonDate.getsDate()));

                initSalonsRecycler();
            }
        }));
    }

    private void getCategoriesAndRoundsFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(AllSalonsActivity.this).executeConnectionToServer(MainActivity.mainInstance,
                REQ_GET_ALL_CATEGORIES, new Request<>(REQ_GET_ALL_CATEGORIES, userId, apiToken,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {

                        categoryList = ParseResponses.parseCategories(mainObject);

                        initCategoriesAdapter();

                        try
                        {
                            roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByCategory(categoryList.get(0).getCategoryName());
                        } catch (IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        initSalonsRecycler();
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initSalonsRecycler();
                        dialogBuilder.hideLoadingDialog();
                        Toast.makeText(AllSalonsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initCategoriesAdapter() {
        filterRecycler.setAdapter(new CategoryAdapter(categoryList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Category category = categoryList.get(position);
                roundsList = GawlaDataBse.getInstance(AllSalonsActivity.this).roundDao().getRoundsByCategory(category.getCategoryName());

                initSalonsRecycler();
            }
        }));
    }

    private void initSalonsRecycler() {
        if (roundsList.size() > 0)
        { // !empty ?
            emptyViewLayout.setVisibility(View.GONE);
            salonsRecycler.setVisibility(View.VISIBLE);
            salonsRecycler.setHasFixedSize(true);
            salonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, RecyclerView.HORIZONTAL, false));
            salonsRecycler.setAdapter(new SalonsAdapter(AllSalonsActivity.this, roundsList));

        }
        else
        {  // empty ?
            emptyViewLayout.setVisibility(View.VISIBLE);
            salonsRecycler.setVisibility(View.GONE);
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
                mBottomSheetDialogFilterBy.dismiss();
            }
        });

        sheetView.findViewById(R.id.btn_filter_by_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCategoriesAndRoundsFromServer();
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
