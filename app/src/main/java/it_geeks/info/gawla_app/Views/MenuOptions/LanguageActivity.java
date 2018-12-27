package it_geeks.info.gawla_app.Views.MenuOptions;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.LanguageAdapter;

public class LanguageActivity extends AppCompatActivity {

    RecyclerView langRecycler;

    List<String> langsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor("#ffffff");
        setContentView(R.layout.activity_language);

        initViews();

        getData();

        initLangRecycler();
    }

    private void initViews() {
        // back
        findViewById(R.id.language_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getData() {
        langsList.add("العربية");
        langsList.add("English");
    }

    private void initLangRecycler() {
        langRecycler = findViewById(R.id.lang_recycler);
        langRecycler.setHasFixedSize(true);
        langRecycler.setLayoutManager(new LinearLayoutManager(LanguageActivity.this, 1, false));
        LanguageAdapter languageAdapter = new LanguageAdapter(LanguageActivity.this, langsList);
        langRecycler.setAdapter(languageAdapter);
    }

    // to change status bar color
    public void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
