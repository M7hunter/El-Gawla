package it_geeks.info.elgawla.views.signing;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.User;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.ParseResponses;
import it_geeks.info.elgawla.repository.RESTful.Request;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.account.MembershipActivity;
import it_geeks.info.elgawla.views.main.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;

import static it_geeks.info.elgawla.util.Constants.REQ_USER_ACTIVATION;

public class ActivationActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnConfirm;

    private String cashedCode, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        init();

        getData();

        handleEvents();
    }

    private void getData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            cashedCode = extras.getString("activation_code");
            phone = extras.getString("phone");

            Log.d("activation", "code:" + cashedCode + ", phone: " + phone);
        }
    }

    private void init() {
        etCode = findViewById(R.id.et_activation_code);
        btnConfirm = findViewById(R.id.btn_activation_confirm);
    }

    private void handleEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();

                if (code.isEmpty())
                {
                    etCode.setError(getString(R.string.empty_hint));
                    etCode.requestFocus();
                }
                else
                {
                    if (cashedCode.equals(code))
                    {
                        startActivity(new Intent(ActivationActivity.this, SignUpActivity.class)
                                .putExtra("phone", phone));
                    }
                    else
                    {
                        etCode.setError(getString(R.string.wrong_code));
                        etCode.requestFocus();
                    }
                }
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCode.getError() != null)
                {
                    etCode.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
