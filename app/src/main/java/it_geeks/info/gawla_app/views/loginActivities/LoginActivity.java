package it_geeks.info.gawla_app.views.loginActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.Controllers.ViewModels.LoginViewModel;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.general.TransHolder;
import it_geeks.info.gawla_app.views.MainActivity;

public class LoginActivity extends AppCompatActivity {
    String TAG = "Mo7";

    private Button btnForgetPassword, btnCreateAccount, btnLogin;
    private EditText etEmail, etPassword;
    ScrollView loginMainScreen;
    TextInputLayout tlEmail, tlPass;

    private CardView loadingCard;

    // facebook
    public static final String providerFacebook = "Facebook";
    // google login
    public static final String providerGoogle = "Google";
    // normal login
    public static final String providerNormalLogin = "Gawla";

    public int REQUEST_GOOGLE = 1000;
    private TextView tvSingIn, tvGooglePlus, tvFacebook;

    // firebase
    CallbackManager mCallbackManager;
    public static String firebaseToken;
    private FirebaseAuth mAuth;
    LoginButton loginButton;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
        setContentView(R.layout.activity_login);

        boolean status = SharedPrefManager.getInstance(LoginActivity.this).isLoggedIn();
        String api_token = SharedPrefManager.getInstance(LoginActivity.this).getUser().getApi_token();

        initialization();

        firebaseInit();

        setupTrans();

        if (status && api_token != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            // login
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (checkEntries(etEmail.getText().toString(), etPassword.getText().toString())) {
                        setLoadingScreen();
                        new LoginViewModel(LoginActivity.this).login(etEmail.getText().toString(), etPassword.getText().toString()); // Login ViewModel
                    }
                }
            });

            btnForgetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                }
            });

            btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
                }
            });
        }
    }

    @SuppressLint("WrongViewCast")
    private void initialization() {
        loadingCard =findViewById(R.id.loading_card);
        loginMainScreen = findViewById(R.id.loginMainScreen);
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

        LinearLayout btnFace = findViewById(R.id.btn_facebook_sign_in);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        LinearLayout btngoogle = findViewById(R.id.btn_google_sign_in);
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
        } else tlPass.setErrorEnabled(false);
        return true;
    }

    public void setLoadingScreen() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void closeLoadingScreen() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static String FirebaseInstanceTokenID() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            // Get new Instance ID token
                            firebaseToken = task.getResult().getToken();
                    }
                });
        return firebaseToken ;
    }   /// firebase token

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
       // if (currentUser != null) updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser,String provider) {
        try {
            String id = currentUser.getProviderId();
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String image = currentUser.getPhotoUrl().toString();
            setLoadingScreen();
            new LoginViewModel(this).socialLogin(id, name, email, image, provider);
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
                            updateUI(user,providerFacebook);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,providerFacebook);
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,providerGoogle);
                        }

                        // ...
                    }
                });
    }


}