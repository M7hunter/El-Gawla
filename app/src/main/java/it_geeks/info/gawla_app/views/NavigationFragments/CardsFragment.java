package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Controllers.Adapters.CardsAdapter;
import it_geeks.info.gawla_app.general.NotificationStatus;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class CardsFragment extends Fragment {

    private RecyclerView cardsRecycler;

    private List<Card> cardsList = new ArrayList<>();

    private ProgressBar cardsProgress;

    private View view = null;

    ImageView imgNotification;

    private TextView tvCardsStoreHeader, tvCardsStoreEmptyHint; // <- trans

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cards, container, false);

        initViews(view);

        setupTrans();

        handleEvents();

        getCardsFromServer(view);

//        checkConnection(view);

        return view;
    }

    private void initViews(View view) {
        cardsProgress = view.findViewById(R.id.cards_progress);
        cardsRecycler = view.findViewById(R.id.cards_recycler);

        //Notification icon
        imgNotification = view.findViewById(R.id.Notification);

        // translatable views
        tvCardsStoreHeader = view.findViewById(R.id.tv_cards_store_header);
        tvCardsStoreEmptyHint = view.findViewById(R.id.tv_cards_empty_hint);
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(getContext());
        transHolder.getCardStoreFragmentTranses(getContext());

        tvCardsStoreHeader.setText(transHolder.cards_store);
        tvCardsStoreEmptyHint.setText(transHolder.cards_empty_hint);
    }

    private void handleEvents() {
        // notification status LiveData
        new NotificationStatus().LiveStatus(getContext(),imgNotification);

        // notofocation onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
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


        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            cardsProgress.setVisibility(View.GONE);
        }
    }

    private void getCardsFromServer(final View view) {
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getContext()).executeConnectionToServer(MainActivity.mainInstance,
                "getAllCards", new Request(userId, apiToken), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cardsList = ParseResponses.parseCards(mainObject);

                        for (int i = 0; i < cardsList.size(); i++) {
                            cardsList.get(i).setPosition(i);
                        }

                        initCardsRecycler();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        initEmptyView(view);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initEmptyView(view);
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initCardsRecycler() {
        cardsRecycler.setHasFixedSize(true);
        cardsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        cardsRecycler.setAdapter(new CardsAdapter(getContext(), cardsList));

        Common.Instance(getContext()).hideProgress(cardsRecycler, cardsProgress);
    }

    private void initEmptyView(View view) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.cards_empty_view);

        cardsProgress.setVisibility(View.GONE);

        if (cardsList.size() > 0) {
            emptyViewLayout.setVisibility(View.INVISIBLE);
            cardsRecycler.setVisibility(View.VISIBLE);

        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            cardsRecycler.setVisibility(View.INVISIBLE);
        }
    }
}
