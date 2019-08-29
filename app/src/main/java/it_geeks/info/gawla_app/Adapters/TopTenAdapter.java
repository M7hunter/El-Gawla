package it_geeks.info.gawla_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.TopTen;

public class TopTenAdapter extends RecyclerView.Adapter<TopTenAdapter.ViewHolder> {

    private List<TopTen> topTenList;

    public TopTenAdapter(List<TopTen> topTens) {
        this.topTenList = topTens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_ten, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopTen topTen = topTenList.get(position);

        // bind
        holder.tvUserNumber.setText(String.valueOf(position + 1));
        holder.tvUserName.setText(topTen.getName());
        holder.tvUserOffer.setText(topTen.getOffer());

        if (position == 0) { // the winner
            holder.iv_winner_cup.setVisibility(View.VISIBLE);
        }
        else {
            holder.iv_winner_cup.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return topTenList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView  tvUserNumber, tvUserName, tvUserOffer;
        ImageView iv_winner_cup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserNumber = itemView.findViewById(R.id.tv_top_ten_number);
            tvUserName = itemView.findViewById(R.id.tv_top_ten_name);
            tvUserOffer = itemView.findViewById(R.id.tv_top_ten_offer);
            iv_winner_cup = itemView.findViewById(R.id.iv_winner_cup);
        }
    }
}
