package it_geeks.info.gawla_app.Adapters;

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

import com.google.gson.JsonObject;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Package;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.views.main.MainActivity;
import it_geeks.info.gawla_app.views.account.MembershipActivity;
import it_geeks.info.gawla_app.views.store.PaymentMethodsActivity;
import it_geeks.info.gawla_app.views.store.PaymentURLActivity;

import static it_geeks.info.gawla_app.util.Constants.MEMBERSHIP_MSG;
import static it_geeks.info.gawla_app.util.Constants.PACKAGE_ID;
import static it_geeks.info.gawla_app.util.Constants.PAYMENT_URL;
import static it_geeks.info.gawla_app.util.Constants.REQ_SET_MEMBERSHIP;

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

        holder.tvTitle.setText(mPackage.getTitle());
        holder.tvPrice.setText(context.getString(R.string.Monthly, mPackage.getPrice()));
        holder.tvBody.setText(mPackage.getBody());
        ((CardView) holder.itemView).setCardBackgroundColor(Color.parseColor(mPackage.getColor()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PaymentMethodsActivity.class);
                i.putExtra(PACKAGE_ID, mPackage.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice, tvBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_package_title);
            tvPrice = itemView.findViewById(R.id.tv_package_price);
            tvBody = itemView.findViewById(R.id.tv_package_body);
        }
    }
}
