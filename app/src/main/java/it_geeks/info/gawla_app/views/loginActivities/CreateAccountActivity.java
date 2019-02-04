package it_geeks.info.gawla_app.views.loginActivities;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.Controllers.ViewModels.CreateAccountViewModel;
import it_geeks.info.gawla_app.Repositry.RequestsActions;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.general.TransHolder;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    EditText etName, etEmail, etPass;
    ProgressBar progressBar;
    ScrollView createAccountMainScreen;
    public int reconnect = 0 ;
    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    String providerFacebook = "facebook";
    // google login
    String providerGoogle = "google";
    GoogleSignInClient mGoogleSignInClient;
    public static int GOOGLE_REQUEST = 1000;
    TextInputLayout tl_create_name,tl_create_email,tl_create_pass;

    Button btnCreateAccount, btnAlreadyHaveAccount;
    TextView tvSignUp, tvGooglePlus, tvFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_create_account);

        initViews();

        setupTrans();

        handleEvents();

        facebookLogin();
    }

    private void initViews() {
        progressBar = findViewById(R.id.register_loading);
        createAccountMainScreen = findViewById(R.id.createAccountMainScreen);
        etName = findViewById(R.id.et_create_account_name);
        etEmail = findViewById(R.id.et_create_account_email);
        etPass = findViewById(R.id.et_create_account_pass);

        // translatable views
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvGooglePlus = findViewById(R.id.tv_google_plus_su);
        tvFacebook = findViewById(R.id.tv_facebook_su);
        tl_create_name = findViewById(R.id.tl_create_name);
        tl_create_email = findViewById(R.id.tl_create_email);
        tl_create_pass = findViewById(R.id.tl_create_pass);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnAlreadyHaveAccount = findViewById(R.id.btn_already_have_account);

        //fb login
        callbackManager = CallbackManager.Factory.create();
        callbackManager = CallbackManager.Factory.create();
        btn_fb_login = (LoginButton) findViewById(R.id.login_button);

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        findViewById(R.id.btn_google_sign_up).setOnClickListener(this);

    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(this);
        transHolder.getSignUpActivityTranses(this);

        tvSignUp.setText(transHolder.sign_up);
        tvGooglePlus.setText(transHolder.via_google_plus);
        tvFacebook.setText(transHolder.via_facebook);
        tl_create_name.setHint(transHolder.full_name);
        tl_create_email.setHint(transHolder.email);
        tl_create_pass.setHint(transHolder.password);
        btnCreateAccount.setText(transHolder.sign_up);
        btnAlreadyHaveAccount.setText(transHolder.already_have_account);
    }

    private void handleEvents() {
        // finished ? goto next page
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingScreen();
                registerNewUser();
            }
        });

        // have account ? goto previous page
        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setLoadingScreen(){
        createAccountMainScreen.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void closeLoadingScreen(){
        createAccountMainScreen.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    // google sign up
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_sign_up:
                signIn();
                break;
        }
    }

    // google sign up
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST);
        setLoadingScreen();
    }

    private void registerNewUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        int countryId = SharedPrefManager.getInstance(CreateAccountActivity.this).getCountry().getCountry_id();

        if (checkEntries(name, email, pass)) {
            connectToServer(new User(name, email, pass), countryId);
        }
    }

    private boolean checkEntries(String name, String email, String pass) {
        // check if empty
        if (name.isEmpty()) {
            tl_create_name.setError("this field can't be empty");
            etName.requestFocus();
            closeLoadingScreen();
            return false;
        }else tl_create_name.setErrorEnabled(false);
        // check validation
        if (name.length() < 6) {
            tl_create_name.setError("name should be more than 5 chars");
            etName.requestFocus();
            closeLoadingScreen();
            return false;
        }else tl_create_name.setErrorEnabled(false);

        if (email.isEmpty()) {
            tl_create_email.setError("this field can't be empty");
            etEmail.requestFocus();
            closeLoadingScreen();
            return false;
        }else tl_create_email.setErrorEnabled(false);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tl_create_email.setError("enter a valid email address");
            etEmail.requestFocus();
            closeLoadingScreen();
            return false;
        }else tl_create_email.setErrorEnabled(false);
        if (pass.isEmpty()) {
            tl_create_pass.setError("this field can't be empty");
            etPass.requestFocus();
            closeLoadingScreen();
            return false;
        }else tl_create_pass.setErrorEnabled(false);

        return true;
    }

    private void connectToServer(final User user, final int countryId) {
        setLoadingScreen();
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer(CreateAccountActivity.this,
                RequestsActions.register.toString(), new Request(user.getName(), user.getEmail(), countryId, user.getPassword()), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                closeLoadingScreen();

                // notify user
                Toast.makeText(CreateAccountActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                // save user data locally
                new CreateAccountViewModel(CreateAccountActivity.this).cacheUserData(mainObject, getResources().getString(R.string.app_name));

                // goto next page
                startActivity(new Intent(CreateAccountActivity.this, SubscribePlanActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            @Override
            public void handleEmptyResponse() {
                closeLoadingScreen();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                closeLoadingScreen();

                // notify user
                Toast.makeText(CreateAccountActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                // try one more time
                if (errorMessage.contains("timeout") && reconnect < 1) {
                    reconnect++;
                    connectToServer(user, countryId);
                }
            }
        });

    }

   // facebook Login
    private void facebookLogin() {
        btn_fb_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setLoadingScreen();
                GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                new CreateAccountViewModel(CreateAccountActivity.this).getData(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                mGraphRequest.setParameters(parameters);
                mGraphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(CreateAccountActivity.this, "canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(CreateAccountActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

        LinearLayout btnFace = findViewById(R.id.btn_facebook_sign_up);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fb_login.performClick();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // google login
        if (requestCode == GOOGLE_REQUEST) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            new CreateAccountViewModel(CreateAccountActivity.this).handleSignInResult(task);
        }

    }

    // google sign up
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}