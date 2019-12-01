package it_geeks.info.elgawla.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it_geeks.info.elgawla.repository.Models.Date;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private Context context;
    private List<Date> dateList;
    private ClickInterface.OnItemClickListener clickListener;
    private int selectedPosition = 0;
    private String currentMonth;

    public DateAdapter(Context context, List<Date> dateList, ClickInterface.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.dateList = dateList;
        this.clickListener = onItemClickListener;
        currentMonth = Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale(SharedPrefManager.getInstance(context).getSavedLang()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salon_date, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Date salonDate = dateList.get(i);

        // check if selected
        if (selectedPosition == i)
        { // selected
            selectedDay(viewHolder);
        }
        else
        { // !selected
            NotSelectedDay(viewHolder, salonDate);
        }

        viewHolder.dayOfMonth.setText(salonDate.getDay_no());
        viewHolder.month.setText(salonDate.getMonth());
        viewHolder.dayOfWeek.setText(salonDate.getDay());
        viewHolder.salonsCount.setText(String.valueOf(salonDate.getSalon_count()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(v, viewHolder.getAdapterPosition());

                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    private void selectedDay(ViewHolder viewHolder) {
        viewHolder.month.setTextColor(Color.WHITE);
        viewHolder.dayOfWeek.setTextColor(Color.WHITE);
        viewHolder.dayOfWeek.setTextColor(Color.WHITE);
        viewHolder.dayOfMonth.setTextColor(Color.WHITE);
        viewHolder.separator.setBackgroundColor(Color.WHITE);
        viewHolder.salonsCount.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_c_primary_dark));
        viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_c_primary));
    }

    private void NotSelectedDay(ViewHolder viewHolder, Date salonDate) {
        viewHolder.month.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        viewHolder.dayOfWeek.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        viewHolder.dayOfWeek.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        viewHolder.dayOfMonth.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        viewHolder.separator.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        viewHolder.salonsCount.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_c_primary));

        if (salonDate.is_today())
        {
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_c_white_bordered_c_primary));
        }
        else
        {
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_rounded_corners_white));
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayOfMonth, month, dayOfWeek, salonsCount;
        View separator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayOfMonth = itemView.findViewById(R.id.date_day_of_month);
            month = itemView.findViewById(R.id.date_month);
            dayOfWeek = itemView.findViewById(R.id.date_day_of_week);
            salonsCount = itemView.findViewById(R.id.date_salons_count);
            separator = itemView.findViewById(R.id.date_separator);
        }
    }
}