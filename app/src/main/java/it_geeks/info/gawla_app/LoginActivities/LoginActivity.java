package it_geeks.info.gawla_app.LoginActivities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.MainActivity;
import it_geeks.info.gawla_app.Models.UserLogin;
import it_geeks.info.gawla_app.Models.data;
import it_geeks.info.gawla_app.Models.request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
private TextView txtForgetPassword , txtCreateAccount;
private Button btnLogin;
private EditText txt_Email,txt_Password;
public static String api_token,user_id;
    SharedPrefManager sharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = new SharedPrefManager(LoginActivity.this);
        boolean status = sharedPreferences.getAccount_Save().getBoolean("status",false);
        api_token = sharedPreferences.getAccount_Save().getString("api_token",null);
        if (status && user_id != null) {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        } else {
            initialization();

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String email = txt_Email.getText().toString();
                    String pass = txt_Password.getText().toString();

                    UserLogin userLogin = new UserLogin(new data("login",new request(email,pass)));
                    if (email.isEmpty()) {
                        txt_Email.setError(getResources().getString(R.string.emptyMail));
                        txt_Email.requestFocus();
                    } else if (pass.isEmpty()) {
                        txt_Password.setError(getResources().getString(R.string.emptyPass));
                        txt_Password.requestFocus();
                    } else {
                        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().loginUser(userLogin);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                        JsonObject data = response.body().getAsJsonObject();
                                        boolean status = data.get("status").getAsBoolean();
                                        if (status) {
                                            JsonObject Mdata = data.getAsJsonObject("userData");
                                            api_token = Mdata.get("api_token").toString();
                                            user_id = Mdata.get("user_id").toString();
                                            new SharedPrefManager(LoginActivity.this).Account_Save(status,api_token,user_id);
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            JsonArray errors = data.getAsJsonArray("errors");
                                            for (int i = 0; i < errors.size() ; i++) {
                                                String s = errors.get(i).getAsString();
                                                Snackbar.make(v, s, 1500).setAction("Action", null).show();
                                            }
                                        }

                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
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
