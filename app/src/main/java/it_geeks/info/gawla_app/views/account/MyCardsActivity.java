package it_geeks.info.gawla_app.views.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.MyCardsAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.MyCardModel;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.DialogBuilder;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_MY_CARDS;

public class MyCardsActivity extends AppCompatActivity {

    private RecyclerView myCardsRecycler;
    private TextView myCardsEmptyView;

    private List<MyCardModel> myCardsList = new ArrayList<>();

    public DialogBuilder dialogBuilder;

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
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        initRecycler();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        initRecycler();
                    }
                });
    }

    private void initRecycler() {
        if (myCardsList.size() > 0)
        {
            myCardsRecycler.setHasFixedSize(true);
            myCardsRecycler.setAdapter(new MyCardsAdapter(this, myCardsList));
            myCardsEmptyView.setVisibility(View.GONE);
            myCardsRecycler.setVisibility(View.VISIBLE);
        }
        else
        {
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
//        findViewById(R.id.btn_cards_store).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MyCardsActivity.this, ));
//            }
//        });

    }
}
