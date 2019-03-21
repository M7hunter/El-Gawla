package it_geeks.info.gawla_app.views.loginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.R;

public class SubscribePlanActivity extends AppCompatActivity {

    private CardView loadingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff",this);
        setContentView(R.layout.activity_subscribe_plan);

        loadingCard = findViewById(R.id.loading_card);

        findViewById(R.id.btn_pay_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubscribePlanActivity.this,MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        findViewById(R.id.card_standard_membership).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMembership("basic");
            }
        });

        findViewById(R.id.card_golden_membership).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMembership("gold");
            }
        });
    }

    public void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void updateMembership(String membership) {
        displayLoading();
        RetrofitClient.getInstance(this).executeConnectionToServer(this, "setUserMembership",
                new Request(SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(), membership), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(SubscribePlanActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SubscribePlanActivity.this,MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject errorObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        hideLoading();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        hideLoading();
                        Toast.makeText(SubscribePlanActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
