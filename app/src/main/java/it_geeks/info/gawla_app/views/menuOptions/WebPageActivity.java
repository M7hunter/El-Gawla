package it_geeks.info.gawla_app.views.menuOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.Common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class WebPageActivity extends AppCompatActivity {

    private WebView webView;
    private CardView loadingCard;
    private TextView tvTitle;

    private String url, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#FFFFFF", this);
        setContentView(R.layout.activity_web_page);

        getData(savedInstanceState);

        initViews();

        bind();

        displayLoading();

        handleEvents();

        initWebView();
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                url = extras.getString("web_page_url");
                title = extras.getString("web_page_title");
            }
        } else {
            url = savedInstanceState.getString("web_page_url");
            title = savedInstanceState.getString("web_page_title");
        }
    }

    private void initViews() {
        webView = findViewById(R.id.web_view);
        loadingCard = findViewById(R.id.loading_card);
        tvTitle = findViewById(R.id.tv_wep_page_title);
    }

    private void bind() {
        tvTitle.setText(title);
    }

    private void handleEvents() {
        // back
        findViewById(R.id.wep_page_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void initWebView() {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                displayLoading();

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);

                hideLoading();
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
