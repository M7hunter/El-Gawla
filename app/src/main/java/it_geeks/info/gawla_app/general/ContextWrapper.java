package it_geeks.info.gawla_app.general;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class ContextWrapper extends android.content.ContextWrapper {

    public ContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, Locale newLocale) {

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= 24) {

            config.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);

            context = context.createConfigurationContext(config);

        } else if (Build.VERSION.SDK_INT >= 17) {

            config.setLocale(newLocale);
            context = context.createConfigurationContext(config);

        } else {

            config.locale = newLocale;
            res.updateConfiguration(config, res.getDisplayMetrics());

        }

        return new ContextWrapper(context);
    }
}
