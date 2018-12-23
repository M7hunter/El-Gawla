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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.Salons;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.Adapters.AllSalonsAdapter;
import it_geeks.info.gawla_app.ViewModels.Adapters.CountrySpinnerAdapter;

public class AllSalonsActivity extends AppCompatActivity {

    public static Activity allSalonsActivityInstance;

    AppCompatSpinner countrySpinner;
    ArrayList<Country> countries = new ArrayList<>();

    RecyclerView allSalonsRecycler;

    List<Salons> salonsList = new ArrayList<>();
    List<Round> roundsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_all_salons);

        allSalonsActivityInstance = this;

        initViews();

        getCountries();

        initCountriesSpinner();

        getSalonsData();

        initSalonsRecycler();
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
        countrySpinner = findViewById(R.id.all_salons_country_spinner);
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
        Salons salons = null;
        for (Round round : roundsList) {
            if (salons == null) {
                List<Round> rounds = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(round.getRound_date());
                salons = new Salons(round.getRound_date(), rounds);
                salonsList.add(salons);

            } else { // salons != null
                if (round.getRound_date().equals(salons.getHeader())) { // already added
                    return;

                } else { // !added
                    List<Round> rounds = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).roundDao().getRoundsByDate(round.getRound_date());
                    salons = new Salons(round.getRound_date(), rounds);
                    salonsList.add(salons);
                }

            }
        }

        // order the list
        Collections.sort(salonsList, new Comparator<Salons>() {
            @Override
            public int compare(Salons o1, Salons o2) {
                return o1.getHeader().compareTo(o2.getHeader());
            }
        });
    }

    private void initSalonsRecycler() {
        allSalonsRecycler = findViewById(R.id.all_salons_recycler);
        allSalonsRecycler.setHasFixedSize(true);
        allSalonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, 1, false));
        AllSalonsAdapter allSalonsAdapter = new AllSalonsAdapter(AllSalonsActivity.this, salonsList);
        allSalonsRecycler.setAdapter(allSalonsAdapter);
    }

    // to change status bar color
    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
