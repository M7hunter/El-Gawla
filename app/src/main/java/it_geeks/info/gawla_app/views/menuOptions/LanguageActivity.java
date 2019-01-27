package it_geeks.info.gawla_app.views.menuOptions;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Controllers.Adapters.LanguageAdapter;
import it_geeks.info.gawla_app.general.OnSwipeTouchListener;

public class LanguageActivity extends AppCompatActivity {

    LinearLayout mainLanguageActivity;

    RecyclerView langRecycler;
    List<String> langList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.Instance(this).changeStatusBarColor("#ffffff", this);
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

        // Swipe Page Back
        mainLanguageActivity = findViewById(R.id.mainLanguageActivity);
        mainLanguageActivity.setOnTouchListener(new OnSwipeTouchListener(LanguageActivity.this){
            public void onSwipeRight() { finish(); }
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
