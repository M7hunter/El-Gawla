package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.gawla_app.repository.Models.Category;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.views.card.BuyCardActivity;
import it_geeks.info.gawla_app.views.salon.SalonActivity;

import static it_geeks.info.gawla_app.util.Constants.REQ_GET_ALL_CATEGORIES;
import static it_geeks.info.gawla_app.util.Constants.REQ_USE_CARD;

public class SalonCardsAdapter extends RecyclerView.Adapter<SalonCardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;
    private int salonId, round_id;
    private BottomSheetDialog mBottomSheetDialogSingleCard;
    private List<Category> categoryList = new ArrayList<>();

    public SalonCardsAdapter(Context context, List<Card> cardList, int salon_id, int round_id) {
        this.context = context;
        this.cardList = cardList;
        this.salonId = salon_id;
        this.round_id = round_id;

        getCategoriesFromServer();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salon_bottom_card, viewGroup, false));
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

        Common.Instance().changeDrawableViewColor(cardIcon, card.getCard_color());

        btnConfirmBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCard(card, btnConfirmBuying, pbBuyCard);
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
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                REQ_USE_CARD, new Request<>(REQ_USE_CARD, userId, apiToken, salonId, card.getCard_id(), round_id
                        , null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
                        card.setCount(card.getCount() - 1);
                        ((SalonActivity) context).getUserCardsForSalonFromServer(); // refresh the cards list

                        try
                        {
                            JSONObject o = new JSONObject();
                            o.put("salon_id", salonId);
                            o.put("user", username);
                            o.put("type", card.getCard_type());
                            ((SalonActivity) context).getSocketUtils().emitData("use_card", o);
                        } catch (JSONException e)
                        {
                            Log.e("socket use_card: ", e.getMessage());
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void handleAfterResponse() {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        displayConfirmationBtn(btnConfirmBuying, pbBuyCard);
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buyCard(final Card card, final View btnConfirmBuying, final ProgressBar pbBuyCard) {
        Intent i = new Intent(context, BuyCardActivity.class);
        i.putExtra("card_to_buy", card);
        i.putExtra("salon_id_to_buy_card", salonId);
        i.putExtra("categories_to_buy_card", (Serializable) categoryList);
        context.startActivity(i);

//        hideSingleConfirmationBtn(btnConfirmBuying, pbBuyCard);
//        RetrofitClient.getInstance(context).executeConnectionToServer(context, "addCardsToUser",
//                new Request(SharedPrefManager.getInstance(context).getUser().getUser_id(),
//                        SharedPrefManager.getInstance(context).getUser().getApi_token(),
//                        card.getCard_id()),
//                new HandleResponses() {
//                    @Override
//                    public void handleTrueResponse(JsonObject mainObject) {
//                        Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
//                        mBottomSheetDialogSingleCard.dismiss();
//                        ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
//                        ((SalonActivity) context).getUserCardsForSalonFromServer(); // refresh the cards list
//                    }
//
//                    @Override
//                    public void handleAfterResponse() {
//                        displaySingleConfirmationBtn(btnConfirmBuying, pbBuyCard);
//                    }
//
//                    @Override
//                    public void handleConnectionErrors(String errorMessage) {
//                        displaySingleConfirmationBtn(btnConfirmBuying, pbBuyCard);
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void displayConfirmationBtn(View btn, ProgressBar pb) {
        btn.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
    }

    private void hideConfirmationBtn(View btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.INVISIBLE);
        pbBuyCard.setVisibility(View.VISIBLE);
    }

    private void displaySingleConfirmationBtn(View btn, ProgressBar pb) {
        btn.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    private void hideSingleConfirmationBtn(View btnConfirmBuying, ProgressBar pbBuyCard) {
        btnConfirmBuying.setVisibility(View.GONE);
        pbBuyCard.setVisibility(View.VISIBLE);
    }

    private void getCategoriesFromServer() {
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                REQ_GET_ALL_CATEGORIES, new Request<>(REQ_GET_ALL_CATEGORIES, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token()
                        , null, null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        categoryList = ParseResponses.parseCategories(mainObject);
                    }

                    @Override
                    public void handleAfterResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
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