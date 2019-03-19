package it_geeks.info.gawla_app.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Adapters.CardListAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.NotificationStatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    public static Activity cardInstance;

    public TextView cardPrice;

    private View vCardIcon;
    private TextView tvCardName, tvCardDesc;
    private Button btnBuy;
    private RecyclerView cardsListRecycler;

    ImageView imgNotification;

    private Card card;
    private List<Card> cardList = new ArrayList<>();

    private BottomSheetDialog mBottomSheetDialogCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        cardInstance = this;

        getCardData(savedInstanceState);

        initViews();

        bindData();

        handleEvents();

        initCardsRecycler();

        initBottomSheetCategory();
    }

    private void getCardData(Bundle savedInstanceState) {
        card = new Card();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                card = (Card) extras.getSerializable("card");
                cardList.addAll((List<Card>) extras.getSerializable("card_list"));

                cardList.remove(card.getPosition());
            }

        } else {
            card = (Card) savedInstanceState.getSerializable("card");
            cardList = (List<Card>) savedInstanceState.getSerializable("card_list");
        }
    }

    private void initViews() {
        vCardIcon = findViewById(R.id.v_card_icon);
        tvCardName = findViewById(R.id.tv_card_name);
        tvCardDesc = findViewById(R.id.tv_card_description);
        btnBuy = findViewById(R.id.btn_buy_card);
        cardsListRecycler = findViewById(R.id.cards_list_recycler);
    }

    private void bindData() {
        Common.Instance(this).changeDrawableViewColor(vCardIcon, card.getCard_color());
        tvCardName.setText(card.getCard_name());
        tvCardDesc.setText(card.getCard_details());
    }

    private void handleEvents() {

        //Notification icon
        imgNotification = findViewById(R.id.Notification);

        // notification status LiveData
        NotificationStatus.notificationStatus(this,imgNotification);

        // notofocation onClick
        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CardActivity.this, NotificationActivity.class));
            }
        });

        // back
        findViewById(R.id.card_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialogCategory.show();
            }
        });
    }

    private void initCardsRecycler() {
        cardsListRecycler.setHasFixedSize(true);
        cardsListRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cardsListRecycler.setAdapter(new CardListAdapter(this, cardList));
    }

    private void initBottomSheetCategory() {
        mBottomSheetDialogCategory = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        final View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_category, null);

        //init bottom sheet views
//        RecyclerView categoryRecycler;
        final CardView btnConfirmBuying;
        final ProgressBar pbBuyCard;

//        categoryRecycler = sheetView.findViewById(R.id.categories_recycler_category);
        cardPrice = sheetView.findViewById(R.id.card_cost_category);
        btnConfirmBuying = sheetView.findViewById(R.id.btn_confirm_buying_card_category);
        pbBuyCard = sheetView.findViewById(R.id.pb_buy_card_category);

//        initCategoryRecycler(categoryRecycler);

        cardPrice.setText(card.getCard_cost());

        btnConfirmBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
                buyCard(btnConfirmBuying, pbBuyCard);
            }
        });

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogCategory.isShowing()) {
                    mBottomSheetDialogCategory.dismiss();

                } else {
                    mBottomSheetDialogCategory.show();
                }
            }
        });

        mBottomSheetDialogCategory.setContentView(sheetView);
        Common.Instance(this).setBottomSheetHeight(sheetView);
        mBottomSheetDialogCategory.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    //TODO: next sprint
//    private void initCategoryRecycler(RecyclerView categoryRecycler) {
//        List<Category> categories = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            categories.add(new Category(i, "cat " + i, "#000000"));
//        }
//
//        categoryRecycler.setHasFixedSize(true);
//        categoryRecycler.setLayoutManager(new LinearLayoutManager(CardActivity.this, RecyclerView.VERTICAL, false));
//        categoryRecycler.setAdapter(new CategoryCardAdapter(this, categories));
//    }

    private void buyCard(final CardView btnConfirmBuying, final ProgressBar pbBuyCard) {
        int user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(this).getUser().getApi_token();

        RetrofitClient.getInstance(this).executeConnectionToServer(this, "addCardsToUser", new Request(user_id, api_token, card.getCard_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(CardActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
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
