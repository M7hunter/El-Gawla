package it_geeks.info.gawla_app.views.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;

import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.DialogBuilder;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.R;

import static it_geeks.info.gawla_app.util.Constants.REQ_SET_MEMBERSHIP;

public class MembershipActivity extends AppCompatActivity {

    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        findViewById(R.id.btn_pay_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MembershipActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    private void updateMembership(String membership) {
        dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_SET_MEMBERSHIP, new Request<>(REQ_SET_MEMBERSHIP, SharedPrefManager.getInstance(this).getUser().getUser_id(), SharedPrefManager.getInstance(this).getUser().getApi_token(), membership
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(MembershipActivity.this, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MembershipActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleAfterResponse() {
                        dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        dialogBuilder.hideLoadingDialog();
                        Toast.makeText(MembershipActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
