package it_geeks.info.elgawla.views.account;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
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

    private RecyclerView rvMyCards;
    private TextView myCardsEmptyView;
    private ProgressBar pbpMyCards;

    private List<MyCardModel> myCardsList = new ArrayList<>();

    public DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;
    private int page = 1, last_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cards);

        initViews();

        getFirstMyCardsFromServer();

        handleEvents();
    }

    private void initViews() {
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
        rvMyCards = findViewById(R.id.my_cards_recycler);
        myCardsEmptyView = findViewById(R.id.my_cards_empty_view);
        pbpMyCards = findViewById(R.id.pbp_my_cards);

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

    private void getFirstMyCardsFromServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).fetchDataPerPageFromServer(
                this,
                new Data(REQ_GET_MY_CARDS, 1), new RequestModel<>(REQ_GET_MY_CARDS, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        myCardsList = ParseResponses.parseMyCards(mainObject);

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
        onLoadMoreMyCards();
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
                            rvMyCards.getAdapter().notifyItemInserted(i);
                        }

                        rvMyCards.smoothScrollToPosition(nextFirstPosition);
                        addScrollListener();
                    }

                    @Override
                    public void handleAfterResponse() {
                        pbpMyCards.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        pbpMyCards.setVisibility(View.GONE);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void onLoadMoreMyCards() {
        pbpMyCards.setVisibility(View.VISIBLE);
        rvMyCards.scrollToPosition(myCardsList.size() - 1);
    }

    private void initRecycler() {
        if (!myCardsList.isEmpty())
        {
            myCardsEmptyView.setVisibility(View.GONE);
            rvMyCards.setVisibility(View.VISIBLE);

            rvMyCards.setHasFixedSize(true);
            rvMyCards.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            rvMyCards.setAdapter(new MyCardsAdapter(this, myCardsList, snackBuilder));

            addScrollListener();
        }
        else
        {
            myCardsEmptyView.setVisibility(View.VISIBLE);
            rvMyCards.setVisibility(View.GONE);
        }
    }

    private void addScrollListener() {
        if (page < last_page)
        {
            rvMyCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (((LinearLayoutManager) rvMyCards.getLayoutManager()).findLastCompletelyVisibleItemPosition() == rvMyCards.getAdapter().getItemCount() - 1)
                    {
                        getNextMyCardsFromServer();
                        rvMyCards.removeOnScrollListener(this);
                    }
                }
            });
        }
    }
}
