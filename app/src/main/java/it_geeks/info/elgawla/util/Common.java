package it_geeks.info.elgawla.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.views.signing.SignInActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_SET_FIREBASE_TOKEN;

public class Common {

    private static final String TAG = "fireToken";
    private static Common common;

    public static Common Instance() {
        if (common == null)
        {
            common = new Common();
        }
        return common;
    }

    // remove unneeded quotes
    public String removeQuotes(String s) {
        // check
        if (s != null)
            if (s.startsWith("\""))
            {
                s = s.substring(1, s.length() - 1);
            }

        return s;
    }

    // remove empty lines
    public String removeEmptyLines(String s) {
        if (s != null)
        {
            return s.replaceAll("(?m)^[ \t]*\r?\n", "");
        }
        return "";
    }

    // hide progress after recycler finish loading
    public void hideLoading(final RecyclerView recyclerView, final View progressBar) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                progressBar.setVisibility(View.GONE);
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
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
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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

    public void signOut(Context context) {
        // local
        SharedPrefManager.getInstance(context).clearUser();
        SharedPrefManager.getInstance(context).clearProvider();

        // facebook
        LoginManager.getInstance().logOut();

        // google
        GoogleSignIn.getClient(context, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();

        // firebase
        FirebaseMessaging.getInstance().unsubscribeFromTopic("salon_" + SharedPrefManager.getInstance(context).getSubscribedSalonId());
        SharedPrefManager.getInstance(context).clearSubscribedSalonId();

        // redirect to sign in
        context.startActivity(new Intent(context, SignInActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
        RetrofitClient.getInstance(context).fetchDataFromServer(context,
                REQ_SET_FIREBASE_TOKEN, new RequestModel<>(REQ_SET_FIREBASE_TOKEN, user_id, apiToken, token,
                        null, null, null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        Log.d(TAG, "firebaseTokenSent");
                    }

                    @Override
                    public void afterResponse() {

                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                    }
                });
    }
}
