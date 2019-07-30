package it_geeks.info.gawla_app.views.card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.Adapters.CardListAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.SnackBuilder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static it_geeks.info.gawla_app.util.Constants.CATEGORY_NAME;
import static it_geeks.info.gawla_app.util.Constants.CAT_ID;
import static it_geeks.info.gawla_app.util.Constants.REQ_ADD_CARDS_TO_USER;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_CARDS_BY_CATEGORY;

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
                Intent i = new Intent(CategoryCardsActivity.this, BuyCardActivity.class);
                i.putExtra("card_to_buy", card);
                i.putExtra("category_id_to_buy_card", catId);
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
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
                    }
                });
    }

    private void initCards() {
        if (cardList.size() > 0)
        {
            tvEmptyView.setVisibility(View.GONE);
            selectedCardLayout.setVisibility(View.VISIBLE);
            cardListLayout.setVisibility(View.VISIBLE);

            card = cardList.get(0);
            cardList.remove(0);

            bindData(card);
            initCardsRecycler();
        }
        else
        {
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
                cardList.set(position, card); // swap store
                card = newCard; // after swapping act normal
            }
        }));
    }

    public void bindData(Card card) {
        Common.Instance().changeDrawableViewColor(vCardIcon, card.getCard_color());
        tsCardName.setText(card.getCard_name());
        tsCardDesc.setText(card.getCard_details());
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
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnackbar();
                    }

                    @Override
                    public void handleAfterResponse() {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
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
