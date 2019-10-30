package it_geeks.info.elgawla.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.util.Common;

public class SalonCardsIconAdapter extends RecyclerView.Adapter<SalonCardsIconAdapter.ViewHolder> {

    private List<Card> cardList;

    SalonCardsIconAdapter(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salon_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Common.Instance().changeDrawableViewColor(viewHolder.cardIcon, cardList.get(i).getCard_color());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View cardIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardIcon = itemView.findViewById(R.id.salon_card_image);
        }
    }
}