package it_geeks.info.elgawla.views.store;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.EventsManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static it_geeks.info.elgawla.util.Constants.PAYMENT_URL;

public class PaymentURLActivity extends AppCompatActivity {

    public static PaymentURLActivity activity;
    private WebView paymentWebView;
    private String paymentUrl;
    private DialogBuilder dialogBuilder;
    public MyJavaScriptInterface mj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_url);
        activity = this;

        paymentUrl = getIntent().getStringExtra(PAYMENT_URL);

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createLoadingDialog(this);

        dialogBuilder.displayLoadingDialog();

        initWebView();

        EventsManager.sendOpenPaymentEvent(this);
    }

    @Override
    protected void onDestroy() {
        paymentWebView.removeJavascriptInterface("Print");
        super.onDestroy();
    }

    private void initWebView() {
        paymentWebView = findViewById(R.id.wv_payment_url);

        mj = new MyJavaScriptInterface();

        paymentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        paymentWebView.getSettings().setJavaScriptEnabled(true);
        paymentWebView.getSettings().setLoadsImagesAutomatically(true);
        paymentWebView.getSettings().setUseWideViewPort(true);
        paymentWebView.getSettings().setLoadWithOverviewMode(true);
        paymentWebView.getSettings().setSupportZoom(false);
        paymentWebView.addJavascriptInterface(mj, "Print");

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            CookieManager.getInstance().setAcceptThirdPartyCookies(paymentWebView, true);
        }
        else
        {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialogBuilder.displayLoadingDialog();
                if (url.contains("success"))
                    paymentWebView.setVisibility(View.INVISIBLE);

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

                if (url.contains("success"))
                {
                    startActivity(new Intent(PaymentURLActivity.this, PaymentReviewActivity.class));
                }
            }
        });

        paymentWebView.loadUrl(paymentUrl);
    }
}
