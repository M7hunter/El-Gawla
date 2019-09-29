package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.repository.Models.ProductSubImage;
import it_geeks.info.elgawla.views.salon.SalonActivity;

public class ProductSubImagesAdapter extends RecyclerView.Adapter<ProductSubImagesAdapter.Holder> {

    private Context context;
    private List<ProductSubImage> imagesList;

    public ProductSubImagesAdapter(Context context, List<ProductSubImage> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product_sub_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final ProductSubImage subImage = imagesList.get(i);

        ImageLoader.getInstance().loadImage(subImage.getImageUrl(), holder.productSubImage);

        // check if video
        if (subImage.getImageUrl().endsWith(".mp4") || subImage.getImageUrl().endsWith(".3gp"))
        {
            setPlayIcon(holder);
        }

        // events
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // swap links & images
                String oldUrl = subImage.getImageUrl();
                subImage.setImageUrl(((SalonActivity) context).SubImage.getImageUrl());

                Drawable drawable = ((SalonActivity) context).ivProductMainViewer.getDrawable();
                ((SalonActivity) context).switchImageVideo(oldUrl, holder.productSubImage.getDrawable());
                holder.productSubImage.setImageDrawable(drawable);
            }
        });
    }

    private void setPlayIcon(Holder holder) {
        Drawable[] layers = new Drawable[2];
        layers[0] = holder.productSubImage.getDrawable();
        layers[1] = context.getResources().getDrawable(R.drawable.ic_play);
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        holder.productSubImage.setImageDrawable(layerDrawable);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView productSubImage;

        Holder(@NonNull View itemView) {
            super(itemView);

            productSubImage = itemView.findViewById(R.id.product_sub_image);
        }
    }
}
