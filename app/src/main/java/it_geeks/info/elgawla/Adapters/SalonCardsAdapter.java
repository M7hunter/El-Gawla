package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Card;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.salon.SalonActivity;
import it_geeks.info.elgawla.views.store.PaymentMethodsActivity;

import static it_geeks.info.elgawla.util.Constants.REQ_CODE_BUY_CARD;
import static it_geeks.info.elgawla.util.Constants.REQ_USE_CARD;

public class SalonCardsAdapter extends RecyclerView.Adapter<SalonCardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;
    private int salonId, round_id;
    public BottomSheetDialog mBottomSheetDialogSingleCard;
    private SnackBuilder snackBuilder;

    public SalonCardsAdapter(Context context, List<Card> cardList, int salon_id, int round_id, View parenView) {
        this.context = context;
        this.cardList = cardList;
        this.salonId = salon_id;
        this.round_id = round_id;
        snackBuilder = new SnackBuilder(parenView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salon_bottom_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Card card = cardList.get(i);

        viewHolder.tvCardTitle.setText(card.getCard_name());
        viewHolder.tvCardCount.setText(String.valueOf(card.getCount()));
        Common.Instance().changeDrawableViewColor(viewHolder.cardIcon, card.getCard_color());

        if (card.getCount() > 0)
        { // use card state
            viewHolder.btn.setBackgroundColor(context.getResources().getColor(R.color.greenBlue));
            viewHolder.btn.setText(context.getString(R.string.use_card));
            viewHolder.tvCardCount.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
        }
        else
        { // buy card  state
            viewHolder.btn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btn.setText(context.getString(R.string.buy_card));
            viewHolder.tvCardCount.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_red));
        }

        //open single card sheet
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (card.getCount() > 0)
                {
                    if (card.getCard_type().equals("gold"))
                    {
                        ((SalonActivity) context).useGoldenCard();
                        ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();

                    }
                    else
                    {
                        useCard(card, viewHolder.btn, viewHolder.pb);
                    }
                }
                else
                {
                    initBottomSheetSingleCard(card).show();
                }
            }
        });
    }

    private BottomSheetDialog initBottomSheetSingleCard(final Card card) {
        mBottomSheetDialogSingleCard = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        final View sheetView = ((SalonActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_single_card, null);

        //init bottom sheet views
        TextView cardTitle, cardDescription, cardCost;
        View cardIcon;
        final CardView btnConfirmBuying;

        cardTitle = sheetView.findViewById(R.id.single_card_title);
        cardDescription = sheetView.findViewById(R.id.single_card_description);
        cardCost = sheetView.findViewById(R.id.single_card_cost);
        cardIcon = sheetView.findViewById(R.id.single_card_icon);
        btnConfirmBuying = sheetView.findViewById(R.id.btn_confirm_buying_card);

        cardTitle.setText(card.getCard_name());
        cardDescription.setText(card.getCard_details());
        cardCost.setText(card.getCard_cost());

        Common.Instance().changeDrawableViewColor(cardIcon, card.getCard_color());

        btnConfirmBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCard(card);
            }
        });

        //close bottom sheet
        sheetView.findViewById(R.id.close_bottom_sheet_single_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetDialogSingleCard.isShowing())
                {
                    mBottomSheetDialogSingleCard.dismiss();
                }
                else
                {
                    mBottomSheetDialogSingleCard.show();
                }
            }
        });

        mBottomSheetDialogSingleCard.setContentView(sheetView);
        Common.Instance().setBottomSheetHeight(sheetView);
        mBottomSheetDialogSingleCard.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        return mBottomSheetDialogSingleCard;
    }

    private void useCard(final Card card, final View btnConfirmBuying, final ProgressBar pbBuyCard) {
        hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
        final int userId = SharedPrefManager.getInstance(context).getUser().getUser_id();
        final String username = SharedPrefManager.getInstance(context).getUser().getName();
        String apiToken = SharedPrefManager.getInstance(context).getUser().getApi_token();
        RetrofitClient.getInstance(context).fetchDataFromServer(context,
                REQ_USE_CARD, new RequestModel<>(REQ_USE_CARD, userId, apiToken, salonId, card.getCard_id(), round_id
                        , null, null), new HandleResponses() {
                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        snackBuilder.setSnackText(mainObject.get("message").getAsString()).showSnack();

                        ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
                        card.setCount(card.getCount() - 1);
                        ((SalonActivity) context).getUserCardsForSalonFromServer(); // refresh the store list

                        try
                        {
                            JSONObject o = new JSONObject();
                            o.put("salon_id", salonId);
                            o.put("user", username);
                            o.put("type", card.getCard_type());
                            o.put("lang", SharedPrefManager.getInstance(context).getSavedLang());
                            ((SalonActivity) context).getSocketUtils().emitData("use_card", o);
                        }
                        catch (JSONException e)
                        {
                            Log.e("socket use_card: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void afterResponse() {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                    }

                    @Override
                    public void onConnectionError(String errorMessage) {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    private void buyCard(final Card card) {
        Intent i = new Intent(context, PaymentMethodsActivity.class);
        i.putExtra("card_to_buy", card);
        i.putExtra("is_card", true);
        ((SalonActivity) context).startActivityForResult(i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), REQ_CODE_BUY_CARD);
    }

    private void displayConfirmationBtn(View btn, ProgressBar pb) {
        btn.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
    }

    private void hideConfirmationBtn(View btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.INVISIBLE);
        pbBuyCard.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCardTitle, tvCardCount;
        View cardIcon;
        Button btn;
        ProgressBar pb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCardTitle = itemView.findViewById(R.id.bottom_card_title);
            tvCardCount = itemView.findViewById(R.id.tv_cards_count_cards_bag);
            cardIcon = itemView.findViewById(R.id.bottom_card_icon);
            btn = itemView.findViewById(R.id.bottom_card_btn);
            pb = itemView.findViewById(R.id.pb_bottom_card);
        }
    }
}