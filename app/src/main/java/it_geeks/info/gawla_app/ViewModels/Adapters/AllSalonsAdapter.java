package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Salons;

public class AllSalonsAdapter extends RecyclerView.Adapter<AllSalonsAdapter.ViewHolder> {

    private Context context;
    private List<Salons> salonsList;

    public AllSalonsAdapter(Context context, List<Salons> salonsList) {
        this.context = context;
        this.salonsList = salonsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salons, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Salons salons = salonsList.get(i);

        viewHolder.header.setText(salons.getHeader());

        viewHolder.recyclerView.setAdapter(new SalonsAdapter(context, salons.getRounds()));
    }

    @Override
    public int getItemCount() {
        return salonsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.salons_header);
            recyclerView = itemView.findViewById(R.id.salons_recycler);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, 0, false));
        }
    }
}
