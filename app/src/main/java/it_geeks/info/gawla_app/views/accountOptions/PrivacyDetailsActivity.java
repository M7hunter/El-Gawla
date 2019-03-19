package it_geeks.info.gawla_app.views.accountOptions;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;

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
    private View loadingCard;

    private boolean editPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
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
        loadingCard = findViewById(R.id.loading_card);

        etEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());

        initProvider();
    }

    public void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PrivacyDetailsActivity.this);
        dialogBuilder.setMessage(getString(R.string.delete_account_hint))
                .setPositiveButton(getResources().getString(R.string.continue_), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
                    public void handleFalseResponse(JsonObject mainObject) {
                    }

                    @Override
                    public void handleEmptyResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Snackbar.make(findViewById(R.id.privacy_details_Page), errorMessage, Snackbar.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void disconnectDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PrivacyDetailsActivity.this);
        dialogBuilder.setMessage(getString(R.string.disconnect) + " ?")
                .setPositiveButton(getResources().getString(R.string.continue_), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disconnect();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
            Log.e("Mo7", e.getMessage() + " ");
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
        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "updateUserData",
                new Request(etEmail.getText().toString(), id, api_token),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        User user = ParseResponses.parseUser(mainObject);
                        etEmail.setText(user.getEmail());
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).saveUser(user);
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

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
                            displayLoading();
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
