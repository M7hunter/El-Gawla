package it_geeks.info.gawla_app.views.loginActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.Controllers.ViewModels.LoginViewModel;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.TransHolder;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    String TAG = "Mo7";

    private Button btnForgetPassword, btnCreateAccount, btnLogin;
    private EditText etEmail, etPassword;
    ScrollView loginMainScreen;
    TextInputLayout tlEmail, tlPass;

    private CardView loadingCard;

    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    public static final String providerFacebook = "facebook";

    // google login
    public static final String providerGoogle = "google";
    GoogleSignInClient mGoogleSignInClient;
    public static int GOOGLE_REQUEST = 1000;

    // normal login
    public static final String providerNormalLogin = "gawla";

    private TextView tvSingIn, tvGooglePlus, tvFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_login);

        initialization();

        firebaseInit();

        setupTrans();

        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (checkEntries(etEmail.getText().toString(), etPassword.getText().toString())) {
                    displayLoading();
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

    @SuppressLint("WrongViewCast")
    private void initialization() {
        loadingCard = findViewById(R.id.loading_card);
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
            tlEmail.setError(getString(R.string.enter_valid_email));
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

    public void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void firebaseInit(){

        //fb login
        callbackManager = CallbackManager.Factory.create();callbackManager = CallbackManager.Factory.create();
        btn_fb_login = (LoginButton) findViewById(R.id.login_button);
        facebookLogin();

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        findViewById(R.id.btn_google_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_facebook_sign_in).setOnClickListener(this);

    }
    // google login
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_sign_in:
                signIn();
                break;
            case R.id.btn_facebook_sign_in:
                btn_fb_login.performClick();
                break;
        }
    }

    // google login
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST);
    }

    // google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = account.getPhotoUrl().toString();
            String provider = providerGoogle;

            Log.e("Mo7", id + name + email + image + provider);
            displayLoading();
            new LoginViewModel(this).socialLogin(id, name, email, image, provider);
        } catch (ApiException e) {
            Log.w("Mo7", "signInResult:failed code=" + e.getStatusCode());

        }
    }
    // fb login
    private void facebookLogin() {
        btn_fb_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                getData(object);
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

        if(AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

    }
    // fb login
    private void getData(final JSONObject object) {

        try{
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/"+object.getString("id")+"/picture?type=normal");
            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();
            String provider = providerFacebook;

            Log.e("Mo7", id + name + email + image + provider);
            displayLoading();
            new LoginViewModel(this).socialLogin(id, name, email, image, provider);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        // google login
        if (requestCode == GOOGLE_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}

