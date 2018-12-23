package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it_geeks.info.gawla_app.General.Common;
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
        final Card card = cardList.get(i);

        viewHolder.cardDescription.setText(card.getCard_details());
        Common.Instance(context).changeDrawableViewColor(viewHolder.cardIcon, card.getCard_color());

        //open single card sheet
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBottomSheetSingleCard(card).show();
            }
        });
    }

    private BottomSheetDialog initBottomSheetSingleCard(Card card) {
        final BottomSheetDialog mBottomSheetDialogSingleCard;
        mBottomSheetDialogSingleCard = new BottomSheetDialog(context);
        final View sheetView = ((SalonActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_single_card, null);

        //init bottom sheet views
        TextView cardTitle, cardDescription, cardCost;
        View cardIcon;
        cardTitle = sheetView.findViewById(R.id.single_card_title);
        cardDescription = sheetView.findViewById(R.id.single_card_description);
        cardCost = sheetView.findViewById(R.id.single_card_cost);
        cardIcon = sheetView.findViewById(R.id.single_card_icon);

        cardTitle.setText(card.getCard_name());
        cardDescription.setText(card.getCard_details());
        cardCost.setText(card.getCard_cost());

        Common.Instance(context).changeDrawableViewColor(cardIcon, card.getCard_color());

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_single_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogSingleCard.isShowing()) {
                    mBottomSheetDialogSingleCard.dismiss();

                } else {
                    mBottomSheetDialogSingleCard.show();
                }
            }
        });

        mBottomSheetDialogSingleCard.setContentView(sheetView);
        Common.Instance(context).setBottomSheetHeight(sheetView);
        mBottomSheetDialogSingleCard.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        return mBottomSheetDialogSingleCard;
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
            btn = itemView.findViewById(R.id.bottom_card_btn);
        }
    }
}