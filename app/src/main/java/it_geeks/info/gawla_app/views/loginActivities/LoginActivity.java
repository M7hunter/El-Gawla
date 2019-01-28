package it_geeks.info.gawla_app.views.loginActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.Controllers.ViewModels.LoginViewModel;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.views.MainActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private TextView txtForgetPassword, txtCreateAccount;
    private Button btnLogin;
    private EditText txt_Email, txt_Password;
    public ProgressBar progressBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_login);

        boolean status = SharedPrefManager.getInstance(LoginActivity.this).isLoggedIn();
        String api_token = SharedPrefManager.getInstance(LoginActivity.this).getUser().getApi_token();

        if (status && api_token != null) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        } else {
            initialization();
            facebookLogin();

            // login
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (checkEntries(txt_Email.getText().toString(),txt_Password.getText().toString())) {
                        setLoadingScreen();
                        new LoginViewModel(LoginActivity.this).login(txt_Email.getText().toString(), txt_Password.getText().toString()); // Login ViewModel
                    }
                }
            });

            txtForgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                }
            });
            txtCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                }
            });
        }
    }

    private boolean checkEntries(String email, String pass) {
        if (email.isEmpty()) {
            tlEmail.setError(getResources().getString(R.string.emptyMail));
            txt_Email.requestFocus();
            return false;
        } else tlEmail.setErrorEnabled(false);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tlEmail.setError("enter a valid email address");
            txt_Email.requestFocus();
            return false;
        } else tlEmail.setErrorEnabled(false);

        if (pass.isEmpty()) {
            tlPass.setError(getResources().getString(R.string.emptyPass));
            txt_Password.requestFocus();
            return false;
        } else tlPass.setErrorEnabled(false);
        return true;
    }

    @SuppressLint("WrongViewCast")
    private void initialization() {

        btnLogin = findViewById(R.id.btnLogin);
        txtForgetPassword = findViewById(R.id.btn_forget_password);
        txtCreateAccount = findViewById(R.id.txt_Create_Account);
        txt_Email = findViewById(R.id.txt_Email);
        txt_Password = findViewById(R.id.txt_Password);
        progressBar = findViewById(R.id.login_loading);
        loginMainScreen = findViewById(R.id.loginMainScreen);

        tlEmail = findViewById(R.id.tl_email);
        tlPass = findViewById(R.id.tl_pass);

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
        loginMainScreen.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void closeLoadingScreen() {
        loginMainScreen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            new LoginViewModel(LoginActivity.this).handleSignInResult(task, providerGoogle);
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