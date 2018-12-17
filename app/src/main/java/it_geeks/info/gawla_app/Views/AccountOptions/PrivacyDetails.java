package it_geeks.info.gawla_app.Views.AccountOptions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;

public class PrivacyDetails extends AppCompatActivity {
    TextView socialUsername, socialProvider, socialOut, editEmail, editPassword;
    EditText accountEmail, accountPassword;
    ImageView providerImage;
    LinearLayout socialDiv;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_privacy_details);
        init();

        accountEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        socialUsername.setText(SharedPrefManager.getInstance(this).getUser().getName());
        if (SharedPrefManager.getInstance(PrivacyDetails.this).getProvider().trim() == "facebook") {
            providerImage.setImageDrawable(getDrawable(R.drawable.com_facebook_button_icon_blue));
            socialProvider.setText(getString(R.string.provider_fb));
        } else if (SharedPrefManager.getInstance(PrivacyDetails.this).getProvider().trim() == "google") {
            providerImage.setImageDrawable(getDrawable(R.drawable.googleg_standard_color_18));
            socialProvider.setText(getString(R.string.provider_google));
        } else{
            socialProvider.setText(SharedPrefManager.getInstance(PrivacyDetails.this).getProvider());
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

    // to change status bar color
    public void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
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
                        if (SharedPrefManager.getInstance(PrivacyDetails.this).getProvider() == "facebook") {
                            LoginManager.getInstance().logOut();
                        } else if (SharedPrefManager.getInstance(PrivacyDetails.this).getProvider() == "google") {
                            if (mGoogleApiClient.isConnected()) {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                mGoogleApiClient.disconnect();
                                mGoogleApiClient.connect();
                            }
                        }
                        SharedPrefManager.getInstance(PrivacyDetails.this).clearUser();
                        SharedPrefManager.getInstance(PrivacyDetails.this).clearProvider();
                        startActivity(new Intent(PrivacyDetails.this, LoginActivity.class));
                        PrivacyDetails.this.finish();
                    } catch (Exception e) {
                        Log.e("Mo7", e.getMessage());
                    }
                    break;

                // back
                case R.id.privacy_details_back:
                    PrivacyDetails.this.onBackPressed();
                    break;

            }

        }
    };

}
