package it_geeks.info.gawla_app.general;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.Interfaces.ConnectionInterface;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.SalonDate;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class Common {

    private static Common common;
    private Context context;
    private String Lang;

    private Common(Context context) {
        this.context = context;
        Lang = Locale.getDefault().getLanguage(); // get device default language
    }

    public static Common Instance(Context context) {
        if (common == null) {
            common = new Common(context);
        }
        return common;
    }

    // change app lang
    public void setLang(String lang) {
        try {
            new WebView(context).destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Lang = lang;
        Configuration configuration = context.getResources().getConfiguration();
        Locale locale;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            locale = new Locale(Lang, "kw");
        } else {
            locale = new Locale(Lang);
        }

        configuration.setLocale(locale);
        Locale.setDefault(locale);

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        SharedPrefManager.getInstance(context).setLang(Lang);
    }

    public void loadImage(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(context)
                    .load(imageUrl)
                    .resize(800, 800)
                    .onlyScaleDown()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void loadLandscapeImage(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(context)
                    .load(imageUrl)
                    .resize(900, 600)
                    .onlyScaleDown()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void loadFittedImage(String imageUrl, ImageView imageView) {
        try {
            Picasso.with(context)
                    .load(imageUrl)
                    .fit()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    // remove unneeded quotes
    public String removeQuotes(String s) {
        // check
        if (s.startsWith("\"")) {
            s = s.substring(1, s.length() - 1);
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

    // set bottom sheet height 'wrap content'
    public void setBottomSheetHeight(final View view) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
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

    // to change status bar color in fragments || activities if wanted
    public void changeStatusBarColor(String color, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((AppCompatActivity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    // connected ?
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void ApplyOnConnection(Context context, ConnectionInterface connectionInterface) {
        LinearLayout noConnectionLayout = ((Activity) context).findViewById(R.id.no_connection);

        if (Common.Instance(context).isConnected()) { // connected
            noConnectionLayout.setVisibility(View.GONE);

            connectionInterface.onConnected();

        } else { // no connection
            noConnectionLayout.setVisibility(View.VISIBLE);

            connectionInterface.onFailed();
        }
    }

    public void sortList(List<SalonDate> list) {
        Collections.sort(list, new Comparator<SalonDate>() {
            @Override
            public int compare(SalonDate o1, SalonDate o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }

    // animate recycler items
    public void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }

    public void updateFirebaseToken() {
        int user_id = SharedPrefManager.getInstance(context).getUser().getUser_id();
        String apiToken = SharedPrefManager.getInstance(context).getUser().getApi_token();

        if (!String.valueOf(user_id).isEmpty() && !apiToken.isEmpty()) {
            // update token
            RetrofitClient.getInstance(context).executeConnectionToServer(context, "setUserFirebaseToken", new Request(user_id, apiToken, FirebaseInstanceId.getInstance().getToken()), new HandleResponses() {
                @Override
                public void handleTrueResponse(JsonObject mainObject) {

                }

                @Override
                public void handleFalseResponse(JsonObject errorObject) {

                }

                @Override
                public void handleEmptyResponse() {

                }

                @Override
                public void handleConnectionErrors(String errorMessage) {
                }
            });
        }
    }
}
