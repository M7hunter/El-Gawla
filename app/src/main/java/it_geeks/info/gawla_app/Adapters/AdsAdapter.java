package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Ad;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.salon.SalonActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_SALON_BY_ID;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {

    private Context context;
    private List<Ad> adList;

    public AdsAdapter(Context context, List<Ad> adList) {
        this.context = context;
        this.adList = adList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ad ad = adList.get(position);

        Picasso.with(context)
                .load(ad.getImage())
                .resize(800, 800)
                .onlyScaleDown()
                .placeholder(R.drawable.flodillus)
                .into(holder.ivAdImage);

        holder.tvAdTitle.setText(ad.getTitle());
        holder.tvAdBody.setText(ad.getBody());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad.isType())
                {
                    getSalonByID(ad.getSalonId());
                }
            }
        });
    }

    private void getSalonByID(int salonId) {
        ((MainActivity) context).dialogBuilder.displayLoadingDialog();
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
                        ((MainActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((MainActivity) context).dialogBuilder.hideLoadingDialog();
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAdImage;
        TextView tvAdTitle, tvAdBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAdImage = itemView.findViewById(R.id.iv_ad_image);
            tvAdTitle = itemView.findViewById(R.id.tv_ad_title);
            tvAdBody = itemView.findViewById(R.id.tv_ad_body);
        }
    }
}
