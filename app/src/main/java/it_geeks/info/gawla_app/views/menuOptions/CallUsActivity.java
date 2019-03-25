package it_geeks.info.gawla_app.views.menuOptions;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;

public class CallUsActivity extends AppCompatActivity {

    private EditText usernameCallUS, emailCallUS, messageCallUS;
    private Button btnSendCallUs;
    private TextInputLayout tlName, tlEmail, tlText;
    private View loadingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_call_us);

        initViews();

        bindViews();

        handleEvents();
    }

    private void initViews() {
        usernameCallUS = findViewById(R.id.usernameCallUs);
        emailCallUS = findViewById(R.id.emailCallUs);
        messageCallUS = findViewById(R.id.messageCallUs);
        btnSendCallUs = findViewById(R.id.btnSendCallUs);

        tlName = findViewById(R.id.tl_call_us_name);
        tlEmail = findViewById(R.id.tl_call_us_email);
        tlText = findViewById(R.id.tl_text);

        loadingCard = findViewById(R.id.loading_card);
    }

    private void bindViews() {
        usernameCallUS.setText(SharedPrefManager.getInstance(CallUsActivity.this).getUser().getName());
        emailCallUS.setText(SharedPrefManager.getInstance(CallUsActivity.this).getUser().getEmail());
    }

    private void handleEvents() {
        // back
        findViewById(R.id.call_us_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // send
        btnSendCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                checkEntries();
            }
        });
    }

    private void checkEntries() {
        if (!usernameCallUS.getText().toString().trim().isEmpty()) { // !empty
            if (!emailCallUS.getText().toString().trim().isEmpty()) { // !empty
                if (!messageCallUS.getText().toString().trim().isEmpty()) { // !empty
                    sendMessage();
                } else { // empty ?
                    messageCallUS.setFocusable(true);
                    tlText.setError(getString(R.string.empty_message));
                }
            } else { // empty ?
                emailCallUS.setFocusable(true);
                tlEmail.setError(getString(R.string.emptyMail));
            }
        } else { // empty ?
            usernameCallUS.setFocusable(true);
            tlName.setError(getString(R.string.empty_name));
        }
    }

    private void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void sendMessage() {
        displayLoading();
        int userID = SharedPrefManager.getInstance(CallUsActivity.this).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(CallUsActivity.this).getUser().getApi_token();
        String username = usernameCallUS.getText().toString();
        String email = emailCallUS.getText().toString();
        String message = messageCallUS.getText().toString();

        tlText.setError(null);
        tlName.setError(null);
        tlEmail.setError(null);

        RetrofitClient.getInstance(CallUsActivity.this).executeConnectionToServer(CallUsActivity.this,
                "setUserContactMessage", new Request(userID, apiToken, username, email, message), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(CallUsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        messageCallUS.setText("");
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        hideLoading();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        hideLoading();
                        Toast.makeText(CallUsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

