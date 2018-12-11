package it_geeks.info.gawla_app.Views.LoginActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private TextView txtForgetPassword , txtCreateAccount;
    private Button btnLogin;
    private EditText txt_Email,txt_Password;
    public static String mApi_token,mUser_id;
    ProgressBar progressBar;
    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    String providerFacebook = "facebook";
    // google login
    String providerGoogle = "google";
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        boolean status = SharedPrefManager.getInstance(LoginActivity.this).isLoggedIn();
        mApi_token = SharedPrefManager.getInstance(LoginActivity.this).getUser().getApi_token();

        if (status && mApi_token != null) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
                        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().loginUser(requestMainBody);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    JsonObject data = response.body().getAsJsonObject();
                                    boolean status = data.get("status").getAsBoolean();
                                    if (status) {
                                        JsonObject Mdata = data.getAsJsonObject("userData");
                                        mApi_token = Mdata.get("api_token").getAsString();
                                        mUser_id = Mdata.get("user_id").getAsString();
                                        String mEmail = Mdata.get("email").getAsString();
                                        String mImage = Mdata.get("image").getAsString();
                                        String mName = Mdata.get("name").getAsString();

                                        SharedPrefManager.getInstance(LoginActivity.this).saveUser(new User(Integer.parseInt(mUser_id), mName, mEmail, mApi_token, mImage));
                                        SharedPrefManager.getInstance(LoginActivity.this).saveUserImage(mImage);

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        JsonArray errors = data.getAsJsonArray("errors");
                                        for (int i = 0; i < errors.size(); i++) {
                                            String s = errors.get(i).getAsString();
                                            Snackbar.make(v, s, 1500).setAction("Action", null).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                } catch (NullPointerException e) {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
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
    private void initialization(){

        btnLogin = findViewById(R.id.btnLogin);
        txtForgetPassword = findViewById(R.id.txt_FrogetPassword);
        txtCreateAccount = findViewById(R.id.txt_Create_Account);
        txt_Email = findViewById(R.id.txt_Email);
        txt_Password = findViewById(R.id.txt_Password);
        progressBar = findViewById(R.id.login_loading);

        //fb login
        callbackManager = CallbackManager.Factory.create();callbackManager = CallbackManager.Factory.create();
        btn_fb_login = (LoginButton) findViewById(R.id.login_button);

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        findViewById(R.id.btn_google_login).setOnClickListener(this);


    }
    // google login
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_login:
                signIn();
                break;
        }
    }


    // google login
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }
    // google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getDisplayName();
            String image = account.getPhotoUrl().toString();
            String provider = providerGoogle;
        } catch (ApiException e) {
            Log.w("", "signInResult:failed code=" + e.getStatusCode());

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

            final int id = object.optInt("id");
            final String name = object.optString("name");
            final String email = object.optString("email");
            final String image = Profile_Picture.toString();
            String provider = providerFacebook;

            try {
                RequestMainBody requestMainBody = new RequestMainBody(new Data("loginOrRegisterWithSocial"),new Request(provider,id,name,email,image));
                Call<JsonObject> call = RetrofitClient.getInstance().getAPI().SocialLoginAndRegister(requestMainBody);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        //TODO  for test
                        mApi_token =  "fj6UGOi3OCWW3kjp2spQOWkqxXW8uP4Ik87xQyK59YjfrGHzzxagjcy1ORFP";
                        mUser_id = String.valueOf(id);
                        SharedPrefManager.getInstance(LoginActivity.this).saveUser(new User(id, name, email, mApi_token, image));
                        SharedPrefManager.getInstance(LoginActivity.this).saveUserImage(image);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(LoginActivity.this, id +" - "+providerFacebook, Toast.LENGTH_LONG).show();
            txt_Email.setText(email);

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
        if (requestCode == 1000) {
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