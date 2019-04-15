package it_geeks.info.gawla_app.general;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.general.Interfaces.AlertButtonsClickListener;

public class DialogBuilder {

    private AlertDialog loadingDialog;
    private Activity activity;

    public void createAlertDialog(Context context, String message, final AlertButtonsClickListener clickListener) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_alert_dialog, null);

        ((TextView) dialogView.findViewById(R.id.tv_alert_body)).setText(message);
        Button btnPositive = dialogView.findViewById(R.id.btn_alert_positive);
        Button btnNegative = dialogView.findViewById(R.id.btn_alert_negative);

        dialogBuilder.setView(dialogView);

        final AlertDialog dialog = dialogBuilder.create();


        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositiveClick();
                dialog.dismiss();
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onNegativeCLick();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void createLoadingDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomLoadingDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null);
        dialogBuilder.setView(dialogView);
        loadingDialog = dialogBuilder.create();
        activity = ((Activity) context);

        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.onBackPressed();
            }
        });
    }

    public void displayLoadingDialog() {
        if (loadingDialog.getWindow() != null)
            loadingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (activity.getWindow() != null)
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loadingDialog.show();
    }

    public void hideLoadingDialog() {
        if (loadingDialog.getWindow() != null)
            loadingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (activity.getWindow() != null)
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        loadingDialog.dismiss();
    }
}
