package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Notification;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.SalonActivity;

import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseRoundByID;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    private Context context;
    private List<Notification> notificationList;
    private BottomSheetDialog bottomSheet;
    private TextView messageTitle, messageBody;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        initBottomSheet();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_notification, viewGroup, false));
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
                if (notification.getType().trim().equals("salons")) {
                    ((NotificationActivity) context).displayLoading();
                    getSalonDataFromServer(notification);

                } else {
                    try {
                        updateBottomSheet(notification);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
            }
        });
    }

    private void getSalonDataFromServer(Notification notification) {
        RetrofitClient.getInstance(context).executeConnectionToServer(MainActivity.mainInstance,
                "getSalonByID", new Request(SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), notification.getId()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Round round = parseRoundByID(mainObject);
                        Intent i = new Intent(context, SalonActivity.class);

                        i.putExtra("round", round);

                        context.startActivity(i);
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((NotificationActivity) context).hideLoading();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                        ((NotificationActivity) context).hideLoading();
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

        public Holder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvBody = itemView.findViewById(R.id.tv_body);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
