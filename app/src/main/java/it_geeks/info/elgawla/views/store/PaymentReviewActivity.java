package it_geeks.info.elgawla.views.store;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.views.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

public class PaymentReviewActivity extends BaseActivity {

    private TextView tvPaymentId, tvResult, tvAmount, tvTransId, tvTrackId, tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_review);

        initViews();

        bindData();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EventsManager.sendPaymentReviewEvent(this, "", "", 0, 0, "", 0);
    }

    private void initViews() {
        tvPaymentId = findViewById(R.id.tv_review_payment_id);
        tvResult = findViewById(R.id.tv_review_result);
        tvAmount = findViewById(R.id.tv_review_amount);
        tvTransId = findViewById(R.id.tv_review_transaction_id);
        tvTrackId = findViewById(R.id.tv_review_track_id);
        tvCreatedAt = findViewById(R.id.tv_review_created_at);
    }

    private void bindData() {
        try
        {
            tvPaymentId.setText(MyJavaScriptInterface.PaymentId);
            tvResult.setText("CAPTURED".equals(MyJavaScriptInterface.Result) ? R.string.captured : R.string.not_capturred);
            tvAmount.setText(MyJavaScriptInterface.Amount);
            tvTransId.setText(MyJavaScriptInterface.Transaction);
            tvTrackId.setText(MyJavaScriptInterface.Track);
            tvCreatedAt.setText(MyJavaScriptInterface.Date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
