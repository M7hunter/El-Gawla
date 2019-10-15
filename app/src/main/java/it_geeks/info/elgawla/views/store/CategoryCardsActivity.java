package it_geeks.info.elgawla.views.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.Adapters.CardListAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.SnackBuilder;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static it_geeks.info.elgawla.util.Constants.CATEGORY_NAME;
import static it_geeks.info.elgawla.util.Constants.CAT_ID;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_CARDS_BY_CATEGORY;

public class CategoryCardsActivity extends AppCompatActivity {

    private View vCardIcon, cardListLayout, selectedCardLayout, pb, tvEmptyView;
    private TextSwitcher tsCardName, tsCardDesc;
    private TextView tvCategoryName;
    private Button btnBuy;
    private RecyclerView cardsListRecycler;

    private Card card, newCard;
    private List<Card> cardList = new ArrayList<>();

    private SnackBuilder snackBuilder;
    int catId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_cards);

        initViews();

        handleEvents();

        getCardData();
    }

    private void initViews() {
        vCardIcon = findViewById(R.id.v_card_icon);
        tvCategoryName = findViewById(R.id.tv_category_cards_title);
        btnBuy = findViewById(R.id.btn_buy_card);
        cardsListRecycler = findViewById(R.id.cards_list_recycler);
        selectedCardLayout = findViewById(R.id.selected_card_layout);
        cardListLayout = findViewById(R.id.cards_list_layout);
        pb = findViewById(R.id.pb_category_cards);
        tvEmptyView = findViewById(R.id.tv_category_cards_empty_view);

        snackBuilder = new SnackBuilder(findViewById(R.id.card_main_layout));

        initSwitchers();
    }

    private void initSwitchers() {
        tsCardName = findViewById(R.id.ts_card_name);
        tsCardDesc = findViewById(R.id.ts_card_description);

        tsCardName.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(CategoryCardsActivity.this);
                tv.setTextSize(15);
                tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(getResources().getColor(R.color.blueGrey));
                tv.setTypeface(Typeface.DEFAULT_BOLD);
                tv.setText(getString(R.string.no_content));
                return tv;
            }
        });

        tsCardDesc.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(CategoryCardsActivity.this);
                tv.setTextSize(12);
                tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(getResources().getColor(R.color.blueGrey));
                return tv;
            }
        });

        tsCardName.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_bottom_up));
        tsCardDesc.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_bottom_up));
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
                Intent i = new Intent(CategoryCardsActivity.this, PaymentMethodsActivity.class);
                i.putExtra("card_to_buy", card);
                i.putExtra("is_card", true);
                startActivity(i);
            }
        });
    }

    private void getCardData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            tvCategoryName.setText(extras.getString(CATEGORY_NAME));
            catId = extras.getInt(CAT_ID);
            getCategoryCardsFromServer(catId);
        }
    }

    private void getCategoryCardsFromServer(int catId) {
        RetrofitClient.getInstance(CategoryCardsActivity.this).executeConnectionToServer(CategoryCardsActivity.this,
                REQ_GET_CARDS_BY_CATEGORY, new Request<>(REQ_GET_CARDS_BY_CATEGORY, SharedPrefManager.getInstance(CategoryCardsActivity.this).getUser().getUser_id(), SharedPrefManager.getInstance(CategoryCardsActivity.this).getUser().getApi_token(),
                        catId, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cardList = ParseResponses.parseCards(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {
                        initCards();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        initCards();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void initCards() {
        if (cardList.size() > 0)
        { // have cards
            tvEmptyView.setVisibility(View.GONE);
            selectedCardLayout.setVisibility(View.VISIBLE);

            card = cardList.get(0); // get first card's data as main card
            bindData(card);

            cardList.remove(0); // remove the main card from the list to display only the others at the bottom
            if (cardList.size() > 0)
            { // still have cards after removing the main one
                cardListLayout.setVisibility(View.VISIBLE);
                initCardsRecycler();
            }
        }
        else
        { // !have cards
            cardListLayout.setVisibility(View.GONE);
            selectedCardLayout.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        }

        if (pb.getVisibility() == View.VISIBLE)
        {
            pb.setVisibility(View.GONE);
        }
    }

    private void initCardsRecycler() {
        cardsListRecycler.setHasFixedSize(true);
        cardsListRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cardsListRecycler.setAdapter(new CardListAdapter(cardList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                newCard = cardList.get(position); // get new card from the list
                bindData(newCard); // bind the new data
                cardList.set(position, card); // swap cards
                card = newCard; // after swapping act normal
            }
        }));
    }

    /**
     * this fun is to display main/selected {@param card}'s data to the user
     */
    public void bindData(Card card) {
        Common.Instance().changeDrawableViewColor(vCardIcon, card.getCard_color());
        tsCardName.setText(card.getCard_name());
        tsCardDesc.setText(card.getCard_details());
    }
}
