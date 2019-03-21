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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
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
    private int salonId, round_id;
    private Socket mSocket;

    public SalonCardsAdapter(Context context, List<Card> cardList, int salon_id, int round_id) {
        this.context = context;
        this.cardList = cardList;
        this.salonId = salon_id;
        this.round_id = round_id;
        connectSocket();
    }

    private void connectSocket() {
        if (mSocket == null) {
            mSocket = new SocketConnection().getSocket();
        }
        if (!mSocket.connected()) {
            mSocket.connect();
        }
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
                if (card.getCount() > 0) {
                    useCard(card, viewHolder.btn, viewHolder.pb);
                } else {
                    buyCard(card, viewHolder.btn, viewHolder.pb);
                }
            }
        });
    }

    private void useCard(final Card card, final Button btnConfirmBuying, final ProgressBar pbBuyCard) {
        hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
        final int userId = SharedPrefManager.getInstance(context).getUser().getUser_id();
        final String username = SharedPrefManager.getInstance(context).getUser().getName();
        String apiToken = SharedPrefManager.getInstance(context).getUser().getApi_token();
        RetrofitClient.getInstance(context).executeConnectionToServer(context, "useCard", new Request(userId, apiToken, card.getCard_id(), salonId, round_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buyCard(final Card card, final Button btnConfirmBuying, final ProgressBar pbBuyCard) {
        hideConfirmationBtn(btnConfirmBuying, pbBuyCard);
        int user_id = SharedPrefManager.getInstance(context).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(context).getUser().getApi_token();
        RetrofitClient.getInstance(context).executeConnectionToServer(context, "addCardsToUser", new Request(user_id, api_token, card.getCard_id()), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                ((SalonActivity) context).mBottomSheetDialogCardsBag.dismiss();
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayConfirmationBtn(Button btn, ProgressBar pb) {
        btn.setVisibility(View.VISIBLE);
        pb.setVisibility(View.INVISIBLE);
    }

    private void hideConfirmationBtn(Button btnConfirmBuying, ProgressBar pbBuyCard) {
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