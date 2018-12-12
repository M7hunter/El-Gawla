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
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    EditText etName, etEmail, etPass;
    ProgressBar progressBar;
    int reconnect = 0;
    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;
    String providerFacebook = "facebook";
    // google login
    String providerGoogle = "google";
    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient googleApiClint;

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

    private void registerNewUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        int countryId = SharedPrefManager.getInstance(CreateAccountActivity.this).getCountryId();

        if (checkEntries(name, email, pass)) {
            RequestMainBody requestMainBody = new RequestMainBody(new Data("register"),
                    new Request(name, email, countryId, pass));
            connectToServer(requestMainBody, new User(name, email, pass));
        }
    }

    private void connectToServer(final RequestMainBody requestMainBody, final User user) {
        try {

        }catch (Exception e){ }
        Call<JsonObject> call = RetrofitClient.getInstance(CreateAccountActivity.this).getAPI().request(requestMainBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.INVISIBLE);
                try {
                    JsonObject object = response.body().getAsJsonObject();
                    boolean status = object.get("status").getAsBoolean();
                    if (status) { // if registration gos well
                        // notify user
                        Toast.makeText(CreateAccountActivity.this, object.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                        // save user data locally
                        handleServerResponse(object, user);
                        SharedPrefManager.getInstance(CreateAccountActivity.this).saveUser(user);

                        // goto next page
                        startActivity(new Intent(CreateAccountActivity.this, SubscribePlanActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                    } else { // if registration have errors
                        // notify user
                        Toast.makeText(CreateAccountActivity.this,
                                handleServerErrors(object),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    // notify user
                    Toast.makeText(CreateAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);

                // notify user
                String tMessage = t.getMessage();
                Toast.makeText(CreateAccountActivity.this, tMessage, Toast.LENGTH_SHORT).show();

                // try one more time
                if (tMessage.equals("timeout") && reconnect < 1) {
                    reconnect++;
                    connectToServer(requestMainBody, user);
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
                Toast.makeText(CreateAccountActivity.this, "canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(CreateAccountActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if(AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

    }

    private void getData(JSONObject object) {
        try{
            URL Profile_Picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();
            String provider = providerFacebook;
            Toast.makeText(CreateAccountActivity.this, id +" - "+ providerFacebook, Toast.LENGTH_LONG).show();
            etEmail.setText(email);
            etName.setText(name);
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

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private void progressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }
}