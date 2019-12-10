package it_geeks.info.elgawla.views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import com.crashlytics.android.Crashlytics;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import io.fabric.sdk.android.Fabric;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(getLanguageAwareContext(newBase));
        Fabric.with(this, new Crashlytics());
    }

    private static Context getLanguageAwareContext(Context context) {
        Locale newLocale = new Locale(SharedPrefManager.getInstance(context).getSavedLang());
        Locale.setDefault(newLocale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(newLocale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        }

        return context.createConfigurationContext(configuration);
    }
}
