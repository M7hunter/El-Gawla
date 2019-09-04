package it_geeks.info.gawla_app.views.store;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.SnackBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.gson.JsonObject;

import static it_geeks.info.gawla_app.util.Constants.FAWRY;
import static it_geeks.info.gawla_app.util.Constants.KNET;
import static it_geeks.info.gawla_app.util.Constants.NULL_INT_VALUE;
import static it_geeks.info.gawla_app.util.Constants.PACKAGE_ID;
import static it_geeks.info.gawla_app.util.Constants.PAYMENT_URL;
import static it_geeks.info.gawla_app.util.Constants.REQ_SET_MEMBERSHIP;

public class PaymentMethodsActivity extends AppCompatActivity {

    private RadioButton rbKnet, rbFawry;
    private ImageView ivKnet, ivFawry;
    private Button btnContinue;
    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    private int packageId;
    private String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        packageId = getIntent().getIntExtra(PACKAGE_ID, NULL_INT_VALUE);

        initViews();

        initMethods();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packageId != NULL_INT_VALUE && method != null && !method.isEmpty()) {
                    updateMembership();
                }
            }
        });
    }

    private void initViews() {
        btnContinue = findViewById(R.id.btn_payment_method_continue);

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
                REQ_SET_MEMBERSHIP, new Request<>(REQ_SET_MEMBERSHIP, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(), packageId, method
                        , null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        String url = (mainObject.get("url").getAsString());

                        if (url != null && !url.isEmpty()) {
                            Intent i = new Intent(PaymentMethodsActivity.this, PaymentURLActivity.class);
                            i.putExtra(PAYMENT_URL, url);
                            startActivity(i);

//                            TaskStackBuilder.create(PaymentMethodsActivity.this)
//                                    .addParentStack(InvoicesActivity.class)
//                                    .addNextIntent(new Intent(PaymentMethodsActivity.this, PaymentURLActivity.class)
//                                            .putExtra(PAYMENT_URL, url))
//                                    .startActivities();
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
