package it_geeks.info.gawla_app.views.NavigationFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Controllers.Adapters.CategoryAdapter;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.OnItemClickListener;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Category;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Controllers.Adapters.CardsAdapter;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;

public class CardsFragment extends Fragment implements OnItemClickListener {

    RecyclerView categoriesRecycler;
    RecyclerView cardsRecycler;

    List<Category> categoryList = new ArrayList<>();
    List<Card> cardsList = new ArrayList<>();

    ProgressBar cardsProgress;

    View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cards, container, false);

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

            getCategoriesFromServer(view);

        } else {
            noConnectionLayout.setVisibility(View.VISIBLE);
            cardsProgress.setVisibility(View.GONE);
        }
    }

    private void getCategoriesFromServer(final View view) {
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
        String apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());

        RetrofitClient.getInstance(getContext()).executeConnectionToServer(MainActivity.mainInstance,
                "getAllCardsCategories", new Request(userId, apiToken), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {

                categoryList = ParseResponses.parseCategories(mainObject);

                initCategoriesRecycler(view);

                getCardsByCategory(categoryList.get(0).getCategoryCards());
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

    private void initCategoriesRecycler(final View view) {
        categoriesRecycler = view.findViewById(R.id.cards_categories_recycler);
        categoriesRecycler.setHasFixedSize(true);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        categoriesRecycler.setAdapter(new CategoryAdapter(getContext(), categoryList, this));
    }

    @Override
    public void onItemClick(View v, int position) {
        cardsProgress.setVisibility(View.VISIBLE);
        Category category = categoryList.get(position);

        // get cards
        getCardsByCategory(category.getCategoryCards());
    }

    private void getCardsByCategory(List<Card> cards) {

                cardsList = cards;

                initCardsRecycler();

                initEmptyView(view);

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
