package it_geeks.info.elgawla.views.signing;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import static it_geeks.info.elgawla.util.Constants.REQ_SEND_SMS;

public class EnterPhoneActivity extends AppCompatActivity {

    private TextInputLayout tlPhone;
    private EditText etPhone;
    private Button btnSend;

    private SnackBuilder snackBuilder;
    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone);

        init();

        handleEvents();
    }

    private void init() {
        tlPhone = findViewById(R.id.tl_phone);
        etPhone = findViewById(R.id.et_phone);
        btnSend = findViewById(R.id.btn_phone_send);

        snackBuilder = new SnackBuilder(findViewById(R.id.phone_main_layout));
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private void handleEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if (checkPhone(phone))
                {
                    requestCodeByPhoneFromServer(phone);
                    btnSend.setEnabled(false);
                }
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlPhone.getError() != null)
                {
                    tlPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean checkPhone(String phone) {
        if (phone.isEmpty())
        {
            tlPhone.setError(getString(R.string.empty_hint));
            etPhone.requestFocus();
            return false;
        }
        else if (!Patterns.PHONE.matcher(phone).matches())
        {
            tlPhone.setError(getString(R.string.enter_valid_phone));
            etPhone.requestFocus();
            return false;
        }
        else tlPhone.setError(null);

        return true;
    }

    private void requestCodeByPhoneFromServer(final String phone) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(this
                , REQ_SEND_SMS, new RequestModel<>(REQ_SEND_SMS, phone, SharedPrefManager.getInstance(EnterPhoneActivity.this).getCountry().getCountry_id()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        startActivity(new Intent(EnterPhoneActivity.this, ActivationActivity.class)
                                .putExtra("activation_code", mainObject.get("code").getAsString())
                                .putExtra("phone", phone)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                    }

                    @Override
                    public void handleAfterResponse() {
                        btnSend.setEnabled(true);
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        btnSend.setEnabled(true);
                        dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }
}
