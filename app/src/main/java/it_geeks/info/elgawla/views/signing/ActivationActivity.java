package it_geeks.info.elgawla.views.signing;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

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
import com.google.gson.JsonObject;

import static it_geeks.info.elgawla.util.Constants.REQ_CONFIRM_CODE;
import static it_geeks.info.elgawla.util.Constants.REQ_SEND_SMS;

public class ActivationActivity extends BaseActivity {

    private EditText et1stD, et2ndD, et3rdD, et4thD;
    private Button btnConfirm;
    private FloatingActionButton fbtnResend;
    private TextView tvResendTimer, tvDigitsErr;
    private CountDownTimer countDownTimer;
    private int timeValue = 60;

    private boolean isNew = true;
    private String cashedCode, receiver;

    private SnackBuilder snackBuilder;

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
            receiver = extras.getString("receiver");
            isNew = extras.getBoolean("newUser");
            if (isNew)
            {
                cashedCode = extras.getString("activation_code");
                Log.d("activation", "code:" + cashedCode + ", phone: " + receiver);
            }
        }
    }

    private void init() {
        et1stD = findViewById(R.id.et_1st_digit);
        et2ndD = findViewById(R.id.et_2nd_digit);
        et3rdD = findViewById(R.id.et_3rd_digit);
        et4thD = findViewById(R.id.et_4th_digit);
        btnConfirm = findViewById(R.id.btn_activation_confirm);
        tvDigitsErr = findViewById(R.id.tv_digits_err);
        tvResendTimer = findViewById(R.id.tv_resend_activation_time);
        fbtnResend = findViewById(R.id.fbtn_resend_code);
        fbtnResend.setEnabled(false);

        et1stD.requestFocus();

        snackBuilder = new SnackBuilder(findViewById(R.id.activation_main_layout));

        countDownTimer = new CountDownTimer(timeValue * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvResendTimer.setText("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                setError(getString(R.string.time_up));
                fbtnResend.setEnabled(true);
            }
        }.start();

        fbtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCodeByPhoneFromServer();
            }
        });
    }

    private void setError(String error) {
        if (tvDigitsErr.getVisibility() != View.VISIBLE)
        {
            tvDigitsErr.setVisibility(View.VISIBLE);
        }
        tvDigitsErr.setText(error);
    }

    private void removeError() {
        if (tvDigitsErr.getVisibility() != View.INVISIBLE)
        {
            tvDigitsErr.setVisibility(View.INVISIBLE);
        }
        tvDigitsErr.setText(null);
    }

    private void handleEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDigits())
                {
                    checkCode();
                }
            }
        });

        initTextWatchers();
    }

    private void initTextWatchers() {
        et1stD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeError();
                if (s.length() > 0)
                {
                    et2ndD.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et2ndD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeError();
                if (s.length() > 0)
                {
                    et3rdD.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et3rdD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeError();
                if (s.length() > 0)
                {
                    et4thD.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et4thD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeError();
                if (s.length() > 0)
                {
                    checkCode();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkCode() {
        String digits = et1stD.getText().toString()
                + et2ndD.getText().toString()
                + et3rdD.getText().toString()
                + et4thD.getText().toString();

        if (isNew)
        {
            if (cashedCode.equals(digits))
            {
                startActivity(new Intent(ActivationActivity.this, SignUpActivity.class)
                        .putExtra("phone", receiver));
            }
            else
            {
                setError(getString(R.string.wrong_code));
            }
        }
        else
        {
            sendCodeToServer(digits);
        }
    }

    private boolean checkDigits() {
        boolean _continue = true;

        if (et4thD.getText().toString().isEmpty())
        {
            setError(getString(R.string.empty_digit));
            et4thD.requestFocus();
            _continue = false;
        }

        if (et3rdD.getText().toString().isEmpty())
        {
            setError(getString(R.string.empty_digit));
            et3rdD.requestFocus();
            _continue = false;
        }

        if (et2ndD.getText().toString().isEmpty())
        {
            setError(getString(R.string.empty_digit));
            et2ndD.requestFocus();
            _continue = false;
        }

        if (et1stD.getText().toString().isEmpty())
        {
            setError(getString(R.string.empty_digit));
            et1stD.requestFocus();
            _continue = false;
        }

        return _continue;
    }

    private void requestCodeByPhoneFromServer() {
        btnConfirm.setEnabled(false);
        RetrofitClient.getInstance(this).fetchDataFromServer(this
                , REQ_SEND_SMS, new RequestModel<>(REQ_SEND_SMS, receiver, SharedPrefManager.getInstance(ActivationActivity.this).getCountry().getCountry_id()
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        removeError();
                        countDownTimer.start();
                        fbtnResend.setEnabled(false);
                    }

                    @Override
                    public void afterResponse() {
                        btnConfirm.setEnabled(true);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        btnConfirm.setEnabled(true);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void sendCodeToServer(String code) {
        btnConfirm.setEnabled(false);
        RetrofitClient.getInstance(this).fetchDataFromServer(this
                , REQ_CONFIRM_CODE, new RequestModel<>(REQ_CONFIRM_CODE, code, receiver,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();

                        SharedPrefManager.getInstance(ActivationActivity.this).saveUser(ParseResponses.parseUser(mainObject));
                        startActivity(new Intent(ActivationActivity.this, ResetPasswordActivity.class));
                    }

                    @Override
                    public void afterResponse() {
                        btnConfirm.setEnabled(true);
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        btnConfirm.setEnabled(true);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

}

