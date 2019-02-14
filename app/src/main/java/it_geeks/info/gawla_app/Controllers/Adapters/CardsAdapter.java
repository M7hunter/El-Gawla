package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.views.CardActivity;

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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Card card = cardList.get(position);

        // bind
        viewHolder.cardTitle.setText(card.getCard_name());
        viewHolder.cardDescription.setText(card.getCard_details());
        Common.Instance(context).changeDrawableViewColor(viewHolder.cardIcon, card.getCard_color());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, CardActivity.class);
                i.putExtra("card", card);
                i.putExtra("card_list", (Serializable) cardList);

                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardTitle, cardDescription;
        View cardIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardTitle = itemView.findViewById(R.id.card_title);
            cardDescription = itemView.findViewById(R.id.card_description);
            cardIcon = itemView.findViewById(R.id.card_icon);
        }
    }
}
