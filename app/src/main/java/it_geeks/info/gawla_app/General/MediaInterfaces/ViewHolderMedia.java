package it_geeks.info.gawla_app.General.MediaInterfaces;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ViewHolderMedia extends RecyclerView.ViewHolder {

    public ViewHolderMedia(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(ItemMedia item);
}
