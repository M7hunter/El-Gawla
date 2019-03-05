package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.BuyingProcess;

public class BuyingProcessAdapter extends RecyclerView.Adapter<BuyingProcessAdapter.ViewHolder> {

    private Context context;
    private List<BuyingProcess> processList;

    public BuyingProcessAdapter(Context context, List<BuyingProcess> processList) {
        this.context = context;
        this.processList = processList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_buying_process, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BuyingProcess buyingProcess = processList.get(i);

        viewHolder.process.setText(buyingProcess.getProcess());
        viewHolder.card.setText(buyingProcess.getCard());
        viewHolder.date.setText(buyingProcess.getDate());
        viewHolder.cost.setText(buyingProcess.getCost());
    }

    @Override
    public int getItemCount() {
        return processList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView process, card, date, cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            process = itemView.findViewById(R.id.item_buying_process_process);
            card = itemView.findViewById(R.id.item_buying_process_card);
            date = itemView.findViewById(R.id.item_buying_process_date);
            cost = itemView.findViewById(R.id.item_buying_process_cost);
        }
    }
}