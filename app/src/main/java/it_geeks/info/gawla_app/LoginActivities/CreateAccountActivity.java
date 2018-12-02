package it_geeks.info.gawla_app.LoginActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Models.Data;
import it_geeks.info.gawla_app.Models.RequestMainBody;
import it_geeks.info.gawla_app.Models.User;
import it_geeks.info.gawla_app.Models.Request;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    EditText etName, etEmail, etPass;
    ProgressBar progressBar;
    int reconnect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initViews();
    }

    private void initViews() {
        // ets
        etName = findViewById(R.id.et_create_account_name);
        etEmail = findViewById(R.id.et_create_account_email);
        etPass = findViewById(R.id.et_create_account_pass);
        progressBar = (ProgressBar)findViewById(R.id.register_loading);

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

    }

    private void registerNewUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if (checkEntries(name, email, pass)) {

            RequestMainBody requestMainBody = new RequestMainBody(new Data("register"),
                    new Request(name, email, pass));

            connectToServer(requestMainBody, new User(name, email, pass));
        }

    }

    private void connectToServer(final RequestMainBody requestMainBody, final User user) {
        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().registerUser(requestMainBody);
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
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));

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
        boolean active = userData.get("active").getAsBoolean();
        String api_token = userData.get("api_token").getAsString();
        String image = userData.get("image").getAsString();

        user.setName(name);
        user.setEmail(email);
        user.setActive(active);
        user.setApi_token(api_token);
        user.setUser_id(userId);
        user.setImage(image);
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

    private void progressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

}