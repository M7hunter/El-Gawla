package it_geeks.info.elgawla.views.store;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.BaseActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentReviewActivity extends BaseActivity {

    private TextView tvPaymentId, tvResult, tvAmount, tvTransId, tvTrackId, tvCreatedAt;
    private String paymentId, result, amount, transactionId, trackId, createdAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_review);

        initViews();

        try
        {
            getData();
            bindData();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        tvPaymentId = findViewById(R.id.tv_review_payment_id);
        tvResult = findViewById(R.id.tv_review_result);
        tvAmount = findViewById(R.id.tv_review_amount);
        tvTransId = findViewById(R.id.tv_review_transaction_id);
        tvTrackId = findViewById(R.id.tv_review_track_id);
        tvCreatedAt = findViewById(R.id.tv_review_created_at);
    }

    private void getData() {
        JSONObject paymentObj = MyJavaScriptInterface.paymentObj;

        try
        {
            paymentId = paymentObj.getString("paymentid");
            result = paymentObj.getString("result");
            amount = paymentObj.getString("amt");
            transactionId = paymentObj.getString("tranid");
            trackId = paymentObj.getString("trackid");
            createdAt = paymentObj.getString("postdate");

            Log.d("json_obj_HERE/paymentId", paymentId);
            Log.d("json_obj_HERE/result", result);
            Log.d("json_obj_HERE/amount", amount);
            Log.d("json_obj_HERE/transId", transactionId);
            Log.d("json_obj_HERE/trackId", trackId);
            Log.d("json_obj_HERE/createdAt", createdAt);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void bindData() {
        tvPaymentId.setText(paymentId);
        tvResult.setText("CAPTURED".equals(result) ? R.string.captured : R.string.not_capturred);
        tvAmount.setText(amount);
        tvTransId.setText(transactionId);
        tvTrackId.setText(trackId);
        tvCreatedAt.setText(createdAt);
    }
}
