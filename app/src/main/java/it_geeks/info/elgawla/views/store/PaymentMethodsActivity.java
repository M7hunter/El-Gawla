package it_geeks.info.elgawla.views.store;

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

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.Models.Package;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.FAWRY;
import static it_geeks.info.elgawla.util.Constants.KNET;
import static it_geeks.info.elgawla.util.Constants.PACKAGE;
import static it_geeks.info.elgawla.util.Constants.PAYMENT_URL;
import static it_geeks.info.elgawla.util.Constants.REQ_ADD_CARDS_TO_USER;
import static it_geeks.info.elgawla.util.Constants.REQ_SET_MEMBERSHIP;

public class PaymentMethodsActivity extends BaseActivity {

    private RadioButton rbKnet, rbFawry;
    private ImageView ivKnet, ivFawry;
    private Button btnContinue;
    //    private FawryButton fawry_button;
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

//        initFawrySDK();

        bindData();

        initMethods();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (method != null && !method.isEmpty())
                {
                    if (isCard)
                    {
                        if (card != null)
                        {
                            buy(REQ_ADD_CARDS_TO_USER, card.getCard_id());
                        }
                    }
                    else if (mPackage != null)
                    {
                        buy(REQ_SET_MEMBERSHIP, mPackage.getId());
                    }
                }
            }
        });
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
            {
                isCard = extras.getBoolean("is_card");

                if (isCard)
                {
                    card = (Card) extras.getSerializable("card_to_buy");
                }
                else
                {
                    mPackage = (Package) extras.getSerializable(PACKAGE);
                }
            }
        }
        else
        {
            isCard = savedInstanceState.getBoolean("is_card");

            if (isCard)
            {
                card = (Card) savedInstanceState.getSerializable("card_to_buy");
            }
            else
            {
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
//        fawry_button = findViewById(R.id.fawry_button);
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
        switch (SharedPrefManager.getInstance(this).getLastMethod())
        {
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
                switch (buttonView.getId())
                {
                    case R.id.rb_knet:
                        if (isChecked)
                        {
                            checkKnet();
                        }
                        break;
                    case R.id.rb_fawry:
                        if (isChecked)
                        {
                            checkFawry();
                        }
                        break;
                }
            }
        };

        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.iv_knet:
                        if (!rbKnet.isChecked())
                        {
                            checkKnet();
                        }
                        break;
                    case R.id.iv_fawry:
                        if (!rbFawry.isChecked())
                        {
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

    private void buy(String request, int id) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                request, new RequestModel<>(request
                        , SharedPrefManager.getInstance(this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , id
                        , method
                        , null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        String url = (mainObject.get("url").getAsString());

                        if (url != null && !url.isEmpty())
                        {
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

//    private void initFawrySDK() {
//        List<PayableItem> cards = new ArrayList<>();
//        cards.add(card);
//
//        try
//        {
//            FawrySdk.initialize(this
//                    , "https://itgeeks.info"
//                    , new FawrySdkCallback() {
//                        @Override
//                        public void onSuccess(String s, Object o) {
//                            Log.d("FawrySDK:", "onSuccess: " + s);
//                            Toast.makeText(PaymentMethodsActivity.this, "success", Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void onFailure(String s) {
//                            Log.d("FawrySDK:", "onFailure: " + s);
//                            Toast.makeText(PaymentMethodsActivity.this, "failure", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    , "1tSa6uxz2nQ4QRwvZcfgRQ=="
//                    , "798346d9175a4998b28f1da3a9c7ad56"
//                    , cards
//                    , FawrySdk.Language.EN
//                    , 123
//                    , null
//                    , UUID.randomUUID());
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        dialogBuilder.hideLoadingDialog();
//        if (requestCode == 123)
//            Log.d("FawrySDK:", "onActivityResult: \n" +
//                    "REQUEST_RESULT:: " +
//                    FawryPluginAppClass.REQUEST_RESULT + "\n" +
//                    "TRX_ID_KEY:: " +
//                    FawryPluginAppClass.TRX_ID_KEY + "\n" +
//                    "EXPIRY_DATE_KEY:: " +
//                    FawryPluginAppClass.EXPIRY_DATE_KEY);
//
//    }
}
