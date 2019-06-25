package it_geeks.info.gawla_app.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_SIGN_IN;
import static it_geeks.info.gawla_app.util.Constants.REQ_SOCIAL_SIGN;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button btnForgetPassword, btnLogin;
    private TextView tvCreateAccount;
    private EditText etEmail, etPassword;
    private TextInputLayout tlEmail, tlPass;

    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    public static final String providerFacebook = "facebook", providerGoogle = "google", providerNormalLogin = "gawla";
    public static int GOOGLE_REQUEST = 1000;

    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);

        initViews();

        firebaseInit();

        handleEvents();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_Email);
        etPassword = findViewById(R.id.et_Password);

        tlEmail = findViewById(R.id.tl_email);
        tlPass = findViewById(R.id.tl_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnForgetPassword = findViewById(R.id.btn_forget_password);
        tvCreateAccount = findViewById(R.id.btn_Create_Account);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private void handleEvents() {
        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (checkEntries(etEmail.getText().toString(), etPassword.getText().toString()))
                {
                    login(etEmail.getText().toString(), etPassword.getText().toString()); // Login ViewModel
                }
            }
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        // goto sign up
        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });

        // use google
        findViewById(R.id.tv_google_plus_si).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // use facebook
        findViewById(R.id.tv_facebook_si).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fb_login.performClick();
            }
        });
    }

    private boolean checkEntries(String email, String pass) {
        if (email.isEmpty())
        {
            tlEmail.setError(getResources().getString(R.string.emptyMail));
            etEmail.requestFocus();
            return false;
        }
        else tlEmail.setErrorEnabled(false);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            tlEmail.setError(getString(R.string.enter_valid_email));
            etEmail.requestFocus();
            return false;
        }
        else tlEmail.setErrorEnabled(false);

        if (pass.isEmpty())
        {
            tlPass.setError(getResources().getString(R.string.emptyPass));
            etPassword.requestFocus();
            return false;
        }
        else
        {
            if (pass.length() < 6)
            {
                tlPass.setError(getResources().getString(R.string.name_length_hint));
                etPassword.requestFocus();
                return false;
            }

            tlPass.setErrorEnabled(false);
        }
        return true;
    }

    public void login(String email, String pass) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(LoginActivity.this).executeConnectionToServer(LoginActivity.this,
                REQ_SIGN_IN, new Request<>(REQ_SIGN_IN, email, pass,
                        null, null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        cacheUserData(mainObject, LoginActivity.providerNormalLogin); // with normal provider
                        Common.Instance().updateFirebaseToken(LoginActivity.this);

                        finish();
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseInit() {
        //fb login
        callbackManager = CallbackManager.Factory.create();
        callbackManager = CallbackManager.Factory.create();
        btn_fb_login = (LoginButton) findViewById(R.id.login_button);
        facebookLogin();

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    // google login
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        String id = "", name = "", email = "", image = "";
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            id = account.getId();
            name = account.getDisplayName();
            email = account.getEmail();
            image = "https://itgeeks.com/images/logo.png";
            if (account.getPhotoUrl() != null)
            {
                image = account.getPhotoUrl().toString();
            }

            socialLogin(id, name, email, image, providerGoogle);
        } catch (ApiException e)
        {
            Log.w("signIn:failed code", "" + e.getStatusCode());
            Crashlytics.logException(e);
        } catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    // fb login
    private void facebookLogin() {
        btn_fb_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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
                Toast.makeText(LoginActivity.this, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        });

        if (AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
    }

    // fb login
    private void getData(final JSONObject object) {
        String id = "", name = "", email = "", image = "";
        try
        {
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/" + object.getString("id") + "/picture?type=normal");

            id = object.optString("id");
            name = object.optString("name");
            email = object.optString("email");
            image = Profile_Picture.toString();

            socialLogin(id, name, email, image, providerFacebook);
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (JSONException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // google login
        if (requestCode == GOOGLE_REQUEST)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        dialogBuilder.displayLoadingDialog();
        int countryId = SharedPrefManager.getInstance(LoginActivity.this).getCountry().getCountry_id();
        RetrofitClient.getInstance(LoginActivity.this).executeConnectionToServer(LoginActivity.this,
                REQ_SOCIAL_SIGN, new Request<>(REQ_SOCIAL_SIGN, provider, id, name, email, image, countryId,
                        null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        cacheUserData(mainObject, provider);
                        Common.Instance().updateFirebaseToken(LoginActivity.this);

                        finish();
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(LoginActivity.this).saveUser(user);
        SharedPrefManager.getInstance(LoginActivity.this).saveProvider(provider); // Provider

        // save || update country
        SharedPrefManager.getInstance(LoginActivity.this)
                .setCountry(GawlaDataBse.getInstance(LoginActivity.this).countryDao().getCountryByID(user.getCountry_id()));
    }
}