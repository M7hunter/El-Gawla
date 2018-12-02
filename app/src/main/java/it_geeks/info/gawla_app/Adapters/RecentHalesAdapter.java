package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.HaleActivity;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.R;

public class RecentHalesAdapter extends RecyclerView.Adapter<RecentHalesAdapter.ViewHolder> {

    private Context context;
    private List<Round> roundsList;

    public RecentHalesAdapter(Context context, List<Round> roundsList) {
        this.context = context;
        this.roundsList = roundsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_round, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Round round = roundsList.get(position);

        Picasso.with(context)
                .load(round.getProduct_image())
                .placeholder(context.getResources().getDrawable(R.drawable.macbook))
                .into(viewHolder.imgProductImage);

        viewHolder.tvProductName.setText(round.getProduct_name());
        viewHolder.tvProductCategory.setText(round.getProduct_category());
        viewHolder.tvProductPrice.setText(round.getProduct_price());
        viewHolder.tvProductDescription.setText(round.getProduct_description());
        viewHolder.tvStartTime.setText(round.getStart_time());
        viewHolder.tvEndTime.setText(round.getEnd_time());
        viewHolder.tvMembersNumber.setText(round.getJoined_members_number());

        viewHolder.btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, HaleActivity.class);
                i.putExtra("product_name", round.getProduct_name());
                i.putExtra("product_image", round.getProduct_image());
                i.putExtra("product_category", round.getProduct_category());
                i.putExtra("product_price", round.getProduct_price());
                i.putExtra("product_description", round.getProduct_description());
                i.putExtra("round_start_time", round.getStart_time());
                i.putExtra("round_end_time", round.getEnd_time());
                i.putExtra("joined_members_number", round.getJoined_members_number());

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roundsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductDescription, tvStartTime, tvEndTime, tvMembersNumber, btnJoinRound;
        ImageView imgProductImage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProductImage = itemView.findViewById(R.id.round_product_image);
            tvProductName = itemView.findViewById(R.id.round_product_name);
            tvProductCategory = itemView.findViewById(R.id.round_product_category);
            tvProductPrice = itemView.findViewById(R.id.round_product_price);
            tvProductDescription = itemView.findViewById(R.id.round_product_description);
            tvStartTime = itemView.findViewById(R.id.round_start_time);
            tvEndTime = itemView.findViewById(R.id.round_end_time);
            tvMembersNumber = itemView.findViewById(R.id.round_members_number);
            btnJoinRound = itemView.findViewById(R.id.round_btn_join);
        }
    }
}
