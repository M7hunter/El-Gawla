package it_geeks.info.gawla_app.Views.MenuOptions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it_geeks.info.gawla_app.ViewModels.Adapters.LangExAdapter;
import it_geeks.info.gawla_app.R;

public class SettingsActivity extends AppCompatActivity {

    ExpandableListView exLangList;
    LangExAdapter langAdapter;
    List<String> headerList = new ArrayList<>();
    HashMap<String, List<String>> listHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupLangData();

        initLangList();
    }

    private void setupLangData() {
        // add headers
        headerList.add(getString(R.string.language));

        // add childes
        List<String> languagesList = new ArrayList<>();
        languagesList.add("English");
        languagesList.add("العربية");

        // main list
        listHashMap.put(headerList.get(0), languagesList);
    }

    private void initLangList() {
        exLangList = findViewById(R.id.ex_lang_list);
        langAdapter = new LangExAdapter(SettingsActivity.this, headerList, listHashMap);
        exLangList.setAdapter(langAdapter);
    }
}
