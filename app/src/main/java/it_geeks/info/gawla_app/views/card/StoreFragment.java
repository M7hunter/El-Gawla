package it_geeks.info.gawla_app.views.card;

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

import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Adapters.CardsAdapter;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.NotificationStatus;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.account.AccountDetailsActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_CARDS;

public class StoreFragment extends Fragment {

    private RecyclerView cardsRecycler;

    private List<Card> cardsList = new ArrayList<>();

    private ProgressBar cardsProgress;

    private ImageView imgNotification;

    private TextView tvCardsStoreHeader, tvCardsStoreEmptyHint; // <- trans

    private View fragmentView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_store, container, false);

        initViews(fragmentView);

        setupTrans();

        handleEvents();

        checkConnection(fragmentView);

        return fragmentView;
    }

    private void initViews(View view) {
        cardsProgress = view.findViewById(R.id.cards_progress);
        cardsRecycler = view.findViewById(R.id.cards_recycler);

        //Notification icon
        imgNotification = view.findViewById(R.id.iv_notification_bell);
        // load user image
        ImageLoader.getInstance().loadUserImage(MainActivity.mainInstance, ((ImageView) fragmentView.findViewById(R.id.iv_user_image)));

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
        NotificationStatus.notificationStatus(getContext(), imgNotification);

        // notification
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NotificationActivity.class));
            }
        });

        fragmentView.findViewById(R.id.iv_user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.mainInstance, AccountDetailsActivity.class));
            }
        });
    }

    private void checkConnection(View view) {
        LinearLayout noConnectionLayout = view.findViewById(R.id.no_connection);

        if (Common.Instance().isConnected(getContext()))
        {
            noConnectionLayout.setVisibility(View.GONE);

            getCardsFromServer(view);

        }
        else
        {
            noConnectionLayout.setVisibility(View.VISIBLE);
            cardsProgress.setVisibility(View.GONE);
        }
    }

    private void getCardsFromServer(final View view) {
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance().removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getContext()).executeConnectionToServer(MainActivity.mainInstance,
                REQ_GET_ALL_CARDS, new Request<>(REQ_GET_ALL_CARDS, userId, apiToken, null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cardsList = ParseResponses.parseCards(mainObject);

                        for (int i = 0; i < cardsList.size(); i++) {
                            cardsList.get(i).setPosition(i);
                        }

                        initCardsRecycler();
                    }

                    @Override
                    public void handleAfterResponse() {
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

        Common.Instance().hideProgress(cardsRecycler, cardsProgress);
    }

    private void initEmptyView(View view) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.cards_empty_view);

        cardsProgress.setVisibility(View.GONE);

        if (cardsList.size() > 0)
        {
            emptyViewLayout.setVisibility(View.INVISIBLE);
            cardsRecycler.setVisibility(View.VISIBLE);

        }
        else
        {
            emptyViewLayout.setVisibility(View.VISIBLE);
            cardsRecycler.setVisibility(View.INVISIBLE);
        }
    }
}
