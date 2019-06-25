package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private int[] slide_Images;
    private String[] slide_headings, slide_desc;

    public SliderAdapter(Context context) {
        slide_Images = new int[]{
                R.drawable.subscribe,
                R.drawable.auction,
                R.drawable.win
        };

        slide_headings = new String[]{
                context.getResources().getString(R.string.slide_heading1),
                context.getResources().getString(R.string.slide_heading2),
                context.getResources().getString(R.string.slide_heading3)
        };

        slide_desc = new String[]{
                context.getResources().getString(R.string.slide_descs1),
                context.getResources().getString(R.string.slide_descs2),
                context.getResources().getString(R.string.slide_descs3)
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_intro, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.slideImageView.setImageResource(slide_Images[position]);
        holder.slideHeading.setText(slide_headings[position]);
        holder.slideDesc.setText(slide_desc[position]);
    }

    @Override
    public int getItemCount() {
        return slide_Images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView slideImageView;
        TextView slideHeading, slideDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            slideImageView = itemView.findViewById(R.id.slide_Image);
            slideHeading = itemView.findViewById(R.id.slide_Heading);
            slideDesc = itemView.findViewById(R.id.slide_Desc);
        }
    }
}