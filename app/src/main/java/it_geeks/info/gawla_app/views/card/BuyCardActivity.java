package it_geeks.info.gawla_app.views.card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.Constants;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.GooglePay;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.NotificationActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_CATEGORIES;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_PAYMENT_PAGE;

public class BuyCardActivity extends AppCompatActivity {

    private static final String TAG = "Buy_card";

    private RadioButton rbVisa, rbMastercard, rbFawry, rbPaypal, rbGooglePay;
    private ImageView ivIncreaseAmount, ivDecreaseAmount, ivVisa, ivMastercard, ivFawry, ivPaypal;
    private TextView tvAmount, tvHeader, tvTotalPrice;
    private Button btnBuy;
    private Spinner spCategory;
    private LinearLayout llUseGooglePay;
    private RelativeLayout rlGooglePayBtn;

    private List<Category> categoryList = new ArrayList<>();
    private List<String> catNamesList = new ArrayList<>();
    private Category selectedCategory;
    private Card card;

    private int amount = 1;
    private int salonId = 0;
    private String method;

    private DialogBuilder dialogBuilder;

    // A client for interacting with the Google Pay API
    private PaymentsClient mPaymentsClient;

    // A Google Pay payment button presented to the viewer for interaction
    private View mGooglePayButton;

    // A constant integer you define to track a request for payment data activity
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_card);

        initGooglePayApiClient();
        initViews();

        if (getCardData(savedInstanceState))
        {
            bindData();
            if (categoryList.size() == 0)
            {
                getCategoriesFromServer();
            }
        }

        handleEvents();
    }

    private void initViews() {
        ivIncreaseAmount = findViewById(R.id.iv_increase_amount);
        ivDecreaseAmount = findViewById(R.id.iv_decrease_amount);
        tvHeader = findViewById(R.id.tv_buy_card_name);
        tvAmount = findViewById(R.id.tv_card_amount);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        spCategory = findViewById(R.id.sp_buy_card_category);
        btnBuy = findViewById(R.id.btn_buy_card_buy);
        llUseGooglePay = findViewById(R.id.ll_use_googlepay);
        rlGooglePayBtn = findViewById(R.id.googlepay);

        initRadioButtons();

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private boolean getCardData(Bundle savedInstanceState) {
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
            {
                card = (Card) extras.getSerializable("card_to_buy");
                salonId = extras.getInt("salon_id_to_buy_card");
                categoryList.addAll((List<Category>) extras.getSerializable("categories_to_buy_card"));

            }

        }
        else
        {
            card = (Card) savedInstanceState.getSerializable("card_to_buy");
            salonId = savedInstanceState.getInt("salon_id_to_buy_card");
            categoryList.addAll((List<Category>) savedInstanceState.getSerializable("categories_to_buy_card"));
        }

        if (categoryList.size() > 0)
        {
            selectedCategory = categoryList.get(0);
        }

        return card != null;
    }

    private void bindData() {
        tvHeader.setText(card.getCard_name());
        getTotalPrice();

        for (Category category : categoryList)
        {
            catNamesList.add(category.getCategoryName());
        }

        initCategoriesSpinner();
    }

    private void handleEvents() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // increase amount
        ivIncreaseAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAmount.setText(String.valueOf(++amount)); // increase by 1
                getTotalPrice();
            }
        });

        // decrease amount
        ivDecreaseAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAmount() > 1)
                {
                    tvAmount.setText(String.valueOf(--amount)); // decrease by 1
                    getTotalPrice();
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCard();
            }
        });

        llUseGooglePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGooglePay();
            }
        });
    }

    private void buyCard() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(BuyCardActivity.this).executeConnectionToServer(BuyCardActivity.this,
                REQ_GET_PAYMENT_PAGE, new Request<>(REQ_GET_PAYMENT_PAGE, SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getUser_id(),
                        SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getApi_token(),
                        salonId,
                        card.getCard_id(),
                        selectedCategory.getCategoryId(),
                        method, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(BuyCardActivity.this, "done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        Toast.makeText(BuyCardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Integer getAmount() {
        amount = Integer.parseInt(tvAmount.getText().toString());
        return amount;
    }

    private String getTotalPrice() {
        // TODO: price should be by category
        String totalPrice = String.valueOf(getAmount() * Float.parseFloat(card.getCard_cost()));
        tvTotalPrice.setText(totalPrice);
        return totalPrice;
    }

    private void initRadioButtons() {
        rbVisa = findViewById(R.id.rb_visa);
        rbMastercard = findViewById(R.id.rb_mastercard);
        rbFawry = findViewById(R.id.rb_fawry);
        rbPaypal = findViewById(R.id.rb_paypal);
        rbGooglePay = findViewById(R.id.rb_googlepay);

        ivVisa = findViewById(R.id.iv_visa);
        ivMastercard = findViewById(R.id.iv_mastercard);
        ivFawry = findViewById(R.id.iv_fawry);
        ivPaypal = findViewById(R.id.iv_paypal);

        method = SharedPrefManager.getInstance(this).getLastMethod();

        switch (method)
        {
            case Constants.VISA:
                checkVisa();
                break;
            case Constants.MASTERCARD:
                checkMastercard();
                break;
            case Constants.FAWRY:
                checkFawry();
                break;
            case Constants.PAYPAL:
                checkPaypal();
                break;
            case Constants.GOOGLEPAY:
                checkGooglePay();
                break;
        }

        /*
         * google pay not supported on APIs < 24
         * so, just hide google pay button form APIs < 24
         * and check visa as default method
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            llUseGooglePay.setVisibility(View.GONE);
            checkVisa();
        }

        CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId())
                {
                    case R.id.rb_visa:
                        if (isChecked)
                        {
                            checkVisa();
                        }

                        break;
                    case R.id.rb_mastercard:
                        if (isChecked)
                        {
                            checkMastercard();
                        }

                        break;
                    case R.id.rb_fawry:
                        if (isChecked)
                        {
                            checkFawry();
                        }

                        break;
                    case R.id.rb_paypal:
                        if (isChecked)
                        {
                            checkPaypal();
                        }

                        break;
                    case R.id.rb_googlepay:
                        if (isChecked)
                        {
                            checkGooglePay();
                        }

                        break;
                }
                Log.d(TAG, method);
            }
        };

        rbVisa.setOnCheckedChangeListener(checkListener);
        rbMastercard.setOnCheckedChangeListener(checkListener);
        rbFawry.setOnCheckedChangeListener(checkListener);
        rbPaypal.setOnCheckedChangeListener(checkListener);
        rbGooglePay.setOnCheckedChangeListener(checkListener);

        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.iv_visa:
                        if (!rbVisa.isChecked())
                        {
                            checkVisa();
                        }

                        break;
                    case R.id.iv_mastercard:
                        if (!rbMastercard.isChecked())
                        {
                            checkMastercard();
                        }

                        break;
                    case R.id.iv_fawry:
                        if (!rbFawry.isChecked())
                        {
                            checkFawry();
                        }

                        break;
                    case R.id.iv_paypal:
                        if (!rbPaypal.isChecked())
                        {
                            checkPaypal();
                        }

                        break;
                    case R.id.ll_use_googlepay:
                        if (!rbGooglePay.isChecked())
                        {
                            checkGooglePay();
                        }

                        break;
                }
                Log.d(TAG, method);
            }
        };

        ivVisa.setOnClickListener(imageClickListener);
        ivMastercard.setOnClickListener(imageClickListener);
        ivFawry.setOnClickListener(imageClickListener);
        ivPaypal.setOnClickListener(imageClickListener);
        llUseGooglePay.setOnClickListener(imageClickListener);
    }

    private void checkVisa() {
        rbVisa.setChecked(true);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(false);
        rbGooglePay.setChecked(false);

        btnBuy.setVisibility(View.VISIBLE);
        rlGooglePayBtn.setVisibility(View.GONE);

        cacheLastMethod(Constants.VISA);
    }

    private void checkMastercard() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(true);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(false);
        rbGooglePay.setChecked(false);

        btnBuy.setVisibility(View.VISIBLE);
        rlGooglePayBtn.setVisibility(View.GONE);

        cacheLastMethod(Constants.MASTERCARD);
    }

    private void checkFawry() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(true);
        rbPaypal.setChecked(false);
        rbGooglePay.setChecked(false);

        btnBuy.setVisibility(View.VISIBLE);
        rlGooglePayBtn.setVisibility(View.GONE);

        cacheLastMethod(Constants.FAWRY);
    }

    private void checkPaypal() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(true);
        rbGooglePay.setChecked(false);

        btnBuy.setVisibility(View.VISIBLE);
        rlGooglePayBtn.setVisibility(View.GONE);

        cacheLastMethod(Constants.PAYPAL);
    }

    private void checkGooglePay() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(false);
        rbGooglePay.setChecked(true);

        possiblyShowGooglePayButton();
        btnBuy.setVisibility(View.GONE);

        cacheLastMethod(Constants.GOOGLEPAY);
    }

    private void cacheLastMethod(String newMethod) {
        method = newMethod;
        SharedPrefManager.getInstance(this).setLastMethod(method);
    }

    private void initGooglePayApiClient() {
        // initialize a Google Pay API client for an environment suitable for testing
        mPaymentsClient = Wallet.getPaymentsClient(this
                , new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build());
    }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button
     */
    @SuppressLint("NewApi")
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent())
        {
            return;
        }

        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null)
        {
            return;
        }

        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try
                        {
                            boolean result = task.getResult(ApiException.class);
                            if (result)
                            {
                                // show Google as a payment option
                                mGooglePayButton = findViewById(R.id.googlepay);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                requestPayment(view);
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception)
                        {
                            // handle developer errors
                            Log.d(TAG, "possiblyShowGooglePayButton", exception);
                        }
                    }
                });
    }

    /**
     * Display the Google Pay payment sheet after interaction with the Google Pay payment button
     *
     * @param view optionally uniquely identify the interactive element prompting for payment
     */
    @SuppressLint("NewApi")
    public void requestPayment(View view) {
        Optional<JSONObject> paymentDataRequestJson = GooglePay.getPaymentDataRequest(getTotalPrice(), "USD");
        if (!paymentDataRequestJson.isPresent())
        {
            return;
        }
        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        if (request != null)
        {
            AutoResolveHelper.resolveTask(mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet
     *
     * @param requestCode the request code originally supplied to AutoResolveHelper in
     *                    requestPayment()
     * @param resultCode  the result code returned by the Google Pay API
     * @param data        an Intent from the Google Pay API containing payment or error data
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     * from an Activity</a>
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String json = null;
                        if (paymentData != null)
                        {
                            json = paymentData.toJson();
                        }
                        else
                        {
                            Log.d(TAG, "paymentData: null");
                        }
                        // if using gateway tokenization, pass this token without modification
//                        String paymentMethodData = ;
                        try
                        {
                            String paymentToken = new JSONObject(json)
                                    .getJSONObject("paymentMethodData")
                                    .getJSONObject("tokenizationData")
                                    .getString("token");

                            Log.d(TAG, "paymentToken:: " + paymentToken);
                            Toast.makeText(this, paymentToken, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        if (status != null)
                            Log.e(TAG, "RESULT_ERROR: " + status.toString());
                        // Log the status for debugging.
                        // Generally, there is no need to show an error to the user.
                        // The Google Pay payment sheet will present any account errors.
                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
        }
    }

    private void initCategoriesSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, catNamesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spinnerAdapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getCategoriesFromServer() {
        RetrofitClient.getInstance(BuyCardActivity.this).executeConnectionToServer(BuyCardActivity.this,
                REQ_GET_ALL_CATEGORIES, new Request<>(REQ_GET_ALL_CATEGORIES, SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getUser_id(), SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getApi_token(),
                        null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                        if (categoryList.size() > 0 && selectedCategory == null)
                        {
                            selectedCategory = categoryList.get(0);
                        }
                        bindData();
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(BuyCardActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
