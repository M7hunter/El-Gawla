package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import java.util.Locale;

public class Common {

    private static Common common;
    private Context context;
    private String Lang;

    public Common(Context context) {
        this.context = context;
        Lang = Locale.getDefault().getLanguage();
    }

    public static synchronized Common Instance(Context context) {
        if (common == null) { common = new Common(context); }
        return common;
    }

    // to change app lang
    public void setLang (String lang) {
        Lang = lang;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(new Locale(Lang));
        context.getResources().updateConfiguration(configuration, displayMetrics);
        SharedPrefManager.getInstance(context).setLang(Lang);
    }

    public String getLang () {
        return Lang;
    }
}
