package it_geeks.info.gawla_app.Views;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.SalonDate;
import it_geeks.info.gawla_app.Repositry.Models.Salons;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.Adapters.AllSalonsAdapter;
import it_geeks.info.gawla_app.ViewModels.Adapters.CountrySpinnerAdapter;
import it_geeks.info.gawla_app.ViewModels.Adapters.DateAdapter;

public class AllSalonsActivity extends AppCompatActivity {

    public static Activity allSalonsActivityInstance;

    AppCompatSpinner countrySpinner;
    ArrayList<Country> countries = new ArrayList<>();

    RecyclerView allSalonsRecycler;
    RecyclerView dateRecycler;

    List<Salons> salonsList = new ArrayList<>();
    List<Round> roundsList = new ArrayList<>();
    List<SalonDate> dateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_all_salons);

        allSalonsActivityInstance = this;

        initViews();

        getDates();

        initDatesRecycler();

//        getCountries();

//        initCountriesSpinner();

        getSalonsData();

        initSalonsRecycler();
    }

    private void getDates() {
        List<String> dates = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsDates();

        for (int i = 0; i < dates.size(); i++) {
            String[] dateParts = dates.get(i).split("-");
            String day = dateParts[0];
            String monthS = "";
            String dayOfWeek = "";
            try {
                monthS = String.valueOf(new SimpleDateFormat("MMMM").parse("12-11-2018"));
                Date dayWeek = new SimpleDateFormat("E", Locale.getDefault()).parse("12-11-2018");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dayWeek);
                dayOfWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

            } catch (ParseException e) {
                Toast.makeText(allSalonsActivityInstance, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            dateList.add(new SalonDate(day, monthS, dayOfWeek, "5"));
        }

    }

    private void initDatesRecycler() {
        dateRecycler = findViewById(R.id.date_recycler);
        dateRecycler.setHasFixedSize(true);
        dateRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, 0, false));
        dateRecycler.setAdapter(new DateAdapter(AllSalonsActivity.this, dateList));
    }

    private void initViews() {
        // back
        findViewById(R.id.all_salons_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

    private void getSalonsData() {
        roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRounds();

        // order the list

    }

    private void initSalonsRecycler() {
        allSalonsRecycler = findViewById(R.id.all_salons_recycler);
        allSalonsRecycler.setHasFixedSize(true);
        allSalonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, 1, false));
        AllSalonsAdapter allSalonsAdapter = new AllSalonsAdapter(AllSalonsActivity.this, salonsList);
        allSalonsRecycler.setAdapter(allSalonsAdapter);
    }

    // to change status bar color
    public void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
