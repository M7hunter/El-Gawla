package it_geeks.info.gawla_app.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Salons;
import it_geeks.info.gawla_app.ViewModels.Adapters.AllSalonsAdapter;

public class AllSalonsActivity extends AppCompatActivity {

    RecyclerView allSalonsRecycler;

    List<Salons> salonsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        getData();

        initRecycler();
    }

    private void getData() {
        for (int i = 0; i < 3; i++) {
//            Salons salons = new Salons("today", );
        }
    }

    private void initRecycler() {
        allSalonsRecycler = findViewById(R.id.all_salons_recycler);
        allSalonsRecycler.setHasFixedSize(true);
        allSalonsRecycler.setLayoutManager(new LinearLayoutManager(AllSalonsActivity.this, 1, false));
        AllSalonsAdapter allSalonsAdapter = new AllSalonsAdapter(AllSalonsActivity.this, salonsList);
        allSalonsRecycler.setAdapter(allSalonsAdapter);
    }
}
