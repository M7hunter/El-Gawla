package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;

    public CardsAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Card card = cardList.get(i);

        viewHolder.cardTitle.setText(card.getCard_name());
        viewHolder.cardDescription.setText(card.getCard_cost());
        viewHolder.cardCount.setText(String.valueOf(card.getCount()));

        GradientDrawable background = (GradientDrawable) viewHolder.cardIcon.getBackground();
        background.setColor(Color.parseColor(card.getCard_color()));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardTitle, cardDescription, cardCount;
        View cardIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardTitle = itemView.findViewById(R.id.card_title);
            cardDescription = itemView.findViewById(R.id.card_description);
            cardCount = itemView.findViewById(R.id.card_count);
            cardIcon = itemView.findViewById(R.id.card_icon);
        }
    }
}
