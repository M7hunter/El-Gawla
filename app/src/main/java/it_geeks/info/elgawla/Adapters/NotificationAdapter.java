package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Models.Notification;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.Models.Round;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.SnackBuilder;
import it_geeks.info.elgawla.views.main.NotificationActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    private Context context;
    private List<Notification> notificationList;
    private BottomSheetDialog bottomSheet;
    private TextView messageTitle, messageBody;
    private SnackBuilder snackBuilder;

    public NotificationAdapter(Context context, List<Notification> notificationList, View parentView) {
        this.context = context;
        this.notificationList = notificationList;
        snackBuilder = new SnackBuilder(parentView);
        initBottomSheet();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Notification notification = notificationList.get(i);

        bind(holder, notification);

        handleEvents(holder, notification);
    }

    private void handleEvents(Holder holder, final Notification notification) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getType().trim().equals("salons"))
                {
                    ((NotificationActivity) context).dialogBuilder.displayLoadingDialog();
                    getSalonDataFromServer(notification);

                }
                else
                {
                    try
                    {
                        updateBottomSheet(notification);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
            }
        });
    }

    private void getSalonDataFromServer(Notification notification) {
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                REQ_GET_SALON_BY_ID, new RequestModel<>(REQ_GET_SALON_BY_ID, SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), notification.getId()
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Round round = parseRoundByID(mainObject);
                        Intent i = new Intent(context, SalonActivity.class);

                        i.putExtra("round", round);

                        context.startActivity(i);
                    }

                    @Override
                    public void handleAfterResponse() {
                        ((NotificationActivity) context).dialogBuilder.hideLoadingDialog();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        snackBuilder.setSnackText(errorMessage).showSnack();
                        ((NotificationActivity) context).dialogBuilder.hideLoadingDialog();
                    }
                });
    }

    private void bind(final Holder holder, final Notification notification) {
        holder.tvTitle.setText(notification.getTitle());
        holder.tvBody.setText(notification.getBody());
        holder.tvDate.setText(notification.getDate());
    }

    private void initBottomSheet() {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_message_notification, null);
        messageTitle = v.findViewById(R.id.message_title);
        messageBody = v.findViewById(R.id.message_body);
        bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(v);
    }

    private void updateBottomSheet(Notification notification) {
        messageTitle.setText(notification.getTitle());
        messageBody.setText(notification.getBody());
        bottomSheet.show();
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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
