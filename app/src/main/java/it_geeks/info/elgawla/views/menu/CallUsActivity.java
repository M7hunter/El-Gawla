package it_geeks.info.elgawla.views.menu;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_SET_USER_MESSAGE;

public class CallUsActivity extends BaseActivity {

    private EditText usernameCallUS, emailCallUS, messageCallUS;
    private Button btnSendCallUs;
    private TextInputLayout tlName, tlEmail, tlText;
    private DialogBuilder dialogBuilder;
    private SnackBuilder snackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.call_us_main_layout));
    }

    private void bindViews() {
        usernameCallUS.setText(SharedPrefManager.getInstance(CallUsActivity.this).getUser().getName());
        emailCallUS.setText(SharedPrefManager.getInstance(CallUsActivity.this).getUser().getEmail());
    }

    private void handleEvents() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
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
        if (!usernameCallUS.getText().toString().trim().isEmpty())
        { // !empty
            if (!emailCallUS.getText().toString().trim().isEmpty())
            { // !empty
                if (!messageCallUS.getText().toString().trim().isEmpty())
                { // !empty
                    sendMessage();
                }
                else
                { // empty ?
                    messageCallUS.setFocusable(true);
                    tlText.setError(getString(R.string.empty_message));
                }
            }
            else
            { // empty ?
                emailCallUS.setFocusable(true);
                tlEmail.setError(getString(R.string.emptyMail));
            }
        }
        else
        { // empty ?
            usernameCallUS.setFocusable(true);
            tlName.setError(getString(R.string.empty_name));
        }
    }

    private void sendMessage() {
        dialogBuilder.displayLoadingDialog();
        int userID = SharedPrefManager.getInstance(CallUsActivity.this).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(CallUsActivity.this).getUser().getApi_token();
        String username = usernameCallUS.getText().toString();
        String email = emailCallUS.getText().toString();
        final String message = messageCallUS.getText().toString();

        tlText.setError(null);
        tlName.setError(null);
        tlEmail.setError(null);

        EventsManager.sendSendMessageToCallUsEvent(CallUsActivity.this, message, 0);
        RetrofitClient.getInstance(CallUsActivity.this).fetchDataFromServer(CallUsActivity.this,
                REQ_SET_USER_MESSAGE, new RequestModel<>(REQ_SET_USER_MESSAGE, userID, apiToken, username, email, message,
                        null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();
                        messageCallUS.setText("");
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

