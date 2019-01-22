package it_geeks.info.gawla_app.Controllers.Adapters;

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

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.SalonActivity;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;

public class SalonsAdapter extends RecyclerView.Adapter<SalonsAdapter.ViewHolder> {

    private Context context;
    private List<Round> rounds;

    public SalonsAdapter(Context context, List<Round> rounds) {
        this.context = context;
        this.rounds = rounds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_round, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Round round = rounds.get(position);

        // bind
        if (round != null) {
            try {
                Picasso.with(context)
                        .load(round.getProduct_image())
                        .placeholder(context.getResources().getDrawable(R.drawable.placeholder))
                        .into(viewHolder.imgProductImage);
            }catch (Exception e){}

            viewHolder.tvProductName.setText(Common.Instance(context).removeEmptyLines(round.getProduct_name()));
            viewHolder.tvProductCategory.setText(Common.Instance(context).removeEmptyLines(round.getCategory_name()));
            viewHolder.tvStartTime.setText(Common.Instance(context).removeEmptyLines(round.getRound_start_time()));
            viewHolder.cardsRecycler.setAdapter(new SalonCardsAdapter(context, GawlaDataBse.getGawlaDatabase(context).cardDao().getCardsById(round.getSalon_id())));

            Common.Instance(context).changeDrawableViewColor(viewHolder.tvProductCategory, round.getCategory_color());

            // open round page
            viewHolder.btnJoinRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, SalonActivity.class);
                    // send round's data to round page
                    i.putExtra("product_id", round.getProduct_id());
                    i.putExtra("salon_id", round.getSalon_id());
                    i.putExtra("product_name", round.getProduct_name());
                    i.putExtra("category_name", round.getCategory_name());
                    i.putExtra("category_color", round.getCategory_color());
                    i.putExtra("country_name", round.getCountry_name());
                    i.putExtra("product_commercial_price", round.getProduct_commercial_price());
                    i.putExtra("product_product_description", round.getProduct_product_description());
                    i.putExtra("product_image", round.getProduct_image());
                    i.putExtra("round_start_time", round.getRound_start_time());
                    i.putExtra("round_end_time", round.getRound_end_time());
                    i.putExtra("first_join_time", round.getFirst_join_time());
                    i.putExtra("second_join_time", round.getSecond_join_time());
                    i.putExtra("round_date", round.getRound_date());
                    i.putExtra("round_time", round.getRound_time());
                    i.putExtra("rest_time", round.getRest_time());

                    // start with transition
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(viewHolder.imgProductImage, "transProductImage");
                    pairs[1] = new Pair<View, String>(viewHolder.tvProductName, "transProductName");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), pairs);
                    context.startActivity(i, options.toBundle());
                }
            });

            Common.Instance(context).setAnimation(viewHolder.itemView);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        ((ViewHolder)holder).clearAnimation();
    }

    @Override
    public int getItemCount() {
        return rounds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvProductCategory, tvStartTime, btnJoinRound;
        ImageView imgProductImage;
        RecyclerView cardsRecycler;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProductImage = itemView.findViewById(R.id.round_product_image);
            tvProductName = itemView.findViewById(R.id.round_product_name);
            tvProductCategory = itemView.findViewById(R.id.round_product_category);
            tvStartTime = itemView.findViewById(R.id.round_start_time);
            btnJoinRound = itemView.findViewById(R.id.round_btn_join);
            cardsRecycler = itemView.findViewById(R.id.salon_cards_recycler);
            cardsRecycler.setHasFixedSize(true);
            cardsRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        }

        private void clearAnimation() {
            itemView.clearAnimation();
        }
    }
}
