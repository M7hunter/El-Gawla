package it_geeks.info.gawla_app.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.ConnectionInterface;
import it_geeks.info.gawla_app.General.OnItemClickListener;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.SalonDate;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Controllers.Adapters.DateAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.SalonsAdapter;

public class AllSalonsActivity extends AppCompatActivity implements OnItemClickListener {

    public static Activity allSalonsActivityInstance;

    ArrayList<Country> countries = new ArrayList<>();

    RecyclerView dateSalonsRecycler;
    RecyclerView dateRecycler;

    List<Round> roundsList = new ArrayList<>();
    List<SalonDate> dateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        allSalonsActivityInstance = this;

        initViews();

        Common.Instance(AllSalonsActivity.this).ApplyOnConnection(AllSalonsActivity.this, new ConnectionInterface() {
            @Override
            public void onConnected() {
                getDatesAndRounds();
            }
        });
    }

    private void initViews() {
        // back
        findViewById(R.id.all_salons_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // notification icon
        findViewById(R.id.all_salon_notification_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllSalonsActivity.this, NotificationActivity.class));
            }
        });
    }

    private void getDatesAndRounds() {
        List<String> dates = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsDates();

        for (String date : dates) {
            dateList.add(transformDateToNames(date));
        }

        Common.Instance(AllSalonsActivity.this).sortList(dateList);

        initDatesRecycler();

        try {
            roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(dateList.get(0).getDate());
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        initSalonsRecycler();

        initSalonsEmptyView(roundsList);
    }

    private void initSalonsEmptyView(List<Round> roundList) {
        LinearLayout emptyViewLayout = findViewById(R.id.all_salons_empty_view);

        if (roundList.size() > 0) { // !empty ?
            emptyViewLayout.setVisibility(View.GONE);

        } else {  // empty ?
            emptyViewLayout.setVisibility(View.VISIBLE);
        }
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

    private void initDatesRecycler() {
        dateRecycler = findViewById(R.id.date_recycler);
        dateRecycler.setHasFixedSize(true);
        dateRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, RecyclerView.HORIZONTAL, false));
        dateRecycler.setAdapter(new DateAdapter(AllSalonsActivity.this, dateList, AllSalonsActivity.this));
    }

    private void initSalonsRecycler() {
        dateSalonsRecycler = findViewById(R.id.date_salons_recycler);
        dateSalonsRecycler.setHasFixedSize(true);
        dateSalonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, RecyclerView.HORIZONTAL, false));
        dateSalonsRecycler.setAdapter(new SalonsAdapter(AllSalonsActivity.this, roundsList));
    }

    @Override
    public void onItemClick(View view, int position) {
        SalonDate salonDate = dateList.get(position);
        roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(salonDate.getDate());

        initSalonsRecycler();

        initSalonsEmptyView(roundsList);
    }

    private void getCountries() {
        countries = (ArrayList<Country>) GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).countryDao().getCountries();
    }
}
