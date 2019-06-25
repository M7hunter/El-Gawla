package it_geeks.info.gawla_app.util;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.Interfaces.ConnectionInterface;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.SalonDate;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

import static it_geeks.info.gawla_app.util.Constants.REQ_SET_FIREBASE_TOKEN;

public class Common {

    private static final String TAG = "fireToken";
    private static Common common;

    private Common() {}

    public static Common Instance() {
        if (common == null)
        {
            common = new Common();
        }
        return common;
    }

    public void setLang(Context context, String lang) {
        try
        {
            new WebView(context).destroy();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            locale = new Locale(lang, "kw");
        }
        else
        {
            locale = new Locale(lang);
        }

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        Locale.setDefault(locale);

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        SharedPrefManager.getInstance(context).setLang(lang);
    }

    // remove unneeded quotes
    public String removeQuotes(String s) {
        // check
        if (s.startsWith("\""))
        {
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

        if (behavior instanceof BottomSheetBehavior)
        {
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

    // connected ?
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void ApplyOnConnection(Context context, ConnectionInterface connectionInterface) {
        LinearLayout noConnectionLayout = ((Activity) context).findViewById(R.id.no_connection);

        if (isConnected(context))
        { // connected
            noConnectionLayout.setVisibility(View.GONE);

            connectionInterface.onConnected();

        }
        else
        { // no connection
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
    public void setAnimation(Context context, View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }

    public void updateFirebaseToken(final Context context) {
        final int userId = SharedPrefManager.getInstance(context).getUser().getUser_id();
        final String apiToken = SharedPrefManager.getInstance(context).getUser().getApi_token();

        if (apiToken != null)
            if (!String.valueOf(userId).isEmpty() && !apiToken.isEmpty())
            {
                String fireToken = SharedPrefManager.getInstance(context).getFirebaseToken();
                if (fireToken.equals(Constants.EMPTY_TOKEN))
                {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (task.isSuccessful())
                            {
                                if (task.getResult() != null)
                                {
                                    String token = task.getResult().getToken();
                                    SharedPrefManager.getInstance(context).setFirebaseToken(token);
                                    updateTokenOnServer(context, userId, apiToken, token);
                                    Log.d(TAG, "onComplete: " + token);
                                }
                            }
                        }
                    });
                }
                else
                {
                    Log.d(TAG, "saved token: " + fireToken);
                    updateTokenOnServer(context, userId, apiToken, fireToken);
                }
            }
    }

    private void updateTokenOnServer(Context context, int user_id, String apiToken, String token) {
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                REQ_SET_FIREBASE_TOKEN, new Request<>(REQ_SET_FIREBASE_TOKEN, user_id, apiToken, token,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Log.d(TAG, "firebaseTokenSent");
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                    }
                });
    }
}
