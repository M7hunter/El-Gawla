package it_geeks.info.gawla_app.views.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.DialogBuilder;
import it_geeks.info.gawla_app.general.Interfaces.AlertButtonsClickListener;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.views.login.LoginActivity;

public class PrivacyDetailsActivity extends AppCompatActivity {

    TextView socialUsername, socialProvider, socialOut;
    Button btnEditEmail, btnEditPassword, btnDeleteAccount;
    ImageView providerImage;
    LinearLayout socialDiv;
    private GoogleApiClient mGoogleApiClient;

    String Provider;

    private int id;
    private String api_token;

    private EditText etEmail, etPass;
    private TextInputLayout tlEmail, tlPass;

    private boolean editPass = false;

    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Common.Instance().changeStatusBarColor(this, "#ffffff");
        setContentView(R.layout.activity_privacy_details);

        id = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getUser().getApi_token();

        init();

        handleEvents();

        // Logout Disconnect
        socialOut.setOnClickListener(click);
        // make user can edit email
        btnEditEmail.setOnClickListener(click);
        // arrow back
        findViewById(R.id.privacy_details_back).setOnClickListener(click);
    }

    private void init() {
        socialUsername = findViewById(R.id.social_username);
        socialProvider = findViewById(R.id.social_provider);
        providerImage = findViewById(R.id.social_image);
        socialDiv = findViewById(R.id.social_div);
        socialOut = findViewById(R.id.social_out);
        etEmail = findViewById(R.id.et_account_email);
        etPass = findViewById(R.id.et_account_pass);
        tlEmail = findViewById(R.id.tl_privacy_details_email);
        tlPass = findViewById(R.id.tl_privacy_details_pass);
        btnEditEmail = findViewById(R.id.btn_edit_email);
        btnEditPassword = findViewById(R.id.btn_edit_password);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);

        etEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());

        initProvider();
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private void handleEvents() {
        // edit pass
        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editPass) {
                    passEditMode();

                } else { // check user entry & send it to the server
                    String pass = etPass.getText().toString();
                    if (checkPass(pass)) {
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
        if (pass.isEmpty()) { // empty ?
            tlPass.setError(getResources().getString(R.string.emptyPass));
            etPass.requestFocus();
            return false;
        } else { // !empty
            return true;
        }
    }

    private void sendPassToServer(String Pass) {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.privacy_details_Page), "updating...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "changeUserPasswordByID",
                new Request(id, api_token, Pass),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleAfterResponse() {
                        snackbar.dismiss();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackbar.setText(errorMessage);
                        snackbar.setDuration(BaseTransientBottomBar.LENGTH_LONG).show();
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
        dialogBuilder.createAlertDialog(this, getString(R.string.delete_account_hint), new AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                deleteAccount();
            }

            @Override
            public void onNegativeCLick() {

            }
        });
    }

    private void deleteAccount() {
        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "deactivateUserAccountByID",
                new Request(id, api_token),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        disconnect();
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).clearUser();

                        startActivity(new Intent(PrivacyDetailsActivity.this, LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Snackbar.make(findViewById(R.id.privacy_details_Page), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void disconnectDialog() {
        dialogBuilder.createAlertDialog(this, getString(R.string.disconnect) + " ?", new AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                disconnect();
            }

            @Override
            public void onNegativeCLick() {

            }
        });
    }

    private void disconnect() {
        try {
            SharedPrefManager.getInstance(this).clearUser();
            startActivity(new Intent(this, LoginActivity.class));
            SharedPrefManager.getInstance(this).clearProvider();
            LoginManager.getInstance().logOut();
            if (mGoogleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void initProvider() {
        Provider = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider();
        switch (Provider) {
            case LoginActivity.providerFacebook:
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_button_icon_blue));
                socialProvider.setText(LoginActivity.providerFacebook);
                break;
            case LoginActivity.providerGoogle:
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.googleg_standard_color_18));
                socialProvider.setText(LoginActivity.providerGoogle);
                break;
            default:
                socialProvider.setText(LoginActivity.providerNormalLogin);
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_gawla_logo_two));
                break;
        }
    }

    private void updateEmail() {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "updateUserData",
                new Request(etEmail.getText().toString(), id, api_token, SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getCountry().getCountry_id()),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        User user = ParseResponses.parseUser(mainObject);
                        etEmail.setText(user.getEmail());
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).saveUser(user);
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        Snackbar.make(findViewById(R.id.privacy_details_Page), R.string.error_occurred, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateEmail();
                            }
                        }).show();
                    }
                }
        );
    }

    // On Click Action
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // edit email
                case R.id.btn_edit_email:
                    if (btnEditEmail.getText().toString() == getString(R.string.edit)) {
                        etEmail.setEnabled(true);
                        btnEditEmail.setText(getString(R.string.save));
                    } else if (btnEditEmail.getText().toString() == getString(R.string.save)) {

                        if (etEmail.getText().toString().isEmpty()) { // empty ?
                            tlEmail.setError(getString(R.string.emptyMail));
                            etEmail.requestFocus();

                        } else { // !empty
                            updateEmail();
                            btnEditEmail.setText(getString(R.string.edit));
                            etEmail.setEnabled(false);
                        }
                    }
                    break;
                // back
                case R.id.privacy_details_back:
                    PrivacyDetailsActivity.this.onBackPressed();
                    break;

                case R.id.social_out:
                    disconnectDialog();
                    break;
            }
        }
    };
}
