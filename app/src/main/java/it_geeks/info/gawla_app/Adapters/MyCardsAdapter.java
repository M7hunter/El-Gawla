package it_geeks.info.gawla_app.Adapters;

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

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.MyCardModel;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.Request;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.util.Common;
import it_geeks.info.gawla_app.util.SnackBuilder;
import it_geeks.info.gawla_app.views.account.MyCardsActivity;
import it_geeks.info.gawla_app.views.salon.SalonActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.gawla_app.util.Constants.REQ_GET_SALON_BY_ID;

public class MyCardsAdapter extends RecyclerView.Adapter<MyCardsAdapter.ViewHolder> {

    private Context context;
    private List<MyCardModel> myCardsList;
    private SnackBuilder snackBuilder;

    public MyCardsAdapter(Context context, List<MyCardModel> myCardsList, View parentView) {
        this.context = context;
        this.myCardsList = myCardsList;
        snackBuilder = new SnackBuilder(parentView);
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
        RetrofitClient.getInstance(context).executeConnectionToServer(
                context,
                REQ_GET_SALON_BY_ID, new Request<>(REQ_GET_SALON_BY_ID, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), salonId,
                        null, null, null, null),
                new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Round round = parseRoundByID(mainObject);

                        Intent i = new Intent(context, SalonActivity.class);
                        i.putExtra("round", round);
                        context.startActivity(i);
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((MyCardsActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((MyCardsActivity) context).dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnackbar();
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
