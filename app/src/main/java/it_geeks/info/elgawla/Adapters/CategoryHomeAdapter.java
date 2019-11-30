package it_geeks.info.elgawla.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;

public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.CatHolder> {

    private List<Category> categoryList;
    private ClickInterface.OnItemClickListener clickListener;

    public CategoryHomeAdapter(List<Category> categoryList, ClickInterface.OnItemClickListener clickListener) {
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View child = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_category, viewGroup, false);
        child.getLayoutParams().width = viewGroup.getWidth() / getItemCount(); // :weight = 1
        return new CatHolder(child);
    }

    @Override
    public void onBindViewHolder(@NonNull final CatHolder catHolder, int i) {
        final Category category = categoryList.get(i);

        catHolder.tvCategory.setText(category.getCategoryName());
        ImageLoader.getInstance().loadIcon(category.getCategoryImage(), catHolder.ivCatIcon);
        catHolder.cat_layout.setBackgroundColor(Color.parseColor(category.getCategoryColor()));

        // select
        catHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, catHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CatHolder extends RecyclerView.ViewHolder {

        TextView tvCategory;
        ImageView ivCatIcon;
        View cat_layout;

        CatHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_cat_label);
            ivCatIcon = itemView.findViewById(R.id.iv_cat_icon);
            cat_layout = itemView.findViewById(R.id.cat_layout);
        }
    }
}