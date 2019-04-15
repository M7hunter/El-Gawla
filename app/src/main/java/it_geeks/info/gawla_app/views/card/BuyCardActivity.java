package it_geeks.info.gawla_app.views.card;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.DialogBuilder;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.NotificationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BuyCardActivity extends AppCompatActivity {

    private static final String TAG = "Buy_card";

    private RadioButton rbVisa, rbMastercard, rbFawry, rbPaypal;
    private ImageView ivIncreaseAmount, ivDecreaseAmount, ivVisa, ivMastercard, ivFawry, ivPaypal;
    private TextView tvAmount, tvHeader;
    private Spinner spCategory;
    private Button btnBuy;

    private List<Category> categoryList = new ArrayList<>();
    private List<String> catNamesList = new ArrayList<>();
    private Card card;
    private Category selectedCategory;

    private int amount = 1;
    private int salonId = 190;
    private String method = "visa";

    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_card);

        initViews();

        if (getCardData(savedInstanceState)) {
            bindData();
            if (categoryList.size() == 0) {
                getCategoriesFromServer();
            }
        }

        handleEvents();

        initRadioButtons();
    }

    private void initViews() {
        ivIncreaseAmount = findViewById(R.id.iv_increase_amount);
        ivDecreaseAmount = findViewById(R.id.iv_decrease_amount);
        tvAmount = findViewById(R.id.tv_card_amount);
        tvHeader = findViewById(R.id.tv_buy_card_name);
        spCategory = findViewById(R.id.sp_buy_card_category);
        btnBuy = findViewById(R.id.btn_buy_card_buy);

        initRadioButtons();

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private boolean getCardData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                card = (Card) extras.getSerializable("card_to_buy");
                salonId = extras.getInt("salon_id_to_buy_card");
                categoryList.addAll((List<Category>) extras.getSerializable("categories_to_buy_card"));

            }

        } else {
            card = (Card) savedInstanceState.getSerializable("card_to_buy");
            salonId = savedInstanceState.getInt("salon_id_to_buy_card");
            categoryList.addAll((List<Category>) savedInstanceState.getSerializable("categories_to_buy_card"));
        }

        if (categoryList.size() > 0) {
            selectedCategory = categoryList.get(0);
        }
        return card != null;
    }

    private void bindData() {
        tvHeader.setText(card.getCard_name());

        for (Category category : categoryList) {
            catNamesList.add(category.getCategoryName());
        }

        initCategoriesSpinner();
    }

    private void handleEvents() {
        // back
        findViewById(R.id.buy_card_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // notification
        findViewById(R.id.notification_bell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuyCardActivity.this, NotificationActivity.class));
            }
        });

        // increase amount
        ivIncreaseAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAmount.setText(String.valueOf(++amount)); // increase by 1
            }
        });

        // decrease amount
        ivDecreaseAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAmount() > 1) {
                    tvAmount.setText(String.valueOf(--amount)); // decrease by 1
                }
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCard();
            }
        });
    }

    private void buyCard() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(BuyCardActivity.this).executeConnectionToServer(BuyCardActivity.this, "getPaymentPage",
                new Request(SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getUser_id(),
                        SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getApi_token(),
                        salonId,
                        card.getCard_id(),
                        selectedCategory.getCategoryId(),
                        method),
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

    private void initRadioButtons() {
        rbVisa = findViewById(R.id.rb_visa);
        rbMastercard = findViewById(R.id.rb_mastercard);
        rbFawry = findViewById(R.id.rb_fawry);
        rbPaypal = findViewById(R.id.rb_paypal);

        ivVisa = findViewById(R.id.iv_visa);
        ivMastercard = findViewById(R.id.iv_mastercard);
        ivFawry = findViewById(R.id.iv_fawry);
        ivPaypal = findViewById(R.id.iv_paypal);

        CompoundButton.OnCheckedChangeListener checkedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.rb_visa:
                        if (isChecked) {
                            checkVisa();
                        }

                        break;
                    case R.id.rb_mastercard:
                        if (isChecked) {
                            checkMastercard();
                        }

                        break;
                    case R.id.rb_fawry:
                        if (isChecked) {
                            checkFawry();
                        }

                        break;
                    case R.id.rb_paypal:
                        if (isChecked) {
                            checkPaypal();
                        }

                        break;
                }
                Log.d(TAG, method);
            }
        };

        rbVisa.setOnCheckedChangeListener(checkedListener);
        rbMastercard.setOnCheckedChangeListener(checkedListener);
        rbFawry.setOnCheckedChangeListener(checkedListener);
        rbPaypal.setOnCheckedChangeListener(checkedListener);

        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_visa:
                        if (!rbVisa.isChecked()) {
                            checkVisa();
                        }

                        break;
                    case R.id.iv_mastercard:
                        if (!rbMastercard.isChecked()) {
                            checkMastercard();
                        }

                        break;
                    case R.id.iv_fawry:
                        if (!rbFawry.isChecked()) {
                            checkFawry();
                        }

                        break;
                    case R.id.iv_paypal:
                        if (!rbPaypal.isChecked()) {
                            checkPaypal();
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
    }

    private void checkVisa() {
        rbVisa.setChecked(true);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(false);

        method = "visa";
    }

    private void checkMastercard() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(true);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(false);

        method = "mastercard";
    }

    private void checkFawry() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(true);
        rbPaypal.setChecked(false);

        method = "fawry";
    }

    private void checkPaypal() {
        rbVisa.setChecked(false);
        rbMastercard.setChecked(false);
        rbFawry.setChecked(false);
        rbPaypal.setChecked(true);

        method = "paypal";
    }

    private Integer getAmount() {
        amount = Integer.parseInt(tvAmount.getText().toString());
        return amount;
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
                "getAllCategories", new Request(SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getUser_id(), SharedPrefManager.getInstance(BuyCardActivity.this).getUser().getApi_token()),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                        if (categoryList.size() > 0 && selectedCategory == null) {
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
