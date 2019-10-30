package it_geeks.info.elgawla.util;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import it_geeks.info.elgawla.repository.Models.Round;

public class RoundDiffCallback extends DiffUtil.Callback{

        private List<Round> oldRoundsList, newRoundsList;

        public RoundDiffCallback(List<Round> newPersons, List<Round> oldPersons) {
            this.newRoundsList = newPersons;
            this.oldRoundsList = oldPersons;
        }

        @Override
        public int getOldListSize() {
            return oldRoundsList.size();
        }

        @Override
        public int getNewListSize() {
            return newRoundsList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldRoundsList.get(oldItemPosition).getSalon_id() == newRoundsList.get(newItemPosition).getSalon_id();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldRoundsList.get(oldItemPosition).equals(newRoundsList.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            //you can return particular field for changed item.
            return newRoundsList.get(newItemPosition);
        }
    }
