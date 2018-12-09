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

public class ProductSubImagesAdapter extends RecyclerView.Adapter<ProductSubImagesAdapter.Holder> {

    private Context context;
    private List<String> imagesList;

    public ProductSubImagesAdapter(Context context, List<String> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      return new Holder(LayoutInflater.from(context).inflate(R.layout.item_product_sub_image,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        String imageUri = imagesList.get(i);
        Picasso.with(context).load(imageUri).placeholder(R.mipmap.ic_launcher_gawla).into(holder.productSubImage);

        // events
        holder.productSubImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
