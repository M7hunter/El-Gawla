package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.views.SalonActivity;

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

    private BottomSheetDialog initBottomSheetSingleCard(final Card card) {
        final BottomSheetDialog mBottomSheetDialogSingleCard;
        mBottomSheetDialogSingleCard = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        final View sheetView = ((SalonActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_single_card, null);

        //init bottom sheet views
        TextView cardTitle, cardDescription, cardCost;
        View cardIcon;
        final CardView btnConfirmBuying;
        final ProgressBar pbBuyCard;

        cardTitle = sheetView.findViewById(R.id.single_card_title);
        cardDescription = sheetView.findViewById(R.id.single_card_description);
        cardCost = sheetView.findViewById(R.id.single_card_cost);
        cardIcon = sheetView.findViewById(R.id.single_card_icon);
        btnConfirmBuying = sheetView.findViewById(R.id.btn_confirm_buying_card);
        pbBuyCard = sheetView.findViewById(R.id.pb_buy_card);

        cardTitle.setText(card.getCard_name());
        cardDescription.setText(card.getCard_details());
        cardCost.setText(card.getCard_cost());

        Common.Instance(context).changeDrawableViewColor(cardIcon, card.getCard_color());

        btnConfirmBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
                buyCard(card, btnConfirmBuying, pbBuyCard);
            }
        });

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

    private void buyCard(Card card, final CardView btnConfirmBuying, final ProgressBar pbBuyCard) {
        int user_id = SharedPrefManager.getInstance(context).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(context).getUser().getApi_token();

        RetrofitClient.getInstance(context).executeConnectionToServer(context, "addCardsToUser", new Request(user_id, api_token,  card.getCard_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
                displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayConfirmationBtn(CardView btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.VISIBLE);
        pbBuyCard.setVisibility(View.GONE);
    }

    private void hideConfirmationBtn(CardView btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.GONE);
        pbBuyCard.setVisibility(View.VISIBLE);
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