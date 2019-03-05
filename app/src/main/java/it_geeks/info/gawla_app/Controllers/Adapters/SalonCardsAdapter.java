package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.general.Common;

public class SalonCardsAdapter extends RecyclerView.Adapter<SalonCardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;

    public SalonCardsAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salon_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Common.Instance(context).changeDrawableViewColor(viewHolder.cardIcon, cardList.get(i).getCard_color());
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