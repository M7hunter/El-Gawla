package it_geeks.info.elgawla.views.signing;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.User;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.account.MembershipActivity;
import it_geeks.info.elgawla.views.main.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;

import static it_geeks.info.elgawla.util.Constants.REQ_USER_ACTIVATION;

public class ActivationActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnConfirm;
    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        init();

        handleEvents();
    }

    private void init() {
        etCode = findViewById(R.id.et_activation_code);
        btnConfirm = findViewById(R.id.btn_activation_confirm);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.activation_main_layout));
    }

    private void handleEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();

                if (code.isEmpty())
                {
                    etCode.setError(getString(R.string.empty_hint));
                    etCode.requestFocus();
                } else
                {
                    connectToServer(code);
                }
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCode.getError() != null)
                {
                    etCode.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void connectToServer(String code) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(ActivationActivity.this).executeConnectionToServer(this, REQ_USER_ACTIVATION
                , new Request<>(REQ_USER_ACTIVATION, SharedPrefManager.getInstance(ActivationActivity.this).getUser().getUser_id(), code
                        , null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        User user = ParseResponses.parseActiveUser(mainObject);

                        Log.d("OkHttp", "is-active:: " + user.isActive());
                        if (user.isActive())
                        {
                            Common.Instance().updateFirebaseToken(ActivationActivity.this);
                            startActivity(new Intent(ActivationActivity.this, MembershipActivity.class));
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
