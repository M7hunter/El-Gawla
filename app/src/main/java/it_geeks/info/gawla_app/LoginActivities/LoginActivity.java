package it_geeks.info.gawla_app.LoginActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.MainActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.SplashActivities.SplashActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
private TextView txtForgetPassword , txtCreateAccount;
private Button btnLogin;
private EditText txt_Email,txt_Password;
public static String api_token;
    SharedPrefManager sharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = new SharedPrefManager(LoginActivity.this);
        String status = sharedPreferences.getAccount_Save().getString("status",null);
        api_token = sharedPreferences.getAccount_Save().getString("api_token",null);
        if (status != null) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        } else {
            initialization();

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = txt_Email.getText().toString();
                    String pass = txt_Password.getText().toString();
                    if (email.isEmpty()) {
                        txt_Email.setError(getResources().getString(R.string.emptyMail));
                    } else if (pass.isEmpty()) {
                        txt_Password.setError(getResources().getString(R.string.emptyPass));
                    } else {
                        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().loginUser(email, pass);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                JsonObject data = response.body().getAsJsonObject();
                                String status = data.get("status").toString();
                                if (status.equals("true")) {
                                    api_token = data.get("api_token").toString();
                                    new SharedPrefManager(LoginActivity.this).Account_Save("true",api_token);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.Invalid_email_password), Toast.LENGTH_SHORT).show();

                                }


                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Check you Internet Access !", Toast.LENGTH_LONG).show();
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

    private void initialization(){
        btnLogin = findViewById(R.id.btnLogin);
        txtForgetPassword = findViewById(R.id.txt_FrogetPassword);
        txtCreateAccount = findViewById(R.id.txt_Create_Account);
        txt_Email = findViewById(R.id.txt_Email);
        txt_Password = findViewById(R.id.txt_Password);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
