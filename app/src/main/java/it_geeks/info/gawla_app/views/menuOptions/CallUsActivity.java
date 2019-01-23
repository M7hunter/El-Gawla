package it_geeks.info.gawla_app.views.menuOptions;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;

public class CallUsActivity extends AppCompatActivity {

    EditText usernameCallUS , emailCallUS , messageCallUS;
    Button btnSendCallUs;
    Snackbar snackbarMessage;

    TextInputLayout tlName, tlEmail, tlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_call_us);

        findViewById(R.id.call_us_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();
        getDefaultData();
        setData();
    }

    private void initViews() {
        usernameCallUS = findViewById(R.id.usernameCallUs);
        emailCallUS = findViewById(R.id.emailCallUs);
        messageCallUS = findViewById(R.id.messageCallUs);
        btnSendCallUs = findViewById(R.id.btnSendCallUs);

        tlName = findViewById(R.id.tl_call_us_name);
        tlEmail = findViewById(R.id.tl_call_us_email);
        tlText = findViewById(R.id.tl_text);
    }

    private void getDefaultData() {
        usernameCallUS.setText(SharedPrefManager.getInstance(CallUsActivity.this).getUser().getName());
        emailCallUS.setText(SharedPrefManager.getInstance(CallUsActivity.this).getUser().getEmail());
    }

    private void setData() {
        btnSendCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!usernameCallUS.getText().toString().trim().isEmpty()){
                    if (!emailCallUS.getText().toString().trim().isEmpty()){
                        if (!messageCallUS.getText().toString().trim().isEmpty()){
                            handleData();
                        }else{
                            messageCallUS.setFocusable(true);
                            tlText.setError(getString(R.string.empty_message));
                        }
                    }else{
                        emailCallUS.setFocusable(true);
                        tlEmail.setError(getString(R.string.emptyMail));
                    }
                }else{
                    usernameCallUS.setFocusable(true);
                    tlName.setError(getString(R.string.empty_name));

                }
            }
        });
    }

    private void handleData() {
        snackbarMessage.make(findViewById(R.id.CallUsParentLayout), getString(R.string.sending_message), Snackbar.LENGTH_INDEFINITE).show();

        int userID = SharedPrefManager.getInstance(CallUsActivity.this).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(CallUsActivity.this).getUser().getApi_token();
        String username = usernameCallUS.getText().toString();
        String email = emailCallUS.getText().toString();
        String message = messageCallUS.getText().toString();

        RetrofitClient.getInstance(CallUsActivity.this).executeConnectionToServer(CallUsActivity.this,
                "setUserContactMessage", new Request(userID,apiToken,username,email,message), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                String message = mainObject.get("message").getAsString();
                snackbarMessage.make(findViewById(R.id.CallUsParentLayout), message, Snackbar.LENGTH_SHORT).show();
                messageCallUS.setText("");
            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                snackbarMessage.make(findViewById(R.id.CallUsParentLayout), R.string.no_connection, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // to change status bar color
    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}

