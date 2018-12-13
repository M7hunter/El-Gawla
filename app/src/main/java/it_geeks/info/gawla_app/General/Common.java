package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it_geeks.info.gawla_app.Repositry.Models.Data;

public class Common {

    private static Common common;
    private Context context;
    private String Lang;

    private Common(Context context) {
        this.context = context;
        Lang = Locale.getDefault().getLanguage(); // get device default language
    }

    public static synchronized Common Instance(Context context) {
        if (common == null) { common = new Common(context); }
        return common;
    }

    // change app lang
    public void setLang (String lang) {
        Lang = lang;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(new Locale(Lang));
        context.getResources().updateConfiguration(configuration, displayMetrics);
        SharedPrefManager.getInstance(context).setLang(Lang);
    }

    // remove unneeded quotes
    public String removeQuotes(String s) {
        // check
        if (s.startsWith("\"")) {
            s =  s.substring(1, s.length() - 1);
        }

        return s;
    }

    // remove empty lines
    public String removeEmptyLines(String s) {
        return s.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    // hide progress after recycler finish loading
    public void hideProgress(final RecyclerView recyclerView, final ProgressBar progressBar) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                progressBar.setVisibility(View.GONE);
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    // get milliseconds time
    public long getCurrentTimeInMillis() {
        long time = System.currentTimeMillis(); // milliseconds
        Log.d("M7", "Time: " + time);

        return time;
    }

    // get formatted time & date
    public Date getCurrentTimeFormatted() {
        Date currentTime = Calendar.getInstance().getTime(); // formatted 'ddd MMM dd HH:mm:ss GMT yyyy'
        Log.d("M7", "current Time: " + currentTime);

        return currentTime;
    }
}
