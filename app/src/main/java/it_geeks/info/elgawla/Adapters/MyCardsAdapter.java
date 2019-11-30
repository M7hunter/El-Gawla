package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.List;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.MyCardModel;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.account.MyCardsActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;
import static it_geeks.info.elgawla.util.Constants.SALON;

public class MyCardsAdapter extends RecyclerView.Adapter<MyCardsAdapter.ViewHolder> {

    private Context context;
    private List<MyCardModel> myCardsList;
    private SnackBuilder snackBuilder;

    public MyCardsAdapter(Context context, List<MyCardModel> myCardsList, SnackBuilder snackBuilder) {
        this.context = context;
        this.myCardsList = myCardsList;
        this.snackBuilder = snackBuilder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MyCardModel myCard = myCardsList.get(position);

        holder.tvCat.setText(myCard.getCard_category());
        Common.Instance().changeDrawableViewColor(holder.vCardColor, myCard.getCard_color());

        if (myCard.getCard_status())
        {
            holder.tvStatus.setText(context.getString(R.string.used));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSalonByID(myCard.getSalon_id());
                }
            });
        }
        else
        {
            holder.tvStatus.setText(context.getString(R.string.unused));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.greenBlue));
        }
    }

    private void getSalonByID(int salonId) {
        ((MyCardsActivity) context).dialogBuilder.displayLoadingDialog();
        RetrofitClient.getInstance(context).fetchDataFromServer(
                context,
                REQ_GET_SALON_BY_ID, new RequestModel<>(REQ_GET_SALON_BY_ID, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), salonId,
                        null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        context.startActivity(new Intent(context, SalonActivity.class).putExtra(SALON, parseRoundByID(mainObject)));
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((MyCardsActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((MyCardsActivity) context).dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return myCardsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View vCardColor;
        TextView tvCat, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vCardColor = itemView.findViewById(R.id.v_my_card_color);
            tvCat = itemView.findViewById(R.id.tv_my_card_category);
            tvStatus = itemView.findViewById(R.id.tv_my_card_status);

        }
    }
}
