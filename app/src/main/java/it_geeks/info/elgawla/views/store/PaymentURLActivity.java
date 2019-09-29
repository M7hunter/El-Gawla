package it_geeks.info.elgawla.views.store;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.DialogBuilder;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static it_geeks.info.elgawla.util.Constants.PAYMENT_URL;

public class PaymentURLActivity extends AppCompatActivity {

    private WebView paymentWebView;
    private String paymentUrl;
    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_url);

        paymentUrl = getIntent().getStringExtra(PAYMENT_URL);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        dialogBuilder.displayLoadingDialog();

        initWebView();
    }

    private void initWebView() {
        paymentWebView = findViewById(R.id.wv_payment_url);

        paymentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        paymentWebView.getSettings().setJavaScriptEnabled(true);
        paymentWebView.getSettings().setLoadsImagesAutomatically(true);
        paymentWebView.getSettings().setUseWideViewPort(true);
        paymentWebView.getSettings().setLoadWithOverviewMode(true);
        paymentWebView.getSettings().setSupportZoom(false);

        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialogBuilder.displayLoadingDialog();

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);

                dialogBuilder.hideLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
                    dialogBuilder.hideLoadingDialog();
            }
        });

        paymentWebView.loadUrl(paymentUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent i = new Intent(this, InvoicesActivity.class);
//        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
