package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.Socket;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.SocketConnection.SocketConnection;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.views.SalonActivity;

public class SalonCardsAdapter extends RecyclerView.Adapter<SalonCardsAdapter.ViewHolder> {

    private Context context;
    private List<Card> cardList;
    private int salonId;
    private int round_id;
    private BottomSheetDialog mBottomSheetDialogSingleCard;
    private Socket mSocket;

    public SalonCardsAdapter(Context context, List<Card> cardList, int salon_id, int round_id) {
        this.context = context;
        this.cardList = cardList;
        this.salonId = salon_id;
        this.round_id = round_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        connectSocket();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salon_bottom_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Card card = cardList.get(i);

        viewHolder.tvCardTitle.setText(card.getCard_name());
        viewHolder.tvCardCount.setText(String.valueOf(card.getCount()));
        Common.Instance(context).changeDrawableViewColor(viewHolder.cardIcon, card.getCard_color());

        if (card.getCount() > 0) { // use card state
            viewHolder.btn.setBackgroundColor(context.getResources().getColor(R.color.greenBlue));
            viewHolder.btn.setText(context.getString(R.string.use_card));
            viewHolder.tvCardCount.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
        } else { // buy card  state
            viewHolder.btn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.btn.setText(context.getString(R.string.buy_card));
            viewHolder.tvCardCount.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_red));
        }

        //open single card sheet
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBottomSheetSingleCard(card).show();
            }
        });
    }

    private BottomSheetDialog initBottomSheetSingleCard(final Card card) {
        mBottomSheetDialogSingleCard = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        final View sheetView = ((SalonActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_single_card, null);

        //init bottom sheet views
        TextView cardTitle, cardDescription, cardCost, tvBuyMainText, tvCostLabel;
        View cardIcon;
        final CardView btnConfirmBuying;
        final ProgressBar pbBuyCard;

        cardTitle = sheetView.findViewById(R.id.single_card_title);
        cardDescription = sheetView.findViewById(R.id.single_card_description);
        cardCost = sheetView.findViewById(R.id.single_card_cost);
        tvBuyMainText = sheetView.findViewById(R.id.tv_buy_card_main_text);
        tvCostLabel = sheetView.findViewById(R.id.tv_price_label_text);
        cardIcon = sheetView.findViewById(R.id.single_card_icon);
        btnConfirmBuying = sheetView.findViewById(R.id.btn_confirm_buying_card);
        pbBuyCard = sheetView.findViewById(R.id.pb_buy_card);

        cardTitle.setText(card.getCard_name());
        cardDescription.setText(card.getCard_details());
        cardCost.setText(card.getCard_cost());

        if (card.getCount() > 0) { // buy card state
            btnConfirmBuying.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_green_270));
            cardCost.setVisibility(View.GONE);
            tvCostLabel.setVisibility(View.GONE);

            tvBuyMainText.setText(context.getString(R.string.use_card));

        } else { // use card state
            btnConfirmBuying.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        Common.Instance(context).changeDrawableViewColor(cardIcon, card.getCard_color());

        btnConfirmBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (card.getCount() > 0) {
                    useCard(card, btnConfirmBuying, pbBuyCard);
                } else {
                    buyCard(card, btnConfirmBuying, pbBuyCard);
                }
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

    private void connectSocket() {
        mSocket = new SocketConnection().getSocket();
        mSocket.connect();
    }

    private void useCard(final Card card, final CardView btnConfirmBuying, final ProgressBar pbBuyCard) {
        hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
        final int userId = SharedPrefManager.getInstance(context).getUser().getUser_id();
        final String username = SharedPrefManager.getInstance(context).getUser().getName();
        String apiToken = SharedPrefManager.getInstance(context).getUser().getApi_token();
        RetrofitClient.getInstance(context).executeConnectionToServer(context, "useCard", new Request(userId, apiToken, card.getCard_id(), salonId, round_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
                mBottomSheetDialogSingleCard.dismiss();
                card.setCount(card.getCount() - 1);
                ((SalonActivity) context).initBottomSheetCardsBag(); // refresh the cards list

                JSONObject use_card = new JSONObject();
                try {
                    use_card.put("user", username);
                    use_card.put("type", card.getCard_type());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                mSocket.emit("use_card", use_card);
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
            }
        });
    }

    private void buyCard(final Card card, final CardView btnConfirmBuying, final ProgressBar pbBuyCard) {
        hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
        int user_id = SharedPrefManager.getInstance(context).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(context).getUser().getApi_token();
        RetrofitClient.getInstance(context).executeConnectionToServer(context, "addCardsToUser", new Request(user_id, api_token, card.getCard_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
                mBottomSheetDialogSingleCard.dismiss();
                ((SalonActivity) context).initBottomSheetCardsBag(); // refresh the cards list
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

        TextView tvCardTitle, tvCardCount;
        View cardIcon;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCardTitle = itemView.findViewById(R.id.bottom_card_title);
            tvCardCount = itemView.findViewById(R.id.tv_cards_count_cards_bag);
            cardIcon = itemView.findViewById(R.id.bottom_card_icon);
            btn = itemView.findViewById(R.id.bottom_card_btn);
        }
    }
}