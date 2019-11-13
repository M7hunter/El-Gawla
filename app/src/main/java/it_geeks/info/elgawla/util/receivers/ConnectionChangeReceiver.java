package it_geeks.info.elgawla.util.receivers;

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

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.views.main.MainActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

public class ConnectionChangeReceiver extends BroadcastReceiver {

    Snackbar snackbar;

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();


            initSnackBar(context);

            if (activeNetInfo != null)
            {
                connectedSnack(context);
            }
            else
            {
                unConnectedSnack(context);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initSnackBar(Context context) {
        if (snackbar == null)
        {
            if (context instanceof MainActivity)
            {
                snackbar = Snackbar.make(((MainActivity) context).getSnackBarContainer(), context.getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE);
            }
            else if (context.getClass().equals(SalonActivity.class))
            {
                snackbar = Snackbar.make(((SalonActivity) context).getSnackBarContainer(), context.getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE);
            }
            else
            {
                return;
            }

            View snackBarView = snackbar.getView();

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();
            params.setMargins(params.leftMargin, 0, params.rightMargin, 0);
            snackBarView.setLayoutParams(params);

            snackBarView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

            TextView tv = (TextView) snackBarView.findViewById(R.id.snackbar_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_small));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            else
            {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    private void connectedSnack(Context context) {
        if (snackbar.isShown())
        {
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.greenBlue));
            snackbar.setText(context.getString(R.string.connected));
            snackbar.setDuration(500).show();
            try
            {
                ((MainActivity) context).recreate();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
    }

    private void unConnectedSnack(Context context) {
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.paleRed));
        snackbar.setText(context.getString(R.string.no_connection));
        snackbar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE).show();
    }
}
