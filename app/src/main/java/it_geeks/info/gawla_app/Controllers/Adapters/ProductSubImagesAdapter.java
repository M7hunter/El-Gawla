package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.General.MediaInterfaces.ItemMedia;
import it_geeks.info.gawla_app.General.MediaInterfaces.ViewHolderMedia;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubVideo;
import it_geeks.info.gawla_app.Views.SalonActivity;

public class ProductSubImagesAdapter extends RecyclerView.Adapter<ViewHolderMedia> {

    private Context context;
    private List<ProductSubImage> imagesList;

    public ProductSubImagesAdapter(Context context, List<ProductSubImage> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @Override
    public int getItemViewType(int position) {
        return imagesList.get(position).getItemType();
    }

    @NonNull
    @Override
    public ViewHolderMedia onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = null;

        switch (i) {
            case ItemMedia.ImageType:
                v = LayoutInflater.from(context).inflate(R.layout.item_product_sub_image, viewGroup, false);
                return new ImageHolder(v);

            case ItemMedia.VideoType:
                v = LayoutInflater.from(context).inflate(R.layout.item_product_sub_image, viewGroup, false);
                return new VideoHolder(v);
        }

        return new ImageHolder(LayoutInflater.from(context).inflate(R.layout.item_product_sub_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMedia viewHolderMedia, int i) {
        final ItemMedia itemMedia = imagesList.get(i);

        viewHolderMedia.bind(itemMedia);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class ImageHolder extends ViewHolderMedia {

        ImageView productSubImage;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            productSubImage = itemView.findViewById(R.id.product_sub_image);
        }

        @Override
        public void bind(ItemMedia item) {
            Picasso.with(context).load(((ProductSubImage) item).getImageUrl()).placeholder(R.drawable.placeholder).into(productSubImage);

            // events
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // swap images
                    Drawable drawable = ((SalonActivity) context).imProductMainImage.getDrawable();
                    ((SalonActivity) context).imProductMainImage.setImageDrawable(productSubImage.getDrawable());
                    productSubImage.setImageDrawable(drawable);

                }
            });
        }
    }

    class VideoHolder extends ViewHolderMedia {

        ImageView productSubImage;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);

            productSubImage = itemView.findViewById(R.id.product_sub_image);
        }

        @Override
        public void bind(ItemMedia item) {
//            Picasso.with(context).load(((ProductSubVideo) item).getVideoUrl()).placeholder(R.drawable.placeholder).into(productSubImage);

            // events
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // swap images
                    Drawable drawable = ((SalonActivity) context).imProductMainImage.getDrawable();
                    ((SalonActivity) context).imProductMainImage.setImageDrawable(productSubImage.getDrawable());
                    productSubImage.setImageDrawable(drawable);

                }
            });
        }
    }
}
