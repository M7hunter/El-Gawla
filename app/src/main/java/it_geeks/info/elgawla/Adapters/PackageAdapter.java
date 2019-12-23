package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Package;
import it_geeks.info.elgawla.views.store.PaymentMethodsActivity;

import static it_geeks.info.elgawla.util.Constants.PACKAGE;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private Context context;
    private List<Package> packageList;

    public PackageAdapter(Context context, List<Package> packageList) {
        this.context = context;
        this.packageList = packageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Package mPackage = packageList.get(position);

        // update span dynamically
        if (position == (packageList.size() - 1))
        {// last row
            if ((packageList.size() % 2) != 0)
            {// one item
                ((StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams()).setFullSpan(true);
            }
        }

        holder.tvPrice.setText(mPackage.getPackage_name());
        holder.tvBody.setText(mPackage.getPackage_description());
        ((CardView) holder.itemView).setCardBackgroundColor(Color.parseColor(mPackage.getPackage_color()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PaymentMethodsActivity.class);
                i.putExtra(PACKAGE, mPackage);
                i.putExtra("is_card", false);
                context.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPrice, tvBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPrice = itemView.findViewById(R.id.tv_price);
            tvBody = itemView.findViewById(R.id.tv_body);
        }
    }
}
