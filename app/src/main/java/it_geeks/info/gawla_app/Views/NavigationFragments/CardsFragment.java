package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Controllers.Adapters.CardsAdapter;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.Views.MainActivity;
import it_geeks.info.gawla_app.Views.NotificationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardsFragment extends Fragment {

    RecyclerView cardsRecycler;
    CardsAdapter cardsAdapter;

    List<Card> cardsList = new ArrayList<>();

    ProgressBar cardsProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        initViews(view);

        checkConnection(view);

        return view;
    }

    private void initViews(View view) {
        cardsProgress = view.findViewById(R.id.cards_progress);
        cardsRecycler = view.findViewById(R.id.cards_recycler);

        // open Notification
        view.findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });
    }

    private void checkConnection(View view) {
        LinearLayout noConnectionLayout = view.findViewById(R.id.no_connection);

        if (Common.Instance(getActivity()).isConnected()) {
            noConnectionLayout.setVisibility(View.GONE);

            getCardsFromServer(view);

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            cardsProgress.setVisibility(View.GONE);
        }
    }

    private void getCardsFromServer(final View view) {
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getContext()).executeConnectionToServer("getCardByUserID", new Request(userId, apiToken), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {

                cardsList = ParseResponses.parseCards(mainObject);

                initCardsRecycler();
            }

            @Override
            public void handleEmptyResponse() {

                initEmptyView(view);

                cardsProgress.setVisibility(View.GONE);
            }
        });
    }

    private void initCardsRecycler() {
        cardsRecycler.setHasFixedSize(true);
        cardsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        cardsAdapter = new CardsAdapter(getContext(), cardsList);
        cardsRecycler.setAdapter(cardsAdapter);

        Common.Instance(getContext()).hideProgress(cardsRecycler, cardsProgress);
    }

    private void initEmptyView(View view) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.cards_empty_view);

        if (cardsList.size() > 0) {
            emptyViewLayout.setVisibility(View.INVISIBLE);
            cardsRecycler.setVisibility(View.VISIBLE);

        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            cardsRecycler.setVisibility(View.INVISIBLE);
        }
    }
}
