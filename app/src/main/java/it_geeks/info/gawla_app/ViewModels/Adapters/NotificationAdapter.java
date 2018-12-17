package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    Context context;
    ArrayList list;

    public NotificationAdapter(Context context, ArrayList list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View v = LayoutInflater.from(context).inflate(R.layout.item_notification,viewGroup,false);
       Holder holder = new Holder(v);
       return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class  Holder extends RecyclerView.ViewHolder{

        public Holder( View itemView) {
            super(itemView);

        }
    }
}
