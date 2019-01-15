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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private TextView txtForgetPassword, txtCreateAccount;
    private Button btnLogin;
    private EditText txt_Email, txt_Password;
    ProgressBar progressBar;
    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    String providerFacebook = "facebook";
    // google login
    String providerGoogle = "google";
    GoogleSignInClient mGoogleSignInClient;
    public static int GOOGLE_REQUEST = 1000;

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
                    String email = txt_Email.getText().toString();
                    String pass = txt_Password.getText().toString();

                    if (email.isEmpty()) {
                        txt_Email.setError(getResources().getString(R.string.emptyMail));
                        txt_Email.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        txt_Email.setError("enter a valid email address");
                        txt_Email.requestFocus();
                    } else if (pass.isEmpty()) {
                        txt_Password.setError(getResources().getString(R.string.emptyPass));
                        txt_Password.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);

                        RetrofitClient.getInstance(LoginActivity.this).executeConnectionToServer(LoginActivity.this
                                , "login", new Request(email, pass), new HandleResponses() {
                            @Override
                            public void handleResponseData(JsonObject mainObject) {

                                cacheUserData(mainObject, getResources().getString(R.string.app_name)); // with normal provider

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                                //hide progress
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void handleEmptyResponse() {
//                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void handleConnectionErrors(String errorMessage) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
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


    @SuppressLint("WrongViewCast")
    private void initialization() {

        btnLogin = findViewById(R.id.btnLogin);
        txtForgetPassword = findViewById(R.id.txt_forget_password);
        txtCreateAccount = findViewById(R.id.txt_Create_Account);
        txt_Email = findViewById(R.id.txt_Email);
        txt_Password = findViewById(R.id.txt_Password);
        progressBar = findViewById(R.id.login_loading);

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

    // google login
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST);
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
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = account.getPhotoUrl().toString();
            String provider = providerGoogle;

            socialLogin(id, name, email, image, provider);

            Log.w("Mo7", id + name + email + image);

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

    // fb login
    private void getData(final JSONObject object) {
        try {
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/" + object.getString("id") + "/picture?type=normal");
            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();
            String provider = providerFacebook;

            socialLogin(id, name, email, image, provider);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // social login
    private void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(LoginActivity.this).getCountry().getCountry_id();
        RetrofitClient.getInstance(LoginActivity.this).executeConnectionToServer(LoginActivity.this,
                "loginOrRegisterWithSocial", new Request(provider, id, name, email, image, countryId), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {

                cacheUserData(mainObject, provider);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(LoginActivity.this).saveUser(user);
        SharedPrefManager.getInstance(LoginActivity.this).saveProvider(provider); // Provider

        // save || update country
        SharedPrefManager.getInstance(LoginActivity.this)
                .setCountry(GawlaDataBse.getGawlaDatabase(LoginActivity.this).countryDao().getCountryByID(user.getCountry_id()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}