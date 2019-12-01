package it_geeks.info.elgawla.views.account;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it_geeks.info.elgawla.Adapters.MyCardsAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.repository.Models.MyCardModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_GET_MY_CARDS;

public class MyCardsActivity extends BaseActivity {

    private RecyclerView myCardsRecycler;
    private TextView myCardsEmptyView;

    private List<MyCardModel> myCardsList = new ArrayList<>();

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;
    private int page = 1, last_page = 1;

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

    private void handleEvents() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getMyCardsFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).fetchDataPerPageFromServer(
                this,
                new Data(REQ_GET_MY_CARDS, 1), new RequestModel<>(REQ_GET_MY_CARDS, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        myCardsList = ParseResponses.parseMyCards(mainObject);
//                        Collections.reverse(myCardsList);

                        last_page = mainObject.get("last_page").getAsInt();
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

    private void getNextMyCardsFromServer() {
        RetrofitClient.getInstance(this).fetchDataPerPageFromServer(
                this,
                new Data(REQ_GET_MY_CARDS, ++page), new RequestModel<>(REQ_GET_MY_CARDS, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        int nextFirstPosition = myCardsList.size();
                        myCardsList.addAll(ParseResponses.parseMyCards(mainObject));
                        for (int i = nextFirstPosition; i < myCardsList.size(); i++)
                        {
                            myCardsRecycler.getAdapter().notifyItemInserted(i);
                        }

                        myCardsRecycler.smoothScrollToPosition(nextFirstPosition);
                        addScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initRecycler() {
        if (!myCardsList.isEmpty())
        {
            myCardsEmptyView.setVisibility(View.GONE);
            myCardsRecycler.setVisibility(View.VISIBLE);

            myCardsRecycler.setHasFixedSize(true);
            myCardsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            myCardsRecycler.setAdapter(new MyCardsAdapter(this, myCardsList, snackBuilder));

            addScrollListener();
        }
        else
        {
            myCardsEmptyView.setVisibility(View.VISIBLE);
            myCardsRecycler.setVisibility(View.GONE);
        }
    }

    private void addScrollListener() {
        if (page < last_page)
        {
            myCardsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (((LinearLayoutManager) myCardsRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition() == myCardsRecycler.getAdapter().getItemCount() - 1)
                    {
                        getNextMyCardsFromServer();
                        Toast.makeText(MyCardsActivity.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                        myCardsRecycler.removeOnScrollListener(this);
                    }
                }
            });
        }
    }
}
