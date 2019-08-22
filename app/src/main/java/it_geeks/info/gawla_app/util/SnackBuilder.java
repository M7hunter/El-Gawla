package it_geeks.info.gawla_app.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import it_geeks.info.gawla_app.util.Interfaces.ClickInterface.SnackAction;

public class SnackBuilder {

    private Snackbar snack;

    public SnackBuilder(View parent) {
        initSnack(parent);
    }

    private void initSnack(View parent) {
        if (snack == null)
            snack = Snackbar.make(parent, "...", Snackbar.LENGTH_LONG);
    }

    public SnackBuilder setSnackText(String text) {
        if (snack != null)
            snack.setText(text);

        return this;
    }

    public SnackBuilder setSnackDuration(int duration) {
        if (snack != null)
            snack.setDuration(duration);

        return this;
    }

    public void showSnack() {
        if (snack != null && !snack.isShown())
            snack.show();
    }

    public void hideSnack() {
        if (snack != null && snack.isShown())
            snack.dismiss();
    }

    public SnackBuilder setSnackAction(String actionLabel, final SnackAction snackAction) {
        if (snack != null)
            snack.setAction(actionLabel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackAction.onClick();
                }
            });

        return this;
    }
}
