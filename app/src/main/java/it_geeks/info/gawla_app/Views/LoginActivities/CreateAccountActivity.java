package it_geeks.info.gawla_app.Views.LoginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Views.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    EditText etName, etEmail, etPass;
    ProgressBar progressBar;
    int reconnect = 0;
    private static String mApi_token, mUser_id;
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
        findViewById(R.id.txt_haveAccount).setOnClickListener(new View.OnClickListener() {
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

    private void connectToServer(final User user, final int countryId) {

        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer("register", new Request(user.getName(), user.getEmail(), countryId, user.getName()), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                progressBar.setVisibility(View.INVISIBLE);
                // notify user
                Toast.makeText(CreateAccountActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                // save user data locally
                handleServerResponse(mainObject, user);
                SharedPrefManager.getInstance(CreateAccountActivity.this).saveUser(user);
                SharedPrefManager.getInstance(CreateAccountActivity.this).saveProvider(getResources().getString(R.string.app_name)); // Provider
                // goto next page
                startActivity(new Intent(CreateAccountActivity.this, SubscribePlanActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                progressBar.setVisibility(View.INVISIBLE);

                // notify user
                String tMessage = errorMessage;
                Toast.makeText(CreateAccountActivity.this, tMessage, Toast.LENGTH_SHORT).show();

                // try one more time
                if (tMessage.equals("timeout") && reconnect < 1) {
                    reconnect++;
                    connectToServer(user, countryId);
                }
            }
        });

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

    private void handleServerResponse(JsonObject object, User user) {
        JsonObject userData = object.get("userData").getAsJsonObject();
        int userId = userData.get("user_id").getAsInt();
        String name = userData.get("name").getAsString();
        String email = userData.get("email").getAsString();
        String api_token = userData.get("api_token").getAsString();
        String image = userData.get("image").getAsString();

        user.setName(name);
        user.setEmail(email);
        user.setApi_token(api_token);
        user.setUser_id(userId);
        user.setImage(image);

        SharedPrefManager.getInstance(CreateAccountActivity.this).saveUserImage(image);
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
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
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer("loginOrRegisterWithSocial", new Request(provider, id, name, email, image, countryId), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                JsonObject data = mainObject.getAsJsonObject();
                mApi_token = data.get("api_token").getAsString();
                mUser_id = String.valueOf(data.get("user_id").getAsInt());

                SharedPrefManager.getInstance(CreateAccountActivity.this).saveUser(new User(Integer.parseInt(mUser_id), name, email, mApi_token, image));
                SharedPrefManager.getInstance(CreateAccountActivity.this).saveUserImage(image);
                SharedPrefManager.getInstance(CreateAccountActivity.this).saveProvider(provider); // Provider
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

    // google sign up
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void progressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }
}