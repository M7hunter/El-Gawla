package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.util.Common;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;
    private ClickInterface.OnItemClickListener itemClickListener;
    private int selectedPosition = 0;

    public CardListAdapter(List<Card> cardList, Context context, ClickInterface.OnItemClickListener itemClickListener) {
        this.cardList = cardList;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat_card_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Card card = cardList.get(position);

        Common.Instance().changeDrawableViewColor(holder.vCardIcon, card.getCard_color());

        if (selectedPosition == position)
        {
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey_border_primary));
        }
        else
        {
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_grey));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, holder.getAdapterPosition());

                selectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View vCardIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vCardIcon = itemView.findViewById(R.id.v_card_icon_cardList);
        }
    }
}
