package it_geeks.info.gawla_app.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.Salons;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.ViewModels.Adapters.AllSalonsAdapter;

public class AllSalonsActivity extends AppCompatActivity {

    RecyclerView allSalonsRecycler;

    List<Salons> salonsList = new ArrayList<>();
    List<Round> roundsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_salons);

        findViewById(R.id.all_salons_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getData();

        initRecycler();
    }

    private void getData() {
        roundsList = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).RoundDao().getRounds();
        List<Round> rounds = new ArrayList<>();
        Salons salons = null;
        for (Round round : roundsList) {
            if (salons != null){
                for (int i = 0; i < salons.getRounds().size(); i++) {
                    if (round.getRound_date().equals(salons.getHeader())){
                        return;
                    }
                    rounds = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).RoundDao().getRoundsByDate(round.getRound_date());
                    salons = new Salons(round.getRound_date(), rounds);
                    salonsList.add(salons);
                }

            } else { // salons == null
                rounds = GawlaDataBse.getGawlaDatabase(AllSalonsActivity.this).RoundDao().getRoundsByDate(round.getRound_date());
                salons = new Salons(round.getRound_date(), rounds);
                salonsList.add(salons);
            }
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
