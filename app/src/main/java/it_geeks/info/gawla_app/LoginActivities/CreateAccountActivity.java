package it_geeks.info.gawla_app.LoginActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Models.User;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountActivity extends AppCompatActivity {

    EditText etName, etEmail, etPass;
    ProgressDialog progressDialog;

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
            final User user = new User(name, email, pass);

            Call<JsonObject> call = RetrofitClient.getInstance().getAPI().registerUser(user);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressDialog.dismiss();

                    try {
                        JsonObject object = response.body().getAsJsonObject();
                        boolean status = object.get("status").getAsBoolean();

                        if (status) { // if registration gos well

                            SharedPrefManager.getInstance(CreateAccountActivity.this).saveUser(user);

                            startActivity(new Intent(CreateAccountActivity.this, SubscribePlanActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                        } else { // if registration have errors
                            Toast.makeText(CreateAccountActivity.this,
                                    handleServerErrors(object),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (NullPointerException e) {
                        Toast.makeText(CreateAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccountActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private boolean checkEntries(String name, String email, String pass) {
        // check if empty
        if (name.isEmpty()) {
            etName.setError("this field can't be empty");
            etName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("this field can't be empty");
            etEmail.requestFocus();
            return false;
        }

        if (pass.isEmpty()) {
            etPass.setError("this field can't be empty");
            etPass.requestFocus();
            return false;
        }

        // check validation
        if (name.length() < 9) {
            etName.setError("name should be more than 8 chars");
            etName.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonObject errors = object.get("errors").getAsJsonObject();
        JsonArray messages = errors.get("message").getAsJsonArray();
        for (int i = 0; i < messages.size(); i++) {
            error = messages.get(i).getAsString();
        }
        return error;
    }

    private void progressDialog() {
        progressDialog = new ProgressDialog(CreateAccountActivity.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();
    }

}