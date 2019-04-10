package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Ad;

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
        Ad ad = adList.get(position);

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
