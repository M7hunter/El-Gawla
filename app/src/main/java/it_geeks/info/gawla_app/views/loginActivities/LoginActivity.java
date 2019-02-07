package it_geeks.info.gawla_app.views.loginActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.Controllers.ViewModels.LoginViewModel;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button btnForgetPassword, btnCreateAccount, btnLogin;
    private EditText etEmail, etPassword;
    ProgressDialog progress;

    ScrollView loginMainScreen;
    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    public static final String providerFacebook = "facebook";
    // google login
    public static final String providerGoogle = "google";
    GoogleSignInClient mGoogleSignInClient;
    public static int GOOGLE_REQUEST = 1000;
    GoogleApiClient mGoogleApiClient;

    TextInputLayout tlEmail, tlPass;

    private TextView tvSingIn, tvGooglePlus, tvFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_login);

        boolean status = SharedPrefManager.getInstance(LoginActivity.this).isLoggedIn();
        String api_token = SharedPrefManager.getInstance(LoginActivity.this).getUser().getApi_token();

        initialization();

        setupTrans();

        if (status && api_token != null) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        } else {
            facebookLogin();

            // login
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (checkEntries(etEmail.getText().toString(), etPassword.getText().toString())) {
                        setLoadingScreen();
                        new LoginViewModel(LoginActivity.this).login(etEmail.getText().toString(), etPassword.getText().toString()); // Login ViewModel
                    }
                }
            });

            btnForgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                }
            });

            btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                }
            });
        }
    }

    @SuppressLint("WrongViewCast")
    private void initialization() {
        progress = new ProgressDialog(this);
        loginMainScreen = findViewById(R.id.loginMainScreen);
        etEmail = findViewById(R.id.et_Email);
        etPassword = findViewById(R.id.et_Password);

        // translatable views
        tvSingIn = findViewById(R.id.tv_sign_in);
        tvGooglePlus = findViewById(R.id.tv_google_plus_si);
        tvFacebook = findViewById(R.id.tv_facebook_si);
        tlEmail = findViewById(R.id.tl_email);
        tlPass = findViewById(R.id.tl_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnForgetPassword = findViewById(R.id.btn_forget_password);
        btnCreateAccount = findViewById(R.id.btn_Create_Account);

        //fb login
        callbackManager = CallbackManager.Factory.create();
        callbackManager = CallbackManager.Factory.create();
        btn_fb_login = (LoginButton) findViewById(R.id.login_button);

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        findViewById(R.id.btn_google_sign_in).setOnClickListener(this);
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(this);
        transHolder.getSignInActivityTranses(this);

        tvSingIn.setText(transHolder.sign_in);
        tvGooglePlus.setText(transHolder.via_google_plus);
        tvFacebook.setText(transHolder.via_facebook);
        tlEmail.setHint(transHolder.email);
        tlPass.setHint(transHolder.password);
        btnLogin.setText(transHolder.sign_in);
        btnForgetPassword.setText(transHolder.forget_pass);
        btnCreateAccount.setText(transHolder.create_account);
    }

    private boolean checkEntries(String email, String pass) {
        if (email.isEmpty()) {
            tlEmail.setError(getResources().getString(R.string.emptyMail));
            etEmail.requestFocus();
            return false;
        } else tlEmail.setErrorEnabled(false);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tlEmail.setError("enter a valid email address");
            etEmail.requestFocus();
            return false;
        } else tlEmail.setErrorEnabled(false);

        if (pass.isEmpty()) {
            tlPass.setError(getResources().getString(R.string.emptyPass));
            etPassword.requestFocus();
            return false;
        } else tlPass.setErrorEnabled(false);
        return true;
    }

    // google login
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_sign_in:
                signIn();
                break;
        }
    }

    // loading screen
    public void setLoadingScreen() {
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

    }

    public void closeLoadingScreen() {
        progress.dismiss();
    }

    // google login
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST);
        setLoadingScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // google login
        if (requestCode == GOOGLE_REQUEST) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission (Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                new LoginViewModel(LoginActivity.this).handleSignInResult(task, providerGoogle);
            }else{
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                new LoginViewModel(LoginActivity.this).handleSignInResult(task, providerGoogle);
            }


        }
    }

    // fb login
    private void facebookLogin() {
        btn_fb_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accesstoken = loginResult.getAccessToken().getToken();
                setLoadingScreen();
                GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                new LoginViewModel(LoginActivity.this).getData(object, providerFacebook);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                mGraphRequest.setParameters(parameters);
                mGraphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

        LinearLayout btnFace = findViewById(R.id.btn_facebook_sign_in);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fb_login.performClick();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (loginMainScreen.getVisibility() == View.INVISIBLE) {
            try {
                SharedPrefManager.getInstance(LoginActivity.this).clearUser();
                SharedPrefManager.getInstance(LoginActivity.this).clearProvider();
                startActivity(new Intent(LoginActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                LoginManager.getInstance().logOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
                SharedPrefManager.getInstance(LoginActivity.this).clearProvider();
            } catch (Exception e) {
                Log.e("Mo7", e.getMessage());
            }
        } else super.onBackPressed();

    }

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}