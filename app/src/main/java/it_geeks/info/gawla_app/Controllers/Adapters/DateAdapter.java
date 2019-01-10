package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import it_geeks.info.gawla_app.General.OnItemClickListener;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.SalonDate;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private Context context;
    private List<SalonDate> dateList;
    private OnItemClickListener clickListener;
    private int selectedPosition = 0;

    public DateAdapter(Context context, List<SalonDate> dateList, OnItemClickListener clickListener) {
        this.context = context;
        this.dateList = dateList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_salon_date, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SalonDate salonDate = dateList.get(i);

        // check if selected
        if (selectedPosition == i) { // selected
            if (Integer.parseInt(salonDate.getDayOfMonth()) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) { // if today
                dayIsToday(viewHolder);
            } else {
                viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white_bordered_nice_blue));
            }
        } else { // !selected
            if (Integer.parseInt(salonDate.getDayOfMonth()) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) { // if today
                dayIsToday(viewHolder);
            } else {
                dayNotToday(viewHolder);
            }
        }

        viewHolder.dayOfMonth.setText(salonDate.getDayOfMonth());
        viewHolder.month.setText(salonDate.getMonth());
        viewHolder.dayOfWeek.setText(salonDate.getDayOfWeek());
        viewHolder.salonsCount.setText(String.valueOf(salonDate.getSalonsCount()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, viewHolder.getAdapterPosition());

                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    private void dayIsToday(ViewHolder viewHolder) {
        viewHolder.separator.setBackgroundColor(Color.parseColor("#FFFFFF"));
        viewHolder.dayOfMonth.setTextColor(Color.parseColor("#FFFFFF"));
        viewHolder.month.setTextColor(Color.parseColor("#FFFFFF"));
        viewHolder.dayOfWeek.setTextColor(Color.parseColor("#FFFFFF"));
        viewHolder.dayOfWeek.setTextColor(Color.parseColor("#FFFFFF"));
        viewHolder.salonsCount.setTextColor(Color.parseColor("#FFFFFF"));
        viewHolder.salonsCount.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_nice_blue));
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_nice_blue));
    }

    private void dayNotToday(ViewHolder viewHolder) {
        viewHolder.separator.setBackgroundColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.dayOfMonth.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.month.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.dayOfWeek.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.dayOfWeek.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.salonsCount.setTextColor(context.getResources().getColor(R.color.niceBlue));
        viewHolder.salonsCount.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_pale_grey));
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white));
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayOfMonth, month, dayOfWeek, salonsCount;
        View separator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayOfMonth = itemView.findViewById(R.id.date_day_of_month);
            month = itemView.findViewById(R.id.date_month);
            dayOfWeek = itemView.findViewById(R.id.date_day_of_week);
            salonsCount = itemView.findViewById(R.id.date_salons_count);
            separator = itemView.findViewById(R.id.date_separator);
        }
    }
}
