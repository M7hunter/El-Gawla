package it_geeks.info.elgawla.views.signing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_FORGOT_PASSWORD;

public class ForgetPasswordActivity extends BaseActivity {

    private Button btnSend;
    private EditText etReceiver;
    private TextInputLayout tlReceiver;
    private ProgressBar pbForgetPass;
    private TextView tvMail, tvPhone;
    private ImageView ivMail, ivPhone;
    private View llMail, llPhone;
    private boolean isMail = true;
    private String receiver;

    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_forget_password);

        initViews();

        handleEvents();
    }

    private void initViews() {
        llMail = findViewById(R.id.ll_forget_pass_mail);
        llPhone = findViewById(R.id.ll_forget_pass_phone);
        tvMail = findViewById(R.id.tv_forget_pass_mail);
        tvPhone = findViewById(R.id.tv_forget_pass_phone);
        ivMail = findViewById(R.id.iv_forget_pass_mail);
        ivPhone = findViewById(R.id.iv_forget_pass_phone);

        etReceiver = findViewById(R.id.et_email_fp);

        btnSend = findViewById(R.id.btn_send_fp);
        tlReceiver = findViewById(R.id.tl_email_fp);
        pbForgetPass = findViewById(R.id.pb_forget_pass);

        snackBuilder = new SnackBuilder(findViewById(R.id.forget_main_layout));
    }

    private void handleEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver = etReceiver.getText().toString();
                if (checkEntries(receiver))
                {
                    hideSendBtn();
                    sendEmail();
                }
            }
        });

        View.OnClickListener clickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId())
                        {
                            case R.id.ll_forget_pass_mail:
                                if (!isMail)
                                {
                                    selectMail();
                                    isMail = true;
                                }
                                break;
                            case R.id.ll_forget_pass_phone:
                                if (isMail)
                                {
                                    selectPhone();
                                    isMail = false;
                                }
                        }
                    }
                };

        llMail.setOnClickListener(clickListener);
        llPhone.setOnClickListener(clickListener);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void selectMail() {
        llMail.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        llPhone.setBackgroundColor(Color.WHITE);
        tvMail.setTextColor(Color.WHITE);
        tvPhone.setTextColor(getResources().getColor(R.color.colorPrimary));
        ivMail.setImageDrawable(getDrawable(R.drawable.ic_mail_white));
        ivPhone.setImageDrawable(getDrawable(R.drawable.ic_phone_blue));

        tlReceiver.setHint(getString(R.string.email));
    }

    private void selectPhone() {
        llPhone.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        llMail.setBackgroundColor(Color.WHITE);
        tvPhone.setTextColor(Color.WHITE);
        tvMail.setTextColor(getResources().getColor(R.color.colorPrimary));
        ivPhone.setImageDrawable(getDrawable(R.drawable.ic_phone_white));
        ivMail.setImageDrawable(getDrawable(R.drawable.ic_mail_blue));

        tlReceiver.setHint(getString(R.string.phone_number));
    }

    private void sendEmail() {
        RetrofitClient.getInstance(ForgetPasswordActivity.this).fetchDataFromServer(ForgetPasswordActivity.this,
                REQ_FORGOT_PASSWORD, new RequestModel<>(REQ_FORGOT_PASSWORD, receiver
                        , null, null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();
                        startActivity(new Intent(ForgetPasswordActivity.this, ActivationActivity.class)
                                .putExtra("receiver", receiver)
                                .putExtra("newUser", false));
                    }

                    @Override
                    public void afterResponse() {
                        displaySendBtn();
                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                        displaySendBtn();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private boolean checkEntries(String receiver) {
        boolean _continue = true;
        if (receiver.isEmpty())
        { // empty ?
            tlReceiver.setError(getString(R.string.empty_hint));
            etReceiver.requestFocus();
            _continue = false;
        }
        else
        { // !empty
            if (isMail)
                if (!Patterns.EMAIL_ADDRESS.matcher(receiver).matches())
                { //!valid
                    tlReceiver.setError(getString(R.string.enter_valid_email));
                    etReceiver.requestFocus();
                    _continue = false;
                }
        }

        return _continue;
    }

    private void displaySendBtn() {
        btnSend.setVisibility(View.VISIBLE);
        pbForgetPass.setVisibility(View.GONE);
    }

    private void hideSendBtn() {
        btnSend.setVisibility(View.INVISIBLE);
        pbForgetPass.setVisibility(View.VISIBLE);
    }
}
