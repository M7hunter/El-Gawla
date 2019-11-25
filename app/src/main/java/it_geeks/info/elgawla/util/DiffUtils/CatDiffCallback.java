package it_geeks.info.elgawla.util.DiffUtils;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import it_geeks.info.elgawla.repository.Models.Category;
import it_geeks.info.elgawla.repository.Models.Salon;

public class CatDiffCallback extends DiffUtil.Callback {

    private List<Category> oldCatsList, newCatsList;

    public CatDiffCallback(List<Category> newPersons, List<Category> oldPersons) {
        this.newCatsList = newPersons;
        this.oldCatsList = oldPersons;
    }

    @Override
    public int getOldListSize() {
        return oldCatsList.size();
    }

    @Override
    public int getNewListSize() {
        return newCatsList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCatsList.get(oldItemPosition).getCategoryId() == newCatsList.get(newItemPosition).getCategoryId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCatsList.get(oldItemPosition).getCategoryName().equals(newCatsList.get(newItemPosition).getCategoryName());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
//        return newCatsList.get(newItemPosition);

        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
