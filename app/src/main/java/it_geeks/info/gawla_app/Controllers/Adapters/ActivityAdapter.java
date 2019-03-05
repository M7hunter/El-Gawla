package it_geeks.info.gawla_app.Controllers.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Activity;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<Activity> activityList;

    public ActivityAdapter(List<Activity> activityList) {
    this.activityList = activityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Activity activity = activityList.get(position);

        // bind
        holder.tvActivityBody.setText(activity.getBody());
        holder.tvActivityTime.setText(activity.getTime());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvActivityBody, tvActivityTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvActivityBody = itemView.findViewById(R.id.tv_activity_body);
            tvActivityTime = itemView.findViewById(R.id.tv_activity_time);
        }
    }
}
