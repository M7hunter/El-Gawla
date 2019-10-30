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

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {

    private List<Category> categoryList;
    private ClickInterface.OnItemClickListener clickListener;

    private int selectedPosition = 0;

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

        viewHolder.tvCategory.setText(category.getCategoryName());
        ImageLoader.getInstance().loadIcon(category.getCategoryImage(), viewHolder.ivCatIcon);
        viewHolder.cat_layout.setBackgroundColor(Color.parseColor(category.getCategoryColor()));

        // selected ?
        if (selectedPosition == i)
        { // selected
            selectedUI(viewHolder, category);
        }
        else
        { // !selected
            unselectedUI(viewHolder);
        }

        // select
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, viewHolder.getAdapterPosition());

                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }


    private void selectedUI(ViewHolder viewHolder, Category category) {
        viewHolder.bottomLine.setBackgroundColor(Color.parseColor(category.getCategoryColor()));
    }

    private void unselectedUI(ViewHolder viewHolder) {
        viewHolder.bottomLine.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory;
        ImageView ivCatIcon;
        View cat_layout, bottomLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_cat_label);
            ivCatIcon = itemView.findViewById(R.id.iv_cat_icon);
            cat_layout = itemView.findViewById(R.id.cat_layout);
            bottomLine = itemView.findViewById(R.id.v_selected_cat);
        }
    }
}