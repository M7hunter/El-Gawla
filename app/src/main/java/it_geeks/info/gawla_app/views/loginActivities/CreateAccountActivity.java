package it_geeks.info.gawla_app.views.loginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.Controllers.ViewModels.CreateAccountViewModel;
import it_geeks.info.gawla_app.Repositry.RequestsActions;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.general.TransHolder;
import static it_geeks.info.gawla_app.views.loginActivities.LoginActivity.providerGoogle;

public class CreateAccountActivity extends AppCompatActivity {
    String TAG = "Mo7";

    EditText etName, etEmail, etPass;
    ScrollView createAccountMainScreen;
    public int reconnect = 0;

    TextInputLayout tl_create_name, tl_create_email, tl_create_pass;
    Button btnCreateAccount, btnAlreadyHaveAccount;
    TextView tvSignUp, tvGooglePlus, tvFacebook;
    public int REQUEST_GOOGLE = 1000;

    // firebase
    CallbackManager mCallbackManager;
    public static String firebaseToken;
    private FirebaseAuth mAuth;
    LoginButton loginButton;
    GoogleSignInClient mGoogleSignInClient;

    private CardView loadingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_create_account);

        initViews();

        firebaseInit();

        setupTrans();

        handleEvents();

    }

    private void initViews() {
        loadingCard =findViewById(R.id.loading_card);
        createAccountMainScreen = findViewById(R.id.createAccountMainScreen);
        etName = findViewById(R.id.et_create_account_name);
        etEmail = findViewById(R.id.et_create_account_email);
        etPass = findViewById(R.id.et_create_account_pass);

        // translatable views
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvGooglePlus = findViewById(R.id.tv_google_plus_su);
        tvFacebook = findViewById(R.id.tv_facebook_su);
        tl_create_name = findViewById(R.id.tl_create_name);
        tl_create_email = findViewById(R.id.tl_create_email);
        tl_create_pass = findViewById(R.id.tl_create_pass);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnAlreadyHaveAccount = findViewById(R.id.btn_already_have_account);

    }

    private void firebaseInit() {
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("129212834621-ntpf3okjbutvp5um90dl651bdoe812ll.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        LinearLayout btnFace = findViewById(R.id.btn_facebook_sign_up);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        LinearLayout btngoogle = findViewById(R.id.btn_google_sign_up);
        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void setupTrans() {
        TransHolder transHolder = new TransHolder(this);
        transHolder.getSignUpActivityTranses(this);

        tvSignUp.setText(transHolder.sign_up);
        tvGooglePlus.setText(transHolder.via_google_plus);
        tvFacebook.setText(transHolder.via_facebook);
        tl_create_name.setHint(transHolder.full_name);
        tl_create_email.setHint(transHolder.email);
        tl_create_pass.setHint(transHolder.password);
        btnCreateAccount.setText(transHolder.sign_up);
        btnAlreadyHaveAccount.setText(transHolder.already_have_account);
    }

    private void handleEvents() {
        // finished ? goto next page
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingScreen();
                registerNewUser();
            }
        });

        // have account ? goto previous page
        btnAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // loading screen
    public void setLoadingScreen() {
        loadingCard.setVisibility(View.VISIBLE);
    }

    public void closeLoadingScreen() {
        loadingCard.setVisibility(View.GONE);
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
            tl_create_name.setError(getString(R.string.empty_hint));
            etName.requestFocus();
            closeLoadingScreen();
            return false;
        } else tl_create_name.setErrorEnabled(false);
        // check validation
        if (name.length() < 6) {
            tl_create_name.setError(getString(R.string.name_length_hint));
            etName.requestFocus();
            closeLoadingScreen();
            return false;
        } else tl_create_name.setErrorEnabled(false);

        if (email.isEmpty()) {
            tl_create_email.setError(getString(R.string.empty_hint));
            etEmail.requestFocus();
            closeLoadingScreen();
            return false;
        } else tl_create_email.setErrorEnabled(false);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tl_create_email.setError(getString(R.string.enter_valid_email));
            etEmail.requestFocus();
            closeLoadingScreen();
            return false;
        } else tl_create_email.setErrorEnabled(false);
        if (pass.isEmpty()) {
            tl_create_pass.setError(getString(R.string.empty_hint));
            etPass.requestFocus();
            closeLoadingScreen();
            return false;
        } else tl_create_pass.setErrorEnabled(false);

        return true;
    }

    private void connectToServer(final User user, final int countryId) {
        setLoadingScreen();
        RetrofitClient.getInstance(CreateAccountActivity.this).executeConnectionToServer(CreateAccountActivity.this,
                RequestsActions.register.toString(), new Request(user.getName(), user.getEmail(), countryId, user.getPassword()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        closeLoadingScreen();

                        // notify user
                        Toast.makeText(CreateAccountActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                        // save user data locally
                        new CreateAccountViewModel(CreateAccountActivity.this).cacheUserData(mainObject, getResources().getString(R.string.app_name));

                        // goto next page
                        startActivity(new Intent(CreateAccountActivity.this, SubscribePlanActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        closeLoadingScreen();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        closeLoadingScreen();

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        // if (currentUser != null) updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser,String provider) {
        try {
            String id = currentUser.getProviderId();
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String image = currentUser.getPhotoUrl().toString();
            setLoadingScreen();
            new CreateAccountViewModel(this).socialLogin(id, name, email, image, provider);
        }catch (Exception e){}
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user,providerGoogle);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,providerGoogle);
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user,providerGoogle);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null,providerGoogle);
                        }

                        // ...
                    }
                });
    }


}