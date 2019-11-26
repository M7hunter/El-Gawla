package it_geeks.info.elgawla.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.util.DiffUtils.CatDiffCallback;
import it_geeks.info.elgawla.util.ImageLoader;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;

public class StoreCategoryAdapter extends RecyclerView.Adapter<StoreCategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private ClickInterface.OnItemClickListener clickListener;

    public StoreCategoryAdapter(List<Category> categoryList, ClickInterface.OnItemClickListener clickListener) {
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_store_category, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Category category = categoryList.get(i);

        viewHolder.catLayout.setBackgroundColor(Color.parseColor(category.getCategoryColor()));
        viewHolder.tvCategoryTitle.setText(category.getCategoryName());
        ImageLoader.getInstance().loadIcon(category.getCategoryImage(), viewHolder.ivCategoryIcon);

        // select
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
    }

    public void updateCatsList(List<Category> newList) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CatDiffCallback(categoryList, newList));

        categoryList.clear();
        categoryList.addAll(newList);
        notifyDataSetChanged();

//        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategoryTitle;
        ImageView ivCategoryIcon;
        View catLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoryTitle = itemView.findViewById(R.id.tv_category_store_title);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_store_icon);
            catLayout = itemView.findViewById(R.id.ll_category_store_layout);
        }
    }
}