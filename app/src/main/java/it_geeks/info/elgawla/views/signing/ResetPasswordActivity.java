package it_geeks.info.elgawla.views.signing;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import static it_geeks.info.elgawla.util.Constants.REQ_CHANGE_PASSWORD;
import static it_geeks.info.elgawla.util.Constants.SERVER_MSG;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout tlPass, tlRePass;
    private EditText etPass, etRePass;
    private Button btnReset;

    private String pass, rePass;

    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        init();

        initTextWatchers();

        handleEvents();
    }

    private void init() {
        tlPass = findViewById(R.id.tl_enter_pass);
        tlRePass = findViewById(R.id.tl_renter_pass);
        etPass = findViewById(R.id.et_enter_pass);
        etRePass = findViewById(R.id.et_renter_pass);
        btnReset = findViewById(R.id.btn_reset_pass);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.reset_pass_main_layout));
    }

    private void initTextWatchers() {
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlPass.getError() != null)
                    tlPass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etRePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlRePass.getError() != null)
                    tlRePass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleEvents() {
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = etPass.getText().toString();
                rePass = etRePass.getText().toString();

                if (checkPass())
                {
                    sendPassToServer();
                }
            }
        });
    }

    private boolean checkPass() {
        boolean _continue = true;
        if (pass.isEmpty())
        {
            tlPass.setError(getString(R.string.empty_hint));
            etPass.requestFocus();
            _continue = false;
        }

        if (rePass.isEmpty())
        {
            tlRePass.setError(getString(R.string.empty_hint));
            etRePass.requestFocus();
            _continue = false;
        }

        if (_continue)
            if (!pass.equals(rePass))
            {
                tlPass.setError(getString(R.string.no_match));

                tlRePass.setError(getString(R.string.no_match));
                etRePass.requestFocus();
                _continue = false;
            }

        return _continue;
    }

    private void sendPassToServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(ResetPasswordActivity.this).executeConnectionToServer(
                ResetPasswordActivity.this,
                REQ_CHANGE_PASSWORD, new Request<>(REQ_CHANGE_PASSWORD, null, null, pass
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get(SERVER_MSG).getAsString()).showSnack();
                        startActivity(new Intent(ResetPasswordActivity.this, SignInActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                }
        );
    }
}
