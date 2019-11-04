package it_geeks.info.elgawla.Adapters;

import android.content.Context;
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
import it_geeks.info.elgawla.views.main.MainActivity;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatHolder> {

    private Context context;
    private List<Category> categoryList;
    private ClickInterface.OnItemClickListener clickListener;

    private int selectedPosition = 0;

    public CategoryAdapter(List<Category> categoryList, Context context, ClickInterface.OnItemClickListener clickListener) {
        this.categoryList = categoryList;
        this.clickListener = clickListener;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (context.getClass().equals(MainActivity.class))
        {
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    public CatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View child = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup, false);
        child.getLayoutParams().width = viewGroup.getWidth() / getItemCount(); // : weight = 1

        if (i == 0)
        {
            return new HomeCatHolder(child);
        }
        return new FilterCatHolder(child);
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

                selectedPosition = catHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        catHolder.bind();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    abstract class CatHolder extends RecyclerView.ViewHolder {

        TextView tvCategory;
        ImageView ivCatIcon;
        View cat_layout, bottomLine;

        CatHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_cat_label);
            ivCatIcon = itemView.findViewById(R.id.iv_cat_icon);
            cat_layout = itemView.findViewById(R.id.cat_layout);
            bottomLine = itemView.findViewById(R.id.v_selected_cat);
        }

        abstract void bind();
    }

    public class HomeCatHolder extends CatHolder {

        HomeCatHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void bind() {
            bottomLine.setVisibility(View.GONE);
        }
    }

    public class FilterCatHolder extends CatHolder {

        FilterCatHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bind() {
            // selected ?
            if (selectedPosition == getAdapterPosition())
            { // selected
                bottomLine.setBackgroundColor(Color.parseColor(categoryList.get(getAdapterPosition()).getCategoryColor()));
            }
            else
            { // !selected
                bottomLine.setBackgroundColor(Color.WHITE);
            }
        }
    }
}