package it_geeks.info.gawla_app.views.loginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.views.MainActivity;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    EditText etName, etEmail, etPass;
    ProgressBar progressBar;
    int reconnect = 0 ;
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
        setContentView(R.layout.activity_create_account);

        initViews();

        facebookLogin();
    }

    private void initViews() {
        // ets
        etName = findViewById(R.id.et_create_account_name);
        etEmail = findViewById(R.id.et_create_account_email);
        etPass = findViewById(R.id.et_create_account_pass);
        progressBar = findViewById(R.id.register_loading);

        // finished ? goto next page
        findViewById(R.id.txtCreateAndLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog();
                registerNewUser();
            }
        });

        // have account ? goto previous page
        findViewById(R.id.create_account_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
    }

    // google sign up
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = account.getPhotoUrl().toString();
            String provider = providerGoogle;

            Log.e("Mo7", id + " - " + name + " - " + email + " - " + provider + " - " + image);
            socialLogin(id, name, email, image, provider);

        } catch (ApiException e) {
            Log.w("", "signInResult:failed code=" + e.getStatusCode());

        }
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
            etName.setError("this field can't be empty");
            etName.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("this field can't be empty");
            etEmail.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        if (pass.isEmpty()) {
            etPass.setError("this field can't be empty");
            etPass.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }

        // check validation
        if (name.length() < 6) {
            etName.setError("name should be more than 5 chars");
            etName.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter a valid email address");
            etEmail.requestFocus();
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }

        return true;
    }

    private void connectToServer(final User user, final int countryId) {
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer(CreateAccountActivity.this,
                "register", new Request(user.getName(), user.getEmail(), countryId, user.getPassword()), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                progressBar.setVisibility(View.INVISIBLE);

                // notify user
                Toast.makeText(CreateAccountActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                // save user data locally
                cacheUserData(mainObject, getResources().getString(R.string.app_name));

                // goto next page
                startActivity(new Intent(CreateAccountActivity.this, SubscribePlanActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            @Override
            public void handleEmptyResponse() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                progressBar.setVisibility(View.INVISIBLE);

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

    private void getData(JSONObject object) {
        try {
            URL Profile_Picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=normal");

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

    private void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(CreateAccountActivity.this).getCountry().getCountry_id();
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer(CreateAccountActivity.this,
                "loginOrRegisterWithSocial", new Request(provider, id, name, email, image, countryId), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {

                cacheUserData(mainObject, provider);

                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                Log.e("Mo7", errorMessage);
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
            handleSignInResult(task);
        }

    }

    public void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(CreateAccountActivity.this).saveUser(user);
        SharedPrefManager.getInstance(CreateAccountActivity.this).saveProvider(provider); // Provider
    }

    // google sign up
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void progressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }
}