package it_geeks.info.gawla_app.General;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.views.MainActivity;

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
            snackbar = Snackbar.make(((MainActivity) context).getMainFrame(), "NO CONNECTION", Snackbar.LENGTH_INDEFINITE);

            View view = snackbar.getView();
            TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_small));
            view.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    private void connectedSnack(Context context) {
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.greenBlue));
        snackbar.setText("CONNECTED");
        snackbar.setDuration(500).show();
    }

    private void unConnectedSnack(Context context) {
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.paleRed));
        snackbar.setText("NO CONNECTION");
        snackbar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE).show();
    }
}
