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
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.DialogBuilder;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button btnForgetPassword, btnCreateAccount, btnLogin;
    private TextView tvSingIn, tvGooglePlus, tvFacebook;
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
        Common.Instance().changeStatusBarColor(this, "#ffffff");
        setContentView(R.layout.activity_login);

        initViews();

        setupTrans();

        firebaseInit();

        handleEvents();
    }

    private void initViews() {
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

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
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

    private void handleEvents() {
        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (checkEntries(etEmail.getText().toString(), etPassword.getText().toString())) {
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
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });

        // use google
        findViewById(R.id.btn_google_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // use facebook
        findViewById(R.id.btn_facebook_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fb_login.performClick();
            }
        });
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
        } else {
            if (pass.length() < 6) {
                tlPass.setError(getResources().getString(R.string.name_length_hint));
                etPassword.requestFocus();
                return false;
            }

            tlPass.setErrorEnabled(false);
        }
        return true;
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

    // google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = "https://itgeeks.com/images/logo.png";
            if (account.getPhotoUrl() != null) {
                image = account.getPhotoUrl().toString();
            }

            socialLogin(id, name, email, image, providerGoogle);
        } catch (ApiException e) {
            Log.w("signIn:failed code", "" + e.getStatusCode());
            Crashlytics.logException(e);
        } catch (Exception e) {
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

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
    }

    // fb login
    private void getData(final JSONObject object) {
        try {
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/" + object.getString("id") + "/picture?type=normal");
            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();

            socialLogin(id, name, email, image, providerFacebook);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        dialogBuilder.displayLoadingDialog();
        int countryId = SharedPrefManager.getInstance(LoginActivity.this).getCountry().getCountry_id();
        RetrofitClient.getInstance(LoginActivity.this).executeConnectionToServer(LoginActivity.this,
                "loginOrRegisterWithSocial", new Request(provider, id, name, email, image, countryId), new HandleResponses() {
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

    public void login(String email, String pass) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(LoginActivity.this).executeConnectionToServer(LoginActivity.this, "login", new Request(email, pass), new HandleResponses() {
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

    private void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(LoginActivity.this).saveUser(user);
        SharedPrefManager.getInstance(LoginActivity.this).saveProvider(provider); // Provider

        // save || update country
        SharedPrefManager.getInstance(LoginActivity.this)
                .setCountry(GawlaDataBse.getInstance(LoginActivity.this).countryDao().getCountryByID(user.getCountry_id()));
    }
}