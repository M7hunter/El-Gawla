package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.MyCardModel;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Models.SalonArchiveModel;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.views.account.MyCardsActivity;
import it_geeks.info.gawla_app.views.account.SalonsArchiveActivity;
import it_geeks.info.gawla_app.views.salon.SalonActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_SALON_BY_ID;

public class MySalonsArchiveAdapter extends RecyclerView.Adapter<MySalonsArchiveAdapter.ViewHolder> {

    private Context context;
    private List<SalonArchiveModel> salonArchiveList;

    public MySalonsArchiveAdapter(Context context, List<SalonArchiveModel> salonArchiveList) {
        this.context = context;
        this.salonArchiveList = salonArchiveList;
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
        holder.tvStatus.setText(salonArchive.getStatus());
        ImageLoader.getInstance().loadFitImage(salonArchive.getProductImage(), holder.ivProductImage);

        if (salonArchive.getStatus().equals("won")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.paleRed));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSalonByID(salonArchive.getSalonId());
            }
        });

    }

    private void getSalonByID(int salonId) {
        ((SalonsArchiveActivity) context).dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(context).executeConnectionToServer(
                context,
                REQ_GET_SALON_BY_ID, new Request<>(REQ_GET_SALON_BY_ID, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), salonId,
                        null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Round round = parseRoundByID(mainObject);

                        Intent i = new Intent(context, SalonActivity.class);
                        i.putExtra("round", round);
                        context.startActivity(i);
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((SalonsArchiveActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((SalonsArchiveActivity) context).dialogBuilder.hideLoadingDialog();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
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
