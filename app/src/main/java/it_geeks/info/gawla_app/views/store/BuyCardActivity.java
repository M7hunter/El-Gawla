package it_geeks.info.gawla_app.views.store;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.SnackBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.JsonObject;

import static it_geeks.info.gawla_app.util.Constants.FAWRY;
import static it_geeks.info.gawla_app.util.Constants.KNET;
import static it_geeks.info.gawla_app.util.Constants.PAYMENT_URL;
import static it_geeks.info.gawla_app.util.Constants.REQ_ADD_CARDS_TO_USER;

public class BuyCardActivity extends AppCompatActivity {

    private static final String TAG = "Buy_card";

    private RadioButton rbKnet, rbFawry;
    private ImageView ivKnet, ivFawry;
    private TextView tvHeader, tvTotalPrice;
    private Button btnBuy;

    private Card card;

    private String method;

    private DialogBuilder dialogBuilder;

    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_card);

        initViews();

        if (getCardData(savedInstanceState)) {
            bindData();
        }

        handleEvents();
    }

    private void initViews() {
        tvHeader = findViewById(R.id.tv_buy_card_name);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBuy = findViewById(R.id.btn_buy_card_buy);

        initRadioButtons();

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.buy_card_main_layout));
    }

    private boolean getCardData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                card = (Card) extras.getSerializable("card_to_buy");
//                catId = extras.getInt("category_id_to_buy_card");
            }
        } else {
            card = (Card) savedInstanceState.getSerializable("card_to_buy");
//            catId = savedInstanceState.getInt("category_id_to_buy_card");
        }

        return card != null;
    }

    private void bindData() {
        tvHeader.setText(card.getCard_name());
        getTotalPrice();
    }

    private void handleEvents() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCard();
            }
        });
    }

    private String getTotalPrice() {
        // TODO: price should be by category
        String totalPrice = String.valueOf(Float.parseFloat(card.getCard_cost()));
        tvTotalPrice.setText(totalPrice);
        return totalPrice;
    }

    private void initRadioButtons() {
        rbKnet = findViewById(R.id.rb_knet);
        rbFawry = findViewById(R.id.rb_fawry);
        ivKnet = findViewById(R.id.iv_knet);
        ivFawry = findViewById(R.id.iv_fawry);

        method = SharedPrefManager.getInstance(this).getLastMethod();

        switch (method) {
            case KNET:
                checkKnet();
                break;
            case FAWRY:
                checkFawry();
                break;
        }

        CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.rb_knet:
                        if (isChecked) {
                            checkKnet();
                        }

                        break;
                    case R.id.rb_fawry:
                        if (isChecked) {
                            checkFawry();
                        }

                        break;
                }
                Log.d(TAG, method);
            }
        };


        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_knet:
                        if (!rbKnet.isChecked()) {
                            checkKnet();
                        }

                        break;
                    case R.id.iv_fawry:
                        if (!rbFawry.isChecked()) {
                            checkFawry();
                        }

                        break;
                }
                Log.d(TAG, method);
            }
        };

        rbKnet.setOnCheckedChangeListener(checkListener);
        rbFawry.setOnCheckedChangeListener(checkListener);
        ivKnet.setOnClickListener(imageClickListener);
        ivFawry.setOnClickListener(imageClickListener);
    }

    private void checkKnet() {
        rbKnet.setChecked(true);
        rbFawry.setChecked(false);
        cacheLastMethod(KNET);
    }

    private void checkFawry() {
        rbKnet.setChecked(false);
        rbFawry.setChecked(true);
        cacheLastMethod(FAWRY);
    }

    private void cacheLastMethod(String newMethod) {
        method = newMethod;
        SharedPrefManager.getInstance(this).setLastMethod(method);
    }

    private void buyCard() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(BuyCardActivity.this).executeConnectionToServer(BuyCardActivity.this, REQ_ADD_CARDS_TO_USER
                , new Request<>(REQ_ADD_CARDS_TO_USER, SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getUser_id(),
                        SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getApi_token(),
                        card.getCard_id(), method
                        , null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        String url = (mainObject.get("url").getAsString());

                        if (url != null && !url.isEmpty()) {
                            Intent i = new Intent(BuyCardActivity.this, PaymentURLActivity.class);
                            i.putExtra(PAYMENT_URL, url);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }
}
