package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.general.Interfaces.OnItemClickListener;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Category;

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

        viewHolder.tvCategory.setText(category.getCategoryName());
        viewHolder.tvCategory.setTextColor(Color.parseColor(category.getCategoryColor()));

        // selected ?
        if (selectedPosition == i) { // selected
            selectedUI(viewHolder, category);
        } else { // !selected
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
        View bottomLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_category);
            bottomLine = itemView.findViewById(R.id.bottom_line_category);
        }
    }
}