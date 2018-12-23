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
import android.widget.Toast;

import java.util.List;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Views.SalonActivity;

public class BottomCardsAdapter extends RecyclerView.Adapter<BottomCardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;

    public BottomCardsAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salon_bottom_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Card card = cardList.get(i);

        viewHolder.cardDescription.setText(card.getCard_details());

        GradientDrawable background = (GradientDrawable) viewHolder.cardIcon.getBackground();
        background.setColor(Color.parseColor(card.getCard_color()));

        //open single card sheet
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
//                ((SalonActivity) context).mBottomSheetDialogSingleCard.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardDescription;
        View cardIcon, btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardDescription = itemView.findViewById(R.id.bottom_card_status);
            cardIcon = itemView.findViewById(R.id.bottom_card_icon);
            btn = itemView.findViewById(R.id.bottom_cards_btn);
        }
    }
}