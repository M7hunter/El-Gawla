package it_geeks.info.gawla_app.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackBuilder {

    private Snackbar mSnackbar;

    public SnackBuilder(View parent) {
        initSnackbar(parent);
    }

    private void initSnackbar(View parent) {
        if (mSnackbar == null)
            mSnackbar = Snackbar.make(parent, "...", Snackbar.LENGTH_LONG);
    }

    public SnackBuilder setSnackText(String text) {
        if (mSnackbar != null)
            mSnackbar.setText(text);

        return this;
    }

    public SnackBuilder setSnackDuration(int duration) {
        if (mSnackbar != null)
            mSnackbar.setDuration(duration);

        return this;
    }

    public void showSnackbar() {
        if (mSnackbar != null && !mSnackbar.isShown())
            mSnackbar.show();
    }

    public void hideSnackbar() {
        if (mSnackbar != null && mSnackbar.isShown())
            mSnackbar.dismiss();
    }
}
