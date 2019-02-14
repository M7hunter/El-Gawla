package it_geeks.info.gawla_app.views.menuOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPageActivity extends AppCompatActivity {

    private WebView webView;
    private CardView loadingCard;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getData(savedInstanceState);

        initViews();

        initWebView();
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                url = extras.getString("web_page_url");
            }
        } else {
            url = savedInstanceState.getString("web_page_url");
        }
    }

    private void initViews() {
        webView = findViewById(R.id.web_view);
        loadingCard = findViewById(R.id.loading_card);
    }

    private void initWebView() {
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                loadingCard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);

                loadingCard.setVisibility(View.GONE);
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
