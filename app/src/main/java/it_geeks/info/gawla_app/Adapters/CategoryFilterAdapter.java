package it_geeks.info.gawla_app.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.util.ImageLoader;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {

    private List<Category> categoryList;
    private ClickInterface.OnItemClickListener clickListener;

    public CategoryFilterAdapter(List<Category> categoryList, ClickInterface.OnItemClickListener clickListener) {
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_filter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Category category = categoryList.get(i);

        viewHolder.cat_layout.setBackgroundColor(Color.parseColor(category.getCategoryColor()));
        viewHolder.tvCategory.setText(category.getCategoryName());

        ImageLoader.getInstance().loadIcon(category.getCategoryImage(), viewHolder.ivCatIcon);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, category.getCategoryId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory;
        ImageView ivCatIcon;
        View cat_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_cat_label);
            ivCatIcon = itemView.findViewById(R.id.iv_cat_icon);
            cat_layout = itemView.findViewById(R.id.cat_layout);
        }
    }
}