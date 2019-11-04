package it_geeks.info.elgawla.views.store;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.Models.Package;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;

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

import static it_geeks.info.elgawla.util.Constants.FAWRY;
import static it_geeks.info.elgawla.util.Constants.KNET;
import static it_geeks.info.elgawla.util.Constants.PACKAGE;
import static it_geeks.info.elgawla.util.Constants.PAYMENT_URL;
import static it_geeks.info.elgawla.util.Constants.REQ_ADD_CARDS_TO_USER;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_MEMBERSHIP;

public class PaymentMethodsActivity extends AppCompatActivity {

    private RadioButton rbKnet, rbFawry;
    private ImageView ivKnet, ivFawry;
    private Button btnContinue;
    private TextView tvHeader, tvTotalPrice;
    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private Card card;
    private Package mPackage;
    private String method;
    private boolean isCard = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        getData(savedInstanceState);

        initViews();

        bindData();

        initMethods();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (method != null && !method.isEmpty()) {
                    if (isCard) {
                        if (card != null) {
                            buyCard();
                        }
                    } else if (mPackage != null) {
                        updateMembership();
                    }
                }
            }
        });
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                isCard = extras.getBoolean("is_card");

                if (isCard) {
                    card = (Card) extras.getSerializable("card_to_buy");
                } else {
                    mPackage = (Package) extras.getSerializable(PACKAGE);
                }
            }
        } else {
            isCard = savedInstanceState.getBoolean("is_card");

            if (isCard) {
                card = (Card) savedInstanceState.getSerializable("card_to_buy");
            } else {
                mPackage = (Package) savedInstanceState.getSerializable(PACKAGE);
            }
        }

        Log.d("isCard", isCard + "");
    }

    private void bindData() {
        tvHeader.setText(isCard ? card.getCard_name() : mPackage.getTitle());
        tvTotalPrice.setText(String.valueOf(Float.parseFloat(isCard ? card.getCard_cost() : mPackage.getPrice())));
    }

    private void initViews() {
        btnContinue = findViewById(R.id.btn_payment_method_continue);
        tvHeader = findViewById(R.id.tv_payment_method_header);
        tvTotalPrice = findViewById(R.id.tv_payment_method_price);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.payment_method_main_layout));

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initMethods() {
        rbKnet = findViewById(R.id.rb_knet);
        rbFawry = findViewById(R.id.rb_fawry);
        ivKnet = findViewById(R.id.iv_knet);
        ivFawry = findViewById(R.id.iv_fawry);

        // initialization
        switch (SharedPrefManager.getInstance(this).getLastMethod()) {
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
        SharedPrefManager.getInstance(this).setLastMethod(newMethod);
    }

    private void updateMembership() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_SET_MEMBERSHIP, new RequestModel<>(REQ_SET_MEMBERSHIP, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(), mPackage.getId(), method
                        , null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        String url = (mainObject.get("url").getAsString());

                        if (url != null && !url.isEmpty()) {
                            Intent i = new Intent(PaymentMethodsActivity.this, PaymentURLActivity.class);
                            i.putExtra(PAYMENT_URL, url);
                            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
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

    private void buyCard() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(PaymentMethodsActivity.this).executeConnectionToServer(PaymentMethodsActivity.this, REQ_ADD_CARDS_TO_USER
                , new RequestModel<>(REQ_ADD_CARDS_TO_USER, SharedPrefManager.getInstance(PaymentMethodsActivity.this).getUser().getUser_id(),
                        SharedPrefManager.getInstance(PaymentMethodsActivity.this).getUser().getApi_token(),
                        card.getCard_id(), method
                        , null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        String url = (mainObject.get("url").getAsString());

                        if (url != null && !url.isEmpty()) {
                            Intent i = new Intent(PaymentMethodsActivity.this, PaymentURLActivity.class);
                            i.putExtra(PAYMENT_URL, url);
                            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
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
