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

import com.crashlytics.android.Crashlytics;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.Storage.GawlaDataBse;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.views.salon.ClosedSalonActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.util.Constants.SALON;

public class SalonsMiniAdapter extends RecyclerView.Adapter<SalonsMiniAdapter.ViewHolder> {

    private Context context;
    private List<Salon> salons;
    public static boolean clickable = true;
    private String from = "";

    public SalonsMiniAdapter(Context context, List<Salon> salons, String from) {
        this.context = context;
        this.salons = salons;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salon_mini, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Salon salon = salons.get(position);

        // bind
        ImageLoader.getInstance().loadImage(salon.getProduct_image(), viewHolder.imgProductImage);
        viewHolder.tvProductName.setText(Common.Instance().removeEmptyLines(salon.getProduct_name()));
        viewHolder.tvSalonId.setText(Common.Instance().removeEmptyLines(String.valueOf(salon.getSalon_id())));
        viewHolder.tvStartTime.setText(from.equals("my_archive") ? salon.getSalon_date() : Common.Instance().removeEmptyLines(salon.getMessage()));

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
