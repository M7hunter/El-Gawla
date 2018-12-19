package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Views.ProductDetailsActivity;

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
      return new Holder(LayoutInflater.from(context).inflate(R.layout.item_product_sub_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        ProductSubImage subImage = imagesList.get(i);

        Picasso.with(context).load(subImage.getImageUrl()).placeholder(R.drawable.palceholder).into(holder.productSubImage);

        // events

        int mainWidth, mainHeight;
        mainWidth = ((ProductDetailsActivity) context).imProductImage.getWidth();
        mainHeight = ((ProductDetailsActivity) context).imProductImage.getHeight();

        int subWidth, subHeight;
        subWidth = holder.itemView.getWidth();
        subHeight = holder.itemView.getHeight();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.animate().scaleX();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView productSubImage;

        public Holder(@NonNull View itemView) {
            super(itemView);

            productSubImage = itemView.findViewById(R.id.product_sub_image);
        }
    }
}
