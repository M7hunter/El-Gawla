package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.util.Interfaces.ClickInterface;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.util.Common;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;
    private ClickInterface.OnItemClickListener itemClickListener;

    public CardListAdapter(Context context, List<Card> cardList, ClickInterface.OnItemClickListener itemClickListener) {
        this.context = context;
        this.cardList = cardList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Card card = cardList.get(position);

        // bind
        Common.Instance().changeDrawableViewColor(holder.vCardIcon, card.getCard_color());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, holder.getAdapterPosition());
                notifyItemChanged(position);
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
