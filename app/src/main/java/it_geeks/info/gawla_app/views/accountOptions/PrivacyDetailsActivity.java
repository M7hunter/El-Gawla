package it_geeks.info.gawla_app.views.accountOptions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;

public class PrivacyDetailsActivity extends AppCompatActivity {
    TextView socialUsername, socialProvider, socialOut, editEmail, editPassword;
    EditText accountEmail, accountPassword;
    ImageView providerImage;
    LinearLayout socialDiv;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_privacy_details);
        init();

        accountEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());
        if (SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider().trim() == "facebook") {
            providerImage.setImageDrawable(getDrawable(R.drawable.com_facebook_button_icon_blue));
            socialProvider.setText(getString(R.string.provider_fb));
        } else if (SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider().trim() == "google") {
            providerImage.setImageDrawable(getDrawable(R.drawable.googleg_standard_color_18));
            socialProvider.setText(getString(R.string.provider_google));
        } else{
            socialProvider.setText(SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider());
            providerImage.setImageDrawable(getDrawable(R.drawable.gawla_logo_blue));
        }

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
                        accountEmail.setEnabled(false);
                        editEmail.setText(getString(R.string.edit));
                    }
                    break;

                //edit pass
                case R.id.edit_password:
                    if (editPassword.getText().toString() == getString(R.string.edit)) {
                        accountPassword.setEnabled(true);
                        editPassword.setText(getString(R.string.save));
                    } else if (editPassword.getText().toString() == getString(R.string.save)) {
                        accountPassword.setEnabled(false);
                        editPassword.setText(getString(R.string.edit));
                    }

                    break;

                //Logout Disconnect
                case R.id.social_out:
                    try {
                        if (SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider().trim() == "facebook") {
                            LoginManager.getInstance().logOut();
                        } else if (SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider().trim() == "google") {
                            if (mGoogleApiClient.isConnected()) {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                mGoogleApiClient.disconnect();
                                mGoogleApiClient.connect();
                            }
                        }
                        Toast.makeText(PrivacyDetailsActivity.this, SharedPrefManager.getInstance(PrivacyDetailsActivity.this).getProvider().trim(), Toast.LENGTH_SHORT).show();
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).clearUser();
                        SharedPrefManager.getInstance(PrivacyDetailsActivity.this).clearProvider();
                        startActivity(new Intent(PrivacyDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

}
