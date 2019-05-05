package it_geeks.info.gawla_app.util.Interfaces;

import android.view.View;

public interface ClickInterface {

    interface OnItemClickListener {

        void onItemClick(View view, int position);
    }


    interface AlertButtonsClickListener {

        void onPositiveClick();

        void onNegativeCLick();
    }
}
