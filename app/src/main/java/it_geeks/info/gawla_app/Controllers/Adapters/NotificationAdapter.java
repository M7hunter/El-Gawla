package it_geeks.info.gawla_app.Controllers.Adapters;

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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Notifications;
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
    private List<Notifications> notificationList;
    private BottomSheetDialog bottomSheet;
    private TextView messageTitle;
    private TextView messageBody;

    public NotificationAdapter(Context context, List<Notifications> notificationList) {
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
        Notifications notification = notificationList.get(i);
        setData(holder, notification);
    }

    private void setData(final Holder holder, final Notifications notification) {
        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());
        holder.date.setText(notification.getDate());

        // On Click NotificationDao
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (notification.getType().trim().equals("salons")) {
                        ((NotificationActivity) context).displayLoading();

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
                                        i.putExtra("round_date", round.getRound_date());
                                        i.putExtra("product_images", (Serializable) round.getProduct_images());
                                        i.putExtra("salon_cards", (Serializable) round.getSalon_cards());

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

                } else {
                    try {
                       updateBottomSheet(notification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initBottomSheet() {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_message_notification, null);
        messageTitle = v.findViewById(R.id.message_title);
        messageBody = v.findViewById(R.id.message_body);
        bottomSheet = new BottomSheetDialog(context);
        bottomSheet.setContentView(v);
    }

    private void updateBottomSheet(Notifications notification) {
        messageTitle.setText(notification.getTitle());
        messageBody.setText(notification.getBody());
        bottomSheet.show();
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView title, body, date;

        public Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            body = itemView.findViewById(R.id.tv_body);
            date = itemView.findViewById(R.id.tv_date);
        }
    }
}
