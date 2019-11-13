package it_geeks.info.elgawla.views.menu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.Adapters.LangAdapter;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Language;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.intro.SplashScreenActivity;

public class LanguageActivity extends BaseActivity {

    private RecyclerView rvLang;
    private List<Language> langList = new ArrayList<>();

    private String selectedLang, appLang;
    private DialogBuilder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        appLang = SharedPrefManager.getInstance(LanguageActivity.this).getSavedLang();
        selectedLang = appLang;
        langList.add(new Language(getString(R.string.arabic), "ar"));
        langList.add(new Language(getString(R.string.english), "en"));

        initRecycler();

        handleEvents();
    }

    private void initRecycler() {
        rvLang = findViewById(R.id.rv_lang);
        rvLang.setHasFixedSize(true);
        rvLang.addItemDecoration(new DividerItemDecoration(rvLang.getContext(), DividerItemDecoration.VERTICAL));
        rvLang.setAdapter(new LangAdapter(this, langList, new ClickInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!langList.get(position).getCode().equals(appLang))
                {
                    selectedLang = langList.get(position).getCode();
                    dialogBuilder.displayAlertDialog();
                    rvLang.getAdapter().notifyDataSetChanged();
                }
            }
        }));
    }

    private void handleEvents() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dialogBuilder = new DialogBuilder();
        dialogBuilder.createAlertDialog(LanguageActivity.this, new ClickInterface.AlertButtonsClickListener() {
            @Override
            public void onPositiveClick() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("selected_lang", "::: " + selectedLang);
                        SharedPrefManager.getInstance(LanguageActivity.this).setLang(selectedLang);
                        restartTheApp();
                    }
                }, 500);
            }

            @Override
            public void onNegativeCLick() {
                selectedLang = appLang;
            }
        });
        dialogBuilder.setAlertText(getString(R.string.restart_hint));
    }

    private void restartTheApp() {
        Intent i = new Intent(LanguageActivity.this, SplashScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
