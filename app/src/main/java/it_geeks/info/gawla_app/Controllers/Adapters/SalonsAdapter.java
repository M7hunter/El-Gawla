package it_geeks.info.gawla_app.Controllers.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.RoundDiffCallback;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.SalonActivity;
import it_geeks.info.gawla_app.repository.Models.Round;
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.tvProductName.setText(Common.Instance(context).removeEmptyLines(round.getProduct_name()));
            viewHolder.tvProductCategory.setText(Common.Instance(context).removeEmptyLines(round.getCategory_name()));
            viewHolder.tvStartTime.setText(Common.Instance(context).removeEmptyLines(round.getMessage()));

            if (round.getSalon_cards() != null) {
                viewHolder.cardsRecycler.setAdapter(new SalonCardsIconAdapter(context, round.getSalon_cards()));
            } else {
                List<Card> cards = GawlaDataBse.getGawlaDatabase(context).cardDao().getCardsById(round.getSalon_id());
                viewHolder.cardsRecycler.setAdapter(new SalonCardsIconAdapter(context, cards));
                round.setSalon_cards(cards);
                round.setProduct_images(GawlaDataBse.getGawlaDatabase(context).productImageDao().getSubImagesById(round.getProduct_id()));
            }

            Common.Instance(context).changeDrawableViewColor(viewHolder.tvProductCategory, round.getCategory_color());

            // open round page
            viewHolder.btnJoinRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        Intent i = new Intent(context, SalonActivity.class);
                        // send round's data to round page
                        i.putExtra("round", round);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), new Pair<View, String>(viewHolder.imgProductImage, "transProductImage"));
                            context.startActivity(i, options.toBundle());
                        } else {
                            context.startActivity(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            Log.d("test_placeholder: ", "salon == null");
        }
    }

    public void updateRoundsList(List<Round> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RoundDiffCallback(rounds, newList));
        diffResult.dispatchUpdatesTo(this);
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
    }
}
