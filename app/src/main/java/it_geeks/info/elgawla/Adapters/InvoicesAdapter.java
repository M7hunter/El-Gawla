package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Invoice;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesAdapter.ViewHolder> {

    private Context context;
    private List<Invoice> invoicesList;

    public InvoicesAdapter(Context context, List<Invoice> processList) {
        this.context = context;
        this.invoicesList = processList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_invoice, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Invoice invoice = invoicesList.get(i);

        viewHolder.no.setText(String.valueOf(invoice.getId()));
        viewHolder.date.setText(invoice.getCreated_at());
        viewHolder.cost.setText(invoice.getTotal());

        // process
        if (invoice.getOption_type().equals("subscribe")) {
            viewHolder.process.setImageDrawable(context.getDrawable(R.drawable.subscribe_icon));

        } else if (invoice.getOption_type().equals("card")) {
            viewHolder.process.setImageDrawable(context.getDrawable(R.drawable.card_icon));
        }

        // status
        if (Integer.parseInt(invoice.getStatus()) == 0) {
            viewHolder.status.setImageDrawable(context.getDrawable(R.drawable.false_icon));

        } else {
            viewHolder.status.setImageDrawable(context.getDrawable(R.drawable.true_icon));
        }
    }

    @Override
    public int getItemCount() {
        return invoicesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView no, date, cost;
        ImageView process, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            no = itemView.findViewById(R.id.item_invoice_no);
            process = itemView.findViewById(R.id.item_invoice_process);
            status = itemView.findViewById(R.id.item_invoice_status);
            date = itemView.findViewById(R.id.item_invoice_date);
            cost = itemView.findViewById(R.id.item_invoice_cost);
        }
    }
}