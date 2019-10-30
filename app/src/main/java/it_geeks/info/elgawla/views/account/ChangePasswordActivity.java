package it_geeks.info.elgawla.views.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.signing.SignInActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_CHANGE_PASSWORD;
import static it_geeks.info.elgawla.util.Constants.REQ_DEACTIVATE_USER_ACCOUNT;
import static it_geeks.info.elgawla.util.Constants.SERVER_MSG;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView socialUsername, socialProvider, socialOut;
    Button btnEditPassword, btnDeleteAccount;
    ImageView providerImage;
    LinearLayout socialDiv;
    private GoogleApiClient mGoogleApiClient;

    private SnackBuilder snackBuilder;

    String Provider;

    private int id;
    private String api_token;

    private EditText etPass;
    private TextInputLayout tlPass;

    private boolean editPass = false;

    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_change_password);

        id = SharedPrefManager.getInstance(ChangePasswordActivity.this).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(ChangePasswordActivity.this).getUser().getApi_token();

        init();

        handleEvents();

        // Logout Disconnect
        socialOut.setOnClickListener(click);
        // arrow back
        findViewById(R.id.back).setOnClickListener(click);
    }

    private void init() {
        socialUsername = findViewById(R.id.social_username);
        socialProvider = findViewById(R.id.social_provider);
        providerImage = findViewById(R.id.social_image);
        socialDiv = findViewById(R.id.social_div);
        socialOut = findViewById(R.id.social_out);
        etPass = findViewById(R.id.et_account_pass);

        tlPass = findViewById(R.id.tl_privacy_details_pass);
        btnEditPassword = findViewById(R.id.btn_edit_password);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);

        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());

        initProvider();
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
        initDisconnectDialog();

        snackBuilder = new SnackBuilder(findViewById(R.id.pass_main_layout));
    }

    private void handleEvents() {
        // edit pass
        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editPass)
                {
                    passEditMode();
                }
                else
                { // check user entry & send it to the server
                    String pass = etPass.getText().toString();
                    if (checkPass(pass))
                    {
                        sendPassToServer(pass);
                    }

                    passDefaultMode();
                }
            }
        });

        // delete account 'deactivate'
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountDialog();
            }
        });
    }

    private boolean checkPass(String pass) {
        if (pass.isEmpty())
        { // empty ?
            tlPass.setError(getResources().getString(R.string.emptyPass));
            etPass.requestFocus();
            return false;
        }
        else
        { // !empty
            return true;
        }
    }

    private void sendPassToServer(String Pass) {
        snackBuilder.setSnackText(getString(R.string.loading)).showSnack();

        RetrofitClient.getInstance(ChangePasswordActivity.this).executeConnectionToServer(
                ChangePasswordActivity.this,
                REQ_CHANGE_PASSWORD, new Request<>(REQ_CHANGE_PASSWORD, id, api_token, Pass
                        , null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get(SERVER_MSG).getAsString()).showSnack();
                        startActivity(new Intent(ChangePasswordActivity.this, SignInActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleAfterResponse() {
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                }
        );
    }

    private void passEditMode() {
        btnEditPassword.setText(getResources().getString(R.string.save));
        etPass.setEnabled(true);
        tlPass.setError(null);
        editPass = true;
    }

    private void passDefaultMode() {
        btnEditPassword.setText(getResources().getString(R.string.edit));
        etPass.setEnabled(false);
        editPass = false;
    }

    private void deleteAccountDialog() {
        dialogBuilder.createAlertDialog(this, new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                deleteAccount();
            }

            @Override
            public void onNegativeCLick() {

            }
        });

        dialogBuilder.setAlertText( getString(R.string.delete_account_hint));
    }

    private void deleteAccount() {
        RetrofitClient.getInstance(ChangePasswordActivity.this).executeConnectionToServer(
                ChangePasswordActivity.this,
                REQ_DEACTIVATE_USER_ACCOUNT, new Request<>(REQ_DEACTIVATE_USER_ACCOUNT, id, api_token
                        , null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get(SERVER_MSG).getAsString()).showSnack();
                        disconnect();
                        SharedPrefManager.getInstance(ChangePasswordActivity.this).clearUser();

                        startActivity(new Intent(ChangePasswordActivity.this, SignInActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                }
        );
    }

    private void initDisconnectDialog() {
        dialogBuilder.createAlertDialog(this, new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                disconnect();
            }

            @Override
            public void onNegativeCLick() {

            }
        });
        dialogBuilder.setAlertText(getString(R.string.disconnect) + " ?");
    }

    private void disconnect() {
        try
        {
            SharedPrefManager.getInstance(this).clearUser();
            startActivity(new Intent(this, SignInActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            SharedPrefManager.getInstance(this).clearProvider();
            LoginManager.getInstance().logOut();
            if (mGoogleApiClient.isConnected())
            {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void initProvider() {
        Provider = SharedPrefManager.getInstance(ChangePasswordActivity.this).getProvider();
        switch (Provider)
        {
            case SignInActivity.providerFacebook:
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_button_icon_blue));
                socialProvider.setText(SignInActivity.providerFacebook);
                break;
            case SignInActivity.providerGoogle:
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.googleg_standard_color_18));
                socialProvider.setText(SignInActivity.providerGoogle);
                break;
            default:
                socialProvider.setText(SignInActivity.providerNormalLogin);
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_gawla_logo_two));
                break;
        }
    }

    // On Click Action
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                // back
                case R.id.back:
                    onBackPressed();
                    break;

                case R.id.social_out:
                    dialogBuilder.displayAlertDialog();
                    break;
            }
        }
    };
}
