package it_geeks.info.gawla_app.views.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.SnackBuilder;

import static it_geeks.info.gawla_app.util.Constants.REQ_FORGOT_PASSWORD;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button btnSend;
    private EditText etEmail;
    private TextInputLayout tlEmail;
    private ProgressBar pbForgetPass;

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
        etEmail = findViewById(R.id.et_email_fp);

        btnSend = findViewById(R.id.btn_send_fp);
        tlEmail = findViewById(R.id.tl_email_fp);
        pbForgetPass = findViewById(R.id.pb_forget_pass);

        snackBuilder = new SnackBuilder(findViewById(R.id.forget_main_layout));
    }

    private void handleEvents() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                if (checkEntries(email))
                {
                    hideSendBtn();
                    sendEmail(email);
                }
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendEmail(String email) {
        RetrofitClient.getInstance(ForgetPasswordActivity.this).executeConnectionToServer(ForgetPasswordActivity.this,
                REQ_FORGOT_PASSWORD, new Request<>(REQ_FORGOT_PASSWORD, email
                        , null, null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();
                    }

                    @Override
                    public void handleAfterResponse() {
                        displaySendBtn();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        displaySendBtn();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private boolean checkEntries(String email) {
        if (email.isEmpty())
        { // empty ?
            tlEmail.setError(getString(R.string.emptyMail));
            etEmail.requestFocus();
            return false;

        }
        else
        { // !empty
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            { // valid email ?
                tlEmail.setError(null);
                return true;

            }
            else
            { //!valid
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
        btnSend.setVisibility(View.INVISIBLE);
        pbForgetPass.setVisibility(View.VISIBLE);
    }
}
