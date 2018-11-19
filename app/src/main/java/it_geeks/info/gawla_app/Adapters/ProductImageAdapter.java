package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it_geeks.info.gawla_app.R;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.Holder> {

    Context context;
    public ProductImageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View v =  LayoutInflater.from(context).inflate(R.layout.img_product_item,viewGroup,false);
      Holder holder = new Holder(v);
      return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class Holder extends RecyclerView.ViewHolder{
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
