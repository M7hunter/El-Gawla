package it_geeks.info.gawla_app.views.card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.Adapters.CardListAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.Common;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static it_geeks.info.gawla_app.util.Constants.REQ_ADD_CARDS_TO_USER;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_CATEGORIES;

public class CardActivity extends AppCompatActivity {

    private View vCardIcon;
    private TextSwitcher tsCardName, tsCardDesc;
    private Button btnBuy;
    private RecyclerView cardsListRecycler;

    private Card card, newCard;
    private List<Card> cardList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        getCategoriesFromServer();

        initViews();

        if (getCardData(savedInstanceState))
        {
            cardList.remove(card.getPosition()); // remove first card from bottom list
            bindData(card);
        }

        handleEvents();

        initCardsRecycler();
    }

    private boolean getCardData(Bundle savedInstanceState) {
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
            {
                card = (Card) extras.getSerializable("card");
                cardList.addAll((List<Card>) extras.getSerializable("card_list"));
            }

        }
        else
        {
            card = (Card) savedInstanceState.getSerializable("card");
            cardList.addAll((List<Card>) savedInstanceState.getSerializable("card_list"));
        }

        return card != null;
    }

    private void initViews() {
        vCardIcon = findViewById(R.id.v_card_icon);
        btnBuy = findViewById(R.id.btn_buy_card);
        cardsListRecycler = findViewById(R.id.cards_list_recycler);

        initSwitchers();
    }

    private void initSwitchers() {
        tsCardName = findViewById(R.id.ts_card_name);
        tsCardDesc = findViewById(R.id.ts_card_description);

        tsCardName.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(CardActivity.this);
                tv.setTextSize(18);
                tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.WHITE);
                tv.setText(getString(R.string.activity_empty_hint));
                return tv;
            }
        });

        tsCardDesc.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(CardActivity.this);
                tv.setTextSize(15);
                tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(getResources().getColor(R.color.blueGrey));
                tv.setText(getString(R.string.activity_empty_hint));
                return tv;
            }
        });

        tsCardName.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_bottom_up));
        tsCardDesc.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_bottom_up));
    }

    public void bindData(Card card) {
        Common.Instance().changeDrawableViewColor(vCardIcon, card.getCard_color());
        tsCardName.setText(card.getCard_name());
        tsCardDesc.setText(card.getCard_details());
    }

    private void handleEvents() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // buy card
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CardActivity.this, BuyCardActivity.class);
                i.putExtra("card_to_buy", card);
                i.putExtra("salon_id_to_buy_card", 190);
                i.putExtra("categories_to_buy_card", (Serializable) categoryList);
                startActivity(i);
            }
        });
    }

    private void initCardsRecycler() {
        cardsListRecycler.setHasFixedSize(true);
        cardsListRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cardsListRecycler.setAdapter(new CardListAdapter(this, cardList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                newCard = cardList.get(position); // get new card from the list
                bindData(newCard); // bind the new data
                cardList.set(position, card); // swap cards
                card = newCard; // after swapping act normal
            }
        }));
    }

    public void getCategoriesFromServer() {
        RetrofitClient.getInstance(CardActivity.this).executeConnectionToServer(CardActivity.this,
                REQ_GET_ALL_CATEGORIES, new Request<>(REQ_GET_ALL_CATEGORIES, SharedPrefManager.getInstance(CardActivity.this).getUser().getUser_id(), SharedPrefManager.getInstance(CardActivity.this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(CardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buyCard(final CardView btnConfirmBuying, final ProgressBar pbBuyCard) {
        hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
        int user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(this).getUser().getApi_token();

        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_ADD_CARDS_TO_USER, new Request<>(REQ_ADD_CARDS_TO_USER, user_id, api_token, card.getCard_id(),
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(CardActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleAfterResponse() {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                        Toast.makeText(CardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayConfirmationBtn(CardView btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.VISIBLE);
        pbBuyCard.setVisibility(View.GONE);
    }

    private void hideConfirmationBtn(CardView btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.GONE);
        pbBuyCard.setVisibility(View.VISIBLE);
    }
}
