package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.OnItemClickListener;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnItemClickListener clickListener;
    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<Category> categoryList, OnItemClickListener clickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Category category = categoryList.get(i);

        // check if selected
        if (selectedPosition == i) { // selected
            selectedUI(viewHolder, category);
        } else { // !selected
            unselectedUI(viewHolder, category);
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
        viewHolder.tvCategory.setTextColor(Color.WHITE);
        Common.Instance(context).changeDrawableViewColor(viewHolder.itemView, category.getCategoryColor());
    }

    private void unselectedUI(ViewHolder viewHolder, Category category) {
        viewHolder.tvCategory.setTextColor(Color.parseColor(category.getCategoryColor()));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
}
