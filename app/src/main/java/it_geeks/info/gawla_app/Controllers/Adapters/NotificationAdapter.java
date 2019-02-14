package it_geeks.info.gawla_app.Controllers.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Notification;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.NotificationActivity;
import it_geeks.info.gawla_app.views.SalonActivity;

import static it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses.parseRoundByID;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    Context context;
    ArrayList<Notification> notificationList;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, viewGroup, false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Notification notification = notificationList.get(i);
        setdata(holder, notification);
    }

    private void setdata(final Holder holder, final Notification notification) {
        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());
        holder.date.setText(notification.getDate());

        // On Click Notification
        holder.notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.notificationCard.setEnabled(false);

                if (notification.getType().trim().equals("salons")) {
                    try {
                        ((NotificationActivity) context).notificationLoading.setVisibility(View.VISIBLE);
                        RetrofitClient.getInstance(context).executeConnectionToServer(MainActivity.mainInstance,
                                "getSalonByID", new Request(SharedPrefManager.getInstance(context).getUser().getUser_id(), SharedPrefManager.getInstance(context).getUser().getApi_token(), notification.getId()), new HandleResponses() {
                                    @Override
                                    public void handleTrueResponse(JsonObject mainObject) {
                                        Round round = parseRoundByID(mainObject);
                                        Intent i = new Intent(context, SalonActivity.class);
                                        // send round's data to round page
                                        i.putExtra("product_id", round.getProduct_id());
                                        i.putExtra("salon_id", round.getSalon_id());
                                        i.putExtra("product_name", round.getProduct_name());
                                        i.putExtra("category_name", round.getCategory_name());
                                        i.putExtra("category_color", round.getCategory_color());
                                        i.putExtra("country_name", round.getCountry_name());
                                        i.putExtra("product_commercial_price", round.getProduct_commercial_price());
                                        i.putExtra("product_product_description", round.getProduct_product_description());
                                        i.putExtra("product_image", round.getProduct_image());
                                        i.putExtra("round_start_time", round.getRound_start_time());
                                        i.putExtra("round_end_time", round.getRound_end_time());
                                        i.putExtra("first_join_time", round.getFirst_join_time());
                                        i.putExtra("second_join_time", round.getSecond_join_time());
                                        i.putExtra("round_date", round.getRound_date());
                                        i.putExtra("round_time", round.getRound_time());
                                        i.putExtra("rest_time", round.getRest_time());
                                        i.putExtra("product_images", (Serializable) round.getProduct_images());
                                        i.putExtra("salon_cards", (Serializable) round.getSalon_cards());

                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context));
                                            context.startActivity(i, options.toBundle());
                                        } else {
                                            context.startActivity(i);
                                        }
                                        ((NotificationActivity) context).notificationLoading.setVisibility(View.GONE);
                                        holder.notificationCard.setEnabled(true);
                                    }

                                    @Override
                                    public void handleFalseResponse(JsonObject mainObject) {
                                        ((NotificationActivity) context).notificationLoading.setVisibility(View.GONE);
                                        holder.notificationCard.setEnabled(true);
                                    }

                                    @Override
                                    public void handleEmptyResponse() {
                                        ((NotificationActivity) context).notificationLoading.setVisibility(View.GONE);
                                        holder.notificationCard.setEnabled(true);
                                    }

                                    @Override
                                    public void handleConnectionErrors(String errorMessage) {
                                        Toast.makeText(MainActivity.mainInstance, errorMessage, Toast.LENGTH_SHORT).show();
                                        ((NotificationActivity) context).notificationLoading.setVisibility(View.GONE);
                                        holder.notificationCard.setEnabled(true);
                                    }
                                });
                    }catch(Exception e){}

                } else {
                    try{
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View vv = layoutInflater.inflate(R.layout.message_notification, null);
                        TextView messageTitle = vv.findViewById(R.id.message_title);
                        TextView messageBody = vv.findViewById(R.id.message_body);
                        messageTitle.setText(notification.getTitle());
                        messageBody.setText(notification.getBody());
                        BottomSheetDialog Dialog = new BottomSheetDialog(context);
                        Dialog.setContentView(vv);
                        Dialog.show();
                    }catch (Exception e){ }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView title, body, date;
        CardView notificationCard;

        public Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            body = itemView.findViewById(R.id.tv_body);
            date = itemView.findViewById(R.id.tv_date);
            notificationCard = itemView.findViewById(R.id.notification_card);
        }
    }

    // On Click Notification
    ;
}