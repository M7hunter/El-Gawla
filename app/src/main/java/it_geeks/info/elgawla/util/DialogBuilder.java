package it_geeks.info.elgawla.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;

public class DialogBuilder {

    private AlertDialog loadingDialog, alertDialog;
    private TextView tvAlertText;
    private Activity activity;

    public void createAlertDialog(Context context, final ClickInterface.AlertButtonsClickListener clickListener) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_alert_dialog, null);

        tvAlertText = dialogView.findViewById(R.id.tv_alert_body);
        Button btnPositive = dialogView.findViewById(R.id.btn_alert_positive);
        Button btnNegative = dialogView.findViewById(R.id.btn_alert_negative);

        dialogBuilder.setView(dialogView);

        alertDialog = dialogBuilder.create();

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPositiveClick();
                alertDialog.dismiss();
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onNegativeCLick();
                alertDialog.dismiss();
            }
        });
    }

    public DialogBuilder setAlertText(String message) {
        if (alertDialog != null && tvAlertText != null)
        {
            tvAlertText.setText(message);
        }

        return this;
    }

    public void displayAlertDialog() {
        alertDialog.show();
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
        try
        {
            if (loadingDialog.getWindow() != null)
                loadingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (activity.getWindow() != null)
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            loadingDialog.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void hideLoadingDialog() {
        try
        {
            if (loadingDialog.getWindow() != null)
                loadingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (activity.getWindow() != null)
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            loadingDialog.dismiss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
