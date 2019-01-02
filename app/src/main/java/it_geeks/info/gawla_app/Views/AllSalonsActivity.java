package it_geeks.info.gawla_app.Views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.SalonDate;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Controllers.Adapters.CountrySpinnerAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.DateAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.SalonsAdapter;

public class AllSalonsActivity extends AppCompatActivity implements DateAdapter.OnItemClickListener {

    public static Activity allSalonsActivityInstance;

    AppCompatSpinner countrySpinner;
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

        checkConnection();
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
                startActivity(new Intent(AllSalonsActivity.this,NotificationActivity.class));
            }
        });
    }

    private void checkConnection() {
        LinearLayout noConnectionLayout = findViewById(R.id.no_connection);

        if (Common.Instance(AllSalonsActivity.this).isConnected()) { // connected
            noConnectionLayout.setVisibility(View.GONE);

            getDatesAndRounds();

        } else { // no connection
            noConnectionLayout.setVisibility(View.VISIBLE);
        }
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
        dateRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, 0, false));
        DateAdapter dateAdapter = new DateAdapter(AllSalonsActivity.this, dateList, AllSalonsActivity.this);
        dateRecycler.setAdapter(dateAdapter);
    }

    private void initSalonsRecycler() {
        dateSalonsRecycler = findViewById(R.id.date_salons_recycler);
        dateSalonsRecycler.setHasFixedSize(true);
        dateSalonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, 0, false));
        dateSalonsRecycler.setAdapter(new SalonsAdapter(AllSalonsActivity.this, roundsList));
    }

    private void getCountries() {
        countries = (ArrayList<Country>) GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).countryDao().getCountries();
    }

    private void initCountriesSpinner() {
//        countrySpinner = findViewById(R.id.all_salons_country_spinner);
        CountrySpinnerAdapter countrySpinnerAdapter = new CountrySpinnerAdapter(AllSalonsActivity.this, countries);
        countrySpinner.setAdapter(countrySpinnerAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        SalonDate salonDate = dateList.get(position);
        roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(salonDate.getDate());
        initSalonsRecycler();
    }
}
