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
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.ViewModels.Adapters.CardsAdapter;
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
                startActivity(new Intent(getContext(),NotificationActivity.class));
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

        RequestMainBody requestMainBody = new RequestMainBody(new Data("getCardByUserID"), new Request(userId, apiToken));
        RetrofitClient.getInstance(getContext()).getAPI().request(requestMainBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject mainObj = response.body().getAsJsonObject();
                    boolean status = mainObj.get("status").getAsBoolean();

                    if (status) { // no errors

                        handleServerResponse(mainObj);

                        initCardsRecycler();

                    } else { // errors from server
                        if (handleServerErrors(mainObj).equals("you are not logged in.")) {
                            startActivity(new Intent(getContext(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            SharedPrefManager.getInstance(getActivity()).clearUser();
                        }

                        Toast.makeText(getActivity(), handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) { // errors of response body
                    Toast.makeText(MainActivity.mainInstance, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                cardsProgress.setVisibility(View.GONE);
                initEmptyView(view);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // errors of connection
                cardsProgress.setVisibility(View.GONE);
                initEmptyView(view);
                Toast.makeText(MainActivity.mainInstance, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleServerResponse(JsonObject object) {
        JsonArray dataArray = object.get("data").getAsJsonArray();

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            int card_id = cardObj.get("card_id").getAsInt();
            String card_name = cardObj.get("card_name").getAsString();
            String card_details = cardObj.get("card_details").getAsString();
            String card_type = cardObj.get("card_type").getAsString();
            String card_color = cardObj.get("card_color").getAsString();
            String card_cost = cardObj.get("card_cost").getAsString();
            int count = cardObj.get("count").getAsInt();

            cardsList.add(
                    new Card(card_name, card_details, card_type, card_color, card_cost, count));
        }
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
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
