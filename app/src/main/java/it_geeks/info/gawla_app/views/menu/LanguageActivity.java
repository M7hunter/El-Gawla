package it_geeks.info.gawla_app.views.menu;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Adapters.LanguageAdapter;

public class LanguageActivity extends AppCompatActivity {

    RecyclerView langRecycler;
    List<String> langList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        initViews();

        getData();

        initLangRecycler();
    }

    private void initViews() {
        // back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getData() {
        langList.add("العربية");
        langList.add("English");
    }

    private void initLangRecycler() {
        langRecycler = findViewById(R.id.lang_recycler);
        langRecycler.setHasFixedSize(true);
        langRecycler.setLayoutManager(new LinearLayoutManager(LanguageActivity.this, RecyclerView.VERTICAL, false));
        langRecycler.setAdapter(new LanguageAdapter(LanguageActivity.this, langList));
    }
}
