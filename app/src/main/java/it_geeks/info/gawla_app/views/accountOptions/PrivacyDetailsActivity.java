package it_geeks.info.gawla_app.views.accountOptions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

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
import it_geeks.info.gawla_app.views.menuOptions.CallUsActivity;
import it_geeks.info.gawla_app.views.menuOptions.PrivacyPolicyActivity;

public class PrivacyDetailsActivity extends AppCompatActivity {
    TextView socialUsername, socialProvider, socialOut, editEmail, editPassword;
    EditText accountEmail, accountPassword;
    ImageView providerImage;
    LinearLayout socialDiv;
    ProgressBar loading;
    private GoogleApiClient mGoogleApiClient;
    ScrollView mainPrivacyDetailsActivity;
    TextInputLayout tlEmail, tlPass;

    String Provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_privacy_details);
        init();

        accountEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());

        // Logout Disconnect
        socialOut.setOnClickListener(click);
        // make user can edit email
        editEmail.setOnClickListener(click);
        // make user can edit pass
        editPassword.setOnClickListener(click);
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
        accountPassword = findViewById(R.id.et_account_password);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        loading = findViewById(R.id.privacy_details_loading);
        initProvider();

        tlEmail = findViewById(R.id.tl_privacy_details_email);
        tlPass = findViewById(R.id.tl_privacy_details_pass);

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
                providerImage.setImageDrawable(getDrawable(R.drawable.com_facebook_button_icon_blue));
                socialProvider.setText(getString(R.string.provider_fb));
                break;
            case LoginActivity.providerGoogle:
                providerImage.setImageDrawable(getDrawable(R.drawable.googleg_standard_color_18));
                socialProvider.setText(getString(R.string.provider_google));
                break;
            default:
                socialProvider.setText(SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider());
                providerImage.setImageDrawable(getDrawable(R.drawable.gawla_logo_blue));
                break;
        }
    }

    // On Click Action
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                // edit email
                case R.id.edit_email:
                    if (editEmail.getText().toString() == getString(R.string.edit)) {
                        accountEmail.setEnabled(true);
                        editEmail.setText(getString(R.string.save));
                    } else if (editEmail.getText().toString() == getString(R.string.save)) {

                        if (accountEmail.getText().toString().isEmpty()) { // empty ?
                            tlEmail.setError(getString(R.string.emptyMail));
                            accountEmail.requestFocus();

                        } else { // !empty
                            updateEmail();
                            editEmail.setText(getString(R.string.edit));
                            accountEmail.setEnabled(false);
                            loading.setVisibility(View.VISIBLE);
                        }
                    }
                    break;

                //edit pass
                case R.id.edit_password:
                    if (editPassword.getText().toString() == getString(R.string.edit)) {
                        accountPassword.setEnabled(true);
                        editPassword.setText(getString(R.string.save));

                    } else if (editPassword.getText().toString() == getString(R.string.save)) {
                        if (accountPassword.getText().toString().isEmpty()) { // empty ?
                            accountPassword.requestFocus();
                            tlPass.setError(getString(R.string.emptyPass));

                        } else { // !empty
                            accountPassword.setEnabled(false);
                            editPassword.setText(getString(R.string.edit));
                        }
                    }

                    break;

                //Logout Disconnect
                case R.id.social_out:
                    try {
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
                        PrivacyDetailsActivity.this.finish();
                    } catch (Exception e) {
                        Log.e("Mo7", e.getMessage());
                    }
                    break;

                // back
                case R.id.privacy_details_back:
                    PrivacyDetailsActivity.this.onBackPressed();
                    break;
            }

        }
    };

    private void updateEmail() {
        int id = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getUser().getApi_token();
        RetrofitClient.getInstance(PrivacyDetailsActivity.this).executeConnectionToServer(
                PrivacyDetailsActivity.this,
                "updateUserData",
                new Request(accountEmail.getText().toString(), id, api_token),
                new HandleResponses() {
                    @Override
                    public void handleResponseData(JsonObject mainObject) {
                        User user = ParseResponses.parseUser(mainObject);
                        accountEmail.setText(user.getEmail());
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).saveUser(user);
                        Toast.makeText(PrivacyDetailsActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
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
