package it_geeks.info.elgawla.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.CatFilterHolder> {

    private List<Category> categoryList;
    private ClickInterface.OnItemClickListener clickListener;

    private int selectedPosition = 0;

    public CategoryFilterAdapter(List<Category> categoryList, ClickInterface.OnItemClickListener clickListener) {
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CatFilterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CatFilterHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_filter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CatFilterHolder holder, int i) {
        final Category category = categoryList.get(i);

        holder.tvCategory.setText(category.getCategoryName());
        ImageLoader.getInstance().loadIcon(category.getCategoryImage(), holder.ivCatIcon);
        holder.cat_layout.setBackgroundColor(Color.parseColor(category.getCategoryColor()));

        // selected ?
        if (selectedPosition == holder.getAdapterPosition())
        { // selected
            holder.bottomLine.setBackgroundColor(Color.parseColor(categoryList.get(holder.getAdapterPosition()).getCategoryColor()));
        }
        else
        { // !selected
            holder.bottomLine.setBackgroundColor(Color.WHITE);
        }

        // select
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, holder.getAdapterPosition());

                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CatFilterHolder extends RecyclerView.ViewHolder {

        TextView tvCategory;
        ImageView ivCatIcon;
        View cat_layout, bottomLine;

        CatFilterHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_cat_label);
            ivCatIcon = itemView.findViewById(R.id.iv_cat_icon);
            cat_layout = itemView.findViewById(R.id.cat_layout);
            bottomLine = itemView.findViewById(R.id.v_selected_cat);
        }
    }
}