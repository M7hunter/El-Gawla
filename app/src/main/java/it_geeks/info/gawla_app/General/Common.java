package it_geeks.info.gawla_app.General;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it_geeks.info.gawla_app.Repositry.Models.SalonDate;
import it_geeks.info.gawla_app.Repositry.Models.Salons;

public class Common {

    private static Common common;
    private Context context;
    private String Lang;

    private Common(Context context) {
        this.context = context;
        Lang = Locale.getDefault().getLanguage(); // get device default language
    }

    public static Common Instance(Context context) {
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

    // make bottom sheet height 'wrap content'
    public void setBottomSheetHeight(final View view) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            final BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = view.getMeasuredHeight();
                    bottomSheetBehavior.setPeekHeight(height);
                }
            });
        }
    }

    // change drawable background
    public void changeDrawableViewColor(View v, String color) {
        GradientDrawable background = (GradientDrawable) v.getBackground();
        background.setColor(Color.parseColor(color));
    }

    // to if there is a connection or not
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void sortList(List<SalonDate> list) {
        Collections.sort(list, new Comparator<SalonDate>() {
            @Override
            public int compare(SalonDate o1, SalonDate o2) {
                return o1.getDayOfMonth().compareTo(o2.getDayOfMonth());
            }
        });
    }
}
