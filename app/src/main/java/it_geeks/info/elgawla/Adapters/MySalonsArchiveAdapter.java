package it_geeks.info.elgawla.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.Models.SalonArchiveModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.account.SalonsArchiveActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseSalon;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;

public class MySalonsArchiveAdapter extends RecyclerView.Adapter<MySalonsArchiveAdapter.ViewHolder> {

    private Context context;
    private List<SalonArchiveModel> salonArchiveList;
    private SnackBuilder snackBuilder;

    public MySalonsArchiveAdapter(Context context, List<SalonArchiveModel> salonArchiveList, SnackBuilder snackBuilder) {
        this.context = context;
        this.salonArchiveList = salonArchiveList;
        this.snackBuilder = snackBuilder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salon_archive, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SalonArchiveModel salonArchive = salonArchiveList.get(position);

        holder.tvProductName.setText(salonArchive.getProductName());
        holder.tvDate.setText(salonArchive.getDate());

        ImageLoader.getInstance().loadFitImage(salonArchive.getProductImage(), holder.ivProductImage);

        if (salonArchive.getStatus())
        {
            holder.tvStatus.setText(context.getString(R.string.won));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else
        {
            holder.tvStatus.setText(context.getString(R.string.lose));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.paleRed));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSalonByID(salonArchive.getSalonId(), holder);
            }
        });

    }

    private void getSalonByID(int salonId, final ViewHolder holder) {
        ((SalonsArchiveActivity) context).dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(context).fetchDataFromServer(
                context,
                REQ_GET_SALON_BY_ID, new RequestModel<>(REQ_GET_SALON_BY_ID, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), salonId,
                        null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Salon salon = parseSalon(mainObject);

                        Intent i = new Intent(context, SalonActivity.class);
                        i.putExtra("salon", salon);

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), new Pair<View, String>(holder.ivProductImage, "transProductImage"));
                        context.startActivity(i, options.toBundle());
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((SalonsArchiveActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((SalonsArchiveActivity) context).dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return salonArchiveList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProductImage;
        TextView tvProductName, tvDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.iv_salonA_product_image);
            tvProductName = itemView.findViewById(R.id.tv_salonA_name);
            tvDate = itemView.findViewById(R.id.tv_salonA_date);
            tvStatus = itemView.findViewById(R.id.tv_salonA_status);

        }
    }
}
