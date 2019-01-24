package it_geeks.info.gawla_app.general;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.SalonActivity;

public class ConnectionChangeReceiver extends BroadcastReceiver {

    Snackbar snackbar;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        initSnackbar(context);

        if (activeNetInfo != null) {
            connectedSnack(context);
        } else {
            unConnectedSnack(context);
        }
    }

    public void initSnackbar(Context context) {
        if (snackbar == null) {
            if (context.getClass().equals(MainActivity.class)) {
                snackbar = Snackbar.make(((MainActivity) context).getSnackbarContainer(), "NO CONNECTION", Snackbar.LENGTH_INDEFINITE);
            } else if (context.getClass().equals(SalonActivity.class)) {
                snackbar = Snackbar.make(((SalonActivity) context).getSnackbarContainer(), "NO CONNECTION", Snackbar.LENGTH_INDEFINITE);
            }

            View snackbarView = snackbar.getView();

            TextView tv = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_small));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
//            snackbarView.setElevation(0);

            snackbarView.setLayoutParams(params);
        }
    }

    private void connectedSnack(Context context) {
        if (snackbar.isShown()) {
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.greenBlue));
            snackbar.setText("CONNECTED");
            snackbar.setDuration(500).show();

            ((MainActivity) context).recreate();
        }
    }

    private void unConnectedSnack(Context context) {
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.paleRed));
        snackbar.setText("NO CONNECTION");
        snackbar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE).show();
    }
}
