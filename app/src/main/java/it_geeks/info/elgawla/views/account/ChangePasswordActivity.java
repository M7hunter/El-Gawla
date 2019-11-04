package it_geeks.info.elgawla.views.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.SnackBuilder;

import static it_geeks.info.elgawla.util.Constants.REQ_CHANGE_PASSWORD;
import static it_geeks.info.elgawla.util.Constants.SERVER_MSG;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tlOldPass, tlNewPass, tlRenterPass;
    private EditText etOldPass, etNewPass, etRenterPass;
    private Button btnChange;

    private String oldPass, newPass, rePass;

    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_change_password);

        init();

        initTextWatchers();

        handleEvents();
    }

    private void init() {
        tlOldPass = findViewById(R.id.tl_old_pass);
        tlNewPass = findViewById(R.id.tl_new_pass);
        tlRenterPass = findViewById(R.id.tl_renter_new_pass);
        etOldPass = findViewById(R.id.et_old_pass);
        etNewPass = findViewById(R.id.et_new_pass);
        etRenterPass = findViewById(R.id.et_renter_new_pass);
        btnChange = findViewById(R.id.btn_change_pass);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.pass_main_layout));
    }

    private void initTextWatchers() {
        etOldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlOldPass.getError() != null)
                    tlOldPass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlNewPass.getError() != null)
                    tlNewPass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etRenterPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tlRenterPass.getError() != null)
                    tlRenterPass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void handleEvents() {
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = etOldPass.getText().toString();
                newPass = etNewPass.getText().toString();
                rePass = etRenterPass.getText().toString();

                if (checkPass())
                {
                    sendPassToServer();
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

    private boolean checkPass() {
        boolean _continue = true;
        if (rePass.isEmpty())
        {
            tlRenterPass.setError(getString(R.string.empty_hint));
            etRenterPass.requestFocus();
            _continue = false;
        }

        if (newPass.isEmpty())
        {
            tlNewPass.setError(getString(R.string.empty_hint));
            etNewPass.requestFocus();
            _continue = false;
        }

        if (oldPass.isEmpty())
        {
            tlOldPass.setError(getString(R.string.empty_hint));
            etOldPass.requestFocus();
            _continue = false;
        }

        if (_continue && oldPass.equals(newPass))
        {
            tlOldPass.setError(getString(R.string.match));

            tlNewPass.setError(getString(R.string.match));
            etNewPass.requestFocus();
            _continue = false;
        }

        if (_continue && !newPass.equals(rePass))
        {
            tlNewPass.setError(getString(R.string.no_match));

            tlRenterPass.setError(getString(R.string.no_match));
            etRenterPass.requestFocus();
            _continue = false;
        }

        return _continue;
    }

    private void sendPassToServer() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(ChangePasswordActivity.this).executeConnectionToServer(
                ChangePasswordActivity.this,
                REQ_CHANGE_PASSWORD, new RequestModel<>(REQ_CHANGE_PASSWORD, SharedPrefManager.getInstance(ChangePasswordActivity.this).getUser().getUser_id(),
                        SharedPrefManager.getInstance(ChangePasswordActivity.this).getUser().getApi_token(), newPass
                        , null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get(SERVER_MSG).getAsString()).showSnack();
                        Common.Instance().signOut(ChangePasswordActivity.this);
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
