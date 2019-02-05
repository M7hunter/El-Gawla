package it_geeks.info.gawla_app.views.accountOptions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;

public class PrivacyDetailsActivity extends AppCompatActivity {
    TextView socialUsername, socialProvider, socialOut;
    Button btnEditEmail, btnEditPassword;
    EditText accountEmail;
    ImageView providerImage;
    LinearLayout socialDiv;
    ProgressBar loading;
    private GoogleApiClient mGoogleApiClient;
    ScrollView mainPrivacyDetailsActivity;
    TextInputLayout tlEmail;

    String Provider;

    int id;
    String api_token;

    ProgressBar pbEditPass;
    TextInputLayout tlOldPass;
    TextInputLayout tlNewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_privacy_details);

        id = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getUser().getUser_id();
        api_token = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getUser().getApi_token();

        init();

        accountEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());

        // Logout Disconnect
        socialOut.setOnClickListener(click);
        // make user can edit email
        btnEditEmail.setOnClickListener(click);
        // make user can edit pass
        btnEditPassword.setOnClickListener(click);
        // arrow back
        findViewById(R.id.privacy_details_back).setOnClickListener(click);

    }

    private void init() {
        socialUsername = findViewById(R.id.social_username);
        socialProvider = findViewById(R.id.social_provider);
        providerImage = findViewById(R.id.social_image);
        socialDiv = findViewById(R.id.social_div);
        socialOut = findViewById(R.id.social_out);
        accountEmail = findViewById(R.id.et_account_email);
        btnEditEmail = findViewById(R.id.btn_edit_email);
        btnEditPassword = findViewById(R.id.btn_edit_password);
        loading = findViewById(R.id.privacy_details_loading);
        initProvider();

        tlEmail = findViewById(R.id.tl_privacy_details_email);

        // Swipe Page Back
        mainPrivacyDetailsActivity = findViewById(R.id.privacy_details_Page);
        mainPrivacyDetailsActivity.setOnTouchListener(new OnSwipeTouchListener(PrivacyDetailsActivity.this) {
            public void onSwipeRight() {
                finish();
            }
        });
    }

    private void initProvider() {
        Provider = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider();
        switch (Provider) {
            case LoginActivity.providerFacebook:
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.com_facebook_button_icon_blue));
                socialProvider.setText(getString(R.string.provider_fb));
                break;
            case LoginActivity.providerGoogle:
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.googleg_standard_color_18));
                socialProvider.setText(getString(R.string.provider_google));
                break;
            default:
                socialProvider.setText(SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider());
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.gawla_logo_blue));
                break;
        }
    }

    // On Click Action
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                // edit email
                case R.id.btn_edit_email:
                    if (btnEditEmail.getText().toString() == getString(R.string.edit)) {
                        accountEmail.setEnabled(true);
                        btnEditEmail.setText(getString(R.string.save));
                    } else if (btnEditEmail.getText().toString() == getString(R.string.save)) {

                        if (accountEmail.getText().toString().isEmpty()) { // empty ?
                            tlEmail.setError(getString(R.string.emptyMail));
                            accountEmail.requestFocus();

                        } else { // !empty
                            updateEmail();
                            btnEditEmail.setText(getString(R.string.edit));
                            accountEmail.setEnabled(false);
                            loading.setVisibility(View.VISIBLE);
                        }
                    }
                    break;

                //edit pass
                case R.id.btn_edit_password:
                    displayEditPassDialog();
                    break;

                //Logout Disconnect
                case R.id.social_out:
                    try { //TODO Here Error in Line 165
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).clearUser();
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).clearProvider();
                        startActivity(new Intent(PrivacyDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        LoginManager.getInstance().logOut();
                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient.connect();
                        }
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).clearProvider();
                        finish();
                    } catch (Exception e) {
                        Log.e("Mo7", e.getMessage() + " ");
                    }
                    break;

                // back
                case R.id.privacy_details_back:
                    PrivacyDetailsActivity.this.onBackPressed();
                    break;
            }

        }
    };

    private void displayEditPassDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PrivacyDetailsActivity.this);

        View dialogView = this.getLayoutInflater().inflate(R.layout.edit_pass_layout, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();

        final EditText etOldPass = dialogView.findViewById(R.id.et_old_pass);
        final EditText etNewPass = dialogView.findViewById(R.id.et_new_pass);
        tlOldPass = dialogView.findViewById(R.id.tl_old_pass);
        tlNewPass = dialogView.findViewById(R.id.tl_new_pass);
        Button btnContinue = dialogView.findViewById(R.id.btn_continue_op);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel_op);
        pbEditPass = dialogView.findViewById(R.id.pb_edit_pass);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = etOldPass.getText().toString();
                String newPass = etNewPass.getText().toString();
                if (oldPass.isEmpty()) { // empty ?
                    tlOldPass.setError(getResources().getString(R.string.emptyPass));
                } else { // !empty
                    if (newPass.isEmpty()) { // empty ?
                        tlNewPass.setError(getResources().getString(R.string.emptyPass));
                    } else { // !empty

                        sendPassToServer(dialog, oldPass, newPass);
                        hideEditFields();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendPassToServer(final AlertDialog dialog, String oldPass, String newPass) {
        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "changeUserPasswordByID",
                new Request(id, api_token, oldPass, newPass),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        dialog.dismiss();
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                        dialog.dismiss();
                        Toast.makeText(PrivacyDetailsActivity.this, ParseResponses.parseServerErrors(mainObject), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleEmptyResponse() {
                        displayEditFields();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Snackbar.make(findViewById(R.id.privacy_details_Page), errorMessage, Snackbar.LENGTH_LONG).show();
                        displayEditFields();
                    }
                }
        );
    }

    private void displayEditFields() {
        pbEditPass.setVisibility(View.GONE);
        tlOldPass.setVisibility(View.VISIBLE);
        tlNewPass.setVisibility(View.VISIBLE);
    }

    private void hideEditFields() {
        pbEditPass.setVisibility(View.VISIBLE);
        tlOldPass.setVisibility(View.GONE);
        tlNewPass.setVisibility(View.GONE);
    }

    private void updateEmail() {
        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "updateUserData",
                new Request(accountEmail.getText().toString(), id, api_token),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        User user = ParseResponses.parseUser(mainObject);
                        accountEmail.setText(user.getEmail());
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).saveUser(user);
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        loading.setVisibility(View.GONE);

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Snackbar.make(findViewById(R.id.privacy_details_Page), R.string.connection_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateEmail();
                            }
                        }).show();
                        loading.setVisibility(View.GONE);
                    }
                }
        );
    }

}
