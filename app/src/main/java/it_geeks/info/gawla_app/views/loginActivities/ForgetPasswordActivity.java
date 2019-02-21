package it_geeks.info.gawla_app.views.loginActivities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.TransHolder;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextView tvForgetPass, tvForgetPassHint;
    private Button btnSend;
    private EditText etEmail;
    private TextInputLayout tlEmail;

    private ProgressBar pbForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_forget_password);

        initViews();

        setupTrans();

        handleEvents();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email_fp);

        // translatable views
        tvForgetPass = findViewById(R.id.tv_forget_pass);
        tvForgetPassHint = findViewById(R.id.tv_forget_pass_hint);
        btnSend = findViewById(R.id.btn_send_fp);
        tlEmail = findViewById(R.id.tl_email_fp);
        pbForgetPass = findViewById(R.id.pb_forget_pass);
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(this);
        transHolder.getForgetPassActivityTranses(this);

        tvForgetPass.setText(transHolder.forget_pass);
        tvForgetPassHint.setText(transHolder.forget_pass_hint);
        tlEmail.setHint(transHolder.email);
        btnSend.setText(transHolder.send);
    }

    private void handleEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                if (checkEntries(email)) {
                    hideSendBtn();
                    sendEmail(email);
                }
            }
        });
    }

    private void sendEmail(String email) {
        RetrofitClient.getInstance(ForgetPasswordActivity.this).executeConnectionToServer(ForgetPasswordActivity.this, "forgotPassword", new Request(etEmail.getText().toString()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(ForgetPasswordActivity.this,mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleFalseResponse(JsonObject mainObject) {

            }

            @Override
            public void handleEmptyResponse() {
                displaySendBtn();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                Toast.makeText(ForgetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                displaySendBtn();
            }
        });
    }

    private boolean checkEntries(String email) {
        if (email.isEmpty()) { // empty ?
            tlEmail.setError(getString(R.string.emptyMail));
            etEmail.requestFocus();
            return false;

        } else { // !empty
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // valid email ?
                tlEmail.setError(null);
                return true;

            } else { //!valid
                tlEmail.setError(getString(R.string.enter_valid_email));
                etEmail.requestFocus();
                return false;
            }
        }
    }

    private void displaySendBtn() {
        btnSend.setVisibility(View.VISIBLE);
        pbForgetPass.setVisibility(View.GONE);
    }

    private void hideSendBtn() {
        btnSend.setVisibility(View.GONE);
        pbForgetPass.setVisibility(View.VISIBLE);
    }
}
