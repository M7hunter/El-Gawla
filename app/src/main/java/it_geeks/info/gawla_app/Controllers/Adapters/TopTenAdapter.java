package it_geeks.info.gawla_app.Controllers.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.TopTen;

public class TopTenAdapter extends RecyclerView.Adapter<TopTenAdapter.ViewHolder> {

    private List<TopTen> topTenList;

    public TopTenAdapter(List<TopTen> topTens) {
        this.topTenList = topTens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopTen topTen = topTenList.get(position);

        // bind
        if(position+1 == 1) holder.tvUserName.setText((position + 1) + " -  " + topTen.getName() + "  :    " + topTen.getOffer() + "  ( WINNER )");
        else holder.tvUserName.setText((position + 1) + " -  " + topTen.getName() + "  :    " + topTen.getOffer());
    }

    @Override
    public int getItemCount() {
        return topTenList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName, tvUserOffer;
        ImageView iv_activity_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tv_activity_body);
            iv_activity_image = itemView.findViewById(R.id.iv_activity_image);
            iv_activity_image.setVisibility(View.GONE);
            tvUserOffer = itemView.findViewById(R.id.tv_activity_time);
            tvUserOffer.setVisibility(View.GONE);
        }
    }
}
