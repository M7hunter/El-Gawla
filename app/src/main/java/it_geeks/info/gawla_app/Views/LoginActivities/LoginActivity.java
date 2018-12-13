package it_geeks.info.gawla_app.Views.LoginActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Views.MainActivity;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private TextView txtForgetPassword, txtCreateAccount;
    private Button btnLogin;
    private EditText txt_Email, txt_Password;
    private static String mApi_token;
    int mUser_id;
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
        setContentView(R.layout.activity_login);

        boolean status = SharedPrefManager.getInstance(LoginActivity.this).isLoggedIn();
        mApi_token = SharedPrefManager.getInstance(LoginActivity.this).getUser().getApi_token();

        if (status && mApi_token != null) {

            Log.d("M7", "API_Token: " + mApi_token);

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

                    RequestMainBody requestMainBody = new RequestMainBody(new Data("login"), new Request(email, pass));
                    if (email.isEmpty()) {
                        txt_Email.setError(getResources().getString(R.string.emptyMail));
                        txt_Email.requestFocus();
                    } else if (pass.isEmpty()) {
                        txt_Password.setError(getResources().getString(R.string.emptyPass));
                        txt_Password.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        Call<JsonObject> call = RetrofitClient.getInstance(LoginActivity.this).getAPI().request(requestMainBody);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    JsonObject mainObj = response.body().getAsJsonObject();
                                    boolean status = mainObj.get("status").getAsBoolean();

                                    if (status) { // no errors

                                        SharedPrefManager.getInstance(LoginActivity.this).saveUser(handleServerResponse(mainObj));
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    } else { // server errors
                                        Toast.makeText(LoginActivity.this, handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                                    }

                                    //hide progress
                                    progressBar.setVisibility(View.GONE);

                                } catch (NullPointerException e) { // errors of response body
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) { // connection errors
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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

    private User handleServerResponse(JsonObject object) {
        JsonObject userData = object.get("userData").getAsJsonObject();

        mUser_id = userData.get("user_id").getAsInt();
        String name = userData.get("name").getAsString();
        String email = userData.get("email").getAsString();
        mApi_token = userData.get("api_token").getAsString();
        String image = userData.get("image").getAsString();

        SharedPrefManager.getInstance(LoginActivity.this).saveUserImage(image);

        return new User(mUser_id, name, email, mApi_token, image);
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
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
    private void socialLogin(String id, final String name, final String email, final String image, String provider) {

        try {
            RequestMainBody requestMainBody = new RequestMainBody(new Data("loginOrRegisterWithSocial"), new Request(provider, id, name, email, image));
            Call<JsonObject> call = RetrofitClient.getInstance(LoginActivity.this).getAPI().request(requestMainBody);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try{
                        JsonObject data = response.body().getAsJsonObject();
                        mApi_token = data.get("api_token").getAsString();
                        mUser_id = data.get("user_id").getAsInt();

                        SharedPrefManager.getInstance(LoginActivity.this).saveUser(new User(mUser_id, name, email, mApi_token, image));
                        SharedPrefManager.getInstance(LoginActivity.this).saveUserImage(image);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }catch (Exception e){
                        Log.e("Mo7", e.getMessage());
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("Mo7", t.getMessage());
                }

            });

        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
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