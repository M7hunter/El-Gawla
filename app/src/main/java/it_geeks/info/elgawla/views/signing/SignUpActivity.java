package it_geeks.info.elgawla.views.signing;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import androidx.core.app.TaskStackBuilder;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.repository.Models.User;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.views.account.MembershipActivity;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.intro.IntroActivity;

import static it_geeks.info.elgawla.util.Constants.PREVIOUS_PAGE_KEY;
import static it_geeks.info.elgawla.util.Constants.REQ_SIGN_UP;
import static it_geeks.info.elgawla.util.Constants.REQ_SOCIAL_SIGN;
import static it_geeks.info.elgawla.views.signing.SignInActivity.GOOGLE_REQUEST;
import static it_geeks.info.elgawla.views.signing.SignInActivity.providerFacebook;
import static it_geeks.info.elgawla.views.signing.SignInActivity.providerGoogle;

public class SignUpActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText etName, etEmail, etPhone, etPass;

    private TextInputLayout tl_create_name, tl_create_email, tl_create_phone, tl_create_pass;
    private Button btnCreateAccount, btnAlreadyHaveAccount;
    private TextView tvGooglePlus, tvFacebook;

    // fb login
    CallbackManager callbackManager;
    LoginButton btn_fb_login;

    // google login
    GoogleSignInClient mGoogleSignInClient;

    private DialogBuilder dialogBuilder;

    private SnackBuilder snackBuilder;

    private String previousPageKey = "no key", phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_sign_up);

        getData();

        initViews();

        firebaseInit();

        handleEvents();
    }

    private void getData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            previousPageKey = extras.getString(PREVIOUS_PAGE_KEY);
            phone = extras.getString("phone");
        }
    }

    private void initViews() {
        etName = findViewById(R.id.et_create_account_name);
        etEmail = findViewById(R.id.et_create_account_email);
        etPhone = findViewById(R.id.et_create_account_phone);
        etPass = findViewById(R.id.et_create_account_pass);
        tl_create_name = findViewById(R.id.tl_create_name);
        tl_create_email = findViewById(R.id.tl_create_email);
        tl_create_phone = findViewById(R.id.tl_create_phone);
        tl_create_pass = findViewById(R.id.tl_create_pass);

        tvGooglePlus = findViewById(R.id.tv_google_sign_up);
        tvFacebook = findViewById(R.id.tv_facebook_sign_up);

        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnAlreadyHaveAccount = findViewById(R.id.btn_already_have_account);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        snackBuilder = new SnackBuilder(findViewById(R.id.sign_up_main_layout));

        etPhone.setText(phone);
        etPhone.setEnabled(false);

        initTextWatchers();
    }

    private void initTextWatchers() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tl_create_name.getError() != null)
                {
                    tl_create_name.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tl_create_email.getError() != null)
                {
                    tl_create_email.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tl_create_phone.getError() != null)
                {
                    tl_create_phone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tl_create_pass.getError() != null)
                {
                    tl_create_pass.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousPageKey != null && previousPageKey.equals(IntroActivity.class.getSimpleName()))
                {
                    startActivity(SignInActivity.class);
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
        String phone = etPhone.getText().toString();
        String pass = etPass.getText().toString();
        int countryId = SharedPrefManager.getInstance(SignUpActivity.this).getCountry().getCountry_id();

        if (checkEntries(name, email, phone, pass))
        {
            connectToServer(new User(name, email, phone, pass), countryId);
        }
    }

    private boolean checkEntries(String name, String email, String phone, String pass) {
        // check if empty
        boolean bass = true;

        // name
        if (name.isEmpty())
        {
            tl_create_name.setError(getString(R.string.empty_hint));
            etName.requestFocus();
            bass = false;
        }
        else if (name.length() < 6)
        {
            tl_create_name.setError(getString(R.string.name_length_hint));
            etName.requestFocus();
            bass = false;
        }
        else tl_create_name.setError(null);

        // email
        if (email.isEmpty())
        {
            tl_create_email.setError(getString(R.string.empty_hint));
            etEmail.requestFocus();
            bass = false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            tl_create_email.setError(getString(R.string.enter_valid_email));
            etEmail.requestFocus();
            bass = false;
        }
        else tl_create_email.setError(null);

        // phone
        if (phone.isEmpty())
        {
            tl_create_phone.setError(getString(R.string.empty_hint));
            etPhone.requestFocus();
            bass = false;
        }
        else if (!Patterns.PHONE.matcher(phone).matches())
        {
            tl_create_phone.setError(getString(R.string.enter_valid_phone));
            etPhone.requestFocus();
            bass = false;
        }
        else tl_create_phone.setError(null);

        // pass
        if (pass.isEmpty())
        {
            tl_create_pass.setError(getString(R.string.empty_hint));
            etPass.requestFocus();
            bass = false;
        }
        else
        {
            if (pass.length() < 6)
            {
                tl_create_pass.setError(getResources().getString(R.string.name_length_hint));
                etPass.requestFocus();
                bass = false;
            }
            else tl_create_pass.setError(null);
        }

        return bass;
    }

    private void connectToServer(final User user, final int countryId) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(SignUpActivity.this).fetchDataFromServer(SignUpActivity.this,
                REQ_SIGN_UP, new RequestModel<>(REQ_SIGN_UP, user.getName(), user.getEmail(), countryId, user.getPhone(), user.getPassword(),
                        null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        // notify user
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();
                        // save user data locally
                        cacheUserData(mainObject, getResources().getString(R.string.app_name));
                        EventsManager.sendSignUpEvent(SignUpActivity.this, "sign up");
                        // goto next page
                        TaskStackBuilder
                                .create(getApplicationContext())
                                .addNextIntentWithParentStack(new Intent(SignUpActivity.this, MembershipActivity.class))
                                .startActivities();
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void firebaseInit() {
        //fb login
        callbackManager = CallbackManager.Factory.create();
        btn_fb_login = findViewById(R.id.login_button);
        facebookLogin();

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignIn.getLastSignedInAccount(this);
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
                                getFacebookData(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                mGraphRequest.setParameters(parameters);
                mGraphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                snackBuilder.setSnackText(getString(R.string.canceled)).showSnack();
            }

            @Override
            public void onError(FacebookException exception) {
                snackBuilder.setSnackText(getString(R.string.error_occurred)).showSnack();
            }
        });

        if (AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }

    }

    private void getFacebookData(final JSONObject object) {
        try
        {
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/" + object.getString("id") + "/picture?type=normal");
            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();

            dialogBuilder.displayLoadingDialog();
            socialLogin(id, name, email, image, providerFacebook);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        catch (JSONException e)
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
            getGoogleData(task);
        }
    }

    private void getGoogleData(Task<GoogleSignInAccount> completedTask) {
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
        }
        catch (ApiException e)
        {
            Log.w("signIn:failed code", "" + e.getStatusCode());
            Crashlytics.logException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    // google login
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        snackBuilder.setSnackText(connectionResult.getErrorMessage()).showSnack();
    }

    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(SignUpActivity.this).getCountry().getCountry_id();
        RetrofitClient.getInstance(SignUpActivity.this).fetchDataFromServer(SignUpActivity.this,
                REQ_SOCIAL_SIGN, new RequestModel<>(REQ_SOCIAL_SIGN, provider, id, name, email, image, countryId,
                        null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cacheUserData(mainObject, provider);
                        Common.Instance().updateFirebaseToken(SignUpActivity.this);
                        EventsManager.sendSignUpEvent(SignUpActivity.this, "sign up");
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
        SharedPrefManager.getInstance(SignUpActivity.this).saveUser(user);
        SharedPrefManager.getInstance(SignUpActivity.this).saveProvider(provider); // Provider
    }

    public void startActivity(Class target) {
        startActivity(new Intent(SignUpActivity.this, target)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}