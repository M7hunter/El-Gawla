package it_geeks.info.elgawla.Adapters;

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

import com.crashlytics.android.Crashlytics;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.DiffUtils.RoundDiffCallback;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.views.salon.ClosedSalonActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;
import it_geeks.info.elgawla.R;

import static it_geeks.info.elgawla.util.Constants.SALON;

public class SalonsAdapter extends RecyclerView.Adapter<SalonsAdapter.ViewHolder> {

    private Context context;
    private List<Salon> salons;
    public static boolean clickable = true;

    public SalonsAdapter(Context context, List<Salon> salons) {
        this.context = context;
        this.salons = salons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salon, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Salon salon = salons.get(position);

        // bind
        if (salon != null)
        {
            ImageLoader.getInstance().loadImage(salon.getProduct_image(), viewHolder.imgProductImage);
            viewHolder.tvProductName.setText(Common.Instance().removeEmptyLines(salon.getProduct_name()));
            viewHolder.tvSalonId.setText(Common.Instance().removeEmptyLines(String.valueOf(salon.getSalon_id())));
            viewHolder.tvStartTime.setText(Common.Instance().removeEmptyLines(salon.getMessage()));

            if (salon.getSalon_cards() == null)
            {

                List<Card> cards = GawlaDataBse.getInstance(context).cardDao().getCardsById(salon.getSalon_id());
                salon.setSalon_cards(cards);
                salon.setProduct_images(GawlaDataBse.getInstance(context).productImageDao().getSubImagesById(salon.getProduct_id()));
            }

            // open salon page
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickable)
                        try
                        {
                            clickable = false;
                            Intent i;
                            Log.d("Ok", "is_closed:: " + salon.isClosed());
                            if (salon.isClosed())
                            {
                                i = new Intent(context, ClosedSalonActivity.class);
                            }
                            else
                            {
                                i = new Intent(context, SalonActivity.class);
                            }
                            // pass salon's data to salon page
                            i.putExtra(SALON, salon);

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), new Pair<View, String>(viewHolder.imgProductImage, "transProductImage"));
                            context.startActivity(i, options.toBundle());
                        }
                        catch (Exception e)
                        {
                            clickable = true;
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                }
            });

        }
        else
        {
            Log.d("test_placeholder: ", "salon == null");
        }
    }

    public void updateRoundsList(List<Salon> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RoundDiffCallback(salons, newList));

        salons.clear();
        salons.addAll(newList);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return salons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvStartTime, tvSalonId;
        ImageView imgProductImage;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProductImage = itemView.findViewById(R.id.round_product_image);
            tvProductName = itemView.findViewById(R.id.round_product_name);
            tvStartTime = itemView.findViewById(R.id.tv_salon_time_state);
            tvSalonId = itemView.findViewById(R.id.tv_salon_id);
        }
    }
}
