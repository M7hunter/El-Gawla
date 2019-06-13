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

import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.util.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.account.MembershipActivity;
import it_geeks.info.gawla_app.views.intro.IntroActivity;

import static it_geeks.info.gawla_app.util.Constants.PREVIOUS_PAGE_KEY;
import static it_geeks.info.gawla_app.util.Constants.REQ_SIGN_UP;
import static it_geeks.info.gawla_app.util.Constants.REQ_SOCIAL_SIGN;
import static it_geeks.info.gawla_app.views.login.LoginActivity.GOOGLE_REQUEST;
import static it_geeks.info.gawla_app.views.login.LoginActivity.providerFacebook;
import static it_geeks.info.gawla_app.views.login.LoginActivity.providerGoogle;

public class CreateAccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText etName, etEmail, etPass;

    TextInputLayout tl_create_name, tl_create_email, tl_create_pass;
    Button btnCreateAccount;
    TextView tvSignUp, tvGooglePlus, tvFacebook, tvAlreadyHaveAccount;

    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;

    // google login
    GoogleSignInClient mGoogleSignInClient;

    private DialogBuilder dialogBuilder;
    private String previousPageKey = "no key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_create_account);

        getKey();

        initViews();

        firebaseInit();

        handleEvents();
    }

    private void getKey() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            previousPageKey = extras.getString(PREVIOUS_PAGE_KEY);
    }

    private void initViews() {
        etName = findViewById(R.id.et_create_account_name);
        etEmail = findViewById(R.id.et_create_account_email);
        etPass = findViewById(R.id.et_create_account_pass);

        // translatable views
        tvSignUp = findViewById(R.id.tv_sign_up_header);
        tvGooglePlus = findViewById(R.id.tv_google_sign_up);
        tvFacebook = findViewById(R.id.tv_facebook_sign_up);
        tl_create_name = findViewById(R.id.tl_create_name);
        tl_create_email = findViewById(R.id.tl_create_email);
        tl_create_pass = findViewById(R.id.tl_create_pass);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);
    }

    private void handleEvents() {
        // finished ? goto next page
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        // have account ? goto previous page
        tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousPageKey != null && previousPageKey.equals(IntroActivity.class.getSimpleName()))
                {
                    startActivity(LoginActivity.class);
                }
                else
                {
                    onBackPressed();
                }
            }
        });

        // use google
        tvGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // use facebook
        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fb_login.performClick();
            }
        });
    }

    // google login
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST);
    }

    private void registerNewUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        int countryId = SharedPrefManager.getInstance(CreateAccountActivity.this).getCountry().getCountry_id();

        if (checkEntries(name, email, pass))
        {
            connectToServer(new User(name, email, pass), countryId);
        }
    }

    private boolean checkEntries(String name, String email, String pass) {
        // check if empty
        if (name.isEmpty())
        {
            tl_create_name.setError(getString(R.string.empty_hint));
            etName.requestFocus();
            return false;
        }
        else tl_create_name.setErrorEnabled(false);
        // check validation
        if (name.length() < 6)
        {
            tl_create_name.setError(getString(R.string.name_length_hint));
            etName.requestFocus();
            return false;
        }
        else tl_create_name.setErrorEnabled(false);

        if (email.isEmpty())
        {
            tl_create_email.setError(getString(R.string.empty_hint));
            etEmail.requestFocus();
            return false;
        }
        else tl_create_email.setErrorEnabled(false);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            tl_create_email.setError(getString(R.string.enter_valid_email));
            etEmail.requestFocus();
            return false;
        }
        else tl_create_email.setErrorEnabled(false);
        if (pass.isEmpty())
        {
            tl_create_pass.setError(getString(R.string.empty_hint));
            etPass.requestFocus();
            return false;
        }
        else
        {
            if (pass.length() < 6)
            {
                tl_create_pass.setError(getResources().getString(R.string.name_length_hint));
                etPass.requestFocus();
                return false;
            }

            tl_create_pass.setErrorEnabled(false);
        }

        return true;
    }

    private void connectToServer(final User user, final int countryId) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer(CreateAccountActivity.this,
                REQ_SIGN_UP, new Request<>(REQ_SIGN_UP, user.getName(), user.getEmail(), countryId, user.getPassword(),
                        null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        // notify user
                        Toast.makeText(CreateAccountActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        // save user data locally
                        cacheUserData(mainObject, getResources().getString(R.string.app_name));

                        Common.Instance().updateFirebaseToken(CreateAccountActivity.this);
                        // goto next page
                        startActivity(MembershipActivity.class);
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        Toast.makeText(CreateAccountActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CreateAccountActivity.this, "canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(CreateAccountActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        });

        if (AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

    }

    private void getData(final JSONObject object) {
        try
        {
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/" + object.getString("id") + "/picture?type=normal");
            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();

            dialogBuilder.displayLoadingDialog();
            socialLogin(id, name, email, image, providerFacebook);
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (JSONException e)
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = "https://itgeeks.com/images/logo.png";
            if (account.getPhotoUrl() != null)
            {
                image = account.getPhotoUrl().toString();
            }

            dialogBuilder.displayLoadingDialog();
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

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(CreateAccountActivity.this).getCountry().getCountry_id();
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer(CreateAccountActivity.this,
                REQ_SOCIAL_SIGN, new Request<>(REQ_SOCIAL_SIGN, provider, id, name, email, image, countryId,
                        null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cacheUserData(mainObject, provider);
                        Common.Instance().updateFirebaseToken(CreateAccountActivity.this);
                        startActivity(MainActivity.class);
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
                    }
                });
    }

    public void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(CreateAccountActivity.this).saveUser(user);
        SharedPrefManager.getInstance(CreateAccountActivity.this).saveProvider(provider); // Provider
    }

    public void startActivity(Class target) {
        startActivity(new Intent(CreateAccountActivity.this, target)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}