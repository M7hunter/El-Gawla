package it_geeks.info.elgawla.views.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it_geeks.info.elgawla.Adapters.MyCardsAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.MyCardModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_MY_CARDS;

public class MyCardsActivity extends AppCompatActivity {

    private RecyclerView myCardsRecycler;
    private TextView myCardsEmptyView;

    private List<MyCardModel> myCardsList = new ArrayList<>();

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);

        initViews();

        getMyCardsFromServer();

        handleEvents();
    }

    private void initViews() {
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
        myCardsRecycler = findViewById(R.id.my_cards_recycler);
        myCardsEmptyView = findViewById(R.id.my_cards_empty_view);
        snackBuilder = new SnackBuilder(findViewById(R.id.my_cards_main_layout));
    }

    private void getMyCardsFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(
                this,
                REQ_GET_MY_CARDS, new Request<>(REQ_GET_MY_CARDS, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        myCardsList = ParseResponses.parseMyCards(mainObject);
                        Collections.reverse(myCardsList);
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        initRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        initRecycler();
                    }
                });
    }

    private void initRecycler() {
        if (myCardsList.size() > 0) {
            myCardsRecycler.setHasFixedSize(true);
            myCardsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            myCardsRecycler.setAdapter(new MyCardsAdapter(this, myCardsList, snackBuilder));

            myCardsEmptyView.setVisibility(View.GONE);
            myCardsRecycler.setVisibility(View.VISIBLE);
        } else {
            myCardsEmptyView.setVisibility(View.VISIBLE);
            myCardsRecycler.setVisibility(View.GONE);
        }
    }

    private void handleEvents() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
