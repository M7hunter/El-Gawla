package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Notification;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.views.SalonActivity;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    Context context;
    ArrayList<Notification> notificationList;
    private static String lastDate;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification,viewGroup,false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Notification notification = notificationList.get(i);
        setdata(holder , notification);
    }

    private void setdata(Holder holder , final Notification notification) {
        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());
        holder.date.setText(notification.getDate());

        // On Click Notification
        holder.notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, notification.getType(), Toast.LENGTH_SHORT).show();
            if (notification.getType() == "salons"){
                Intent i = new Intent(context, SalonActivity.class);
                i.putExtra("salon_id",notification.getId());
                Toast.makeText(context, notification.getId()+"", Toast.LENGTH_SHORT).show();
                context.startActivity(i);
            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class  Holder extends RecyclerView.ViewHolder{
        TextView title, body, date;
        CardView notificationCard;

        public Holder( View itemView) {
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
