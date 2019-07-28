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
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.account.MembershipActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_SET_MEMBERSHIP;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private Context context;
    private List<Package> packageList;
    private SnackBuilder snackBuilder;

    public PackageAdapter(Context context, List<Package> packageList, View parentView) {
        this.context = context;
        this.packageList = packageList;
        snackBuilder = new SnackBuilder(parentView);
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
                updateMembership(mPackage.getId());
            }
        });
    }

    private void updateMembership(int packageId) {
        ((MembershipActivity) context).dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                REQ_SET_MEMBERSHIP, new Request<>(REQ_SET_MEMBERSHIP, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), packageId
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnackbar();
                        context.startActivity(new Intent(context, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((MembershipActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((MembershipActivity) context).dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
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
