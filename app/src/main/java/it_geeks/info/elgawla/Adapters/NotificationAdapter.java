package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Notification;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.salon.ClosedSalonActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;
import static it_geeks.info.elgawla.util.Constants.SALON;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    private Context context;
    private List<Notification> notificationList;
    private SnackBuilder snackBuilder;

    public NotificationAdapter(Context context, List<Notification> notificationList, View parentView) {
        this.context = context;
        snackBuilder = new SnackBuilder(parentView);
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final Notification notification = notificationList.get(i);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvBody.setText(notification.getBody());
        holder.tvDate.setText(notification.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getType().trim().equals("salons"))
                {
                    ((NotificationActivity) context).dialogBuilder.displayLoadingDialog();
                    getSalonDataFromServer(notification);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateList(List<Notification> newList) {
        notificationList.clear();
        notificationList.addAll(newList);
        notifyDataSetChanged();
    }

    private void getSalonDataFromServer(Notification notification) {
        RetrofitClient.getInstance(context).fetchDataFromServer(context,
                REQ_GET_SALON_BY_ID, new RequestModel<>(REQ_GET_SALON_BY_ID
                        , SharedPrefManager.getInstance(context).getUser().getUser_id()
                        , SharedPrefManager.getInstance(context).getUser().getApi_token()
                        , notification.getId()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Salon salon = parseRoundByID(mainObject);

                        Intent i;
                        if (salon.isClosed())
                        {
                            i = new Intent(context, ClosedSalonActivity.class);
                        }
                        else
                        {
                            i = new Intent(context, SalonActivity.class);
                        }

                        i.putExtra(SALON, salon);
                        context.startActivity(i);
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((NotificationActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((NotificationActivity) context).dialogBuilder.hideLoadingDialog();
                        snackBuilder.setSnackText(errorMessage).showSnack();
                    }
                });
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBody, tvDate;

        Holder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvBody = itemView.findViewById(R.id.tv_body);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
