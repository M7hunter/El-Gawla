package it_geeks.info.elgawla.views;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(getLanguageAwareContext(newBase));
    }

    private static Context getLanguageAwareContext(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(new Locale(SharedPrefManager.getInstance(context).getSavedLang()));
        return context.createConfigurationContext(configuration);
    }
}
