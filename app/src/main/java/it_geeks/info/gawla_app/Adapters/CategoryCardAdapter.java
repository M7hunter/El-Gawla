package it_geeks.info.gawla_app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Category;

public class CategoryCardAdapter extends RecyclerView.Adapter<CategoryCardAdapter.ViewHolder> {

    private List<Category> categoryList;
    private int clickedPosition = 0;

    public CategoryCardAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Category category = categoryList.get(position);

        holder.rbCategory.setText(category.getCategoryName());

        if (clickedPosition == position) {
            holder.rbCategory.setChecked(true);
//            ((CardActivity) context).cardPrice.setText(String.valueOf(categoryList.get(position).getCategoryId()));
        } else {
            holder.rbCategory.setChecked(false);
        }

        holder.rbCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                clickedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton rbCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rbCategory = itemView.findViewById(R.id.rb_category);
        }
    }
}
