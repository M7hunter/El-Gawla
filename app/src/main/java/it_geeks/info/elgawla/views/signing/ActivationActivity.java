package it_geeks.info.elgawla.views.signing;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import static it_geeks.info.elgawla.util.Constants.REQ_SEND_SMS;

public class ActivationActivity extends AppCompatActivity {

    private TextInputLayout tlCode;
    private EditText etCode;
    private Button btnConfirm;
    private FloatingActionButton fbtnResend;
    private TextView tvResendTimer;
    private CountDownTimer countDownTimer;
    private int timeValue = 60;

    private String cashedCode, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        init();

        getData();

        handleEvents();
    }

    private void getData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            cashedCode = extras.getString("activation_code");
            phone = extras.getString("phone");

            Log.d("activation", "code:" + cashedCode + ", phone: " + phone);
        }
    }

    private void init() {
        tlCode = findViewById(R.id.tl_code);
        etCode = findViewById(R.id.et_activation_code);
        btnConfirm = findViewById(R.id.btn_activation_confirm);
        tvResendTimer = findViewById(R.id.tv_resend_activation_time);
        fbtnResend = findViewById(R.id.fbtn_resend_code);
        fbtnResend.setEnabled(false);

        countDownTimer = new CountDownTimer(timeValue * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvResendTimer.setText("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                fbtnResend.setEnabled(true);
            }
        }.start();

        fbtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCodeByPhoneFromServer(phone);
            }
        });
    }

    private void handleEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();

                if (code.isEmpty())
                {
                    tlCode.setError(getString(R.string.empty_hint));
                    etCode.requestFocus();
                }
                else
                {
                    if (cashedCode.equals(code))
                    {
                        // todo: activate user on server
                        startActivity(new Intent(ActivationActivity.this, SignUpActivity.class)
                                .putExtra("phone", phone));
                    }
                    else
                    {
                        tlCode.setError(getString(R.string.wrong_code));
                        etCode.requestFocus();
                    }
                }
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlCode.getError() != null)
                {
                    tlCode.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void requestCodeByPhoneFromServer(final String phone) {
        RetrofitClient.getInstance(this).executeConnectionToServer(this
                , REQ_SEND_SMS, new RequestModel<>(REQ_SEND_SMS, phone, SharedPrefManager.getInstance(ActivationActivity.this).getCountry().getCountry_id()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        countDownTimer.start();
                        fbtnResend.setEnabled(false);
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {

                    }
                });
    }

}

